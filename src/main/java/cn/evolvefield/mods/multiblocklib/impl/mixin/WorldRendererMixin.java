/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Jamalam360
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package cn.evolvefield.mods.multiblocklib.impl.mixin;

import cn.evolvefield.mods.multiblocklib.api.Multiblock;
import cn.evolvefield.mods.multiblocklib.api.MultiblockLib;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * @author Jamalam360
 */

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow
    private @Nullable ClientWorld world;

    @Shadow
    private static void drawShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
    }

    /**
     * Checks if the block is a multiblock and if so, renders the multiblocks outline (from the bottom left of
     * the multiblock), rather than rendering the blocks outline.
     */
    @Inject(
            method = "drawBlockOutline",
            at = @At("HEAD"),
            cancellable = true
    )
    public void multiblocklib$modifyOutlineForMultiblocks(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        Optional<Multiblock> multiblock = MultiblockLib.INSTANCE.getMultiblock(world, blockPos);
        if (multiblock.isPresent()) {
            drawShapeOutline(
                    matrices,
                    vertexConsumer,
                    multiblock.get().getOutlineShape(),
                    (double) multiblock.get().getMatchResult().bottomLeftPosCorrected().getX() - d,
                    (double) multiblock.get().getMatchResult().bottomLeftPosCorrected().getY() - e,
                    (double) multiblock.get().getMatchResult().bottomLeftPosCorrected().getZ() - f,
                    0.0F,
                    0.0F,
                    0.0F,
                    0.4F
            );
            ci.cancel();
        }
    }
}
