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
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Function;

/**
 * Caches function calls based on a concurrent hash map.
 * @param <F> the argument type
 * @param <T> the return value type
 * @author Martin Steiger
 */
final class CachingFunctionHashMap<F, T> extends CachingFunction<F, T> {
    private final Function<? super F, ? extends T> function;

    private final Map<F, T> cache = new ConcurrentHashMap<F, T>();

    /**
     * @param function the function to cache
     */
    CachingFunctionHashMap(Function<? super F, ? extends T> function) {
        this.function = function;
    }
    
    /**
     * @param function the function to wrap
     * @return the caching function
     */
    public static <F, T> CachingFunctionHashMap<F, T> wrap(Function<? super F, ? extends T> function) {
        return new CachingFunctionHashMap<>(function);
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

    @Override
    public void invalidate(F input) {
        cache.remove(input);
    }

    @Override
    public void invalidateAll() {
        cache.clear();
    }
}
