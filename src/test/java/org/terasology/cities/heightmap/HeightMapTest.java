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

package org.terasology.cities.heightmap;

import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.*;
import org.terasology.math.Vector2i;

/**
 * Some tests on {@link HeightMap}s
 * @author Martin Steiger
 */
public class HeightMapTest {

    private static HeightMap hm = new NoiseHeightMap();
    private Random r = new Random(12345);
    
    @Test
    public void symmetricAlongXTest() {
        SymmetricHeightMap shm = HeightMaps.symmetricAlongX(hm);
        basicSymmetryTest(shm);
        
        assertEquals(new Vector2i(-123, 456), shm.getMirrored(new Vector2i(123, 456)));
    }
    
    @Test
    public void symmetricAlongZTest() {
        SymmetricHeightMap shm = HeightMaps.symmetricAlongZ(hm);
        basicSymmetryTest(shm);
        
        assertEquals(new Vector2i(123, -456), shm.getMirrored(new Vector2i(123, 456)));
    }
    
    @Test
    public void symmetricAlongDiagTest() {
        SymmetricHeightMap shm = HeightMaps.symmetricAlongDiagonal(hm);
        basicSymmetryTest(shm);
        
        assertEquals(new Vector2i(100, 0), shm.getMirrored(new Vector2i(0, -100)));
        assertEquals(new Vector2i(0, 100), shm.getMirrored(new Vector2i(-100, 0)));
        assertEquals(new Vector2i(10, 10), shm.getMirrored(new Vector2i(-10, -10)));
    }
    
    private void basicSymmetryTest(SymmetricHeightMap shm) {
        assertEquals(new Vector2i(0, 0), shm.getMirrored(new Vector2i(0, 0)));
        
        for (int i = 0; i < 100; i++) {
            Vector2i test = nextRandomPos();
            
            assertEquals(shm.isMirrored(test), shm.isMirrored(test.x, test.y));
    
            boolean isMirrored = shm.isMirrored(test);
            Vector2i mirrored = shm.getMirrored(test);
            
            assertTrue(isMirrored != shm.isMirrored(mirrored));
            assertEquals(test, shm.getMirrored(mirrored));
        }
    }
    
    private Vector2i nextRandomPos() {
        return new Vector2i(r.nextInt(1000) - 500, r.nextInt(1000) - 500);
    }

}
