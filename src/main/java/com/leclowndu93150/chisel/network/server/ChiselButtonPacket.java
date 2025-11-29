package com.leclowndu93150.chisel.network.server;

import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import com.leclowndu93150.chisel.inventory.HitechChiselMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet for the hitech chisel "chisel all" button.
 */
public class ChiselButtonPacket {

    private final int[] slotIds;

    public ChiselButtonPacket(int[] slotIds) {
        this.slotIds = slotIds;
    }

    public static void encode(ChiselButtonPacket packet, FriendlyByteBuf buf) {
        buf.writeVarIntArray(packet.slotIds);
    }

    public static ChiselButtonPacket decode(FriendlyByteBuf buf) {
        return new ChiselButtonPacket(buf.readVarIntArray());
    }

    public static void handle(ChiselButtonPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                chiselAll(player, packet.slotIds);
            }
        });
        context.setPacketHandled(true);
    }

    public static void chiselAll(Player player, int[] slots) {
        if (!(player.containerMenu instanceof HitechChiselMenu menu)) {
            return;
        }

        ItemStack chisel = menu.getChisel();
        ItemStack target = menu.getTargetItem();

        if (!(chisel.getItem() instanceof IChiselItem)) {
            return;
        }

        if (chisel.isEmpty() || target.isEmpty()) {
            return;
        }

        TagKey<Item> targetGroup = CarvingHelper.getCarvingGroupForItem(target);
        if (targetGroup == null) {
            return;
        }

        boolean chiseledAny = false;
        for (int menuSlotIndex : slots) {
            if (menuSlotIndex < 0 || menuSlotIndex >= menu.slots.size()) {
                continue;
            }

            Slot slot = menu.getSlot(menuSlotIndex);
            ItemStack stack = slot.getItem();

            if (!stack.isEmpty()) {
                TagKey<Item> stackGroup = CarvingHelper.getCarvingGroupForItem(stack);
                if (stackGroup == null || !stackGroup.equals(targetGroup)) {
                    continue;
                }

                int toChisel = stack.getCount();

                if (chisel.isDamageableItem()) {
                    int durabilityLeft = chisel.getMaxDamage() - chisel.getDamageValue();
                    toChisel = Math.min(toChisel, durabilityLeft);

                    if (toChisel <= 0) {
                        break;
                    }

                    chisel.setDamageValue(chisel.getDamageValue() + toChisel);
                }

                ItemStack result = new ItemStack(target.getItem(), toChisel);
                slot.set(result);
                chiseledAny = true;

                if (chisel.isDamageableItem() && chisel.getDamageValue() >= chisel.getMaxDamage()) {
                    chisel.shrink(1);
                    break;
                }
            }
        }

        if (chiseledAny) {
            Block targetBlock = target.getItem() instanceof BlockItem blockItem
                ? blockItem.getBlock()
                : Blocks.AIR;
            CarvingHelper.playChiselSound(player.level(), player, targetBlock);
        }
    }
}
