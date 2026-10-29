package com.heibai.furnace.mixin;

import com.heibai.furnace.util.FurnaceHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin 注入 AbstractFurnaceBlockEntity.serverTick
 * 当原版熔炉完成一次烧炼周期（原版刚成功烧完第1个物品，即将调用 setRecipeUsed）时触发，
 * 一次性烧完当前输入槽中的所有剩余物品。
 * 熔炉完全以原版时间正常运行，不修改任何烧炼时间。
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class AbstractFurnaceBlockEntityMixin {

    @Inject(
        method = "serverTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;setRecipeUsed(Lnet/minecraft/world/item/crafting/Recipe;)V"
        )
    )
    private static void onBurnSuccess(Level level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci) {
        FurnaceHelper.processRemainingBatch(level, blockEntity);
    }
}
