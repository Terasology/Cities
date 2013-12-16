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

package org.terasology.world.generator.city;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Set;

import org.terasology.math.TeraMath;
import org.terasology.math.Vector2i;
import org.terasology.math.Vector3i;
import org.terasology.world.block.Block;
import org.terasology.world.chunks.Chunk;
import org.terasology.world.generator.city.model.City;
import org.terasology.world.generator.city.model.HipRoof;
import org.terasology.world.generator.city.model.Sector;
import org.terasology.world.generator.city.model.Sectors;
import org.terasology.world.generator.city.model.SimpleBuilding;
import org.terasology.world.generator.city.model.SimpleLot;
import org.terasology.world.generator.city.model.Sector.Orientation;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import nexus.model.raster.RasterRegistration;
import nexus.model.raster.Rasterizer;
import nexus.model.raster.ReflectionRegistrar;
import nexus.model.raster.TeraBrush;
import nexus.model.raster.standard.Dummy;
import nexus.model.raster.standard.HipRoofRasterizer;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class MyGenerator
{
    private RasterRegistration rasterizer = new RasterRegistration();
    private BlockTypeFunction blockType = new BlockTypeFunction();
    private TeraBrush brush = new TeraBrush(blockType);
	private WorldFacade facade;

    
    public MyGenerator(String worldSeed) {
    	facade = new WorldFacade(worldSeed);

//    	ReflectionRegistrar rr = new ReflectionRegistrar(rasterizer);
//		rr.registerPackageOfClass(Dummy.class);
    	rasterizer.register(HipRoof.class, HipRoofRasterizer.class);
   }


	/**
	 * @param chunk
	 */

    public void writeChunk(Chunk chunk) {
        int wx = chunk.getBlockWorldPosX(0);
		int wz = chunk.getBlockWorldPosZ(0);

        int sx = (int) TeraMath.fastFloor((double) wx / Sector.SIZE);
        int sz = (int) TeraMath.fastFloor((double) wz / Sector.SIZE);
        Sector sector = Sectors.getSector(new Vector2i(sx, sz));
        
        drawRoads(sector, chunk);
        drawCities(sector, chunk);
    }
    
    private void drawRoads(Sector sector, Chunk chunk) {
        int wx = chunk.getBlockWorldPosX(0);
        int wz = chunk.getBlockWorldPosZ(0);

        int chunkSizeX = chunk.getChunkSizeX();
        int chunkSizeZ = chunk.getChunkSizeZ();

        Rectangle2D chunkRect = new Rectangle2D.Double(wx, wz, chunkSizeX, chunkSizeZ);

        Shape roadArea = facade.getRoadArea(sector);
        
        if (!roadArea.intersects(chunkRect)) {
            return;
        }
        
        Function<Vector2i, Integer> heightMap = facade.getHeightMap();
        
        for (int z = 0; z < chunkSizeZ; z++) {
            for (int x = 0; x < chunkSizeX; x++) {
            
                if (roadArea.contains(wx + x, wz + z)) {
					int y = heightMap.apply(new Vector2i(wx + x, wz + z));
                	brush.setBlock(chunk, wx + x, y, wz + z, BlockTypes.ROAD_SURFACE);
                }
            }
        }
    }
    
    
    private void drawCities(Sector sector, Chunk chunk) {
        int wx = chunk.getBlockWorldPosX(0);
        int wz = chunk.getBlockWorldPosZ(0);

        Rectangle2D chunkRect = new Rectangle2D.Double(wx, wz, chunk.getChunkSizeX(), chunk.getChunkSizeZ());
        
        Set<City> cities = Sets.newHashSet(facade.getCities(sector));
        
        for (Orientation dir : Orientation.values()) {
            cities.addAll(facade.getCities(sector.getNeighbor(dir)));
        }
        
        for (City city : cities) {
            Set<SimpleLot> lots = facade.getLots(city);
            
            for (SimpleLot lot : lots) {
                if (lot.getShape().intersects(chunkRect)) {
                    rasterLot(chunk, lot);
                }
            }
        }
    }
    
    private void rasterLot(Chunk chunk, SimpleLot lot)
    {
        int wx = chunk.getBlockWorldPosX(0);
        int wz = chunk.getBlockWorldPosZ(0);
        
        Rectangle rc = lot.getShape();
        
        Function<Vector2i, Integer> heightMap = facade.getHeightMap();

        for (int z = rc.y; z < rc.y + rc.height; z++) {
            for (int x = rc.x; x < rc.x + rc.width; x++) {
                if ((x >= wx && x < wx + chunk.getChunkSizeX()) &&
                    (z >= wz && z < wz + chunk.getChunkSizeZ())) {
                    int y = heightMap.apply(new Vector2i(x, z));
                    brush.setBlock(chunk, x, y, z, BlockTypes.LOT_EMPTY);
                }
            }
        }
        
        for (SimpleBuilding blg : lot.getBuildings()) {
            
            rasterBuilding(chunk, blg);
        }
    }

    private void rasterBuilding(Chunk chunk, SimpleBuilding blg)
    {
        Rectangle rc = blg.getLayout();
        
        int baseHeight = blg.getBaseHeight();
        int wallHeight = blg.getWallHeight();

        brush.clearAbove(chunk, rc, baseHeight);
        
        brush.fill(chunk, rc, baseHeight - 1, baseHeight, BlockTypes.BUILDING_FLOOR);
        brush.fillAirBelow(chunk, rc, baseHeight - 2, BlockTypes.BUILDING_FLOOR);
        
        // wall along z
        brush.createWallZ(chunk, rc.y, rc.y + rc.height, rc.x, baseHeight, wallHeight, BlockTypes.BUILDING_WALL);
        brush.createWallZ(chunk, rc.y, rc.y + rc.height, rc.x + rc.width - 1, baseHeight, wallHeight, BlockTypes.BUILDING_WALL);

        // wall along x
        brush.createWallX(chunk, rc.x, rc.x + rc.width, rc.y, baseHeight, wallHeight, BlockTypes.BUILDING_WALL);
        brush.createWallX(chunk, rc.x, rc.x + rc.width, rc.y + rc.height - 1, baseHeight, wallHeight, BlockTypes.BUILDING_WALL);

        // door
        Rectangle door = blg.getDoor();
        Vector3i doorFrom = new Vector3i(door.x, baseHeight, door.y);
        Vector3i doorTo = new Vector3i(door.x + door.width, baseHeight + blg.getDoorHeight(), door.y + door.height);
        brush.fill(chunk, doorFrom, doorTo, BlockTypes.AIR);
        
        rasterize(chunk, blg.getRoof());
    }

    private <T> void rasterize(Chunk chunk, T obj) {
        Optional<Rasterizer<T>> opt = rasterizer.getRasterizer(obj);
        if (opt.isPresent()) {
            Rasterizer<T> r = opt.get();
            r.raster(chunk, brush, obj);
        }
    }
    
}
