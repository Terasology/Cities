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

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * A simple, but effective thread-safe profiler for micro-benchmarking.
 * Use start() with an arbitrary object identifier to start, get() for intermediate results, and stop() for the final timing.
 * @author Martin Steiger
 */
public final class Profiler {
    
    private static final Map<Object, Long> TIME_MAP = Maps.newConcurrentMap();
    
    private Profiler() {
        // private constructor
    }

    /**
     * Starts a measurement with the specified ID
     * @param id an identifier object
     */
    public static void start(Object id) {
        long time = measure();
        TIME_MAP.put(id, time);
    }
    
    /**
     * Removes a measurement recording with the specified ID
     * @param id an identifier object
     * @return the time in milliseconds
     */
    public static double stop(Object id) {
        double time = get(id);
        TIME_MAP.remove(id);
        return time;
    }

    /**
     * Get the time since start() was last called
     * @param id an identifier object
     * @return the time in milliseconds
     */
    public static double get(Object id) {
        Long start = TIME_MAP.get(id);
        long time = measure();

        if (start == null) {
            throw new IllegalArgumentException("Invalid id '" + String.valueOf(id) + "'");
        }

        return (time - start) / 1000000.0;
    }

    /**
     * Get the time since start() was last called as formatted string (e.g. 334.22ms)
     * @param id an identifier object
     * @return the time in milliseconds as formatted string
     */
    public static String getAsString(Object id) {
        double time = get(id);
        return String.format("%.2fms.", time);
    }

    private static long measure() {
        return System.nanoTime();
    }
}
