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

package org.terasology.cities.raster;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * A registration that controls who is responsible for which class of the model 
 * @author Martin Steiger
 */
public class RasterRegistry {

    private static final Logger logger = LoggerFactory.getLogger(RasterRegistry.class);

    private final Map<Class<?>, Rasterizer<?>> map = Maps.newHashMap();

    /**
     * @param eleClass the domain model element
     * @param rasterizer the rasterizer that converts the element into blocks
     */
    public <T> void register(Class<T> eleClass, Rasterizer<? super T> rasterizer) {
        if (map.containsKey(eleClass)) {
            logger.warn("Class {} already rasterized by {} - overriding...", eleClass, map.get(eleClass));
        }

        map.put(eleClass, rasterizer);
    }

    /**
     * @param obj the object that should be rasterized
     * @return the rasterizer
     */
    public <T> Optional<Rasterizer<T>> getRasterizer(T obj) {
        if (obj == null) {
            logger.warn("Object is null -- skipping");
            return Optional.absent();
        }

        return getRasterizer((Class<T>) obj.getClass());
    }

    /**
     * A convenience wrapper
     * @param brush the brush to use
     * @param ti the terrain info
     * @param obj the object to rasterize
     */
    public <T> void rasterize(Brush brush, TerrainInfo ti, T obj) {
        Optional<Rasterizer<T>> opt = getRasterizer(obj);
        if (opt.isPresent()) {
            Rasterizer<T> r = opt.get();
            r.raster(brush, ti, obj);
        } else {
            logger.debug("No rasterizer found for object {} -- skipping", obj);
        }
    }

    /**
     * @param objClass the class of the object that should be rasterized
     * @return the rasterizer, if available
     */
    public <T> Optional<Rasterizer<T>> getRasterizer(Class<T> objClass) {
        Rasterizer<T> raster = (Rasterizer<T>) map.get(objClass);

        return Optional.fromNullable(raster);
    }
}
