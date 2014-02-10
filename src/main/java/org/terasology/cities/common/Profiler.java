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
 * A simple, but effective thread-safe profiler for micro-benchmarking.
 * Use start() with an arbitrary object identifier to start, get() for results
 * @author Martin Steiger
 */
public final class Profiler {
    
    private long start;
    
    private Profiler() {
        start = measure();
    }

    /**
     * Starts a measurement with a generated ID
     * @return the generated ID
     */
    public static Profiler start() {
        Profiler id = new Profiler();
        
        return id;
    }
    
    /**
     * Get the time
     * @return the time in milliseconds
     */
    public double get() {
        long now = measure();
        return (now - start) / 1000000.0;
    }

    /**
     * Get the time as formatted string (e.g. 334.22ms)
     * @return the time in milliseconds as formatted string
     */
    public String getAsString() {
        return asString(get());
    }

    /**
     * Get the time and reset the timer
     * @return the time in milliseconds
     */
    public double getAndReset() {
        long now = measure();
        double val = (now - start) / 1000000.0;
        start = now;
        return val;
    }

    /**
     * Get the time as formatted string (e.g. 334.22ms) and reset the timer
     * @return the time in milliseconds as formatted string
     */
    public String getAsStringAndReset() {
        return asString(getAndReset());
    }
    
    /**
     * @param value in milliseconds
     * @return a formatted string (e.g. 334.22ms)
     */
    private static String asString(double value) {
        return String.format("%.2fms.", value);
    }
    
    private static long measure() {
        return System.nanoTime();
    }

}
