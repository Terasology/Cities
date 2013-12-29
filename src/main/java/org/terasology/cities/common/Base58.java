/*
 * Copyright 2013 MovingBlocks
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

package org.terasology.cities.common;

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
