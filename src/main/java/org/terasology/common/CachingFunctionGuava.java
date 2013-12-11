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

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Caches function calls using guava's {@link LoadingCache}.
 * @param <F> the argument type
 * @param <T> the return value type
 * @author Martin Steiger
 */
final class CachingFunctionGuava<F, T> extends CachingFunction<F, T> {

    private final LoadingCache<F, T> cache;

    CachingFunctionGuava(final Function<? super F, ? extends T> function, int maxSize) {
        CacheLoader<F, T> loader = new CacheLoader<F, T>() {
            @Override
            public T load(F key) {
                return function.apply(key);
            }
        };

        cache = CacheBuilder.newBuilder().maximumSize(maxSize).build(loader);
    }
    
    @Override
    public T apply(F input) {
        // the cache loader does not throw checked exceptions, we can use getUnchecked() here
        T result = cache.getUnchecked(input);

        return result;
    }

    @Override
    public void invalidate(F input) {
        cache.invalidate(input);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }
}
