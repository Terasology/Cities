/*
 * Copyright 2015 MovingBlocks
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

package org.terasology.cities.walls;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.bldg.BuildingFacet;
import org.terasology.cities.bldg.BuildingPart;
import org.terasology.cities.bldg.RectBuildingPart;
import org.terasology.cities.bldg.SimpleTower;
import org.terasology.cities.bldg.Tower;
import org.terasology.cities.blocked.BlockedAreaFacet;
import org.terasology.cities.common.Edges;
import org.terasology.cities.door.Door;
import org.terasology.cities.door.DoorFacet;
import org.terasology.cities.door.WingDoor;
import org.terasology.cities.roof.RoofFacet;
import org.terasology.cities.sites.Site;
import org.terasology.cities.sites.SiteFacet;
import org.terasology.cities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.cities.terrain.BuildableTerrainFacet;
import org.terasology.cities.window.RectWindow;
import org.terasology.cities.window.SimpleWindow;
import org.terasology.cities.window.Window;
import org.terasology.cities.window.WindowFacet;
import org.terasology.commonworld.Orientation;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Circle;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.Updates;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

/**
 * Generates a {@link TownWall} around a given settlement
 * while respecting blocked areas
 */
@Produces(TownWallFacet.class)
@Updates(@Facet(BuildingFacet.class)) // TODO: find out why specifying door/window/roof does not work
@Requires({
    @Facet(SiteFacet.class),
    @Facet(BlockedAreaFacet.class),
    @Facet(BuildableTerrainFacet.class),
    @Facet(InfiniteSurfaceHeightFacet.class)})
public class TownWallFacetProvider implements FacetProvider {

    private static final Logger logger = LoggerFactory.getLogger(TownWallFacetProvider.class);

    private final Cache<Site, Optional<TownWall>> cache = CacheBuilder.newBuilder().build();

    private long seed;

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public void process(GeneratingRegion region) {
        InfiniteSurfaceHeightFacet heightFacet = region.getRegionFacet(InfiniteSurfaceHeightFacet.class);
        TownWallFacet wallFacet = new TownWallFacet(region.getRegion(), region.getBorderForFacet(TownWallFacet.class));
        SiteFacet siteFacet = region.getRegionFacet(SiteFacet.class);
        BlockedAreaFacet blockedAreaFacet = region.getRegionFacet(BlockedAreaFacet.class);
        BuildableTerrainFacet buildableAreaFacet = region.getRegionFacet(BuildableTerrainFacet.class);
        BuildingFacet buildingFacet = region.getRegionFacet(BuildingFacet.class);
        WindowFacet windowFacet = region.getRegionFacet(WindowFacet.class);
        RoofFacet roofFacet = region.getRegionFacet(RoofFacet.class);
        DoorFacet doorFacet = region.getRegionFacet(DoorFacet.class);

        for (Site site : siteFacet.getSettlements()) {
            if (Circle.intersects(site.getPos(), site.getRadius(), wallFacet.getWorldRegion())) {
                try {
                    Optional<TownWall> opt = cache.get(site, () -> generate(site, heightFacet, buildableAreaFacet, blockedAreaFacet));
                    if (opt.isPresent()) {
                        wallFacet.addTownWall(opt.get());
                        for (Tower tower : opt.get().getTowers()) {
                            buildingFacet.addBuilding(tower);

                            // TODO: add bounds check
                            for (BuildingPart part : tower.getParts()) {
                                for (Window wnd : part.getWindows()) {
                                    windowFacet.addWindow(wnd);
                                }
                                for (Door door : part.getDoors()) {
                                    doorFacet.addDoor(door);
                                }
                                roofFacet.addRoof(part.getRoof());
                            }
                        }
                    }
                } catch (ExecutionException e) {
                    logger.error("Could not compute buildings for {}", region.getRegion(), e);
                }
            }
        }

        region.setRegionFacet(TownWallFacet.class, wallFacet);
    }

    private Optional<TownWall> generate(Site city, InfiniteSurfaceHeightFacet heightFacet, BuildableTerrainFacet buildableAreaFacet, BlockedAreaFacet blockedAreaFacet) {
        int minRadForTownWall = 150;
        if (city.getRadius() < minRadForTownWall) {
            return Optional.empty();
        }

        Random rand = new FastRandom(seed ^ city.getPos().hashCode());

        int cx = city.getPos().getX();
        int cz = city.getPos().getY();

        Vector2i center = new Vector2i(cx, cz);

        int maxWallThick = 4;
        float maxRad = city.getRadius() - maxWallThick * 0.5f;


        TownWall tw = new TownWall();

        double step = Math.toRadians(5);

        // generate towers first
        List<Vector2i> tp = getTowerPosList(rand, center, maxRad, step);
        List<Boolean> blocked = getBlockedPos(tp, buildableAreaFacet, blockedAreaFacet);
        SimpleTower lastTower = null;
        SimpleTower firstTower = null;
        int lastHit = Short.MIN_VALUE;
        int firstHit = Short.MIN_VALUE;

        for (int i = 0; i < tp.size(); i++) {

            boolean thisOk = blocked.get(i);
            boolean nextOk = blocked.get((i + 1) % tp.size());
            boolean prevOk = blocked.get((i + tp.size() - 1) % tp.size());

            if (thisOk) {
                Vector2i towerPos = tp.get(i);
                Vector2i dirFromCenter = new Vector2i(center).sub(towerPos);
                Orientation orient = orientationFromDirection(dirFromCenter);

                if (!prevOk) {                          // the previous location was blocked -> this is the second part of a gate
                    SimpleTower tower = createGateTower(orient, heightFacet, tp.get(i));
                    tw.addTower(tower);

                    if (firstHit < 0) {
                        firstHit = i;
                        firstTower = tower;
                    }

                    if (lastHit >= 0) {
                        Vector2i start = getAnchor(lastTower, -90);
                        Vector2i end = getAnchor(tower, 90);
                        tw.addWall(createGateWall(start, end));
                    }
                    lastHit = i;
                    lastTower = tower;
                } else

                if (!nextOk) {                          // the next location is blocked -> this is the first part of a gate
                    SimpleTower tower = createGateTower(orient, heightFacet, tp.get(i));
                    tw.addTower(tower);

                    if (firstHit < 0) {
                        firstHit = i;
                        firstTower = tower;
                    }
                    if (lastHit >= 0) {
                        Vector2i start = getAnchor(lastTower, -90);
                        Vector2i end = getAnchor(tower, 90);

                        tw.addWall(createSolidWall(start, end));
                    }
                    lastHit = i;
                    lastTower = tower;
                } else

                if (i - lastHit > 5 && tp.size() + i - firstHit > 5) {          // the last/next tower is n segments away -> place another one
                    SimpleTower tower = createTower(orient, heightFacet, tp.get(i));
                    tw.addTower(tower);

                    if (firstHit < 0) {
                        firstHit = i;
                        firstTower = tower;
                    }

                    if (lastHit >= 0) {
                        Vector2i start = getAnchor(lastTower, -90);
                        Vector2i end = getAnchor(tower, 90);
                        tw.addWall(createSolidWall(start, end));
                    }
                    lastHit = i;
                    lastTower = tower;
                }
            }
        }

        // connect first and last tower to close the circle
        // --> this could be a gate segment
        // --> TODO: find out
        if (firstHit >= 0 && lastHit >= 0) {
            Vector2i start = getAnchor(lastTower, -90);
            Vector2i end = getAnchor(firstTower, 90);
            tw.addWall(createSolidWall(start, end));
        }

        return Optional.of(tw);
    }

    private Vector2i getAnchor(SimpleTower tower, int degrees) {
        Orientation orientation = tower.getOrientation();
        Rect2i lastRect = tower.getStaircase().getShape();
        return Edges.getCorner(lastRect, orientation.getRotated(degrees));
    }

    private static Orientation orientationFromDirection(Vector2i d) {
        if (Math.abs(d.x()) >= Math.abs(d.y())) {
            if (d.x() > 0) {
                return Orientation.EAST;
            } else {
                return Orientation.WEST;
            }
        } else {
            if (d.y() > 0) {
                return Orientation.SOUTH;
            } else {
                return Orientation.NORTH;
            }
        }
    }

    private WallSegment createSolidWall(Vector2i start, Vector2i end) {
        int wallThick = 4;
        int wallHeight = 8;

        WallSegment wall = new SolidWallSegment(start, end, wallThick, wallHeight);
        return wall;
    }

    private WallSegment createGateWall(BaseVector2i start, BaseVector2i end) {
        int wallHeight = 8;
        int wallThick = 4;

        WallSegment wall = new GateWallSegment(start, end, wallThick, wallHeight);
        return wall;
    }

    private List<Boolean> getBlockedPos(List<Vector2i> tp, BuildableTerrainFacet buildable, BlockedAreaFacet blocked) {
        List<Boolean> list = Lists.newArrayList();

        for (Vector2i pos : tp) {
            Rect2i layout = getTowerRect(pos);
            boolean ok = buildable.isBuildable(layout) && !blocked.isBlocked(layout);
            list.add(Boolean.valueOf(ok));
        }

        return list;
    }

    private SimpleTower createTower(int towerHeight, Orientation orient, InfiniteSurfaceHeightFacet hm, Vector2i pos) {
        int baseHeight = TeraMath.floorToInt(hm.getWorld(pos)) + 1;

        Rect2i layout = getTowerRect(pos);
        int stairHalfLoop = layout.width() - 2 + layout.height() - 2 - 2;
        int wndHeight = stairHalfLoop + 3;
        SimpleTower tower = new SimpleTower(orient, layout, baseHeight, towerHeight);
        Vector2i windowPos = new Vector2i(Edges.getCorner(layout, orient));
        tower.getStaircase().addWindow(new SimpleWindow(orient.getOpposite(), windowPos, baseHeight + wndHeight));
        return tower;
    }

    private SimpleTower createTower(Orientation orient, InfiniteSurfaceHeightFacet hm, Vector2i towerPos) {
        int towerHeight = 9;
        SimpleTower tower = createTower(towerHeight, orient, hm, towerPos);
        RectBuildingPart staircase = tower.getStaircase();
        Rect2i staircaseRect = staircase.getShape().expand(-1, -1); // inner staircase
        Vector2i c1 = Edges.getCorner(staircaseRect, orient.getRotated(180 - 45));
        Vector2i c2 = Edges.getCorner(staircaseRect, orient.getRotated(180 + 45));
        Rect2i doorRect = Rect2i.createEncompassing(c1, c2);
        int roofLevel = staircase.getTopHeight();
        staircase.addDoor(new WingDoor(orient.getOpposite(), doorRect, roofLevel, roofLevel + 1));
        return tower;
    }

    private SimpleTower createGateTower(Orientation orient, InfiniteSurfaceHeightFacet hm, Vector2i towerPos) {
        int towerHeight = 10;
        SimpleTower tower = createTower(towerHeight, orient, hm, towerPos);
        return tower;
    }

    private List<Vector2i> getTowerPosList(Random rand, Vector2i center, double maxRad, double step) {

        double maxRadiusDiv = maxRad * 0.1;

        double maxAngularDiv = step * 0.1;

        List<Vector2i> list = Lists.newArrayList();

        double ang = rand.nextDouble(0, Math.PI);   // actually, the range doesn't matter
        double endAng = ang + Math.PI * 2.0 - step; // subtract one step to avoid duplicate first/last position

        while (ang < endAng) {
            double rad = maxRad - rand.nextDouble() * maxRadiusDiv;

            int tx = center.x + (int) (rad * Math.cos(ang));
            int ty = center.y + (int) (rad * Math.sin(ang));

            list.add(new Vector2i(tx, ty));

            double angDiv = (rand.nextDouble() - 0.5) * maxAngularDiv;
            ang += step + angDiv;
       }

        return list;
    }

    private Rect2i getTowerRect(Vector2i tp) {
        int towerRad = 3;

        // make sure the width/height are odd to make the BattlementRoof look pretty
        return Rect2i.createFromMinAndSize(tp.x - towerRad, tp.y - towerRad, towerRad * 2 - 1, towerRad * 2 - 1);

    }
}
