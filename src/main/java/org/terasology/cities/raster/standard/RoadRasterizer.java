/*
 * Copyright 2013 MovingBlocks
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

package org.terasology.cities.raster.standard;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.List;

import javax.vecmath.Point2i;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.common.PathUtils;
import org.terasology.cities.model.Road;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;

import com.google.common.collect.Lists;

/**
 * Draws road shapes on the terrain surface
 * @author Martin Steiger
 */
public class RoadRasterizer implements Rasterizer<Road> {

    @Override
    public void raster(Brush brush, TerrainInfo ti, Road road) {
        List<Point2i> pts = Lists.newArrayList(road.getPoints());

        pts.add(0, road.getStart().getCoords());
        pts.add(road.getEnd().getCoords());

        Path2D path = PathUtils.createSegmentPath(pts);

        float strokeWidth = (float) road.getWidth();
            
        int cap = BasicStroke.CAP_ROUND;    // end of path
        int join = BasicStroke.JOIN_ROUND;  // connected path segments
        BasicStroke thick = new BasicStroke(strokeWidth, cap, join);

        Shape shape = thick.createStrokedShape(path);
        
        brush.fillShape(shape, ti.getHeightMap(), 1, BlockTypes.ROAD_SURFACE);
    }

}
