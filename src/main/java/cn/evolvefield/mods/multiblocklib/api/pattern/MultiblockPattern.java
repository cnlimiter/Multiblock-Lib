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

package cn.evolvefield.mods.multiblocklib.api.pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

/**
 * Represents a multiblock pattern as defined in JSON files in the
 * {@code /data/[namespace]/multiblock_patterns} directory.
 *
 * @author Jamalam360
 * @devoloper cnlimiter
 */
public record MultiblockPattern(ResourceLocation identifier, Layer[] layers) {
    /**
     * @return The length of this pattern in the y-axis
     * @implNote This is the number of layers in this pattern. The return
     * value does not take into account repeatable layers.
     */
    public int height() {
        return layers.length;
    }

    /**
     * @return The length of this pattern in the x-axis
     */
    public int width() {
        return layers[0].rows[0].length();
    }

    /**
     * @return The length of this pattern in the z-axis
     */
    public int depth() {
        return layers[0].rows.length;
    }

    public static MultiblockPattern deserialize(ResourceLocation id, JsonElement json) {
        JsonObject obj = json.getAsJsonObject();
        int version = obj.has("version") ? obj.get("version").getAsInt() : MultiblockPatterns.LATEST_VERSION;

        switch (version) {
            case 1:
                JsonArray layers = obj.get("layers").getAsJsonArray();
                Layer[] layerArray = new Layer[layers.size()];
                for (int i = 0; i < layers.size(); i++) {
                    layerArray[i] = Layer.deserialize(layers.get(i));
                }

                return new MultiblockPattern(id, layerArray);
            default:
                throw new IllegalArgumentException("Unknown version " + version + " for pattern " + id);
        }

    }

    public record Layer(int min, int max, String[] rows) {
        public static MultiblockPattern.Layer deserialize(JsonElement json) {
            JsonObject obj = json.getAsJsonObject();
            JsonElement minObj = obj.get("min");
            JsonElement maxObj = obj.get("max");
            int min = 1;
            int max = 1;

            if (minObj != null) {
                min = minObj.getAsInt();
            }

            if (maxObj != null) {
                max = maxObj.getAsInt();
            }

            if (min > max) {
                throw new IllegalArgumentException("min > max");
            } else if (min < 1) {
                throw new IllegalArgumentException("min < 1");
            }

            JsonArray rowsObj = obj.get("rows").getAsJsonArray();
            String[] rows = new String[rowsObj.size()];

            for (int i = 0; i < rowsObj.size(); i++) {
                rows[i] = rowsObj.get(i).getAsString();
            }

            return new MultiblockPattern.Layer(min, max, rows);
        }
    }
}
