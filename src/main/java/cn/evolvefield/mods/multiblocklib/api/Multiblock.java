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

package cn.evolvefield.mods.multiblocklib.api;

import cn.evolvefield.mods.multiblocklib.api.pattern.MatchResult;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

/**
 * @author Jamalam360
 */
@SuppressWarnings("unused")
public abstract class Multiblock {
    private final World world;
    private final MatchResult matchResult;
    private final VoxelShape shape;

    public Multiblock(World world, MatchResult match) {
        this.world = world;
        this.matchResult = match;
        this.shape = Block.createCuboidShape(0, 0, 0, match.width() * 16, match.height() * 16, match.depth() * 16);
    }

    public void tick() {
    }

    public ActionResult onUse(World world, BlockPos clickPos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        return ActionResult.PASS;
    }

    public void onNeighborUpdate(BlockPos pos, BlockPos neighborPos) {
    }

    /**
     * @param forced Whether this multiblock is being disassembled forcefully (i.e. one of its blocks was broken)
     * @return Whether the multiblock can be disassembled. It is recommended to return true if forced is true.
     */
    public boolean onDisassemble(boolean forced) {
        return true;
    }

    public VoxelShape getOutlineShape() {
        return shape;
    }

    public NbtCompound writeTag() {
        return new NbtCompound();
    }

    public void readTag(NbtCompound tag) {
    }

    public List<BlockState> getBlocks(Block block) {
        return getBlocks((cachedBlockPosition -> cachedBlockPosition.getBlockState().isOf(block)));
    }

    public List<BlockState> getBlocks(Predicate<CachedBlockPosition> predicate) {
        return BlockPos.stream(matchResult.box())
                .filter((blockPos) -> predicate.test(new CachedBlockPosition(world, blockPos, true)))
                .map(world::getBlockState)
                .toList();
    }

    public World getWorld() {
        return world;
    }

    public MatchResult getMatchResult() {
        return matchResult;
    }
}
