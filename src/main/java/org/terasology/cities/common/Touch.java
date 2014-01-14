
package org.terasology.cities.common;

/**
 * Port of 
 * https://code.google.com/p/touchscreen-apps
 */
public abstract class Touch
{
	private static final int LINE_OVERLAP_NONE = 0;     // No line overlap
	private static final int LINE_OVERLAP_MAJOR = 0x01; // Overlap - first go major then minor direction
	private static final int LINE_OVERLAP_MINOR = 0x02; // Overlap - first go minor then major direction
	private static final int LINE_OVERLAP_BOTH = 0x03;  // Overlap - both

	static final int LINE_THICKNESS_MIDDLE = 0;
	static final int LINE_THICKNESS_DRAW_CLOCKWISE = 1;
	static final int LINE_THICKNESS_DRAW_COUNTERCLOCKWISE = 2;

	
	private static final int DISPLAY_WIDTH = 1000;
	private static final int DISPLAY_HEIGHT = 1000;

	private static class ThickLine {
        int StartX;
        int StartY;
        int EndX;
        int EndY;
        int Thickness;
        int ThicknessMode;
        int Color;
        int BackgroundColor;
	};


	/**
	 * modified Bresenham
	 */
	void drawLine(int aXStart, int aYStart, int aXEnd, int aYEnd, int aColor) {
		drawLineOverlap(aXStart, aYStart, aXEnd, aYEnd, LINE_OVERLAP_NONE, aColor);
	}

	/**
	 * modified Bresenham with optional overlap (esp. for drawThickLine())
	 * Overlap draws additional pixel when changing minor direction - for standard bresenham overlap = LINE_OVERLAP_NONE (0)
	 *
	 *  Sample line:
	 *
	 *    00+
	 *     -0000+
	 *         -0000+
	 *             -00
	 *
	 *  0 pixels are drawn for normal line without any overlap
	 *  + pixels are drawn if LINE_OVERLAP_MAJOR
	 *  - pixels are drawn if LINE_OVERLAP_MINOR
	 */
	void drawLineOverlap(int aXStart, int aYStart, int aXEnd, int aYEnd, int aOverlap, int aColor) {
		int tDeltaX, tDeltaY, tDeltaXTimes2, tDeltaYTimes2, tError, tStepX, tStepY;

		/*
		 * Clip to display size
		 */
		if (aXStart >= DISPLAY_WIDTH) {
			aXStart = DISPLAY_WIDTH - 1;
		}
		if (aXStart < 0) {
			aXStart = 0;
		}
		if (aXEnd >= DISPLAY_WIDTH) {
			aXEnd = DISPLAY_WIDTH - 1;
		}
		if (aXEnd < 0) {
			aXEnd = 0;
		}
		if (aYStart >= DISPLAY_HEIGHT) {
			aYStart = DISPLAY_HEIGHT - 1;
		}
		if (aYStart < 0) {
			aYStart = 0;
		}
		if (aYEnd >= DISPLAY_HEIGHT) {
			aYEnd = DISPLAY_HEIGHT - 1;
		}
		if (aYEnd < 0) {
			aYEnd = 0;
		}

		if ((aXStart == aXEnd) || (aYStart == aYEnd)) {
			//horizontal or vertical line -> fillRect() is faster
			fillRect(aXStart, aYStart, aXEnd, aYEnd, aColor);
		} else {
			//calculate direction
			tDeltaX = aXEnd - aXStart;
			tDeltaY = aYEnd - aYStart;
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
				while (aXStart != aXEnd) {
					// step in main direction
					aXStart += tStepX;
					if (tError >= 0) {
						if ((aOverlap & LINE_OVERLAP_MAJOR) != 0) {
							// draw pixel in main direction before changing
							drawPixel(aXStart, aYStart, aColor);
						}
						// change Y
						aYStart += tStepY;
						if ((aOverlap & LINE_OVERLAP_MINOR) != 0) {
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
				while (aYStart != aYEnd) {
					aYStart += tStepY;
					if (tError >= 0) {
						if ((aOverlap & LINE_OVERLAP_MAJOR) != 0) {
							// draw pixel in main direction before changing
							drawPixel(aXStart, aYStart, aColor);
						}
						aXStart += tStepX;
						if ((aOverlap & LINE_OVERLAP_MINOR) != 0) {
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


void fillRect(int x0, int y0, int x1, int y1, int color) {
    int size;
    int tmp, i;

    if (x0 > x1) {
        tmp = x0;
        x0 = x1;
        x1 = tmp;
    }
    if (y0 > y1) {
        tmp = y0;
        y0 = y1;
        y1 = tmp;
    }

    if (x1 >= DISPLAY_WIDTH) {
        x1 = DISPLAY_WIDTH - 1;
    }
    if (y1 >= DISPLAY_HEIGHT) {
        y1 = DISPLAY_HEIGHT - 1;
    }

    for (int y = y0; y <= y1; y++)
    {
        for (int x = x0; x <= x1; x++) 
        {
            drawPixel(x, y, color);
        }
    }
}



	protected abstract void drawPixel(int aXStart, int i, int aColor);

	/**
	 * Bresenham with thickness
	 * no pixel missed and every pixel only drawn once!
	 */
	void drawThickLine(int aXStart, int aYStart, int aXEnd, int aYEnd, int aThickness, int aThicknessMode,
			int aColor) {
		int i, tDeltaX, tDeltaY, tDeltaXTimes2, tDeltaYTimes2, tError, tStepX, tStepY;

		if(aThickness <= 1) {
			drawLineOverlap(aXStart, aYStart, aXEnd, aYEnd, LINE_OVERLAP_NONE, aColor);
		}
		/*
		 * Clip to display size
		 */
		if (aXStart >= DISPLAY_WIDTH) {
			aXStart = DISPLAY_WIDTH - 1;
		}
		if (aXStart < 0) {
			aXStart = 0;
		}
		if (aXEnd >= DISPLAY_WIDTH) {
			aXEnd = DISPLAY_WIDTH - 1;
		}
		if (aXEnd < 0) {
			aXEnd = 0;
		}
		if (aYStart >= DISPLAY_HEIGHT) {
			aYStart = DISPLAY_HEIGHT - 1;
		}
		if (aYStart < 0) {
			aYStart = 0;
		}
		if (aYEnd >= DISPLAY_HEIGHT) {
			aYEnd = DISPLAY_HEIGHT - 1;
		}
		if (aYEnd < 0) {
			aYEnd = 0;
		}

		/**
		 * For coordinatesystem with 0.0 topleft
		 * Swap X and Y delta and calculate clockwise (new delta X inverted)
		 * or counterclockwise (new delta Y inverted) rectangular direction.
		 * The right rectangular direction for LINE_OVERLAP_MAJOR toggles with each octant
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
		int tOverlap = 0;
		// adjust for right direction of thickness from line origin
		int tDrawStartAdjustCount = aThickness / 2;
		if (aThicknessMode == LINE_THICKNESS_DRAW_COUNTERCLOCKWISE) {
			tDrawStartAdjustCount = aThickness - 1;
		} else if (aThicknessMode == LINE_THICKNESS_DRAW_CLOCKWISE) {
			tDrawStartAdjustCount = 0;
		}

		// which octant are we now
		if (tDeltaX >= tDeltaY) {
			if (tSwap) {
				tDrawStartAdjustCount = (aThickness - 1) - tDrawStartAdjustCount;
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
			for (i = tDrawStartAdjustCount; i > 0; i--) {
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
			drawLine(aXStart, aYStart, aXEnd, aYEnd, aColor);
			// draw aThickness lines
			tError = tDeltaYTimes2 - tDeltaX;
			for (i = aThickness; i > 1; i--) {
				// change X (main direction here)
				aXStart += tStepX;
				aXEnd += tStepX;
				tOverlap = LINE_OVERLAP_NONE;
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
					tOverlap = LINE_OVERLAP_MAJOR;
				}
				tError += tDeltaYTimes2;
				drawLineOverlap(aXStart, aYStart, aXEnd, aYEnd, tOverlap, aColor);
			}
		} else {
			// the other octant
			if (tSwap) {
				tStepX = -tStepX;
			} else {
				tDrawStartAdjustCount = (aThickness - 1) - tDrawStartAdjustCount;
				tStepY = -tStepY;
			}
			// adjust draw start point
			tError = tDeltaXTimes2 - tDeltaY;
			for (i = tDrawStartAdjustCount; i > 0; i--) {
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
			drawLine(aXStart, aYStart, aXEnd, aYEnd, aColor);
			tError = tDeltaXTimes2 - tDeltaY;
			for (i = aThickness; i > 1; i--) {
				aYStart += tStepY;
				aYEnd += tStepY;
				tOverlap = LINE_OVERLAP_NONE;
				if (tError >= 0) {
					aXStart += tStepX;
					aXEnd += tStepX;
					tError -= tDeltaYTimes2;
					tOverlap = LINE_OVERLAP_MAJOR;
				}
				tError += tDeltaXTimes2;
				drawLineOverlap(aXStart, aYStart, aXEnd, aYEnd, tOverlap, aColor);
			}
		}
	}
	/**
	 * The same as before, but no clipping, some pixel are drawn twice (use LINE_OVERLAP_BOTH)
	 * and direction of thickness changes for each octant (except for LINE_THICKNESS_MIDDLE and aThickness odd)
	 */
	void drawThickLineSimple(int aXStart, int aYStart, int aXEnd, int aYEnd, int aThickness, int aThicknessMode,
			int aColor) {
		int i, tDeltaX, tDeltaY, tDeltaXTimes2, tDeltaYTimes2, tError, tStepX, tStepY;

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
		int tOverlap;
		// which octant are we now
		if (tDeltaX > tDeltaY) {
			if (aThicknessMode == LINE_THICKNESS_MIDDLE) {
				// adjust draw start point
				tError = tDeltaYTimes2 - tDeltaX;
				for (i = aThickness / 2; i > 0; i--) {
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
			drawLine(aXStart, aYStart, aXEnd, aYEnd, aColor);
			// draw aThickness lines
			tError = tDeltaYTimes2 - tDeltaX;
			for (i = aThickness; i > 1; i--) {
				// change X (main direction here)
				aXStart += tStepX;
				aXEnd += tStepX;
				tOverlap = LINE_OVERLAP_NONE;
				if (tError >= 0) {
					// change Y
					aYStart += tStepY;
					aYEnd += tStepY;
					tError -= tDeltaXTimes2;
					tOverlap = LINE_OVERLAP_BOTH;
				}
				tError += tDeltaYTimes2;
				drawLineOverlap(aXStart, aYStart, aXEnd, aYEnd, tOverlap, aColor);
			}
		} else {
			// adjust draw start point
			if (aThicknessMode == LINE_THICKNESS_MIDDLE) {
				tError = tDeltaXTimes2 - tDeltaY;
				for (i = aThickness / 2; i > 0; i--) {
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
			drawLine(aXStart, aYStart, aXEnd, aYEnd, aColor);
			tError = tDeltaXTimes2 - tDeltaY;
			for (i = aThickness; i > 1; i--) {
				aYStart += tStepY;
				aYEnd += tStepY;
				tOverlap = LINE_OVERLAP_NONE;
				if (tError >= 0) {
					aXStart += tStepX;
					aXEnd += tStepX;
					tError -= tDeltaYTimes2;
					tOverlap = LINE_OVERLAP_BOTH;
				}
				tError += tDeltaXTimes2;
				drawLineOverlap(aXStart, aYStart, aXEnd, aYEnd, tOverlap, aColor);
			}
		}
	}

	/**
	 * aNewRelEndX + Y are new x and y values relative to start point
	 */
	void refreshVector(ThickLine aLine, int aNewRelEndX, int aNewRelEndY) {
		int tNewEndX = aLine.StartX + aNewRelEndX;
		int tNewEndY = aLine.StartY + aNewRelEndY;
		if (aLine.EndX != tNewEndX || aLine.EndX != tNewEndY) {
			//clear old line
			drawThickLine(aLine.StartX, aLine.StartY, aLine.EndX, aLine.EndY, aLine.Thickness, aLine.ThicknessMode,
					aLine.BackgroundColor);
			// Draw new line
			/**
			 * clipping
			 */
			if (tNewEndX < 0) {
				tNewEndX = 0;
			} else if (tNewEndX > DISPLAY_WIDTH - 1) {
				tNewEndX = DISPLAY_WIDTH - 1;
			}
			aLine.EndX = tNewEndX;

			if (tNewEndY < 0) {
				tNewEndY = 0;
			} else if (tNewEndY > DISPLAY_HEIGHT - 1) {
				tNewEndY = DISPLAY_HEIGHT - 1;
			}
			aLine.EndY = tNewEndY;

			drawThickLine(aLine.StartX, aLine.StartY, tNewEndX, tNewEndY, aLine.Thickness, aLine.ThicknessMode, aLine.Color);
		}
	}
	/**
	 * @}
	 */

}
