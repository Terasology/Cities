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
