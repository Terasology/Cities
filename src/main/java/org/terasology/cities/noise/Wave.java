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

package org.terasology.cities.noise;

import com.google.common.base.Preconditions;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public abstract class Wave {

    /**
     * @param value the value in the range [0..1]
     * @return the function value
     */
    public abstract double get(double value);

    /**
     * Creates a function that looks like this:
     * <pre>
     *   /\    /\
     *  /  \  /  \
     * /    \/    \
     * </pre>
     * @param waveLength the width of one hat
     * @param multipliers the heights of the hats
     * @return an univariate function
     */
    public static Wave getHat(final double waveLength, final double[] multipliers) {
        
        return new Wave() {
    
            public double get(double v) {
                Preconditions.checkArgument(v >= 0 && v <= 1, "Value must be in range [0..1]");
                
                double hpos = getWaveletPos(waveLength, v);
                int suppIdx = getIndex(v, waveLength);
        
                return multipliers[suppIdx] * hpos;
            }
        };
    }
    
    /**
     * Creates a function that looks like a series of sine waves of different heights.
     * @param waveLength the width of one sine wave
     * @param multipliers the heights of the waves
     * @return an univariate function
     */
    public static Wave getSine(final double waveLength, final double[] multipliers) {
        return new Wave() {
    
            public double get(double v) {
                Preconditions.checkArgument(v >= 0 && v <= 1, "Value must be in range [0..1]");

                double hpos = getWaveletPos(waveLength, v);
                int suppIdx = getIndex(v, waveLength);
        
                double sine = Math.sin(hpos * Math.PI * 0.5);
                sine = sine * sine;
                return multipliers[suppIdx] * sine;
            }
        };
    }    
    
    /**
     * @param v the value
     * @param waveLength the wavelength
     * @return the index of the wavelet
     */
    protected static int getIndex(double v, double waveLength) {
        int suppIdx = (int) Math.floor(v / waveLength);
        
        // the last point of the last wavelet is a special case
        if (v == 1.0) {
            suppIdx--;
        }
        
        return suppIdx;
    }

    /**
     * @param waveLength the wavelength
     * @param v the value
     * @return the relative position in a wavelet [0..1..0][0..1..0]
     */
    private static double getWaveletPos(final double waveLength, double v) {
        return waveLength - 2.0 * Math.abs(waveLength * 0.5 - v % waveLength);
    }
    
}

