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
import com.oprisnik.semdroid.app.manifest.AndroidReceiver;
import com.oprisnik.semdroid.app.manifest.IntentFilter;
import com.oprisnik.semdroid.feature.instance.MethodInstanceGenerator;
import com.oprisnik.semdroid.permissions.PermissionMap;
import com.oprisnik.semdroid.utils.DexUtils;
import com.oprisnik.semdroid.utils.Log;
import com.oprisnik.semdroid.utils.SparseIntHistogram;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;

/**
 * V1 method instance generator. Uses thresholds derived from statistics and
 * adds permissions, intent actions, local variables, opcode histogram and
 * invoked methods.
 * <p/>
 * .
 */
public class V1MIG extends MethodInstanceGenerator {

    private static final String TAG = "V1MIG";

    @Override
    protected void getMethodInstance(DexMethod method,
                                     InstanceDataElement results) {

        int appcount = getStatistics().getAppCount();

        int opcodeThresholdMin = appcount / 2;
        int methodCallClassNameThreshold = appcount / 2;
        int localVarThreshold = appcount / 2;

        Log.v(TAG, "------------------------------");
        Log.v(TAG, "Method: " + method.toString());

        Set<String> intentFilterAction = new HashSet<String>();

        for (String permission : method.getPermissions()) {
            if (PermissionMap.SEE_INTENT_KEY.equals(permission)) {

                List<AndroidReceiver> receivers = method.getApp().getManifest()
                        .getReceivers();
                // Log.v(TAG, "Looking for Receiver: "
                // + method.getDexClass().getFullName());
                for (AndroidReceiver r : receivers) {
                    // Log.v(TAG, "Receiver defined: " +
                    // r.getFullyQualifiedName());
                    if (r.getFullyQualifiedName().equals(
                            method.getDexClass().getFullName())) {
                        // Log.v(TAG,
                        // "Receiver Found! Checking Intent filter...");
                        List<IntentFilter> inf = r.getIntentFilters();
                        for (IntentFilter f : inf) {
                            // Log.v(TAG, "IntentFilter: " + f.getAction());
                            intentFilterAction.add(f.getAction());
                        }
                    }
                }
            } else {
                results.addValue(getSymbolicFeatureDataElement(
                        "usesPermission", permission));
                Log.v(TAG, "usesPermission: " + permission);
            }
        }

        for (String ifa : intentFilterAction) {
            results.addValue(getSymbolicFeatureDataElement(
                    "usesIntentFilterAction", ifa));
            Log.v(TAG, "usesIntentFilterAction: " + ifa);
        }

        SparseIntHistogram opcodeHisto = new SparseIntHistogram();
        Set<String> localVarSet = new HashSet<String>();
        Set<String> invokesSet = new HashSet<String>();

        for (CodeElement e : method.getCode()) {
            if (e instanceof Opcode) {
                Opcode op = (Opcode) e;
                String grouped = getOpcodeGroupName(op);
                if (grouped != null
                        && getStatistics().getOpcodeCount(grouped) >= opcodeThresholdMin) {
                    opcodeHisto.increase(getOpcodeGroup(op));
                }
            } else if (e instanceof MethodCall) {

                MethodCall mc = (MethodCall) e;
                if (mc.getDexMethod() == null) {
                    // API call
                    // we use class names only:
                    String classname = mc.getClassName();
                    if (classname != null && classname.startsWith("android")) {
                        if (getStatistics().getMethodCallClassCount(classname) >= methodCallClassNameThreshold) {
                            invokesSet.add(classname);
                        }
                    }
                }

            } else if (e instanceof LocalVariable) {
                LocalVariable l = (LocalVariable) e;
                String type = l.getType();
                if (type != null
                        && (DexUtils.isBasicDataType(type) || DexUtils
                        .dataTypeStartsWith(type, "android"))) {
                    if (getStatistics().getLocalVarTypeCount(type) >= localVarThreshold)
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
