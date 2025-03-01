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

package cn.evolvefield.mods.multiblocklib.api.components;

import cn.evolvefield.mods.multiblocklib.api.Multiblock;
import cn.evolvefield.mods.multiblocklib.impl.components.MultiblockProviderImpl;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * A cardinal component that stores all created {@link Multiblock}s in the {@link net.minecraft.world.level.Level}.
 *
 * @author Jamalam360, cnlimiter

 * @implSpec The implementation should also tick the {@link Multiblock}s using
 * {@link Multiblock#tick}, and save them to NBT.
 * @see MultiblockProviderImpl
 */
public interface MultiblockProvider extends ComponentV3 {
    Optional<Multiblock> getMultiblock(BlockPos pos);

    Multiblock[] getAllMultiblocks();

    @ApiStatus.Internal
    void addMultiblock(Multiblock multiblock);

    @ApiStatus.Internal
    void removeMultiblock(Multiblock multiblock);
}
