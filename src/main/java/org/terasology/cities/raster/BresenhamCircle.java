/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.raster;

import java.awt.Color;

/**
 * Draws and fills circles
 * @author Martin Steiger
 */
public class BresenhamCircle {
    private final PixelDrawer pixelDrawer;

    /**
     * @param pixelDrawer the instance that does the actual drawing
     */
    public BresenhamCircle(PixelDrawer pixelDrawer) {
        this.pixelDrawer = pixelDrawer;
    }

    /**
     * @param cx the center x
     * @param cy the center y
     * @param rad the radius
     * @param color the color
     */
    public void fillCircle(int cx, int cy, int rad, Color color) {
        for (int y = 0; y <= rad; y++) {
            for (int x = 0; x * x + y * y <= (rad + 0.5) * (rad + 0.5); x++) {
                drawPixel(cx + x, cy + y, color);
                drawPixel(cx - x, cy + y, color);
                drawPixel(cx - x, cy - y, color);
                drawPixel(cx + x, cy - y, color);
            }
        }
    }

    private void drawPixel(int x, int y, Color color) {
        pixelDrawer.drawPixel(x, y, color);
    }

    /**
     * Horn's algorithm B. K. P. Horn: Circle Generators for Display Devices.
     * Computer Graphics and Image Processing 5, 2 (June 1976)
     * @param cx the center x
     * @param cy the center y
     * @param rad the radius
     * @param color the color
     */
    public void drawCircle(int cx, int cy, int rad, Color color) {
        int d = -rad;
        int x = rad;
        int y = 0;
        while (y <= x) {
            drawPixel(cx + x, cy + y, color);
            drawPixel(cx - x, cy + y, color);
            drawPixel(cx - x, cy - y, color);
            drawPixel(cx + x, cy - y, color);

            drawPixel(cx + y, cy + x, color);
            drawPixel(cx - y, cy + x, color);
            drawPixel(cx - y, cy - x, color);
            drawPixel(cx + y, cy - x, color);

            d = d + 2 * y + 1;
            y = y + 1;
            if (d > 0) {
                d = d - 2 * x + 2;
                x = x - 1;
            }
        }
    }
}
