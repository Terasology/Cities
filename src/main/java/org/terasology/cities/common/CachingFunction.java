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

import com.google.common.base.Function;

/**
 * Caches function calls (thread-safe)
 * @param <F> the argument type
 * @param <T> the return value type
 * @author Martin Steiger
 */
public abstract class CachingFunction<F, T> implements Function<F, T> {

    /**
     * @param function the function to wrap
     * @return the caching function
     */
    public static <F, T> CachingFunction<F, T> wrap(Function<? super F, ? extends T> function) {
        return new CachingFunctionGuava<F, T>(function, 1000);
    }
    
    /**
     * Discards any cached value for {@code input}.
     * @param input the
     */
    public abstract void invalidate(F input);

    /**
     * Discards all entries in the cache
     */
    public abstract  void invalidateAll();
}
