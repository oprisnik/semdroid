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
import com.oprisnik.semdroid.app.DexMethod;

import at.tuflowgraphy.semantic.base.domain.data.DatasetDataElement;
import at.tuflowgraphy.semantic.base.domain.metadata.DSimpleStringMetaData;

/**
 * Feature layer generator with multiple layers (methods, classes, apps).
 */
public class MultiFeatureLayerGenerator extends BasicFeatureLayerGenerator {

    @Override
    public void generateFeatureLayers(App app, DatasetDataElement results) {
        //First layer: methods
        for (DexClass c : app.getClasses()) {
            if (shouldAddClass(c)) {
                for (DexMethod m : c.getMethods()) {
                    if (shouldAddMethod(m)) {
                        results.addInstance(getMethodInstance(m));
                    }
                }
            }
        }

        //Second layer: classes

        DatasetDataElement classes = results.getNextDataSet();
        if (classes == null) {
            // no next DatasetDataElement defined => create
            classes = new DatasetDataElement();
            DSimpleStringMetaData metaData = new DSimpleStringMetaData();
            metaData.setBasicInformation("Classes");
            classes.setName("Classes"); // not really needed...
            classes.setMetaData(metaData);
            results.setNextDataSet(classes);
        }

        for (DexClass c : app.getClasses()) {
            if (shouldAddClass(c)) {
                classes.addInstance(getClassInstance(c));
            }
        }

        //Third layer: apps

        DatasetDataElement apps = classes.getNextDataSet();
        if (apps == null) {
            // no next DatasetDataElement defined => create
            apps = new DatasetDataElement();
            DSimpleStringMetaData metaData = new DSimpleStringMetaData();
            metaData.setBasicInformation("Apps");
            apps.setName("Apps"); // not really needed...
            apps.setMetaData(metaData);
            classes.setNextDataSet(apps);
        }

        apps.addInstance(getAppInstance(app));
    }

    @Override
    public boolean needsMethodInstanceGenerator() {
        return true;
    }

    @Override
    public boolean needsClassInstanceGenerator() {
        return true;
    }

    @Override
    public boolean needsAppInstanceGenerator() {
        return true;
    }

}
