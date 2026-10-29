package com.heibai.furnace.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 模组配置类 / Mod Configuration
 */
public class FurnaceConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue AFFECT_VANILLA_FURNACE;
    public static final ForgeConfigSpec.BooleanValue AFFECT_BLAST_FURNACE;
    public static final ForgeConfigSpec.BooleanValue AFFECT_SMOKER;
    public static final ForgeConfigSpec.BooleanValue GRANT_EXPERIENCE;

    static {
        BUILDER.push("General Settings / 常规设置");

        GRANT_EXPERIENCE = BUILDER
                .comment("Whether to accumulate full experience for every item smelted in the batch when taken from output slot.",
                         "是否为批量烧炼的每个物品都计算并给予完整的经验值。")
                .define("grantExperience", true);

        BUILDER.pop();

        BUILDER.push("Furnace Types / 熔炉类型生效设置");

        AFFECT_VANILLA_FURNACE = BUILDER
                .comment("Whether regular furnaces (熔炉) are affected.",
                         "是否对普通原版熔炉生效。")
                .define("affectVanillaFurnace", true);

        AFFECT_BLAST_FURNACE = BUILDER
                .comment("Whether blast furnaces (高炉) are affected.",
                         "是否对原版高炉生效。")
                .define("affectBlastFurnace", true);

        AFFECT_SMOKER = BUILDER
                .comment("Whether smokers (烟熏炉) are affected.",
                         "是否对原版烟熏炉生效。")
                .define("affectSmoker", true);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
