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

package cn.evolvefield.mods.multiblocklib.impl.pattern;

import cn.evolvefield.mods.multiblocklib.api.pattern.MultiblockPatterns;
import cn.evolvefield.mods.multiblocklib.api.pattern.MultiblockPattern;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Jamalam360, cnlimiter

 */
public class MultiblockPatternsImpl implements MultiblockPatterns {
    protected static final List<MultiblockPattern> PATTERNS = new ArrayList<>();

    @Override
    public MultiblockPattern[] getPatterns() {
        return PATTERNS.toArray(new MultiblockPattern[0]);
    }

    @Override
    public MultiblockPattern[] getPatterns(String namespace) {
        return PATTERNS.stream().filter(pattern -> pattern.identifier().getNamespace().equals(namespace)).toArray(MultiblockPattern[]::new);
    }

    @Override
    public Optional<MultiblockPattern> getPattern(ResourceLocation id) {
        for (MultiblockPattern pattern : PATTERNS) {
            if (pattern.identifier().equals(id)) {
                return Optional.of(pattern);
            }
        }

        return Optional.empty();
    }

    @ApiStatus.Internal
    @Override
    public void add(MultiblockPattern pattern) {
        PATTERNS.add(pattern);
    }

    @ApiStatus.Internal
    @Override
    public void clear() {
        PATTERNS.clear();
    }
}
