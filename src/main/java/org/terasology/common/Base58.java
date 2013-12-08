/*
 * Copyright 2013 MovingBlocks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.terasology.common;

/**
 * Creates human-readable Base58 strings from numbers (e.g. hashcodes) 
 * @author Martin Steiger
 */
public final class Base58 {
    
    private static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    
    private Base58() {
        // private constructor
    }

    /**
     * @param orgVal a long value
     * @return a human-readable string of the value
     */
    public static String encode(long orgVal) {
        
        // invert negative values and ignore this info
        long val = (orgVal < 0) ? -orgVal : orgVal;        
        StringBuffer s = new StringBuffer();

        int length = ALPHABET.length();
        
        while (val >= length) {
            long mod = val % length;
            s.append(ALPHABET.charAt((int) mod));
            val = val / length;
        }
        s.append(ALPHABET.charAt((int) val));

        return s.toString();
    }}
