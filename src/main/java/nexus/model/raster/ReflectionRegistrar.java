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

import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers all {@link Rasterizer}s of a given 
 * package at the {@link RasterRegistration}
 * @author Martin Steiger
 */
public class ReflectionRegistrar
{
	private static final Logger logger = LoggerFactory.getLogger(ReflectionRegistrar.class);
	
	private RasterRegistration rasterizer;

	/**
	 * @param rasterizer the registration
	 */
	public ReflectionRegistrar(RasterRegistration rasterizer)
	{
		this.rasterizer = rasterizer;
	}

	/**
	 * @param exampleClass one class from the package that should be added
	 */
	public <T> void registerPackageOfClass(Class<?> exampleClass) {
		registerPackage(exampleClass.getPackage(), exampleClass.getClassLoader());
	}

	/**
	 * @param pkg the package that should be searched
	 * @param loader the class loader for the package
	 */
	public <T> void registerPackage(Package pkg, ClassLoader loader) {
		ConfigurationBuilder builder = new ConfigurationBuilder().setScanners(
				new TypeAnnotationsScanner());

		builder.addClassLoader(loader).addUrls(ClasspathHelper.forPackage(pkg.getName(), loader));
		Reflections reflections = new Reflections(builder);
		
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Rasterizes.class);
		
		for (Class<?> clz : classes) {
			Rasterizes r = clz.getAnnotation(Rasterizes.class);
			Class<?> target = r.target();

			@SuppressWarnings("unchecked")
			Class<Rasterizer<T>> typedRaster = (Class<Rasterizer<T>>)clz;
			
			@SuppressWarnings("unchecked")
			Class<T> typedTarget = (Class<T>) target;
			
			rasterizer.register(typedTarget, typedRaster);
			
			logger.debug("Registering {} for {}", clz, target);
		}
		
	}

}
