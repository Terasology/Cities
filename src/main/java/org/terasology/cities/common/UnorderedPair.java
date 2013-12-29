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

import java.util.Objects;

/**
 * Overrides equals() so that [a, b] equals [b, a]
 * @param <T> the element type
 * @author Martin Steiger
 */
public final class UnorderedPair<T> {

    private final T a;
    private final T b;

    /**
     * @param a city a
     * @param b city b
     */
    public UnorderedPair(T a, T b) {
        this.a = a;
        this.b = b;
    }
    
    /**
     * @return one element of the pair
     */
    public T getA() {
        return this.a;
    }

    /**
     * @return the other element of the pair
     */
    public T getB() {
        return this.b;
    }

    @Override
    public int hashCode() {
        // hash code must be equal for [a, b] and [b, a]
        return Math.max(Objects.hash(a, b), Objects.hash(b, a));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        
        if (obj.getClass() != getClass()) {
            return false;
        }
        
        UnorderedPair<?> that = (UnorderedPair<?>) obj;
        
        return (Objects.equals(this.a, that.a) && Objects.equals(this.b, that.b))
            || (Objects.equals(this.a, that.b) && Objects.equals(this.b, that.a));
    }

    @Override
    public String toString() {
        return "UnorderedPair [" + this.a + ", " + this.b + "]";
    }

    
}
