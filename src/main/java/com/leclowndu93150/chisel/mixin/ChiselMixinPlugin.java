package com.leclowndu93150.chisel.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Mixin plugin to conditionally load mod-specific mixins.
 * This ensures FTB Ultimine mixins only load when the mod is present.
 */
public class ChiselMixinPlugin implements IMixinConfigPlugin {

    private static final String FTBULTIMINE_MIXIN = "com.leclowndu93150.chisel.mixin.compat.ftbultimine.ShapeContextMixin";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.equals(FTBULTIMINE_MIXIN)) {
            return isModLoaded("ftbultimine");
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        if (isModLoaded("ftbultimine")) {
            return List.of("compat.ftbultimine.ShapeContextMixin");
        }
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static boolean isModLoaded(String modId) {
        try {
            Class.forName("dev.ftb.mods.ftbultimine.FTBUltimine", false, ChiselMixinPlugin.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
