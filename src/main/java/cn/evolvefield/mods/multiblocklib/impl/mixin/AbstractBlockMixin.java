/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Jamalam360, cnlimiter
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
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * @author Jamalam360
 * @devoloper cnlimiter
 */

@Mixin(BlockBehaviour.class)
public abstract class AbstractBlockMixin {
    /**
     * Checks if the block is a part of a multiblock, and if it is, forwards the {@code onUse} call to the multiblock.
     */
    @Inject(
            method = "use",
            at = @At("HEAD"),
            cancellable = true
    )
    public void multiblocklib$checkForMultiblockOnUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        Optional<Multiblock> multiblock = MultiblockLib.INSTANCE.getMultiblock(world, pos);
        multiblock.ifPresent(value -> cir.setReturnValue(value.onUse(world, pos, player, hand, hit)));
    }

    /**
     * Checks if the block is a part of a multiblock, and if it is, forwards the {@code neighborUpdate} event to the multiblock.
     */
    @Inject(
            method = "neighborChanged",
            at = @At("HEAD")
    )
    public void multiblocklib$checkForMultiblockNeighborUpdate(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean notify, CallbackInfo ci){
        Optional<Multiblock> multiblock = MultiblockLib.INSTANCE.getMultiblock(world, pos);
        multiblock.ifPresent(value -> value.onNeighborUpdate(pos, fromPos));
    }
}
