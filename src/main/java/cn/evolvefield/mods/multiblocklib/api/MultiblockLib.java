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

import cn.evolvefield.mods.multiblocklib.api.pattern.MultiblockPattern;
import cn.evolvefield.mods.multiblocklib.impl.MultiblockLibImpl;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * The main MultiblockLib API. Responsible for registering {@link Multiblock}s
 * and assembling/disassembling them.
 *
 * @author Jamalam360
 * @see MultiblockLibImpl
 */
public interface MultiblockLib {
    MultiblockLib INSTANCE = new MultiblockLibImpl();

    /**
     * @param identifier The {@link Identifier} of the {@link MultiblockPattern} to register.
     * @param provider   The {@link MultiblockProvider} to register.
     * @param keys       The {@link Map} of keys to use for the {@link MultiblockPattern}.
     */
    void registerMultiblock(Identifier identifier, MultiblockProvider provider, Map<Character, Predicate<CachedBlockPosition>> keys);

    /**
     * Checks all registered multiblock patterns for a match. It is more efficient to use one of
     * the other methods below if you have a pattern ID.
     *
     * @param world     The {@link World} to use.
     * @param direction The {@link Direction} to check.
     * @param pos       The {@link BlockPos} of the bottom left corner of the multiblock.
     * @return {@code true} if the multiblock was successfully assembled, {@code false} otherwise.
     */
    boolean tryAssembleMultiblock(World world, Direction direction, BlockPos pos);


    /**
     * @param patternId The {@link Identifier} of the {@link MultiblockPattern} to test for.
     * @param world     The {@link World} to use.
     * @param direction The {@link Direction} to check.
     * @param pos       The {@link BlockPos} of the bottom left corner of the multiblock.
     * @return {@code true} if the multiblock was successfully assembled, {@code false} otherwise.
     */
    boolean tryAssembleMultiblock(Identifier patternId, World world, Direction direction, BlockPos pos);


    /**
     * @param pattern The {@link MultiblockPattern} to test for.
     * @param world   The {@link World} to use.
     * @param direction The {@link Direction} to check.
     * @param pos     The {@link BlockPos} of the bottom left corner of the multiblock.
     * @return {@code true} if the multiblock was successfully assembled, {@code false} otherwise.
     */
    boolean tryAssembleMultiblock(MultiblockPattern pattern, World world, Direction direction, BlockPos pos);

    /**
     * @param multiblock The {@link Multiblock} to try to disassemble.
     * @param forced     {@code true} if the multiblock should be disassembled forcefully. If a
     *                   {@link Multiblock} is not disassembled forcefully, it will only
     *                   be disassembled if the return value of {@link Multiblock#onDisassemble}
     *                   is {@code true}.
     * @return {@code true} if the multiblock was successfully disassembled, {@code false} otherwise.
     */
    boolean tryDisassembleMultiblock(Multiblock multiblock, boolean forced);


    /**
     * Checks whether the given {@link BlockPos} is within the bounds of a multiblock.
     *
     * @param world The {@link World} to use.
     * @param pos   The {@link BlockPos} of the block to check.
     * @return {@code true} if the block is within the bounds of a multiblock, {@code false} otherwise.
     */
    Optional<Multiblock> getMultiblock(World world, BlockPos pos);
}
