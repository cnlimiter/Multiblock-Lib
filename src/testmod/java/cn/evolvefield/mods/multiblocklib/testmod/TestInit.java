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

package cn.evolvefield.mods.multiblocklib.testmod;

import cn.evolvefield.mods.multiblocklib.api.Multiblock;
import cn.evolvefield.mods.multiblocklib.api.MultiblockLib;
import cn.evolvefield.mods.multiblocklib.api.pattern.MultiblockPatternKeyBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Jamalam360, cnlimiter

 */
public class TestInit implements ModInitializer {
    private static final Map<Character, Predicate<BlockInWorld>> DEFAULT_KEYS = MultiblockPatternKeyBuilder.start()
            .where('G', BlockInWorld.hasState(state -> state.getBlock() == Blocks.GLASS))
            .where('I', BlockInWorld.hasState(state -> state.getBlock() == Blocks.IRON_BLOCK))
            .build();

    @Override
    public void onInitialize() {
        MultiblockLib.INSTANCE.registerMultiblock(new ResourceLocation("multiblocklibtest", "rotatable"), TestMultiblock::new, DEFAULT_KEYS);
        MultiblockLib.INSTANCE.registerMultiblock(new ResourceLocation("multiblocklibtest", "other"), TestMultiblock::new, DEFAULT_KEYS);
        MultiblockLib.INSTANCE.registerMultiblock(new ResourceLocation("multiblocklibtest", "test"), TestMultiblock::new, DEFAULT_KEYS);
        MultiblockLib.INSTANCE.registerMultiblock(new ResourceLocation("multiblocklibtest", "chonk"), TestMultiblock::new, DEFAULT_KEYS);

        MultiblockLib.INSTANCE.registerMultiblock(
                new ResourceLocation("multiblocklibtest", "big_chest"),
                BigChestMultiblock::new,
                MultiblockPatternKeyBuilder.start()
                        .where('L', BlockInWorld.hasState(state -> state.is(BlockTags.LOGS)))
                        .where('P', BlockInWorld.hasState(state -> state.is(BlockTags.PLANKS)))
                        .where('I', BlockInWorld.hasState(state -> state.is(Blocks.IRON_BLOCK)))
                        .build()
        );

        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("multiblocklibtest", "test_assembler"), new TestAssemblerItem());
    }

    static class TestAssemblerItem extends Item {
        public TestAssemblerItem() {
            super(new FabricItemSettings());
        }

        @Override
        public InteractionResult useOn(UseOnContext context) {
            Optional<Multiblock> multiblock = MultiblockLib.INSTANCE.getMultiblock(context.getLevel(), context.getClickedPos());
            if (multiblock.isPresent()) {
                if (MultiblockLib.INSTANCE.tryDisassembleMultiblock(multiblock.get(), false)) {
                    if (context.getLevel().isClientSide) {
                        context.getPlayer().playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 2.0F, 1.0F);
                    }

                    return InteractionResult.SUCCESS;
                }
            } else {
                if (MultiblockLib.INSTANCE.tryAssembleMultiblock(context.getLevel(), context.getHorizontalDirection(), context.getClickedPos())) {
                    if (context.getLevel().isClientSide) {
                        context.getPlayer().playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 2.0F, 1.0F);
                    }

                    return InteractionResult.SUCCESS;
                }
            }

            return InteractionResult.PASS;
        }

    }
}
