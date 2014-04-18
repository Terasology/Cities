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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import javax.vecmath.Point2i;

import org.junit.Test;
import org.terasology.cities.symmetry.Symmetries;
import org.terasology.cities.symmetry.Symmetry;

/**
 * Some tests on {@link HeightMap}s
 * @author Martin Steiger
 */
public class HeightMapTest {

    private static HeightMap hm = new NoiseHeightMap();
    private Random r = new Random(12345);
    
    @Test
    public void symmetricAlongXTest() {
        Symmetry sym = Symmetries.alongX();
        basicSymmetryTest(sym);
        
        assertEquals(new Point2i(123, -457), sym.getMirrored(new Point2i(123, 456)));
    }
    
    @Test
    public void symmetricAlongZTest() {
        Symmetry sym = Symmetries.alongZ();
        basicSymmetryTest(sym);
        
        assertEquals(new Point2i(-124, 456), sym.getMirrored(new Point2i(123, 456)));
    }
    
    @Test
    public void symmetricAlongPosDiagTest() {
        Symmetry sym = Symmetries.alongPositiveDiagonal();
        basicSymmetryTest(sym);
        
        assertEquals(new Point2i(100, 0), sym.getMirrored(new Point2i(0, 100)));
        assertEquals(new Point2i(0, 100), sym.getMirrored(new Point2i(100, 0)));
        assertEquals(new Point2i(10, 10), sym.getMirrored(new Point2i(10, 10)));
    }
    
    @Test
    public void symmetricAlongNegDiagTest() {
        Symmetry sym = Symmetries.alongNegativeDiagonal();
        basicSymmetryTest(sym);
        
        assertEquals(new Point2i(99, -1), sym.getMirrored(new Point2i(0, -100)));
        assertEquals(new Point2i(-1, 99), sym.getMirrored(new Point2i(-100, 0)));
        assertEquals(new Point2i(9, 9), sym.getMirrored(new Point2i(-10, -10)));
    }
    
    private void basicSymmetryTest(Symmetry sym) {
        
        for (int i = 0; i < 100; i++) {
            Point2i test = nextRandomPos();
            
            assertEquals(sym.isMirrored(test), sym.isMirrored(test.x, test.y));
    
            boolean isMirrored = sym.isMirrored(test);
            Point2i mirrored = sym.getMirrored(test);
            
            assertTrue(isMirrored != sym.isMirrored(mirrored));
            assertEquals(test, sym.getMirrored(mirrored));
        }
    }
    
    private Point2i nextRandomPos() {
        return new Point2i(r.nextInt(1000) - 500, r.nextInt(1000) - 500);
    }

}
