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

package org.terasology.cities.swing;

import java.awt.Color;
import java.util.EnumSet;
import java.util.Set;

/**
 * Based on code from the touchscreen-apps projects by Armin Joachimsmeyer
 * https://code.google.com/p/touchscreen-apps
 * <p>
 * More specifically, the class is a port of this c file:
 * </p>
 * <p> 
 * https://code.google.com/p/touchscreen-apps/source/browse/src/lib/thickLine.c
 * </p>
 */
public class BresenhamLine {
    private static enum Overlap {
        MAJOR, // Overlap - first go major then minor direction
        MINOR; // Overlap - first go minor then major direction
    }

    /**
     * Thickness mode
     */
    public static enum ThicknessMode {
        /**
         * Line goes through the center 
         */
        MIDDLE, 
        
        /**
         * Line goes along the border (clockwise) 
         */
        CLOCKWISE, 

        /**
         * Line goes along the border (counter-clockwise) 
         */
        COUNTERCLOCKWISE
    }

    private final PixelDrawer pixelDrawer;
    
    /**
     * @param pixelDrawer the instance that does the actual drawing
     */
    public BresenhamLine(PixelDrawer pixelDrawer) {
        this.pixelDrawer = pixelDrawer; 
    }

    /**
     * Bresenham line with a thickness of 1
     * @param xStart x0
     * @param yStart y0
     * @param xEnd x1
     * @param yEnd y1
     * @param color the color of the line
     */
    public void drawLine(int xStart, int yStart, int xEnd, int yEnd, Color color) {
        drawLineOverlap(xStart, yStart, xEnd, yEnd, EnumSet.noneOf(Overlap.class), color);
    }

    /*
     * modified Bresenham with optional overlap (esp. for drawThickLine())
     * Overlap draws additional pixel when changing minor direction - for standard bresenham overlap = LINE_OVERLAP_NONE (0)
     * <pre>
     *  Sample line:
     *    00+
     *     -0000+
     *         -0000+
     *             -00
     *  0 pixels are drawn for normal line without any overlap
     *  + pixels are drawn if LINE_OVERLAP_MAJOR
     *  - pixels are drawn if LINE_OVERLAP_MINOR
     * </pre>
     */
    private void drawLineOverlap(int xStart, int yStart, int xEnd, int yEnd, Set<Overlap> aOverlap, Color aColor) {
        int tDeltaX;
        int tDeltaY;
        int tDeltaXTimes2;
        int tDeltaYTimes2;
        int tError;
        int tStepX;
        int tStepY;

        int aXStart = xStart;
        int aYStart = yStart;
        
        /*
         * Clip to display size
         */
        
        // ---- BUG ----
        // TODO: invesigate and fix
//        if (aXStart >= DISPLAY_WIDTH) {
//            aXStart = DISPLAY_WIDTH - 1;
//        }
//        if (aXStart < 0) {
//            aXStart = 0;
//        }
//        if (aXEnd >= DISPLAY_WIDTH) {
//            aXEnd = DISPLAY_WIDTH - 1;
//        }
//        if (aXEnd < 0) {
//            aXEnd = 0;
//        }
//        if (aYStart >= DISPLAY_HEIGHT) {
//            aYStart = DISPLAY_HEIGHT - 1;
//        }
//        if (aYStart < 0) {
//            aYStart = 0;
//        }
//        if (aYEnd >= DISPLAY_HEIGHT) {
//            aYEnd = DISPLAY_HEIGHT - 1;
//        }
//        if (aYEnd < 0) {
//            aYEnd = 0;
//        }

        if ((aXStart == xEnd) || (aYStart == yEnd)) {
            //horizontal or vertical line -> fillRect() is faster
            fillRect(aXStart, aYStart, xEnd, yEnd, aColor);
        } else {
            //calculate direction
            tDeltaX = xEnd - aXStart;
            tDeltaY = yEnd - aYStart;
            if (tDeltaX < 0) {
                tDeltaX = -tDeltaX;
                tStepX = -1;
            } else {
                tStepX = +1;
            }
            if (tDeltaY < 0) {
                tDeltaY = -tDeltaY;
                tStepY = -1;
            } else {
                tStepY = +1;
            }
            tDeltaXTimes2 = tDeltaX << 1;
            tDeltaYTimes2 = tDeltaY << 1;
            //draw start pixel
            drawPixel(aXStart, aYStart, aColor);
            if (tDeltaX > tDeltaY) {
                // start value represents a half step in Y direction
                tError = tDeltaYTimes2 - tDeltaX;
                while (aXStart != xEnd) {
                    // step in main direction
                    aXStart += tStepX;
                    if (tError >= 0) {
                        if (aOverlap.contains(Overlap.MAJOR)) {
                            // draw pixel in main direction before changing
                            drawPixel(aXStart, aYStart, aColor);
                        }
                        // change Y
                        aYStart += tStepY;
                        if (aOverlap.contains(Overlap.MINOR)) {
                            // draw pixel in minor direction before changing
                            drawPixel(aXStart - tStepX, aYStart, aColor);
                        }
                        tError -= tDeltaXTimes2;
                    }
                    tError += tDeltaYTimes2;
                    drawPixel(aXStart, aYStart, aColor);
                }
            } else {
                tError = tDeltaXTimes2 - tDeltaY;
                while (aYStart != yEnd) {
                    aYStart += tStepY;
                    if (tError >= 0) {
                        if (aOverlap.contains(Overlap.MAJOR)) {
                            // draw pixel in main direction before changing
                            drawPixel(aXStart, aYStart, aColor);
                        }
                        aXStart += tStepX;
                        if (aOverlap.contains(Overlap.MINOR)) {
                            // draw pixel in minor direction before changing
                            drawPixel(aXStart, aYStart - tStepY, aColor);
                        }
                        tError -= tDeltaYTimes2;
                    }
                    tError += tDeltaXTimes2;
                    drawPixel(aXStart, aYStart, aColor);
                }
            }
        }
    }

    private void fillRect(int x0, int y0, int x1, int y1, Color color) {

        int sx = Math.min(x0, x1);
        int sy = Math.min(y0, y1);
        int ex = Math.max(x0, x1);
        int ey = Math.max(y0, y1);

//        sx = TeraMath.clamp(sx, DISPLAY_MIN_X, DISPLAY_MIN_X + DISPLAY_WIDTH - 1);
//        ex = TeraMath.clamp(ex, DISPLAY_MIN_X, DISPLAY_MIN_X + DISPLAY_WIDTH - 1);
//        
//        sy = TeraMath.clamp(sy, DISPLAY_MIN_Y, DISPLAY_MIN_Y + DISPLAY_HEIGHT - 1);
//        ey = TeraMath.clamp(ey, DISPLAY_MIN_Y, DISPLAY_MIN_Y + DISPLAY_HEIGHT - 1);
        
        for (int y = sy; y <= ey; y++) {
            for (int x = sx; x <= ex; x++) {
                drawPixel(x, y, color);
            }
        }
    }

    /**
     * Bresenham with thickness - no pixel missed and every pixel only drawn once!
     * @param x0 x0
     * @param y0 y0
     * @param x1 x1
     * @param y1 y1
     * @param thickness the thickness 
     * @param mode the mode
     * @param color the color
     */
    public void drawThickLine(int x0, int y0, int x1, int y1, int thickness, ThicknessMode mode, Color color) {

        if (thickness <= 1) {
            drawLineOverlap(x0, y0, x1, y1, EnumSet.noneOf(Overlap.class), color);
        }

        int tDeltaX;
        int tDeltaY;
        int tDeltaXTimes2;
        int tDeltaYTimes2;
        int tError;
        int tStepX;
        int tStepY;

        int aXStart = x0;
        int aYStart = y0;
        int aXEnd = x1;
        int aYEnd = y1;
        
        /**
         * For coordinatesystem with 0.0 topleft
         * Swap X and Y delta and calculate clockwise (new delta X inverted)
         * or counterclockwise (new delta Y inverted) rectangular direction.
         * The right rectangular direction for LineOverlap.MAJOR toggles with each octant
         */
        tDeltaY = aXEnd - aXStart;
        tDeltaX = aYEnd - aYStart;
        // mirror 4 quadrants to one and adjust deltas and stepping direction
        boolean tSwap = true; // count effective mirroring
        if (tDeltaX < 0) {
            tDeltaX = -tDeltaX;
            tStepX = -1;
            tSwap = !tSwap;
        } else {
            tStepX = +1;
        }
        if (tDeltaY < 0) {
            tDeltaY = -tDeltaY;
            tStepY = -1;
            tSwap = !tSwap;
        } else {
            tStepY = +1;
        }
        tDeltaXTimes2 = tDeltaX << 1;
        tDeltaYTimes2 = tDeltaY << 1;
        Set<Overlap> tOverlap;

        // adjust for right direction of thickness from line origin
        int tDrawStartAdjustCount;
        
        switch (mode) {
        case COUNTERCLOCKWISE: 
            tDrawStartAdjustCount = thickness - 1;
            break;
        case CLOCKWISE:
            tDrawStartAdjustCount = 0;
            break;
        case MIDDLE:
            tDrawStartAdjustCount = thickness / 2;
            break;
            default:
                throw new IllegalStateException();
        }

        // which octant are we now
        if (tDeltaX >= tDeltaY) {
            if (tSwap) {
                tDrawStartAdjustCount = (thickness - 1) - tDrawStartAdjustCount;
                tStepY = -tStepY;
            } else {
                tStepX = -tStepX;
            }
            /*
             * Vector for draw direction of lines is rectangular and counterclockwise to original line
             * Therefore no pixel will be missed if LINE_OVERLAP_MAJOR is used
             * on changing in minor rectangular direction
             */
            // adjust draw start point
            tError = tDeltaYTimes2 - tDeltaX;
            for (int i = tDrawStartAdjustCount; i > 0; i--) {
                // change X (main direction here)
                aXStart -= tStepX;
                aXEnd -= tStepX;
                if (tError >= 0) {
                    // change Y
                    aYStart -= tStepY;
                    aYEnd -= tStepY;
                    tError -= tDeltaXTimes2;
                }
                tError += tDeltaYTimes2;
            }
            //draw start line
            drawLine(aXStart, aYStart, aXEnd, aYEnd, color);
            // draw aThickness lines
            tError = tDeltaYTimes2 - tDeltaX;
            for (int i = thickness; i > 1; i--) {
                // change X (main direction here)
                aXStart += tStepX;
                aXEnd += tStepX;
                tOverlap = EnumSet.noneOf(Overlap.class);
                if (tError >= 0) {
                    // change Y
                    aYStart += tStepY;
                    aYEnd += tStepY;
                    tError -= tDeltaXTimes2;
                    /*
                     * change in minor direction reverse to line (main) direction
                     * because of chosing the right (counter)clockwise draw vector
                     * use LINE_OVERLAP_MAJOR to fill all pixel
                     *
                     * EXAMPLE:
                     * 1,2 = Pixel of first lines
                     * 3 = Pixel of third line in normal line mode
                     * - = Pixel which will be drawn in LINE_OVERLAP_MAJOR mode
                     *           33
                     *       3333-22
                     *   3333-222211
                     * 33-22221111
                     *  221111                     /\
                         *  11                          Main direction of draw vector
                     *  -> Line main direction
                     *  <- Minor direction of counterclockwise draw vector
                     */
                    tOverlap = EnumSet.of(Overlap.MAJOR);
                }
                tError += tDeltaYTimes2;
                drawLineOverlap(aXStart, aYStart, aXEnd, aYEnd, tOverlap, color);
            }
        } else {
            // the other octant
            if (tSwap) {
                tStepX = -tStepX;
            } else {
                tDrawStartAdjustCount = (thickness - 1) - tDrawStartAdjustCount;
                tStepY = -tStepY;
            }
            // adjust draw start point
            tError = tDeltaXTimes2 - tDeltaY;
            for (int i = tDrawStartAdjustCount; i > 0; i--) {
                aYStart -= tStepY;
                aYEnd -= tStepY;
                if (tError >= 0) {
                    aXStart -= tStepX;
                    aXEnd -= tStepX;
                    tError -= tDeltaYTimes2;
                }
                tError += tDeltaXTimes2;
            }
            //draw start line
            drawLine(aXStart, aYStart, aXEnd, aYEnd, color);
            tError = tDeltaXTimes2 - tDeltaY;
            for (int i = thickness; i > 1; i--) {
                aYStart += tStepY;
                aYEnd += tStepY;
                tOverlap = EnumSet.noneOf(Overlap.class);
                if (tError >= 0) {
                    aXStart += tStepX;
                    aXEnd += tStepX;
                    tError -= tDeltaYTimes2;
                    tOverlap = EnumSet.of(Overlap.MAJOR);
                }
                tError += tDeltaXTimes2;
                drawLineOverlap(aXStart, aYStart, aXEnd, aYEnd, tOverlap, color);
            }
        }
    }
    /**
     * Bresenham with thickness, but no clipping, some pixel are drawn twice (use LINE_OVERLAP_BOTH)
     * and direction of thickness changes for each octant (except for LINE_THICKNESS_MIDDLE and aThickness odd)
     * @param x0 x0
     * @param y0 y0
     * @param x1 x1
     * @param y1 y1
     * @param thickness the thickness 
     * @param mode the mode
     * @param color the color
     */
    public void drawThickLineSimple(int x0, int y0, int x1, int y1, int thickness, ThicknessMode mode, Color color) {

        int tDeltaX;
        int tDeltaY;
        int tDeltaXTimes2;
        int tDeltaYTimes2;
        int tError;
        int tStepX;
        int tStepY;
        
        int aXStart = x0;
        int aYStart = y0;
        int aXEnd = x1;
        int aYEnd = y1;
        
        tDeltaY = aXStart - aXEnd;
        tDeltaX = aYEnd - aYStart;
        // mirror 4 quadrants to one and adjust deltas and stepping direction
        if (tDeltaX < 0) {
            tDeltaX = -tDeltaX;
            tStepX = -1;
        } else {
            tStepX = +1;
        }
        if (tDeltaY < 0) {
            tDeltaY = -tDeltaY;
            tStepY = -1;
        } else {
            tStepY = +1;
        }
        tDeltaXTimes2 = tDeltaX << 1;
        tDeltaYTimes2 = tDeltaY << 1;
        Set<Overlap> tOverlap;
        // which octant are we now
        if (tDeltaX > tDeltaY) {
            if (mode == ThicknessMode.MIDDLE) {
                // adjust draw start point
                tError = tDeltaYTimes2 - tDeltaX;
                for (int i = thickness / 2; i > 0; i--) {
                    // change X (main direction here)
                    aXStart -= tStepX;
                    aXEnd -= tStepX;
                    if (tError >= 0) {
                        // change Y
                        aYStart -= tStepY;
                        aYEnd -= tStepY;
                        tError -= tDeltaXTimes2;
                    }
                    tError += tDeltaYTimes2;
                }
            }
            //draw start line
            drawLine(aXStart, aYStart, aXEnd, aYEnd, color);
            // draw aThickness lines
            tError = tDeltaYTimes2 - tDeltaX;
            for (int i = thickness; i > 1; i--) {
                // change X (main direction here)
                aXStart += tStepX;
                aXEnd += tStepX;
                tOverlap = EnumSet.noneOf(Overlap.class);
                if (tError >= 0) {
                    // change Y
                    aYStart += tStepY;
                    aYEnd += tStepY;
                    tError -= tDeltaXTimes2;
                    tOverlap = EnumSet.of(Overlap.MINOR, Overlap.MAJOR);
                }
                tError += tDeltaYTimes2;
                drawLineOverlap(aXStart, aYStart, aXEnd, aYEnd, tOverlap, color);
            }
        } else {
            // adjust draw start point
            if (mode == ThicknessMode.MIDDLE) {
                tError = tDeltaXTimes2 - tDeltaY;
                for (int i = thickness / 2; i > 0; i--) {
                    aYStart -= tStepY;
                    aYEnd -= tStepY;
                    if (tError >= 0) {
                        aXStart -= tStepX;
                        aXEnd -= tStepX;
                        tError -= tDeltaYTimes2;
                    }
                    tError += tDeltaXTimes2;
                }
            }
            //draw start line
            drawLine(aXStart, aYStart, aXEnd, aYEnd, color);
            tError = tDeltaXTimes2 - tDeltaY;
            for (int i = thickness; i > 1; i--) {
                aYStart += tStepY;
                aYEnd += tStepY;
                tOverlap = EnumSet.noneOf(Overlap.class);
                if (tError >= 0) {
                    aXStart += tStepX;
                    aXEnd += tStepX;
                    tError -= tDeltaYTimes2;
                    tOverlap = EnumSet.of(Overlap.MINOR, Overlap.MAJOR);
                }
                tError += tDeltaXTimes2;
                drawLineOverlap(aXStart, aYStart, aXEnd, aYEnd, tOverlap, color);
            }
        }
    }

    private void drawPixel(int x, int y, Color color) {
        pixelDrawer.drawPixel(x, y, color);
    }
}
