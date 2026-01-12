package com.leclowndu93150.chisel.item;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.leclowndu93150.chisel.ChiselConfig;
import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.api.carving.IChiselMode;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import com.leclowndu93150.chisel.carving.ChiselMode;
import com.leclowndu93150.chisel.compat.ftbultimine.FTBUltimineHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@EventBusSubscriber
public class ChiselController {

    // Cache to prevent double-firing of events
    private static final LoadingCache<Player, Long> CLICK_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .weakKeys()
            .build(new CacheLoader<>() {
                @Override
                public Long load(Player key) {
                    return 0L;
                }
            });

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        ItemStack held = event.getItemStack();

        if (!(held.getItem() instanceof IChiselItem chisel)) {
            return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Direction side = event.getFace();
        BlockState state = level.getBlockState(pos);

        if (!chisel.canChiselBlock(level, player, event.getHand(), pos, state)) {
            return;
        }

        TagKey<Item> blockGroup = CarvingHelper.getCarvingGroupForBlock(state.getBlock());
        if (blockGroup == null) {
            return;
        }

        boolean fuzzy = chisel.isFuzzyMode(held);
        Iterable<? extends BlockPos> candidates = getCandidates(player, pos, side, chisel.getMode(held), fuzzy);

        ItemStack target = chisel.getTarget(held);

        if (!target.isEmpty()) {
            TagKey<Item> targetGroup = CarvingHelper.getCarvingGroupForItem(target);
            if (blockGroup.equals(targetGroup)) {
                Block targetBlock = Block.byItem(target.getItem());
                if (targetBlock != null) {
                    setAll(candidates, player, state, targetBlock, held, event.getHand(), fuzzy);
                    event.setCanceled(true);
                }
            }
        } else {
            List<Item> variations = CarvingHelper.getItemsInGroup(blockGroup);
            if (variations.isEmpty()) {
                return;
            }

            int index = -1;
            for (int i = 0; i < variations.size(); i++) {
                if (Block.byItem(variations.get(i)) == state.getBlock()) {
                    index = i;
                    break;
                }
            }

            index = player.isCrouching() ? index - 1 : index + 1;
            index = (index + variations.size()) % variations.size();

            Block nextBlock = Block.byItem(variations.get(index));
            if (nextBlock != null) {
                setAll(candidates, player, state, nextBlock, held, event.getHand(), fuzzy);
                event.setCanceled(true);
            }
        }
    }

    private static void setAll(Iterable<? extends BlockPos> candidates, Player player, BlockState origState, Block targetBlock, ItemStack chisel, InteractionHand hand, boolean fuzzy) {
        if (!checkClickCache(player)) return;

        for (BlockPos pos : candidates) {
            if (chisel.isEmpty()) {
                break;
            }
            setVariation(player, pos, origState, targetBlock, chisel, hand, fuzzy);
        }
    }

    private static boolean checkClickCache(Player player) {
        long time = player.level().getGameTime();
        int cooldown = ChiselConfig.carvingCooldownTicks;
        long lastClick = CLICK_CACHE.getUnchecked(player);
        if (lastClick > time - cooldown) {
            if (player.level().isClientSide) {
                long ticksRemaining = cooldown - (time - lastClick);
                double secondsRemaining = ticksRemaining / 20.0;
                player.displayClientMessage(Component.translatable("chisel.message.cooldown", String.format("%.1f", secondsRemaining)), true);
            }
            return false;
        }
        CLICK_CACHE.put(player, time);
        return true;
    }

    private static void setVariation(Player player, BlockPos pos, BlockState origState, Block targetBlock, ItemStack chiselStack, InteractionHand hand, boolean fuzzy) {
        Level level = player.level();
        BlockState curState = level.getBlockState(pos);

        if (curState.getBlock() == targetBlock) {
            return;
        }

        if (fuzzy) {
            if (!CarvingHelper.areInSameGroup(origState, curState)) {
                return;
            }
        } else {
            if (origState != curState) {
                return;
            }
        }

        if (chiselStack.getItem() instanceof IChiselItem chisel) {
            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;

            if (chisel.onChisel(level, player, chiselStack, targetBlock)) {
                chiselStack.hurtAndBreak(1, player, slot);
            }

            if (chiselStack.isEmpty() || chiselStack.getCount() <= 0) {
                ItemStack targetItem = chisel.getTarget(chiselStack);
                player.getInventory().setItem(player.getInventory().selected, targetItem.isEmpty() ? ItemStack.EMPTY : targetItem);
                return;
            }

            if (level.isClientSide) {
                CarvingHelper.playChiselSound(level, player, targetBlock);
                level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(curState));
            }

            level.setBlockAndUpdate(pos, targetBlock.defaultBlockState());
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() == InteractionHand.OFF_HAND) {
            ItemStack mainhandStack = event.getEntity().getMainHandItem();
            if (mainhandStack.getItem() instanceof IChiselItem) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        ItemStack stack = event.getPlayer().getMainHandItem();
        if (event.getPlayer().getAbilities().instabuild && !stack.isEmpty() && stack.getItem() instanceof IChiselItem) {
            event.setCanceled(true);
        }
    }

    private static Iterable<? extends BlockPos> getCandidates(Player player, BlockPos pos, Direction side, IChiselMode mode, boolean fuzzy) {
        if (ChiselConfig.enableUltimineCompat && ModList.get().isLoaded("ftbultimine")) {
            Optional<Collection<BlockPos>> ultimineSelection = FTBUltimineHelper.getBlockSelection(player);
            if (ultimineSelection.isPresent()) {
                return ultimineSelection.get();
            }
        }
        if (mode instanceof ChiselMode chiselMode) {
            return chiselMode.getCandidates(player, pos, side, fuzzy);
        }
        return mode.getCandidates(player, pos, side);
    }
}
