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

package cn.evolvefield.mods.multiblocklib.testmod;

import cn.evolvefield.mods.multiblocklib.api.Multiblock;
import cn.evolvefield.mods.multiblocklib.api.pattern.MultiblockPatternKeyBuilder;
import cn.evolvefield.mods.multiblocklib.api.MultiblockLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author Jamalam360
 */
public class TestInit implements ModInitializer {
    private static final Map<Character, Predicate<CachedBlockPosition>> DEFAULT_KEYS = MultiblockPatternKeyBuilder.start()
            .where('G', CachedBlockPosition.matchesBlockState(state -> state.getBlock() == Blocks.GLASS))
            .where('I', CachedBlockPosition.matchesBlockState(state -> state.getBlock() == Blocks.IRON_BLOCK))
            .build();

    @Override
    public void onInitialize() {
        MultiblockLib.INSTANCE.registerMultiblock(new Identifier("multiblocklibtest", "rotatable"), TestMultiblock::new, DEFAULT_KEYS);
        MultiblockLib.INSTANCE.registerMultiblock(new Identifier("multiblocklibtest", "other"), TestMultiblock::new, DEFAULT_KEYS);
        MultiblockLib.INSTANCE.registerMultiblock(new Identifier("multiblocklibtest", "test"), TestMultiblock::new, DEFAULT_KEYS);
        MultiblockLib.INSTANCE.registerMultiblock(new Identifier("multiblocklibtest", "chonk"), TestMultiblock::new, DEFAULT_KEYS);

        MultiblockLib.INSTANCE.registerMultiblock(
                new Identifier("multiblocklibtest", "big_chest"),
                BigChestMultiblock::new,
                MultiblockPatternKeyBuilder.start()
                        .where('L', CachedBlockPosition.matchesBlockState(state -> state.isIn(BlockTags.LOGS)))
                        .where('P', CachedBlockPosition.matchesBlockState(state -> state.isIn(BlockTags.PLANKS)))
                        .where('I', CachedBlockPosition.matchesBlockState(state -> state.isOf(Blocks.IRON_BLOCK)))
                        .build()
        );

        Registry.register(Registry.ITEM, new Identifier("multiblocklibtest", "test_assembler"), new TestAssemblerItem());
    }

    static class TestAssemblerItem extends Item {
        public TestAssemblerItem() {
            super(new FabricItemSettings().group(ItemGroup.TOOLS));
        }

        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            Optional<Multiblock> multiblock = MultiblockLib.INSTANCE.getMultiblock(context.getWorld(), context.getBlockPos());
            if (multiblock.isPresent()) {
                if (MultiblockLib.INSTANCE.tryDisassembleMultiblock(multiblock.get(), false)) {
                    if (context.getWorld().isClient) {
                        context.getPlayer().playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0F, 1.0F);
                    }

                    return ActionResult.SUCCESS;
                }
            } else {
                if (MultiblockLib.INSTANCE.tryAssembleMultiblock(context.getWorld(), context.getPlayerFacing(), context.getBlockPos())) {
                    if (context.getWorld().isClient) {
                        context.getPlayer().playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0F, 1.0F);
                    }

                    return ActionResult.SUCCESS;
                }
            }

            return ActionResult.PASS;
        }
    }
}
