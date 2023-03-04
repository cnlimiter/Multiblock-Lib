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

import cn.evolvefield.mods.multiblocklib.impl.pattern.MultiblockPatternsImpl;
import cn.evolvefield.mods.multiblocklib.api.MultiblockLib;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * Keeps a record of al the {@link MultiblockPattern}s that have been added via JSON.
 *
 * @see MultiblockPatternsImpl
 * @author Jamalam360
 */
public interface MultiblockPatterns {
    MultiblockPatterns INSTANCE = new MultiblockPatternsImpl();
    int LATEST_VERSION = 1;

    /**
     * @return All patterns that have been discovered and registered (all discovered JSON files).
     * @implNote Patterns returned by this method are not guaranteed to have been registered with
     * {@link MultiblockLib#registerMultiblock}
     */
    MultiblockPattern[] getPatterns();


    /**
     * @param namespace The namespace of the mod to get the patterns from.
     * @return All the patterns that have been discovered in the given namespace
     * (usually a mod ID).
     */
    MultiblockPattern[] getPatterns(String namespace);

    Optional<MultiblockPattern> getPattern(Identifier id);

    /**
     * Users of MultiblockLib should not call this method directly - let the resource reload listener
     * handle it for you.
     *
     * @param pattern The pattern to register.
     */
    @ApiStatus.Internal
    void add(MultiblockPattern pattern);

    /**
     * Users of MultiblockLib should not call this method directly - let the resource reload listener
     * handle it for you.
     */
    @ApiStatus.Internal
    void clear();
}
