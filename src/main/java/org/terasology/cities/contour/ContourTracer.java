/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.contour;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMapAdapter;

/**
 * Heavily inspired by sample code from the book 
 * <p>
 * "Digital Image Processing - An Algorithmic Introduction using Java" 
 * by Wilhelm Burger and Mark J. Burge
 * </p>
 * <pre>
 * https://github.com/biometrics/imagingbook
 * </pre>
 * @author Martin Steiger
 */
public class ContourTracer {
    private static final byte FOREGROUND = 1;
    private static final byte BACKGROUND = 0;

    private List<Contour> outerContours;
    private List<Contour> innerContours;


    // label values in labelArray can be:
    // 0 ... unlabeled
    // -1 ... previously visited background pixel
    // >0 ... valid label
    private Array2D labelArray;

    private final HeightMap dataMap;
    private final int width;
    private final int height;

    /**
     * @param orgHm the original height map to use
     * @param width the width of the scanning area
     * @param height the height of the scanning area
     * @param threshold the sea level threshold
     */
    public ContourTracer(final HeightMap orgHm, final int width, final int height, final int threshold) {
        // Create auxil. arrays, which are "padded", i.e.,
        // are 2 rows and 2 columns larger than the image:

        this.width = width;
        this.height = height;
        
        labelArray = new Array2D(width, height, 1, BACKGROUND);

        this.dataMap = new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                if (x == -1 || z == -1)
                    return BACKGROUND;
                
                if (x == width || z == height)
                    return BACKGROUND;
                
                if (orgHm.apply(x, z) > threshold) {
                    return BACKGROUND;
                } else {
                    return FOREGROUND;
                }
            }
        };
    }

    /**
     * @return a list of outer contours
     */
    public List<Contour> getOuterContours() {
        if (outerContours == null) {
            outerContours = new ArrayList<>();
            innerContours = new ArrayList<>();
            findAllContours();
        }
        
        return outerContours;
    }

    /**
     * @return a list of inner contours (islands)
     */
    public List<Contour> getInnerContours() {
        if (innerContours == null) {
            outerContours = new ArrayList<>();
            innerContours = new ArrayList<>();
            findAllContours();
        }
        
        return innerContours;
    }

    private Contour traceOuterContour(int cx, int cy, int label) {
        return traceContour(cx, cy, label, 0);
    }

    private Contour traceInnerContour(int cx, int cy, int label) {
        return traceContour(cx, cy, label, 1);
    }

    // Trace one contour starting at (xS,yS)
    // in direction dS with label label
    // trace one contour starting at (xS,yS) in direction dS
    private Contour traceContour(int xS, int yS, int label, int dS) {
        Contour cont = new Contour();

        int xT; // T = successor of starting point (xS,yS)
        int yT;
        
        int xP; // P = previous contour point
        int yP;
        
        int xC; // C = current contour point
        int yC;
        
        Point pt = new Point(xS, yS);
        int dNext = findNextPoint(pt, dS);
        cont.addPoint(pt);
        
        xP = xS;
        yP = yS;
        xT = pt.x;
        yT = pt.y;
        xC = pt.x;
        yC = pt.y;

        boolean done = (xS == xT && yS == yT); // true if isolated pixel

        while (!done) {
            labelArray.set(xC, yC, label);
            pt = new Point(xC, yC);
            int dSearch = (dNext + 6) % 8;
            dNext = findNextPoint(pt, dSearch);
            xP = xC;
            yP = yC;
            xC = pt.x;
            yC = pt.y;
            // are we back at the starting position?
            done = (xP == xS && yP == yS && xC == xT && yC == yT);
            if (!done) {
                cont.addPoint(pt);
            }
        }
        return cont;
    }

    /** 
     * @param pt the start point (<b>modified during op</b>)
     * @param startDir the start search direction
     * @return the final tracing direction
     */
    private int findNextPoint(Point pt, int startDir) {

        final int[][] delta = {
            {+1, 0}, {+1, +1}, {0, +1}, {-1, +1}, 
            {-1, 0}, {-1, -1}, {0, -1}, {+1, -1}};

        int dir = startDir;
        
        for (int i = 0; i < 7; i++) {
            int x = pt.x + delta[dir][0];
            int y = pt.y + delta[dir][1];
            if (dataMap.apply(x, y) == BACKGROUND) {
                labelArray.set(x, y, -1); // mark surrounding background pixels
                dir = (dir + 1) % 8;
            } else { // found non-background pixel
                pt.x = x;
                pt.y = y;
                break;
            }
        }
        return dir;
    }

    private void findAllContours() {
        int label = 0; // current label
        int maxLabel = 0;
        
        // scan top to bottom, left to right
        for (int v = 0; v < height; v++) {
            label = 0; // no label
            for (int u = 0; u < width; u++) {

                if (dataMap.apply(u, v) == FOREGROUND) {
                    if (label != 0) { // keep using same label
                        labelArray.set(u, v, label);
                    } else {
                        label = labelArray.get(u, v);
                        if (label == 0) { // unlabeled - new outer contour
                            maxLabel++;
                            label = maxLabel;
                            Contour oc = traceOuterContour(u, v, label);
                            outerContours.add(oc);
                            labelArray.set(u, v, label);
                        }
                    }
                } else {            // BACKGROUND pixel
                    if (label != 0) {
                        if (labelArray.get(u, v) == 0) { // unlabeled - new inner
                                                     // contour
                            Contour ic = traceInnerContour(u - 1, v, label);
                            innerContours.add(ic);
                        }
                        label = 0;
                    }
                }
            }
        }
    }
}
