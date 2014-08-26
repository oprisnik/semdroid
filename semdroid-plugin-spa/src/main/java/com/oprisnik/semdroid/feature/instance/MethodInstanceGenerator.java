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
import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.utils.SparseIntHistogram;

import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;
import at.tuflowgraphy.semantic.base.domain.metadata.DObjectLinkMetaData;

/**
 * Adds features of a given method to an instance object.
 */
public abstract class MethodInstanceGenerator extends BasicInstanceGenerator {

    public InstanceDataElement getMethodInstance(DexMethod method,
                                                 boolean addLabels) {
        // create an instance
        InstanceDataElement instanceDataElement = new InstanceDataElement();
        DObjectLinkMetaData metaData = new DObjectLinkMetaData();
        metaData.setBasicInformation(method.toString());

        if (needsLabelableObjectLinks())
            metaData.setLinkedObject(method);

        instanceDataElement.setMetaData(metaData);
        getMethodInstance(method, instanceDataElement);

        if (addLabels) {
            if (method.getLabels().size() > 0) {
                for (Label label : method.getLabels()) {
                    addLabel(instanceDataElement, label.getName());
                }
            } else {
                addLabel(instanceDataElement, Label.NULL);
            }
        }
        return instanceDataElement;
    }

    public double[] getOpcodeHistogram(DexMethod method, boolean normalized) {
        SparseIntHistogram histo = method.getOpcodeHistogram(getMethodCallInclusionDepth());
        return getOpcodeHistogram(histo, normalized);
    }

    public double[] getBasicLocalVarHistogram(DexMethod method, boolean normalize) {
        return getBasicLocalVarHistogram(method.getLocalVariables(getMethodCallInclusionDepth()), normalize);
    }

    /**
     * Adds the features for the given method to the results Instance element.
     * The InstanceDataElement is already set up, only the features have to be
     * added.
     *
     * @param method  method to use
     * @param results resulting InstanceDataElement to use
     */
    protected abstract void getMethodInstance(DexMethod method,
                                              InstanceDataElement results);


}
