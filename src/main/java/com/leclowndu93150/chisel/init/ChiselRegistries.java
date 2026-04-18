package com.leclowndu93150.chisel.init;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.chunkdata.OffsetData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.function.Supplier;


public class ChiselRegistries {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Chisel.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Chisel.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Chisel.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Chisel.MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, Chisel.MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Chisel.MODID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Chisel.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, Chisel.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Chisel.MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Chisel.MODID);

    public static final Supplier<AttachmentType<OffsetData>> OFFSET_DATA = ATTACHMENT_TYPES.register("offset_data",
            () -> AttachmentType.builder(OffsetData::new)
                    .serialize(new IAttachmentSerializer<OffsetData>() {
                        @Override
                        public OffsetData read(IAttachmentHolder holder, ValueInput input) {
                            OffsetData data = new OffsetData();
                            input.read("data", CompoundTag.CODEC).ifPresent(data::readFromNBT);
                            return data;
                        }

                        @Override
                        public boolean write(OffsetData attachment, ValueOutput output) {
                            if (attachment.isEmpty()) return false;
                            CompoundTag tag = new CompoundTag();
                            attachment.writeToNBT(tag);
                            output.store("data", CompoundTag.CODEC, tag);
                            return true;
                        }
                    })
                    .build());
}
