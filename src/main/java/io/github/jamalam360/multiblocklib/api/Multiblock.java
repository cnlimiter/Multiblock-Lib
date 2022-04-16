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

package io.github.jamalam360.multiblocklib.api;

import io.github.jamalam360.multiblocklib.api.pattern.MatchResult;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

/**
 * @author Jamalam360
 */
public abstract class Multiblock {
    private final BlockPos bottomLeftPos;
    private final World world;
    private final MatchResult matchResult;
    private final BlockBox box;
    private final VoxelShape shape;

    public Multiblock(BlockPos pos, World world, MatchResult match) {
        this.bottomLeftPos = pos;
        this.world = world;
        this.matchResult = match;
        this.box = match.box();
        this.shape = Block.createCuboidShape(0, 0, 0, match.width() * 16, match.height() * 16, match.depth() * 16);
    }

    public BlockPos getBottomLeftPos() {
        return bottomLeftPos;
    }

    public World getWorld() {
        return world;
    }

    public MatchResult getMatchResult() {
        return matchResult;
    }

    public BlockBox getBox() {
        return box;
    }

    public void tick(MultiblockContext context) {
    }

    public ActionResult onUse(PlayerEntity user, BlockPos clickPos, MultiblockContext context) {
        return ActionResult.PASS;
    }

    public VoxelShape getOutlineShape(MultiblockContext context) {
        return shape;
    }

    public NbtCompound writeTag() {
        return new NbtCompound();
    }

    public void readTag(NbtCompound tag) {
    }

    /**
     * @param context The context.
     * @param forced  Whether this multiblock is being disassembled forcefully (i.e. one of its blocks was broken)
     * @return Whether the multiblock can be disassembled. It is recommended to return true if forced is true.
     */
    public boolean onDisassemble(MultiblockContext context, boolean forced) {
        return true;
    }
}