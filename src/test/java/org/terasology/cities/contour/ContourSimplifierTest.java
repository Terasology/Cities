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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests the {@link ContourSimplifier} class
 * @author Martin Steiger
 */
public class ContourSimplifierTest {
    
    /**
     * Some simple tests
     */
    @Test
    public void test() {
        List<Point> pts = new ArrayList<Point>();

        for (int i = 3; i < 5; i++) {
            Point p1 = new Point(i, 0);
            pts.add(p1);
        }
        for (int i = 0; i < 5; i++) {
            Point p1 = new Point(5 + i, i);
            pts.add(p1);
        }

        for (int i = 0; i < 5; i++) {
            Point p1 = new Point(10, i + 5);
            pts.add(p1);
        }

        for (int i = 0; i < 5; i++) {
            Point p1 = new Point(10 - i, 10 - i);
            pts.add(p1);
        }

        for (int i = 0; i < 5; i++) {
            Point p1 = new Point(5 - i, 5);
            pts.add(p1);
        }

        for (int i = 0; i < 5; i++) {
            Point p1 = new Point(0, 5 - i);
            pts.add(p1);
        }

        for (int i = 0; i < 3; i++) {
            Point p1 = new Point(i, 0);
            pts.add(p1);
        }
        List<Point> simplePts = ContourSimplifier.simplify(pts);
    
        char[][] data = new char[11][11];
        for (int i = 0; i < data.length; i++) {
            Arrays.fill(data[i], ' ');
        }
    
        for (Point p : pts) {
            data[p.y][p.x] = 'O';
        }
    
    
        for (Point p : simplePts) {
            data[p.y][p.x] = 'X';
        }
        
        for (char[] line : data) {
            System.out.println(new String(line));
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
