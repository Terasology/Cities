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
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.terasology.cities.terrain.HeightMaps;

import com.google.common.collect.Lists;

/**
 * Tests {@link ContourTracer}
 * @author Martin Steiger
 */
public class ContourTracingTest {

    @Test
    public void test() {
        
        List<String> data = Arrays.asList(
            "             ",
            "    XXXXXX   ",
            "  XXXXXXXXXX ",
            " XXX   XX    ",
            "   XXXXXXX   ",
            "  XXXXXXXX   ",
            "    XXXX     ",
            "   XXXX      ",
            "   XXX       ",
            "             ");

        int width = data.get(0).length();
        int height = data.size();
        
        Rectangle rc = new Rectangle(0, 0, width, height);
        ContourTracer ct = new ContourTracer(HeightMaps.stringBased(data), rc, ' ');
        List<Contour> all = Lists.newArrayList();
        all.addAll(ct.getOuterContours());
        all.addAll(ct.getInnerContours());
        drawContour(all, width, height);
        
        List<String> desired = Arrays.asList(
            "-------------",
            "----XXXXXX---",
            "--XXOOO--XXX-",
            "-XXO---OX----",
            "---XOOO--X---",
            "--XX----XX---",
            "----X--X-----",
            "---X--X------",
            "---XXX-------",
            "-------------");
        
        List<String> reality = drawContour(all, width, height);

        for (int line = 0; line < height; line++) {
            assertEquals("Line " + line, desired.get(line), reality.get(line));
        }
    }
    
    private List<String> drawContour(Collection<Contour> cts, int width, int height) {
        
        char[] glyphs = {'X', 'O', 'H', 'V', 'S', 'I'};
        
        List<String> result = Lists.newArrayList();
        String current = "";
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Point p = new Point(x, y);
                char contId = '-';
                int idx = 0;
                
                for (Contour c : cts) {
                    if (c.getPoints().contains(p)) {
                        contId = glyphs[idx];
                        break;
                    }
                    idx++;
                    
                    if (idx >= glyphs.length) {
                        idx = 0;
                    }
                }
                
                current += contId;
            }
            
            result.add(current);
            current = "";
        }
        
        return result;
    }
}
