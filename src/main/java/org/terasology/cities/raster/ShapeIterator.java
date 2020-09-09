// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.raster;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that runs along a shape using the flattening path iterator, generating Line2D elements.
 */
public class ShapeIterator implements Iterable<Line2D> {

    private final Shape shape;
    private final double shapeFlatness;

    /**
     * @param shape The shape
     * @param shapeFlatness The flatness of the shape, that will be passed to the {@link
     *         Shape#getPathIterator(java.awt.geom.AffineTransform, double)} method
     */
    public ShapeIterator(Shape shape, double shapeFlatness) {
        this.shape = shape;
        this.shapeFlatness = shapeFlatness;
    }

    /**
     * Returns an Iterable over the segments of the given shape
     */
    @Override
    public Iterator<Line2D> iterator() {
        return new SegmentIterator(shape, shapeFlatness);
    }

    /**
     * Utility class that allows iterating over the segments of a shape
     */
    private static final class SegmentIterator implements Iterator<Line2D> {
        /**
         * The (flattening) path iterator for the shape
         */
        private final PathIterator pi;

        /**
         * Space for the coordinates of the path iterator
         */
        private final double[] coords = new double[6];

        /**
         * The the last point visited with SEG_MOVETO
         */
        private final Point2D firstPoint = new Point2D.Double();

        /**
         * The previous visited point
         */
        private final Point2D previousPoint = new Point2D.Double();

        /**
         * The next segment that will be returned
         */
        private Line2D nextSegment;

        /**
         * Creates a new SegmentIterator for the given shape
         *
         * @param shape The shape
         * @param shapeFlatness The flatness of the shape, that will be passed to the {@link
         *         Shape#getPathIterator(java.awt.geom.AffineTransform, double)} method
         */
        private SegmentIterator(Shape shape, double shapeFlatness) {
            pi = shape.getPathIterator(null, shapeFlatness);
            nextSegment = computeNext();
        }

        /**
         * Compute the next segment
         *
         * @return The next segment, or <code>null</code> if there are no more segments
         */
        private Line2D computeNext() {
            Line2D result = null;
            if (!pi.isDone()) {
                int type = pi.currentSegment(coords);
                if (type == PathIterator.SEG_MOVETO) {
                    firstPoint.setLocation(coords[0], coords[1]);
                    previousPoint.setLocation(firstPoint);
                    pi.next();

                    if (!pi.isDone()) {
                        int nextType = pi.currentSegment(coords);
                        if (nextType == PathIterator.SEG_LINETO) {
                            result = new Line2D.Double(previousPoint.getX(), previousPoint.getY(), coords[0],
                                    coords[1]);
                            previousPoint.setLocation(coords[0], coords[1]);
                        }
                        pi.next();
                    }
                } else if (type == PathIterator.SEG_LINETO) {
                    result = new Line2D.Double(previousPoint.getX(), previousPoint.getY(), coords[0], coords[1]);
                    previousPoint.setLocation(coords[0], coords[1]);
                    pi.next();
                } else if (type == PathIterator.SEG_CLOSE) {
                    result = new Line2D.Double(previousPoint.getX(), previousPoint.getY(), firstPoint.getX(),
                            firstPoint.getY());
                    previousPoint.setLocation(firstPoint.getX(), firstPoint.getY());
                    pi.next();
                }
            }
            return result;
        }

        @Override
        public boolean hasNext() {
            return nextSegment != null;
        }

        @Override
        public Line2D next() {
            if (nextSegment == null) {
                throw new NoSuchElementException("No more elements");
            }
            Line2D result = nextSegment;
            nextSegment = computeNext();
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("May not remove elements with this iterator");

        }

    }

}
