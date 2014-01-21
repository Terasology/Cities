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
import java.awt.geom.Line2D;
import java.util.List;

import javax.vecmath.Point2i;
import javax.vecmath.Point3d;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.common.Plane2d;
import org.terasology.cities.model.Road;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMapAdapter;
import org.terasology.cities.terrain.HeightMaps;
import org.terasology.math.TeraMath;

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

        float strokeWidth = (float) road.getWidth();
        int cap = BasicStroke.CAP_ROUND;    // end of path
        int join = BasicStroke.JOIN_ROUND;  // connected path segments
        BasicStroke thick = new BasicStroke(strokeWidth, cap, join);

        for (int i = 0; i < pts.size() - 1; i++) {
            Point2i p0 = pts.get(i + 0);
            Point2i p1 = pts.get(i + 1);

            Line2D line = new Line2D.Double(p0.x, p0.y, p1.x, p1.y);
            Shape shape = thick.createStrokedShape(line);

            Point3d start = new Point3d(p0.x, p0.y, ti.getHeightMap().apply(p0.x, p0.y));
            Point3d end = new Point3d(p1.x, p1.y, ti.getHeightMap().apply(p1.x, p1.y));
            
            final Plane2d plane = new Plane2d(start, end);
            HeightMap hm = new HeightMapAdapter() {
                
                @Override
                public int apply(int x, int z) {
                    return  TeraMath.ceilToInt(plane.getZ(x, z));
                }
            };
            
            // clear area above floor level
            brush.fillShape(shape, hm, HeightMaps.offset(ti.getHeightMap(), 1), BlockTypes.AIR);
            brush.fillShape(shape, hm, 1, BlockTypes.ROAD_SURFACE);
//
//            brush.fillShape(shape, ti.getHeightMap(), hm, BlockTypes.ROAD_SURFACE);
        }
    }

}
