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

import com.google.common.collect.Maps;
import net.minecraft.block.pattern.CachedBlockPosition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Wraps around a {@link HashMap} to provide a nice API with `where` rather than `put`
 * @author Jamalam360
 */
public class MultiblockPatternKeyBuilder {
    private final Map<Character, Predicate<CachedBlockPosition>> keys = Maps.newHashMap();

    private MultiblockPatternKeyBuilder(){}

    public static MultiblockPatternKeyBuilder start(){
        return new MultiblockPatternKeyBuilder();
    }

    public MultiblockPatternKeyBuilder where(char key, Predicate<CachedBlockPosition> predicate) {
        keys.put(key, predicate);
        return this;
    }

    public Map<Character, Predicate<CachedBlockPosition>> build(){
        return keys;
    }
}
