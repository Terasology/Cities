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
