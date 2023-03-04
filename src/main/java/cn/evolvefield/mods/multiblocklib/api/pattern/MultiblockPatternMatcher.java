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

package cn.evolvefield.mods.multiblocklib.api.pattern;

import cn.evolvefield.mods.multiblocklib.impl.pattern.MultiblockPatternMatcherImpl;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Tests a given {@link BlockPos} in a {@link World} to see if it matches a {@link MultiblockPattern}.
 *
 * @author Jamalam360
 * @see MultiblockPatternMatcherImpl
 */
public interface MultiblockPatternMatcher {
    MultiblockPatternMatcher INSTANCE = new MultiblockPatternMatcherImpl();

    /**
     * @param bottomLeft The bottom left corner of the pattern, when facing in the positive X direction.
     * @param direction  The {@link Direction} the user is facing.
     * @param world      The world to test the pattern in.
     * @param pattern    The {@link MultiblockPattern} to test against.
     * @param keys       A map of character keys to block predicates to use to match {@link MultiblockPattern} rows to.
     * @return An {@link Optional} containing the {@link MatchResult} if the pattern matches,
     * or an empty {@link Optional} if it does not.
     */
    Optional<MatchResult> tryMatchPattern(BlockPos bottomLeft, Direction direction, World world, MultiblockPattern pattern, Map<Character, Predicate<CachedBlockPosition>> keys);
}
