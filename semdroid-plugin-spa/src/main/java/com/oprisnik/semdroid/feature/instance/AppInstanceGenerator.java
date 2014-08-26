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

package com.oprisnik.semdroid.feature.instance;

import com.oprisnik.semdroid.analysis.results.Label;
import com.oprisnik.semdroid.app.App;

import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;
import at.tuflowgraphy.semantic.base.domain.metadata.DObjectLinkMetaData;

/**
 * Adds features of a given app to an instance object.
 */
public abstract class AppInstanceGenerator extends BasicInstanceGenerator {

    public InstanceDataElement getAppInstance(App app, boolean addLabels) {
        InstanceDataElement instanceDataElement = new InstanceDataElement();
        DObjectLinkMetaData metaData = new DObjectLinkMetaData();
        metaData.setBasicInformation(app.getApkFileName() + " " + app.getName());
        instanceDataElement.setMetaData(metaData);
        if (needsLabelableObjectLinks())
            metaData.setLinkedObject(app);

        getAppInstance(app, instanceDataElement);

        if (addLabels) {
            if (app.getLabels().size() > 0) {
                for (Label label : app.getLabels()) {
                    addLabel(instanceDataElement, label.getName());
                }
            } else {
                addLabel(instanceDataElement, Label.NULL);
            }
        }
        return instanceDataElement;
    }

    /**
     * Adds the features for the given app to the results Instance element. The
     * InstanceDataElement is already set up, only the features have to be
     * added.
     *
     * @param app     application to use
     * @param results resulting InstanceDataElement to use
     */
    protected abstract void getAppInstance(App app, InstanceDataElement results);

}
