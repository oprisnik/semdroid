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
import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.app.DexMethod;

import java.util.HashSet;
import java.util.Set;

import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;
import at.tuflowgraphy.semantic.base.domain.metadata.DObjectLinkMetaData;

/**
 * Adds features of a given class to an instance object.
 */
public abstract class ClassInstanceGenerator extends BasicInstanceGenerator {

    public InstanceDataElement getClassInstance(DexClass clazz,
                                                boolean addLabels) {

        // create an instance
        InstanceDataElement instanceDataElement = new InstanceDataElement();
        DObjectLinkMetaData metaData = new DObjectLinkMetaData();
        metaData.setBasicInformation(clazz.getFullName());
        if (needsLabelableObjectLinks())
            metaData.setLinkedObject(clazz);

        instanceDataElement.setMetaData(metaData);
        getClassInstance(clazz, instanceDataElement);

        if (addLabels) {
            if (clazz.getLabels().size() > 0) {
                for (Label label : clazz.getLabels()) {
                    addLabel(instanceDataElement, label.getName());
                }
            } else {
                addLabel(instanceDataElement, Label.NULL);
            }
        }

        return instanceDataElement;
    }


    protected Set<String> getPermissions(DexClass clazz) {
        Set<String> data = new HashSet<String>();
        if (getClassFilter().use(clazz)) {
            for (DexMethod method : clazz.getMethods()) {
                if (getMethodFilter().use(method)) {
                    data.addAll(method.getPermissions(getMethodCallInclusionDepth()));
                }
            }
        }
        return data;
    }

    /**
     * Adds the features for the given class to the results Instance element.
     * The InstanceDataElement is already set up, only the features have to be
     * added.
     *
     * @param clazz   class to use
     * @param results resulting InstanceDataElement to use
     */
    protected abstract void getClassInstance(DexClass clazz,
                                             InstanceDataElement results);

}
