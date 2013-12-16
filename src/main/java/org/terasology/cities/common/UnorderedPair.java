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
