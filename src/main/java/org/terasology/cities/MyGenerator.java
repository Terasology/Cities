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

package org.terasology.cities;

import java.awt.Shape;
import java.util.Set;

import org.terasology.cities.model.City;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sector.Orientation;
import org.terasology.cities.model.Sectors;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.ChunkBrush;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.raster.standard.CityRasterizer;
import org.terasology.cities.raster.standard.RoadRasterizer;
import org.terasology.cities.terrain.CachingHeightMap;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.math.TeraMath;
import org.terasology.math.Vector2i;
import org.terasology.world.chunks.Chunk;

import com.google.common.collect.Sets;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class MyGenerator {
    private BlockTheme theme = new BlockTheme();
    private WorldFacade facade;
    private HeightMap heightMap;

    public MyGenerator(String worldSeed, HeightMap heightMap) {
        this.heightMap = heightMap;
        facade = new WorldFacade(worldSeed, heightMap);

        theme.register(BlockTypes.ROAD_SURFACE, "core:Gravel");
        theme.register(BlockTypes.LOT_EMPTY, "core:dirt");
        theme.register(BlockTypes.BUILDING_WALL, "Cities:stonawall1");
        theme.register(BlockTypes.BUILDING_FLOOR, "Cities:stonawall1dark");
        theme.register(BlockTypes.BUILDING_FOUNDATION, "core:gravel");
        theme.register(BlockTypes.ROOF_FLAT, "Cities:rooftiles1");
        theme.register(BlockTypes.ROOF_HIP, "Cities:wood3");
        theme.register(BlockTypes.ROOF_SADDLE, "Cities:wood3");
        theme.register(BlockTypes.ROOF_DOME, "core:plank");
        theme.register(BlockTypes.ROOF_GABLE, "core:plank");
    }

    /**
     * @param chunk the chunk to generate
     */
    public void writeChunk(Chunk chunk) {
        int wx = chunk.getBlockWorldPosX(0);
        int wz = chunk.getBlockWorldPosZ(0);

        int sx = (int) TeraMath.fastFloor((double) wx / Sector.SIZE);
        int sz = (int) TeraMath.fastFloor((double) wz / Sector.SIZE);
        Sector sector = Sectors.getSector(new Vector2i(sx, sz));

        Brush brush = new ChunkBrush(chunk, theme);

        CachingHeightMap cachedHm = new CachingHeightMap(brush.getAffectedArea(), heightMap);
        TerrainInfo ti = new TerrainInfo(cachedHm); 
        
        drawCities(sector, ti, brush);
        drawRoads(sector, ti, brush);
    }

    private void drawRoads(Sector sector, TerrainInfo ti, Brush brush) {
        Shape roadArea = facade.getRoadArea(sector);

        RoadRasterizer rr = new RoadRasterizer();
        rr.raster(brush, ti, roadArea);
    }

    private void drawCities(Sector sector, TerrainInfo ti, Brush brush) {
        Set<City> cities = Sets.newHashSet(facade.getCities(sector));

        for (Orientation dir : Orientation.values()) {
            cities.addAll(facade.getCities(sector.getNeighbor(dir)));
        }

        CityRasterizer cr = new CityRasterizer();

        for (City city : cities) {
            cr.raster(brush, ti, city);
        }
    }

}
