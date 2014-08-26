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
import com.oprisnik.semdroid.app.LocalVariable;
import com.oprisnik.semdroid.app.MethodCall;
import com.oprisnik.semdroid.feature.instance.MethodInstanceGenerator;
import com.oprisnik.semdroid.utils.DexUtils;

import java.util.HashSet;
import java.util.Set;

import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;

/**
 * Method based feature vector generator for asymmetric cryptography.
 */
public class AsymmetricCryptoMIG extends MethodInstanceGenerator {

    private static final String TAG = "AsymmetricCryptoMIG";

    @Override
    protected void getMethodInstance(DexMethod method,
                                     InstanceDataElement results) {

        double[] histogram = getOpcodeHistogram(method, true);
        results.addValue(getDistanceBasedFeatureDataElement("opcodes",
                histogram));

        Set<String> localVarSet = new HashSet<String>();
        Set<String> invokesSet = new HashSet<String>();

        for (LocalVariable l : method.getLocalVariables(getMethodCallInclusionDepth())) {
            String type = l.getType();
            if (type != null
                    && (DexUtils.isBasicDataType(type) || DexUtils
                    .dataTypeStartsWith(type, "java.math"))) {
                localVarSet.add(type);
            }
        }
        for (MethodCall mc : method.getMethodCalls(getMethodCallInclusionDepth())) {
            if (mc.getClassName().startsWith("java.math")) {
                invokesSet.add(mc.getName());
            }
        }

        for (String classname : invokesSet) {
            results.addValue(getSymbolicFeatureDataElement("invokes", classname));
//            Log.v(TAG, "invokes: " + classname);
        }
        for (String type : localVarSet) {
            results.addValue(getSymbolicFeatureDataElement("localVar", type));
//            Log.v(TAG, "LocalVar: " + type);
        }
    }

}
