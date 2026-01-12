package com.leclowndu93150.chisel;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Chisel.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChiselConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.DoubleValue CONCRETE_VELOCITY_MULT = BUILDER
            .comment("Speed multiplier when walking on concrete blocks")
            .defineInRange("concreteVelocityMult", 1.35, 1.0, 2.0);

    private static final ForgeConfigSpec.BooleanValue ALLOW_CHISEL_DAMAGE = BUILDER
            .comment("Whether chisel tools take damage when used")
            .define("allowChiselDamage", true);

    private static final ForgeConfigSpec.IntValue IRON_CHISEL_MAX_DAMAGE = BUILDER
            .comment("Maximum durability of the iron chisel")
            .defineInRange("ironChiselMaxDamage", 512, 1, 10000);

    private static final ForgeConfigSpec.IntValue DIAMOND_CHISEL_MAX_DAMAGE = BUILDER
            .comment("Maximum durability of the diamond chisel")
            .defineInRange("diamondChiselMaxDamage", 5056, 1, 50000);

    private static final ForgeConfigSpec.IntValue HITECH_CHISEL_MAX_DAMAGE = BUILDER
            .comment("Maximum durability of the iChisel (hitech chisel)")
            .defineInRange("hitechChiselMaxDamage", 10048, 1, 100000);

    private static final ForgeConfigSpec.IntValue CARVING_COOLDOWN_TICKS = BUILDER
            .comment("Cooldown between carving actions in ticks (20 ticks = 1 second, default 4 = 0.2s)")
            .defineInRange("carvingCooldownTicks", 4, 0, 100);

    private static final ForgeConfigSpec.BooleanValue IRON_CHISEL_CAN_LEFT_CLICK = BUILDER
            .comment("Whether the iron chisel can left-click chisel blocks in the world")
            .define("ironChiselCanLeftClick", false);

    private static final ForgeConfigSpec.BooleanValue IRON_CHISEL_HAS_MODES = BUILDER
            .comment("Whether the iron chisel has access to chisel modes")
            .define("ironChiselHasModes", false);

    private static final ForgeConfigSpec.IntValue IRON_CHISEL_ATTACK_DAMAGE = BUILDER
            .comment("Attack damage of the iron chisel")
            .defineInRange("ironChiselAttackDamage", 2, 0, 50);

    private static final ForgeConfigSpec.IntValue DIAMOND_CHISEL_ATTACK_DAMAGE = BUILDER
            .comment("Attack damage of the diamond chisel")
            .defineInRange("diamondChiselAttackDamage", 3, 0, 50);

    private static final ForgeConfigSpec.IntValue HITECH_CHISEL_ATTACK_DAMAGE = BUILDER
            .comment("Attack damage of the iChisel")
            .defineInRange("hitechChiselAttackDamage", 3, 0, 50);

    private static final ForgeConfigSpec.BooleanValue AUTO_CHISEL_POWERED = BUILDER
            .comment("Whether the auto chisel accepts Forge Energy (FE)")
            .define("autoChiselPowered", true);

    private static final ForgeConfigSpec.BooleanValue AUTO_CHISEL_NEEDS_POWER = BUILDER
            .comment("Whether the auto chisel requires power to function")
            .define("autoChiselNeedsPower", false);

    // ==================== WORLDGEN CONFIGURATION ====================

    // Marble worldgen config
    private static final ForgeConfigSpec.BooleanValue MARBLE_ENABLED = BUILDER
            .comment("Enable marble ore generation")
            .define("worldgen.marble.enabled", true);

    private static final ForgeConfigSpec.IntValue MARBLE_VEIN_COUNT = BUILDER
            .comment("Number of marble veins per chunk")
            .defineInRange("worldgen.marble.veinCount", 1, 0, 64);

    private static final ForgeConfigSpec.IntValue MARBLE_VEIN_SIZE = BUILDER
            .comment("Maximum size of marble veins")
            .defineInRange("worldgen.marble.veinSize", 48, 1, 128);

    private static final ForgeConfigSpec.IntValue MARBLE_MIN_Y = BUILDER
            .comment("Minimum Y level for marble generation")
            .defineInRange("worldgen.marble.minY", 0, -64, 320);

    private static final ForgeConfigSpec.IntValue MARBLE_MAX_Y = BUILDER
            .comment("Maximum Y level for marble generation")
            .defineInRange("worldgen.marble.maxY", 60, -64, 320);

    // Limestone worldgen config
    private static final ForgeConfigSpec.BooleanValue LIMESTONE_ENABLED = BUILDER
            .comment("Enable limestone ore generation")
            .define("worldgen.limestone.enabled", true);

    private static final ForgeConfigSpec.IntValue LIMESTONE_VEIN_COUNT = BUILDER
            .comment("Number of limestone veins per chunk")
            .defineInRange("worldgen.limestone.veinCount", 1, 0, 64);

    private static final ForgeConfigSpec.IntValue LIMESTONE_VEIN_SIZE = BUILDER
            .comment("Maximum size of limestone veins")
            .defineInRange("worldgen.limestone.veinSize", 48, 1, 128);

    private static final ForgeConfigSpec.IntValue LIMESTONE_MIN_Y = BUILDER
            .comment("Minimum Y level for limestone generation")
            .defineInRange("worldgen.limestone.minY", 0, -64, 320);

    private static final ForgeConfigSpec.IntValue LIMESTONE_MAX_Y = BUILDER
            .comment("Maximum Y level for limestone generation")
            .defineInRange("worldgen.limestone.maxY", 60, -64, 320);

    // Diabase worldgen config
    private static final ForgeConfigSpec.BooleanValue DIABASE_ENABLED = BUILDER
            .comment("Enable diabase ore generation")
            .define("worldgen.diabase.enabled", true);

    private static final ForgeConfigSpec.IntValue DIABASE_VEIN_COUNT = BUILDER
            .comment("Number of diabase veins per chunk")
            .defineInRange("worldgen.diabase.veinCount", 1, 0, 64);

    private static final ForgeConfigSpec.IntValue DIABASE_VEIN_SIZE = BUILDER
            .comment("Maximum size of diabase veins")
            .defineInRange("worldgen.diabase.veinSize", 48, 1, 128);

    private static final ForgeConfigSpec.IntValue DIABASE_MIN_Y = BUILDER
            .comment("Minimum Y level for diabase generation (near lava level)")
            .defineInRange("worldgen.diabase.minY", -64, -64, 320);

    private static final ForgeConfigSpec.IntValue DIABASE_MAX_Y = BUILDER
            .comment("Maximum Y level for diabase generation")
            .defineInRange("worldgen.diabase.maxY", 0, -64, 320);

    // Basalt worldgen config
    private static final ForgeConfigSpec.BooleanValue BASALT_ENABLED = BUILDER
            .comment("Enable basalt ore generation")
            .define("worldgen.basalt.enabled", false);

    private static final ForgeConfigSpec.IntValue BASALT_VEIN_COUNT = BUILDER
            .comment("Number of basalt veins per chunk")
            .defineInRange("worldgen.basalt.veinCount", 1, 0, 64);

    private static final ForgeConfigSpec.IntValue BASALT_VEIN_SIZE = BUILDER
            .comment("Maximum size of basalt veins")
            .defineInRange("worldgen.basalt.veinSize", 48, 1, 128);

    private static final ForgeConfigSpec.IntValue BASALT_MIN_Y = BUILDER
            .comment("Minimum Y level for basalt generation")
            .defineInRange("worldgen.basalt.minY", -64, -64, 320);

    private static final ForgeConfigSpec.IntValue BASALT_MAX_Y = BUILDER
            .comment("Maximum Y level for basalt generation")
            .defineInRange("worldgen.basalt.maxY", 0, -64, 320);

    // FTB Ultimine compatibility
    private static final ForgeConfigSpec.BooleanValue ENABLE_ULTIMINE_COMPAT_CONFIG = BUILDER
            .comment("Enable FTB Ultimine integration (requires FTB Ultimine to be installed)")
            .define("enableUltimineCompat", true);

    private static final ForgeConfigSpec.BooleanValue ULTIMINE_GROUP_VARIANTS = BUILDER
            .comment("When using FTB Ultimine with a chisel, select blocks of the same type together")
            .define("ultimineGroupVariants", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    // Non-worldgen public fields
    public static double concreteVelocityMult;
    public static boolean allowChiselDamage;
    public static int ironChiselMaxDamage;
    public static int diamondChiselMaxDamage;
    public static int hitechChiselMaxDamage;
    public static int carvingCooldownTicks;
    public static boolean ironChiselCanLeftClick;
    public static boolean ironChiselHasModes;
    public static int ironChiselAttackDamage;
    public static int diamondChiselAttackDamage;
    public static int hitechChiselAttackDamage;
    public static boolean autoChiselPowered;
    public static boolean autoChiselNeedsPower;
    public static boolean enableUltimineCompat;
    public static boolean ultimineGroupVariants;

    private static final Map<String, OreGenConfig> oreConfigs = new HashMap<>();

    public record OreGenConfig(boolean enabled, int veinCount, int veinSize, int minY, int maxY) {
    }

    public static OreGenConfig getOreConfig(String oreType) {
        return oreConfigs.get(oreType.toLowerCase());
    }

    public static Map<String, OreGenConfig> getAllOreConfigs() {
        return oreConfigs;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            concreteVelocityMult = CONCRETE_VELOCITY_MULT.get();
            allowChiselDamage = ALLOW_CHISEL_DAMAGE.get();
            ironChiselMaxDamage = IRON_CHISEL_MAX_DAMAGE.get();
            diamondChiselMaxDamage = DIAMOND_CHISEL_MAX_DAMAGE.get();
            hitechChiselMaxDamage = HITECH_CHISEL_MAX_DAMAGE.get();
            carvingCooldownTicks = CARVING_COOLDOWN_TICKS.get();
            ironChiselCanLeftClick = IRON_CHISEL_CAN_LEFT_CLICK.get();
            ironChiselHasModes = IRON_CHISEL_HAS_MODES.get();
            ironChiselAttackDamage = IRON_CHISEL_ATTACK_DAMAGE.get();
            diamondChiselAttackDamage = DIAMOND_CHISEL_ATTACK_DAMAGE.get();
            hitechChiselAttackDamage = HITECH_CHISEL_ATTACK_DAMAGE.get();
            autoChiselPowered = AUTO_CHISEL_POWERED.get();
            autoChiselNeedsPower = AUTO_CHISEL_NEEDS_POWER.get();
            enableUltimineCompat = ENABLE_ULTIMINE_COMPAT_CONFIG.get();
            ultimineGroupVariants = ULTIMINE_GROUP_VARIANTS.get();

            // Load worldgen configs
            oreConfigs.clear();
            oreConfigs.put("marble", new OreGenConfig(
                    MARBLE_ENABLED.get(),
                    MARBLE_VEIN_COUNT.get(),
                    MARBLE_VEIN_SIZE.get(),
                    MARBLE_MIN_Y.get(),
                    MARBLE_MAX_Y.get()
            ));
            oreConfigs.put("limestone", new OreGenConfig(
                    LIMESTONE_ENABLED.get(),
                    LIMESTONE_VEIN_COUNT.get(),
                    LIMESTONE_VEIN_SIZE.get(),
                    LIMESTONE_MIN_Y.get(),
                    LIMESTONE_MAX_Y.get()
            ));
            oreConfigs.put("diabase", new OreGenConfig(
                    DIABASE_ENABLED.get(),
                    DIABASE_VEIN_COUNT.get(),
                    DIABASE_VEIN_SIZE.get(),
                    DIABASE_MIN_Y.get(),
                    DIABASE_MAX_Y.get()
            ));
            oreConfigs.put("basalt", new OreGenConfig(
                    BASALT_ENABLED.get(),
                    BASALT_VEIN_COUNT.get(),
                    BASALT_VEIN_SIZE.get(),
                    BASALT_MIN_Y.get(),
                    BASALT_MAX_Y.get()
            ));
        }
    }
}
