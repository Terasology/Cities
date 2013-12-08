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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Function;

/**
 * Caches function calls (thread-safe)
 * @param <F> the argument type
 * @param <T> the return value type
 * @author Martin Steiger
 */
public final class CachingFunction<F, T> implements Function<F, T> {
    private final Function<? super F, ? extends T> function;

    private final Map<F, T> cache = new ConcurrentHashMap<F, T>();

    /**
     * @param function the function to cache
     */
    private CachingFunction(Function<? super F, ? extends T> function) {
        this.function = function;
    }
    
    /**
     * @param function the function to wrap
     * @return the caching function
     */
    public static <F, T> CachingFunction<F, T> wrap(Function<? super F, ? extends T> function) {
        return new CachingFunction<>(function);
    }
    
    @Override
    public T apply(F input) {
        T result = cache.get(input);

        if (result == null) {
            // explicitly check whether the value is stored, but null
            if (cache.containsKey(input)) {
                return null;
            }

            result = function.apply(input);

            cache.put(input, result);
        }

        return result;
    }

    /**
     * Discards any cached value for {@code input}.
     * @param input the
     */
    public void invalidate(F input) {
        cache.remove(input);
    }

    /**
     * Discards all entries in the cache
     */
    public void invalidateAll() {
        cache.clear();
    }
}
