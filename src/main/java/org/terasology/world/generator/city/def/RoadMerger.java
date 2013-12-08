/*
 * Copyright 2013 MovingBlocks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.terasology.world.generator.city.def;

import java.util.List;
import java.util.Set;

import javax.vecmath.Point2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.world.generator.city.model.Junction;
import org.terasology.world.generator.city.model.Road;
import org.terasology.world.generator.city.model.Sector;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Split and merges overlapping roads
 * @author "Martin Steiger"
 */
public class RoadMerger {

    private static final Logger logger = LoggerFactory.getLogger(RoadMerger.class);
    
    /**
     * @param roads a set of roads
     * @return a new set of roads with overlapping segment points merged
     */
    public Set<Road> mergeRoads(Set<Road> roads) {
        List<Road> list = Lists.newArrayList(roads);
        Set<Road> newRoads = Sets.newHashSet();
        
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                newRoads.addAll(testAndMerge(list.get(i), list.get(j)));
            }
        }
        
        return newRoads;
    }

    private Set<Road> testAndMerge(Road road1o, Road road2o) {
        Road road1 = road1o;
        Road road2 = road2o;
        
        int start1 = -1;
        int end1 = -1;
        int start2 = -1;
        int end2 = -1;

        for (int i = 0; i < road1.getPoints().size(); i++) {
            Point2d p1 = road1.getPoints().get(i);
            
            for (int j = 0; j < road2.getPoints().size(); j++) {
                Point2d p2 = road2.getPoints().get(j);
                
                if (equals(p1, p2)) {
                    if (start1 == -1) {
                        start1 = i;
                        start2 = j;
                    }
                    
                    end1 = i;
                    end2 = j;
                }
            }
        }
        
        if (start1 == -1) {
//            return Sets.newHashSet(road1, road2);
            return Sets.newHashSet();
        }

        logger.debug("Merge " + start1 + " to " + end1 + "(" + road1.getPoints().size() + ") and " + start2 + " and " + end2 + "(" + road2.getPoints().size() + ")");
    
        if (start1 > end1) {
            int tmp = end1;
            end1 = start1;
            start1 = tmp;
            road1 = new Road(road1.getEnd(), road1.getStart());
        }
        
        if (start2 > end2) {
            int tmp = end2;
            end2 = start2;
            start2 = tmp;
            road2 = new Road(road2.getEnd(), road2.getStart());
        }

        Junction comStart;
        Junction comEnd;
        
        if (road1.getStart().equals(road2.getStart())) {
            comStart = road1.getStart();
        } else {
            comStart = new Junction(road1.getPoints().get(start1)); 
        }
        
        if (road1.getEnd().equals(road2.getEnd())) {
            comEnd = road1.getEnd();
        } else {
            comEnd = new Junction(road1.getPoints().get(start1)); 
        }

        Road commonRoad = new Road(comStart, comEnd);
        commonRoad.setWidth(road1.getWidth() + road2.getWidth());
        
        Set<Road> result = Sets.newHashSet(commonRoad);

        if (road1.getStart().equals(commonRoad.getStart())) {
            Road newRoad1 = new Road(commonRoad.getEnd(), road1.getEnd());

            // add segments from the original road
            for (int i = end1 + 1; i < road1.getPoints().size(); i++) {
                newRoad1.add(road1.getPoints().get(i));
            }
            
            result.add(newRoad1);
        }        

        if (road2.getStart().equals(commonRoad.getStart())) {
            Road newRoad2 = new Road(commonRoad.getEnd(), road2.getEnd());
            
            // add segments from the original road
            for (int i = end2 + 1; i < road2.getPoints().size(); i++) {
                newRoad2.add(road2.getPoints().get(i));
            }

            result.add(newRoad2);
        }        

        if (road1.getEnd().equals(commonRoad.getEnd())) {
            Road newRoad1 = new Road(road1.getStart(), commonRoad.getStart());
            
            // add segments from the original road
            for (int i = 0; i < start1; i++) {
                newRoad1.add(road1.getPoints().get(i));
            }

            result.add(newRoad1);
        }        

        if (road2.getEnd().equals(commonRoad.getEnd())) {
            Road newRoad2 = new Road(road2.getStart(), commonRoad.getStart());

            // add segments from the original road
            for (int i = 0; i < start2; i++) {
                newRoad2.add(road2.getPoints().get(i));
            }

            result.add(newRoad2);
        }        

        return result;
    }

    boolean equals(Point2d p1, Point2d p2) {
        int bx1 = (int) (p1.x * Sector.SIZE);
        int by1 = (int) (p1.y * Sector.SIZE);
        int bx2 = (int) (p2.x * Sector.SIZE);
        int by2 = (int) (p2.y * Sector.SIZE);
        
        return (bx1 == bx2) && (by1 == by2);
    }
}
