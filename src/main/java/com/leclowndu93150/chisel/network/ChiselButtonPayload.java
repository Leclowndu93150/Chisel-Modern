package com.leclowndu93150.chisel.network;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import com.leclowndu93150.chisel.inventory.HitechChiselMenu;
import com.leclowndu93150.chisel.inventory.SlotChiselSelection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Payload for the hitech chisel "chisel all" button.
 */
public record ChiselButtonPayload(int[] slotIds) implements CustomPacketPayload {

    public static final Type<ChiselButtonPayload> TYPE = new Type<>(Chisel.id("chisel_button"));

    public static final StreamCodec<FriendlyByteBuf, ChiselButtonPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ChiselButtonPayload decode(FriendlyByteBuf buf) {
            return new ChiselButtonPayload(buf.readVarIntArray());
        }

        @Override
        public void encode(FriendlyByteBuf buf, ChiselButtonPayload payload) {
            buf.writeVarIntArray(payload.slotIds);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                chiselAll(player, slotIds);
            }
        });
    }

    public static void chiselAll(Player player, int[] slots) {
        if (!(player.containerMenu instanceof HitechChiselMenu menu)) {
            return;
        }

        ItemStack chisel = menu.getChiselStack();
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

        for (int slotIndex : slots) {
            ItemStack stack = player.getInventory().getItem(slotIndex);
            if (!stack.isEmpty()) {
                TagKey<Item> stackGroup = CarvingHelper.getCarvingGroupForItem(stack);
                if (stackGroup == null || !stackGroup.equals(targetGroup)) {
                    continue;
                }

                ItemStack result = new ItemStack(target.getItem(), stack.getCount());
                player.getInventory().setItem(slotIndex, result);

                if (chisel.isDamageableItem()) {
                    chisel.setDamageValue(chisel.getDamageValue() + 1);
                    if (chisel.getDamageValue() >= chisel.getMaxDamage()) {
                        break;
                    }
                }
            }
        }
    }
}
