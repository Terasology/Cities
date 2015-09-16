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

package org.terasology.cities.bldg.gen;

import java.util.Set;

import org.terasology.cities.bldg.Building;
import org.terasology.cities.parcels.Parcel;
import org.terasology.cities.surface.InfiniteSurfaceHeightFacet;

/**
 *
 */
public interface BuildingGenerator {

    Set<Building> generate(Parcel parcel, InfiniteSurfaceHeightFacet heightFacet);

}
