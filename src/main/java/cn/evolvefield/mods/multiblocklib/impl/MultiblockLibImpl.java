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

package cn.evolvefield.mods.multiblocklib.impl;

import cn.evolvefield.mods.multiblocklib.api.Multiblock;
import com.google.common.collect.Maps;
import cn.evolvefield.mods.multiblocklib.api.MultiblockLib;
import cn.evolvefield.mods.multiblocklib.api.MultiblockProvider;
import cn.evolvefield.mods.multiblocklib.api.pattern.MatchResult;
import cn.evolvefield.mods.multiblocklib.api.pattern.MultiblockPattern;
import cn.evolvefield.mods.multiblocklib.api.pattern.MultiblockPatternMatcher;
import cn.evolvefield.mods.multiblocklib.api.pattern.MultiblockPatterns;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Implementation of {@link MultiblockLib}. Should not be used directly by library users - access through {@link MultiblockLib#INSTANCE}.
 *
 * @author Jamalam360, cnlimiter

 */
public class MultiblockLibImpl implements MultiblockLib {
    private static final Map<ResourceLocation, Map<Character, Predicate<BlockInWorld>>> MULTIBLOCK_PATTERNS_TO_KEYS = Maps.newHashMap();
    private static final Map<ResourceLocation, MultiblockProvider> MULTIBLOCK_PATTERNS_TO_PROVIDERS = Maps.newHashMap();

    @Override
    public void registerMultiblock(ResourceLocation identifier, MultiblockProvider provider, Map<Character, Predicate<BlockInWorld>> keys) {
        MULTIBLOCK_PATTERNS_TO_PROVIDERS.put(identifier, provider);
        MULTIBLOCK_PATTERNS_TO_KEYS.put(identifier, keys);
    }

    @Override
    public boolean tryAssembleMultiblock(Level world, Direction direction, BlockPos pos) {
        for (MultiblockPattern pattern : MultiblockPatterns.INSTANCE.getPatterns()) {
            if (tryAssembleMultiblock(pattern, world, direction, pos)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean tryAssembleMultiblock(ResourceLocation patternId, Level world, Direction direction, BlockPos pos) {
        Optional<MultiblockPattern> optional = MultiblockPatterns.INSTANCE.getPattern(patternId);
        return optional.filter(multiblockPattern -> tryAssembleMultiblock(multiblockPattern, world, direction, pos)).isPresent();
    }

    @Override
    public boolean tryAssembleMultiblock(MultiblockPattern pattern, Level world, Direction direction, BlockPos pos) {
        if (MultiblockComponentsInit.PROVIDER.get(world).getMultiblock(pos).isPresent()) {
            return true;
        }

        if (!MULTIBLOCK_PATTERNS_TO_KEYS.containsKey(pattern.identifier())) {
            MultiblockLogger.INSTANCE.error("No multiblock keys registered for pattern: " + pattern.identifier());
            throw new IllegalStateException("No multiblock keys registered for pattern: " + pattern.identifier());
        }

        Optional<MatchResult> result = MultiblockPatternMatcher.INSTANCE.tryMatchPattern(pos, direction, world, pattern, MULTIBLOCK_PATTERNS_TO_KEYS.get(pattern.identifier()));
        if (result.isPresent()) {
            if (!MULTIBLOCK_PATTERNS_TO_PROVIDERS.containsKey(pattern.identifier())) {
                MultiblockLogger.INSTANCE.error("No multiblock provider registered for multiblock pattern: " + pattern.identifier());
                throw new IllegalStateException("No multiblock provider registered for multiblock pattern: " + pattern.identifier());
            }

            MultiblockProvider provider = MULTIBLOCK_PATTERNS_TO_PROVIDERS.get(pattern.identifier());
            Multiblock multiblock = provider.getMultiblock(world, result.get());
            MultiblockComponentsInit.PROVIDER.get(world).addMultiblock(multiblock);
            return true;
        }

        return false;
    }

    @Override
    public boolean tryDisassembleMultiblock(Multiblock multiblock, boolean forced) {
        boolean ableToDisassemble = multiblock.onDisassemble(forced);
        if (ableToDisassemble) {
            MultiblockComponentsInit.PROVIDER.get(multiblock.getLevel()).removeMultiblock(multiblock);
            return true;
        }

        return false;
    }

    @Override
    public Optional<Multiblock> getMultiblock(Level world, BlockPos pos) {
        return MultiblockComponentsInit.PROVIDER.get(world).getMultiblock(pos);
    }
}
