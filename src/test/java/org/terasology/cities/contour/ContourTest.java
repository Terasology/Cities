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

package org.terasology.cities.contour;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * Tests the {@link Contour} class
 * @author Martin Steiger
 */
public class ContourTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ContourTest.class);
    
    /**
     * Some simple tests on curve simplification
     */
    @Test
    public void test() {
        Contour pts = new Contour();

        for (int i = 3; i < 5; i++) {
            Point p1 = new Point(i, 0);
            pts.addPoint(p1);
        }
        for (int i = 0; i < 5; i++) {
            Point p1 = new Point(5 + i, i);
            pts.addPoint(p1);
        }

        for (int i = 0; i < 5; i++) {
            Point p1 = new Point(10, i + 5);
            pts.addPoint(p1);
        }

        for (int i = 0; i < 5; i++) {
            Point p1 = new Point(10 - i, 10 - i);
            pts.addPoint(p1);
        }

        for (int i = 0; i < 5; i++) {
            Point p1 = new Point(5 - i, 5);
            pts.addPoint(p1);
        }

        for (int i = 0; i < 5; i++) {
            Point p1 = new Point(0, 5 - i);
            pts.addPoint(p1);
        }

        for (int i = 0; i < 3; i++) {
            Point p1 = new Point(i, 0);
            pts.addPoint(p1);
        }
        Collection<Point> simplePts = pts.getSimplifiedCurve();
    
        char[][] data = new char[11][11];
        for (int i = 0; i < data.length; i++) {
            Arrays.fill(data[i], ' ');
        }
    
        for (Point p : pts.getPoints()) {
            data[p.y][p.x] = 'O';
        }
    
    
        for (Point p : simplePts) {
            data[p.y][p.x] = 'X';
        }
        
        for (char[] line : data) {
            logger.info(new String(line));
        }
    
        List<Point> shouldBe = ImmutableList.of(
                new Point(3, 0),
                new Point(5, 0),
                new Point(10, 5),
                new Point(10, 10),
                new Point(5, 5),
                new Point(0, 5),
                new Point(0, 0),
                new Point(2, 0));
    
        assertEquals(shouldBe, simplePts);
    }
}
