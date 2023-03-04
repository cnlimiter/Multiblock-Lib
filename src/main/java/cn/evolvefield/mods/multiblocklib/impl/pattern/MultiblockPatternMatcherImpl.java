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

package cn.evolvefield.mods.multiblocklib.impl.pattern;

import cn.evolvefield.mods.multiblocklib.api.pattern.MatchResult;
import cn.evolvefield.mods.multiblocklib.api.pattern.MultiblockPattern;
import cn.evolvefield.mods.multiblocklib.api.pattern.MultiblockPatternMatcher;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Jamalam360
 */
@SuppressWarnings("unchecked")
public class MultiblockPatternMatcherImpl implements MultiblockPatternMatcher {
    @Override
    public Optional<MatchResult> tryMatchPattern(BlockPos bottomLeft, Direction direction, World world, MultiblockPattern pattern, Map<Character, Predicate<CachedBlockPosition>> keys) {
        if (direction == Direction.DOWN || direction == Direction.UP) {
            return Optional.empty();
        }

        return tryMatchPattern(bottomLeft, direction, world, pattern, keys, 0);
    }

    private Optional<MatchResult> tryMatchPattern(BlockPos bottomLeft, Direction direction, World world, MultiblockPattern pattern, Map<Character, Predicate<CachedBlockPosition>> keys, int rotateCount) {
        boolean checkedAllLayers = false;
        int layerNumber = 0;
        int loopCount = 0;
        BlockPos finalPos = bottomLeft.mutableCopy().toImmutable();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        while (!checkedAllLayers) {
            if (layerNumber >= pattern.layers().length) {
                checkedAllLayers = true;
                continue;
            }

            MultiblockPattern.Layer layer = pattern.layers()[layerNumber];
            Predicate<CachedBlockPosition>[][] blocks = constructPredicateListFromLayer(layer, keys);

            switch (rotateCount) {
                case 0:
                    break;
                case 1:
                    blocks = rotateClockwise(blocks);
                    break;
                case 2:
                    blocks = rotateClockwise(rotateClockwise(blocks));
                    break;
                case 3:
                    blocks = rotateClockwise(rotateClockwise(rotateClockwise(blocks)));
                    break;
            }

            boolean layerIsRepeatable = layer.min() != 1 && layer.max() != 1;

            mutable.setX(bottomLeft.getX());
            mutable.setY(bottomLeft.getY() + loopCount);
            mutable.setZ(bottomLeft.getZ());

            if (layerIsRepeatable) {
                int matches = matchesRepeatableLayer(blocks, world, mutable, direction);
                if (matches == -1 || matches < layer.min() || matches > layer.max()) {
                    if (rotateCount < 4) {
                        return tryMatchPattern(bottomLeft, direction, world, pattern, keys, rotateCount + 1);
                    }

                    return Optional.empty();
                } else {
                    loopCount += matches;
                    layerNumber++;
                }
            } else {
                if (!matchesLayer(blocks, world, mutable, direction)) {
                    if (rotateCount < 4) {
                        return tryMatchPattern(bottomLeft, direction, world, pattern, keys, rotateCount + 1);
                    }

                    return Optional.empty();
                } else {
                    loopCount++;
                    layerNumber++;
                }
            }

            finalPos = mutable.toImmutable();
        }

        BlockPos realBottomLeftFacingEast = correctToBottomLeft(pattern, bottomLeft, direction);

        return Optional.of(
                new MatchResult(pattern, bottomLeft, realBottomLeftFacingEast, BlockBox.create(realBottomLeftFacingEast, realBottomLeftFacingEast.mutableCopy().move(pattern.width(), loopCount, pattern.depth())), loopCount, pattern.width(), pattern.depth())
        );
    }

    private int matchesRepeatableLayer(Predicate<CachedBlockPosition>[][] blocks, World world, BlockPos.Mutable mutable, Direction direction) {
        int matches = 0;
        BlockPos base = mutable.toImmutable();

        while (true) {
            if (matchesLayer(blocks, world, mutable, direction)) {
                matches++;
            } else {
                if (matches == 0) {
                    return -1;
                } else {
                    return matches;
                }
            }

            mutable.move(Direction.UP);
            mutable.setX(base.getX());
            mutable.setZ(base.getZ());
        }
    }

    private boolean matchesLayer(Predicate<CachedBlockPosition>[][] blocks, World world, BlockPos.Mutable pos, Direction direction) {
        BlockPos bottomLeft = pos.toImmutable();
        for (int rowIndex = 0; rowIndex < blocks.length; rowIndex++) {
            Predicate<CachedBlockPosition>[] row = blocks[rowIndex];

            switch (direction) {
                case NORTH -> pos.setX(bottomLeft.getX() + rowIndex);
                case SOUTH -> pos.setX(bottomLeft.getX() - rowIndex);
                case EAST -> pos.setZ(bottomLeft.getZ() + rowIndex);
                case WEST -> pos.setZ(bottomLeft.getZ() - rowIndex);
            }

            for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
                Predicate<CachedBlockPosition> block = row[columnIndex];

                switch (direction) {
                    case NORTH -> pos.setZ(bottomLeft.getZ() - columnIndex);
                    case SOUTH -> pos.setZ(bottomLeft.getZ() + columnIndex);
                    case EAST -> pos.setX(bottomLeft.getX() + columnIndex);
                    case WEST -> pos.setX(bottomLeft.getX() - columnIndex);
                }

                if (!block.test(new CachedBlockPosition(world, pos, true))) {
                    return false;
                }
            }
        }

        return true;
    }

    private Predicate<CachedBlockPosition>[][] constructPredicateListFromLayer(MultiblockPattern.Layer layer, Map<Character, Predicate<CachedBlockPosition>> key) {
        Predicate<CachedBlockPosition>[][] blocks = (Predicate<CachedBlockPosition>[][]) new Predicate[layer.rows()[0].length()][layer.rows().length];

        for (int i = 0; i < layer.rows().length; i++) {
            for (int j = 0; j < layer.rows()[i].length(); j++) {
                char c = layer.rows()[i].charAt(j);
                if (key.containsKey(c)) {
                    blocks[j][i] = key.get(c);
                } else {
                    throw new IllegalArgumentException("Invalid character: " + c);
                }
            }
        }

        return blocks;
    }

    private BlockPos correctToBottomLeft(MultiblockPattern pattern, BlockPos bottomLeft, Direction direction) {
        BlockPos newPos = null;
        switch (direction) {
            case NORTH -> newPos = bottomLeft.add(0, 0, -(pattern.width() - 1));
            case SOUTH -> newPos = bottomLeft.add(-(pattern.depth() - 1), 0, 0);
            case EAST -> newPos = bottomLeft;
            case WEST -> newPos = bottomLeft.add(-(pattern.depth() - 1), 0, -(pattern.width() - 1));
        }

        return newPos;
    }

    /**
     * From Stack overflow lol
     */
    private static Predicate<CachedBlockPosition>[][] rotateClockwise(Predicate<CachedBlockPosition>[][] matrix) {
        int size = matrix.length;
        Predicate<CachedBlockPosition>[][] ret = new Predicate[size][size];

        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                ret[i][j] = matrix[size - j - 1][i];
            }
        }

        return ret;
    }
}
