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

package org.terasology.cities.common;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests {@link Profiler}
 * @author Martin Steiger
 */
public class ProfilerTest {

    @Test
    public void testSimple() {
        Profiler p = Profiler.start();
        sleep(1000);
        double time = p.get();
        assertTrue(time > 900 && time < 1100);
        sleep(1000);
        double time2 = p.getAndReset();
        assertTrue(time2 > 1900 && time2 < 2100);
        sleep(1000);
        double time3 = p.get();
        assertTrue(time3 > 900 && time3 < 1100);
    }

    /**
     * @param i
     */
    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
