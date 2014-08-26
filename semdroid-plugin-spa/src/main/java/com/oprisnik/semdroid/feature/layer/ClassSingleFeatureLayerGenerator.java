/*
 * Copyright 2014 Alexander Oprisnik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oprisnik.semdroid.feature.layer;

import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.DexClass;

import at.tuflowgraphy.semantic.base.domain.data.DatasetDataElement;

/**
 * Single feature layer generator for classes.
 */
public class ClassSingleFeatureLayerGenerator extends
        BasicFeatureLayerGenerator {

    @Override
    public void generateFeatureLayers(App app, DatasetDataElement results) {
        for (DexClass c : app.getClasses()) {
            if (shouldAddClass(c)) {
                results.addInstance(getClassInstance(c));
            }
        }
    }

    @Override
    public boolean needsMethodInstanceGenerator() {
        return false;
    }

    @Override
    public boolean needsClassInstanceGenerator() {
        return true;
    }

    @Override
    public boolean needsAppInstanceGenerator() {
        return false;
    }

}
