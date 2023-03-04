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

package cn.evolvefield.mods.multiblocklib.impl.components;

import cn.evolvefield.mods.multiblocklib.api.Multiblock;
import cn.evolvefield.mods.multiblocklib.api.MultiblockLib;
import cn.evolvefield.mods.multiblocklib.api.components.MultiblockProvider;
import com.google.common.collect.Maps;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A cardinal component that stores all created {@link Multiblock}s in the {@link Level}.
 * It is also responsible for ticking {@link Multiblock}s.
 *
 * @author Jamalam360
 * @devoloper cnlimiter
 */
public class MultiblockProviderImpl implements MultiblockProvider, ServerTickingComponent, AutoSyncedComponent {
    private final Level provider;
    private final Map<BlockPos[], Multiblock> MULTIBLOCKS = Maps.newHashMap();

    public MultiblockProviderImpl(Level provider) {
        this.provider = provider;
    }

    @Override
    public void serverTick() {
        for (Multiblock multiblock : getAllMultiblocks()) {
            multiblock.tick();
        }
    }

    @ApiStatus.Internal
    @Override
    public void addMultiblock(Multiblock multiblock) {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos.betweenClosedStream(multiblock.getMatchResult().box()).forEach(pos -> positions.add(pos.immutable()));
        System.out.println(positions.size());
        System.out.println(positions);

        MULTIBLOCKS.put(positions.toArray(new BlockPos[0]), multiblock);
    }

    @ApiStatus.Internal
    @Override
    public void removeMultiblock(Multiblock multiblock) {
        final List<BlockPos[]> keys = new ArrayList<>();
        MULTIBLOCKS.forEach((key, value) -> {
            if (value == multiblock) {
                keys.add(key);
            }
        });

        for (BlockPos[] key : keys) {
            MULTIBLOCKS.remove(key);
        }
    }

    @Override
    public Optional<Multiblock> getMultiblock(BlockPos pos) {
        List<Map.Entry<BlockPos[], Multiblock>> filtered = MULTIBLOCKS.entrySet().stream()
                .filter(entry -> Arrays.asList(entry.getKey()).contains(pos))
                .toList();

        if (filtered.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(filtered.get(0).getValue());
        }
    }

    @Override
    public Multiblock[] getAllMultiblocks() {
        return MULTIBLOCKS.values().toArray(new Multiblock[0]);
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag) {
        CompoundTag compound = new CompoundTag();

        int multiblockNumber = 0;
        for (Map.Entry<BlockPos[], Multiblock> entry : MULTIBLOCKS.entrySet()) {
            multiblockNumber++;
            CompoundTag multiblockTag = new CompoundTag();
            multiblockTag.putIntArray("BottomLeft", new int[]{entry.getValue().getMatchResult().bottomLeftPos().getX(), entry.getValue().getMatchResult().bottomLeftPos().getY(), entry.getValue().getMatchResult().bottomLeftPos().getZ()});
            multiblockTag.putString("PatternIdentifier", entry.getValue().getMatchResult().pattern().identifier().toString());
            multiblockTag.put("MultiblockTag", entry.getValue().writeTag());
            compound.put("Multiblock" + multiblockNumber, multiblockTag);
        }
        compound.putInt("MultiblockLength", multiblockNumber);

        tag.put("Multiblocks", compound);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        CompoundTag compound = tag.getCompound("Multiblocks");
        int multiblockLength = compound.getInt("MultiblockLength");

        for (int i = 1; i <= multiblockLength; i++) {
            CompoundTag multiblockTag = compound.getCompound("Multiblock" + i);
            int[] bottomLeftArr = multiblockTag.getIntArray("BottomLeft");
            BlockPos bottomLeft = new BlockPos(bottomLeftArr[0], bottomLeftArr[1], bottomLeftArr[2]);
            ResourceLocation identifier = new ResourceLocation(multiblockTag.getString("PatternIdentifier"));

            if (MultiblockLib.INSTANCE.tryAssembleMultiblock(identifier, provider, Direction.EAST, bottomLeft)) {
                this.getMultiblock(bottomLeft).ifPresent(multiblock -> multiblock.readTag(multiblockTag.getCompound("MultiblockTag")));
            }
        }
    }
}
