/*
 * Copyright 2014 MovingBlocks
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

package org.terasology.cities.array;

/**
 * Delegates all method calls to a given instance
 * @author Martin Steiger
 */
public class DelegatingIntArray2D implements IntArray2D {

    private final IntArray2D delegate;
    
    /**
     * @param delegate the object to delegate calls to
     */
    public DelegatingIntArray2D(IntArray2D delegate) {
        this.delegate = delegate;
    }

    @Override
    public void set(int x, int y, int value) {
        delegate.set(x, y, value);
    }

    @Override
    public int get(int x, int y) {
        return delegate.get(x, y);
    }

    @Override
    public int getWidth() {
        return delegate.getWidth();
    }

    @Override
    public int getHeight() {
        return delegate.getHeight();
    }

}
