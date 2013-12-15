/*
 * Copyright (C) 2012-2013 Martin Steiger
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

package nexus.model.raster;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * A registration that controls who is responsible for which class of the model 
 * @author Martin Steiger
 */
public class RasterRegistration
{
	private static final Logger logger = LoggerFactory.getLogger(RasterRegistration.class);
	
	private final Map<Class<?>, Class<? extends Rasterizer<?>>> map = Maps.newHashMap();
	
	/**
	 * @param eleClass the domain model element
	 * @param rasterClass the rasterizer that converts the element into blocks
	 */
	public <T> void register(Class<T> eleClass, Class<? extends Rasterizer<T>> rasterClass) {
		if (map.containsKey(eleClass)) {
			logger.warn("Class {} already rasterized by {} - overriding...", eleClass, map.get(eleClass));
		}
				
		map.put(eleClass, rasterClass);
	}
	

	/**
	 * @param obj the object that should be rasterized
	 * @return the rasterizer
	 */
	public <T> Optional<Rasterizer<T>> getRasterizer(T obj)
	{
		if (obj == null) {
			logger.warn("Object is null -- skipping");
			return Optional.absent();
		}
			
		return getRasterizer((Class<T>)obj.getClass());
	}

	/**
	 * @param objClass the class of the object that should be rasterized
	 * @return the rasterizer
	 */
	public <T> Optional<Rasterizer<T>> getRasterizer(Class<T> objClass) {
		Class<Rasterizer<T>> clz = (Class<Rasterizer<T>>) map.get(objClass);
		if (clz == null) {
			logger.warn("No match found for {}", objClass);
			return Optional.absent();
		}
		
		Rasterizer<T> instance;
		
		try
		{
			instance = clz.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			logger.warn("Could not instantiate " + clz + "  - does it have a public default contructor?", e);
			return Optional.absent();
		}

		return Optional.of(instance);
	}
}
