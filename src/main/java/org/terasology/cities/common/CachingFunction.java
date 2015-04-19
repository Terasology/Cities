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
 * Caches function calls (thread-safe)
 * @param <F> the argument type
 * @param <T> the return value type
 * @author Martin Steiger
 */
public abstract class CachingFunction<F, T> {

    /**
     * @param function the function to wrap
     * @return the caching function
     */
    public static <F, T> LoadingCache<F, T> wrap(Function<F, T> function) {
        return CacheBuilder.newBuilder().build(CacheLoader.from(function));
    }
}
