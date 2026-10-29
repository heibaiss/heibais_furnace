package com.heibai.furnace.util;

import com.heibai.furnace.config.FurnaceConfig;
import com.heibai.furnace.mixin.AbstractFurnaceBlockEntityAccessor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;

/**
 * 熔炉核心辅助处理类
 * 熔炉完全以原版时间正常运行，燃料消耗完全跟随原版。
 * 当完成一次烧炼周期（原版刚烧完第1个物品）时，一次性烧完当前输入槽中的所有剩余物品并结算输出。
 */
public class FurnaceHelper {

    /**
     * 判断当前熔炉是否在配置中启用了批量烧炼
     */
    public static boolean shouldHandle(AbstractFurnaceBlockEntity furnace) {
        if (furnace instanceof BlastFurnaceBlockEntity) {
            return FurnaceConfig.AFFECT_BLAST_FURNACE.get();
        } else if (furnace instanceof SmokerBlockEntity) {
            return FurnaceConfig.AFFECT_SMOKER.get();
        } else if (furnace instanceof FurnaceBlockEntity) {
            return FurnaceConfig.AFFECT_VANILLA_FURNACE.get();
        }
        return FurnaceConfig.AFFECT_VANILLA_FURNACE.get();
    }

    /**
     * 当原版熔炉完成一次烧炼周期（原版刚成功烧完第1个物品）时触发。
     * 将输入槽中剩余的所有物品一次性全部烧完并输出。
     * 燃料消耗完全遵循原版，不进行任何额外的燃料扣除。
     */
    public static void processRemainingBatch(Level level, AbstractFurnaceBlockEntity furnace) {
        if (level == null || level.isClientSide()) {
            return;
        }

        if (!shouldHandle(furnace)) {
            return;
        }

        ItemStack input = furnace.getItem(0);
        if (input.isEmpty()) {
            return;
        }

        AbstractFurnaceBlockEntityAccessor accessor = (AbstractFurnaceBlockEntityAccessor) furnace;
        AbstractCookingRecipe recipe = accessor.getQuickCheck().getRecipeFor(furnace, level).orElse(null);
        if (recipe == null) {
            return;
        }

        RegistryAccess registryAccess = level.registryAccess();
        ItemStack recipeResult = recipe.assemble(furnace, registryAccess);
        if (recipeResult.isEmpty()) {
            recipeResult = recipe.getResultItem(registryAccess);
        }
        if (recipeResult.isEmpty()) {
            return;
        }

        ItemStack currentOutput = furnace.getItem(2);
        if (currentOutput.isEmpty()) {
            return;
        }

        // 必须与现有产物槽物品相同
        if (!ItemStack.isSameItemSameTags(currentOutput, recipeResult)) {
            return;
        }

        int maxOutputStack = Math.min(furnace.getMaxStackSize(), recipeResult.getMaxStackSize());
        int currentOutputCount = currentOutput.getCount();
        int spaceRemaining = maxOutputStack - currentOutputCount;
        if (spaceRemaining <= 0) {
            return;
        }

        int resultPerCraft = Math.max(1, recipeResult.getCount());
        int maxCraftsByOutput = spaceRemaining / resultPerCraft;
        if (maxCraftsByOutput <= 0) {
            return;
        }

        int availableInput = input.getCount();
        int itemsToSmelt = Math.min(availableInput, maxCraftsByOutput);
        if (itemsToSmelt <= 0) {
            return;
        }

        boolean isWetSponge = input.is(Blocks.WET_SPONGE.asItem());

        // 扣除输入槽物品
        input.shrink(itemsToSmelt);

        // 产物槽增加全部产出
        int totalOutputGrown = itemsToSmelt * resultPerCraft;
        currentOutput.grow(totalOutputGrown);

        // 原版湿海绵退水桶逻辑
        if (isWetSponge) {
            ItemStack fuelSlot = furnace.getItem(1);
            if (!fuelSlot.isEmpty() && fuelSlot.is(Items.BUCKET)) {
                furnace.setItem(1, new ItemStack(Items.WATER_BUCKET));
            }
        }

        // 为这批批量烧出的所有物品累积全额经验值
        if (FurnaceConfig.GRANT_EXPERIENCE.get()) {
            for (int i = 0; i < itemsToSmelt; i++) {
                furnace.setRecipeUsed(recipe);
            }
        }

        furnace.setChanged();
    }
}
