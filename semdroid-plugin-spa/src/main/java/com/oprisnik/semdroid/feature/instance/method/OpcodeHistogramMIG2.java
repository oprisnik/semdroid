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

package com.oprisnik.semdroid.feature.instance.method;

import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.feature.instance.MethodInstanceGenerator;
import com.oprisnik.semdroid.utils.SparseIntHistogram;

import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;

/**
 * Method instance generator that uses opcodes represented as a histogram.
 */
public class OpcodeHistogramMIG2 extends MethodInstanceGenerator {

    @Override
    protected void getMethodInstance(DexMethod method,
                                     InstanceDataElement results) {
        SparseIntHistogram histo = method.getOpcodeHistogram(getMethodCallInclusionDepth());
        // apply grouping and get double array
        SparseIntHistogram groupHisto = getOpcodeGroupHistogram(histo);
        double[] opcodes = getNormedOpcodeArray(groupHisto);
//        Log.d(TAG,  method.getLabels().toString()+ " Opcodes: " + Arrays.toString(opcodes));
        results.addValue(getDistanceBasedFeatureDataElement("opcodes",
                opcodes));
        results.addValue(getDistanceBasedFeatureDataElement("opcodeCount", groupHisto.getValueCount()));
    }

}
