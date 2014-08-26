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
import com.oprisnik.semdroid.utils.Log;

import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;

/**
 * Method instance generator that uses API calls and used permissions.
 */
public class ApiCallsAndPermissionsMIG extends MethodInstanceGenerator {

    private static final String TAG = "ApiCallsAndPermissionsMIG";

    @Override
    protected void getMethodInstance(DexMethod method,
                                     InstanceDataElement results) {

        for (String permission : method.getPermissions()) {
            results.addValue(getSymbolicFeatureDataElement("usesPermission",
                    permission));
            // Log.v(TAG, permission);
        }

        for (CodeElement e : method.getCode()) {
            if (e instanceof Opcode) {
            } else if (e instanceof MethodCall) {

                MethodCall mc = (MethodCall) e;
                if (mc.getDexMethod() == null) {
                    // API call
                    results.addValue(getSymbolicFeatureDataElement("invokes",
                            e.getName()));
                }
            } else if (e instanceof LocalVariable) {
            } else {
                Log.e(TAG, "Unknown type: " + e.getName());
            }
        }
    }
}
