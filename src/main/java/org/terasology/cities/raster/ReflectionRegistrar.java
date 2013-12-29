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

import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers all {@link Rasterizer}s of a given 
 * package at the {@link RasterRegistry}
 * @deprecated Modules are not allowed to use reflection if they ever do add this back again
 * @author Martin Steiger
 */
@Deprecated
public class ReflectionRegistrar {

    private static final Logger logger = LoggerFactory.getLogger(ReflectionRegistrar.class);

    private RasterRegistry rasterizer;

    /**
     * @param rasterizer the registration
     */
    public ReflectionRegistrar(RasterRegistry rasterizer) {
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
        ConfigurationBuilder builder = new ConfigurationBuilder().setScanners(new TypeAnnotationsScanner());

        builder.addClassLoader(loader).addUrls(ClasspathHelper.forPackage(pkg.getName(), loader));
        Reflections reflections = new Reflections(builder);

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Rasterizes.class);

        for (Class<?> clz : classes) {
            Rasterizes r = clz.getAnnotation(Rasterizes.class);
            Class<?> target = r.target();

            @SuppressWarnings("unchecked")
            Class<Rasterizer<T>> typedRaster = (Class<Rasterizer<T>>) clz;

            @SuppressWarnings("unchecked")
            Class<T> typedTarget = (Class<T>) target;

            try {
                Rasterizer<T> raster = typedRaster.newInstance();
                rasterizer.register(typedTarget, raster);

                logger.debug("Registering {} for {}", clz, target);
            } catch (InstantiationException | IllegalAccessException e) {
                logger.warn("Could not instantiate " + clz + "  - does it have a public default contructor?", e);
            }

        }

    }

}
