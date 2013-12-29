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
