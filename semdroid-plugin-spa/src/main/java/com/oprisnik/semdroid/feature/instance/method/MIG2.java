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

import com.oprisnik.semdroid.app.CodeElement;
import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.app.LocalVariable;
import com.oprisnik.semdroid.app.MethodCall;
import com.oprisnik.semdroid.app.Opcode;
import com.oprisnik.semdroid.feature.instance.MethodInstanceGenerator;
import com.oprisnik.semdroid.utils.DexUtils;
import com.oprisnik.semdroid.utils.Log;
import com.oprisnik.semdroid.utils.SparseIntHistogram;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;

/**
 * Method based feature vector generator.
 */
public class MIG2 extends MethodInstanceGenerator {

    private static final String TAG = "MIG2";

    @Override
    protected void getMethodInstance(DexMethod method,
                                     InstanceDataElement results) {

        SparseIntHistogram opcodeHisto = new SparseIntHistogram();
        Set<String> localVarSet = new HashSet<String>();
        Set<String> invokesSet = new HashSet<String>();

        for (CodeElement e : method.getCode()) {
            if (e instanceof Opcode) {
                Opcode op = (Opcode) e;
                opcodeHisto.increase(getOpcodeGroup(op));

            } else if (e instanceof MethodCall) {

                MethodCall mc = (MethodCall) e;
                if (mc.getDexMethod() == null) {
                    // API call
                    // we use class names only:
                    String classname = mc.getClassName();
                    if (classname != null && (classname.startsWith("android") || classname.startsWith("java"))) {
                        invokesSet.add(classname);
                    }
                }

            } else if (e instanceof LocalVariable) {
                LocalVariable l = (LocalVariable) e;
                String type = l.getType();
                if (type != null
                        && (DexUtils.isBasicDataType(type) || DexUtils
                        .dataTypeStartsWith(type, "android") || DexUtils
                        .dataTypeStartsWith(type, "java"))) {
                    localVarSet.add(type);
                }
            } else {
                Log.e(TAG, "Unknown type: " + e.getName());
            }
        }

        double[] allOpGroupHisto = getNormedOpcodeArray(opcodeHisto);

        results.addValue(getDistanceBasedFeatureDataElement("opcodes",
                allOpGroupHisto));
        Log.v(TAG, "opcode histo: " + Arrays.toString(allOpGroupHisto));

        for (String classname : invokesSet) {
            results.addValue(getSymbolicFeatureDataElement("invokes", classname));
            Log.v(TAG, "invokes: " + classname);
        }
        for (String type : localVarSet) {
            results.addValue(getSymbolicFeatureDataElement("localVar", type));
            Log.v(TAG, "LocalVar: " + type);
        }
        Log.v(TAG, "------------------------------");
    }

}
