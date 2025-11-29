package com.leclowndu93150.chisel;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

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

    private static final ForgeConfigSpec.IntValue MARBLE_AMOUNT = BUILDER
            .comment("Amount of marble veins per chunk (0 to disable)")
            .defineInRange("marbleAmount", 20, 0, 100);

    private static final ForgeConfigSpec.IntValue LIMESTONE_AMOUNT = BUILDER
            .comment("Amount of limestone veins per chunk (0 to disable)")
            .defineInRange("limestoneAmount", 18, 0, 100);

    private static final ForgeConfigSpec.IntValue BASALT_VEIN_AMOUNT = BUILDER
            .comment("Amount of basalt veins per chunk (0 to disable)")
            .defineInRange("basaltVeinAmount", 0, 0, 100);

    private static final ForgeConfigSpec.IntValue DIABASE_AMOUNT = BUILDER
            .comment("Amount of diabase veins per chunk near lava level (0 to disable)")
            .defineInRange("diabaseAmount", 8, 0, 100);

    // FTB Ultimine compatibility
    private static final ForgeConfigSpec.BooleanValue ENABLE_ULTIMINE_COMPAT_CONFIG = BUILDER
            .comment("Enable FTB Ultimine integration (requires FTB Ultimine to be installed)")
            .define("enableUltimineCompat", true);

    private static final ForgeConfigSpec.BooleanValue ULTIMINE_GROUP_VARIANTS = BUILDER
            .comment("When using FTB Ultimine with a chisel, select blocks of the same type together")
            .define("ultimineGroupVariants", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

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
    public static int marbleAmount;
    public static int limestoneAmount;
    public static int basaltVeinAmount;
    public static int diabaseAmount;
    public static boolean enableUltimineCompat;
    public static boolean ultimineGroupVariants;

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
            marbleAmount = MARBLE_AMOUNT.get();
            limestoneAmount = LIMESTONE_AMOUNT.get();
            basaltVeinAmount = BASALT_VEIN_AMOUNT.get();
            diabaseAmount = DIABASE_AMOUNT.get();
            enableUltimineCompat = ENABLE_ULTIMINE_COMPAT_CONFIG.get();
            ultimineGroupVariants = ULTIMINE_GROUP_VARIANTS.get();
        }
    }
}
