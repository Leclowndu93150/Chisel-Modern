package com.leclowndu93150.chisel.command;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.client.util.CTMDetection;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.init.ChiselItems;
import com.leclowndu93150.chisel.inventory.ChiselMenu;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.ArrayList;
import java.util.List;

public class ChiselDebugCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("chisel")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.literal("debug")
                        .then(Commands.literal("placectm")
                                .executes(ChiselDebugCommands::placeCTMBlocks))
                        .then(Commands.literal("placeall")
                                .executes(ChiselDebugCommands::placeAllBlocks))
                        .then(Commands.literal("openchisel")
                                .executes(ChiselDebugCommands::openDebugChisel))
                )
        );
    }

    private static int openDebugChisel(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("This command must be run by a player"));
            return 0;
        }

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.isEmpty()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ChiselItems.IRON_CHISEL.get()));
        } else if (!(mainHand.getItem() instanceof com.leclowndu93150.chisel.api.IChiselItem)) {
            source.sendFailure(Component.literal("Hold a chisel or have an empty main hand"));
            return 0;
        }

        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("Debug Chisel (All Blocks)");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player p) {
                ChiselMenu menu = new ChiselMenu(containerId, playerInv, InteractionHand.MAIN_HAND);
                menu.setDebugShowAll(true);
                return menu;
            }
        }, buf -> {
            buf.writeBoolean(true);  // MAIN_HAND
            buf.writeBoolean(true);  // debugShowAll
        });

        source.sendSuccess(() -> Component.literal("Opened debug chisel with all variants"), false);
        return 1;
    }

    private static int placeCTMBlocks(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        ServerPlayer player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("This command must be run by a player"));
            return 0;
        }

        BlockPos startPos = player.blockPosition();
        List<Block> ctmBlocks = new ArrayList<>();

        // Collect all blocks that have CTM
        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            for (DeferredBlock<?> deferredBlock : blockType.getAllBlocks()) {
                Block block = deferredBlock.get();
                if (CTMDetection.hasCTM(block.asItem())) {
                    ctmBlocks.add(block);
                }
            }
        }

        if (ctmBlocks.isEmpty()) {
            source.sendFailure(Component.literal("No CTM blocks found! Make sure CTM detection has been initialized."));
            return 0;
        }

        // Place blocks in 2x2 groups on the floor
        int blocksPerRow = (int) Math.ceil(Math.sqrt(ctmBlocks.size()));
        int x = 0;
        int z = 0;

        for (int i = 0; i < ctmBlocks.size(); i++) {
            Block block = ctmBlocks.get(i);
            BlockState state = block.defaultBlockState();

            // Place 2x2 pattern (no gaps)
            int baseX = startPos.getX() + (x * 2);
            int baseZ = startPos.getZ() + (z * 2);
            int y = startPos.getY();

            level.setBlockAndUpdate(new BlockPos(baseX, y, baseZ), state);
            level.setBlockAndUpdate(new BlockPos(baseX + 1, y, baseZ), state);
            level.setBlockAndUpdate(new BlockPos(baseX, y, baseZ + 1), state);
            level.setBlockAndUpdate(new BlockPos(baseX + 1, y, baseZ + 1), state);

            x++;
            if (x >= blocksPerRow) {
                x = 0;
                z++;
            }
        }

        source.sendSuccess(() -> Component.literal("Placed " + ctmBlocks.size() + " CTM blocks in 2x2 patterns"), true);
        return 1;
    }

    private static int placeAllBlocks(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        ServerPlayer player = source.getPlayer();

        if (player == null) {
            source.sendFailure(Component.literal("This command must be run by a player"));
            return 0;
        }

        BlockPos startPos = player.blockPosition();
        List<Block> allBlocks = new ArrayList<>();

        // Collect all chisel blocks
        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            for (DeferredBlock<?> deferredBlock : blockType.getAllBlocks()) {
                allBlocks.add(deferredBlock.get());
            }
        }

        if (allBlocks.isEmpty()) {
            source.sendFailure(Component.literal("No chisel blocks found!"));
            return 0;
        }

        // Place blocks in 2x2 groups on the floor
        int blocksPerRow = (int) Math.ceil(Math.sqrt(allBlocks.size()));
        int x = 0;
        int z = 0;

        for (int i = 0; i < allBlocks.size(); i++) {
            Block block = allBlocks.get(i);
            BlockState state = block.defaultBlockState();

            // Place 2x2 pattern (no gaps)
            int baseX = startPos.getX() + (x * 2);
            int baseZ = startPos.getZ() + (z * 2);
            int y = startPos.getY();

            level.setBlockAndUpdate(new BlockPos(baseX, y, baseZ), state);
            level.setBlockAndUpdate(new BlockPos(baseX + 1, y, baseZ), state);
            level.setBlockAndUpdate(new BlockPos(baseX, y, baseZ + 1), state);
            level.setBlockAndUpdate(new BlockPos(baseX + 1, y, baseZ + 1), state);

            x++;
            if (x >= blocksPerRow) {
                x = 0;
                z++;
            }
        }

        source.sendSuccess(() -> Component.literal("Placed " + allBlocks.size() + " chisel blocks in 2x2 patterns"), true);
        return 1;
    }
}
