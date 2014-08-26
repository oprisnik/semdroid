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
import com.oprisnik.semdroid.app.Opcode;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.feature.instance.MethodInstanceGenerator;
import com.oprisnik.semdroid.feature.instance.config.IGConfiguration;
import com.oprisnik.semdroid.feature.value.LocalVarFeatureValueGenerator;
import com.oprisnik.semdroid.feature.value.MethodCallFeatureValueGenerator;
import com.oprisnik.semdroid.filter.LocalVarFilter;
import com.oprisnik.semdroid.filter.MethodCallFilter;

import java.util.List;

import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;

/**
 * Configurable method instance generator.
 */
public class ConfigurableMIG extends MethodInstanceGenerator {

    protected MethodCallFilter mMethodCallFilter;
    protected MethodCallFeatureValueGenerator mMethodCallFVG;


    protected LocalVarFilter mLocalVarFilter;
    protected LocalVarFeatureValueGenerator mLocalVarFVG;


    protected IGConfiguration mConfig;

    @Override
    public void init(Config config) throws BadConfigException {
        super.init(config);

        mConfig = new IGConfiguration();
        mConfig.init(config);

        mMethodCallFilter = getMethodCallFilter();
        mMethodCallFVG = getMethodCallFeatureValueGenerator();

        mLocalVarFilter = getLocalVarFilter();
        mLocalVarFVG = getLocalVarFeatureValueGenerator();
    }

    @Override
    protected void getMethodInstance(DexMethod method, InstanceDataElement results) {

        // opcodes
        if (mConfig.addOpcodeHistogram || mConfig.addOpcodeCount || mConfig.addOpcodes) {
            List<Opcode> opcodes = method.getOpcodes(getMethodCallInclusionDepth());
            if (mConfig.addOpcodeHistogram) {
                results.addValue(getDistanceBasedFeatureDataElement("opcodeHisto",
                        getOpcodeHistogram(opcodes, mConfig.normalizeOpcodeHistogram)));
            }

            if (mConfig.addOpcodeCount) {
                results.addValue(getDistanceBasedFeatureDataElement("opcodeCount", getOpcodeGrouper().getGroupedList(opcodes).size()));
            }

            if (mConfig.addOpcodes) {
                addOpcodes("opcode", opcodes, results);
            }
        }

        // method calls
        if (mConfig.addMethodCalls || mConfig.addMethodCallCount) {
            List<MethodCall> methodCalls = method.getMethodCalls(getMethodCallInclusionDepth());
            if (mConfig.addMethodCalls) {
                addMethodCalls(methodCalls, results);
            }

            if (mConfig.addMethodCallCount) {
                results.addValue(getDistanceBasedFeatureDataElement("methodCallCount", methodCalls.size()));
            }
        }

        // local variables
        if (mConfig.addBasicLocalVarHistogram || mConfig.addLocalVars || mConfig.addLocalVarCount) {
            List<LocalVariable> localVariables = method.getLocalVariables(getMethodCallInclusionDepth());
            if (mConfig.addBasicLocalVarHistogram) {
                results.addValue(getDistanceBasedFeatureDataElement("basicLocalVarHisto", getBasicLocalVarHistogram(localVariables, mConfig.normalizeBasicLocalVarHistogram)));
            }
            if (mConfig.addLocalVars) {
                addLocalVars(localVariables, results);
            }

            if (mConfig.addLocalVarCount) {
                results.addValue(getDistanceBasedFeatureDataElement("localVariableCount", localVariables.size()));
            }
        }


        // permissions
        if (mConfig.addPermissions) {
            for (String permission : method.getPermissions()) {
                results.addValue(getSymbolicFeatureDataElement("usesPermission",
                        permission));
            }
        }
    }

    protected void addMethodCalls(List<MethodCall> methodCalls, InstanceDataElement results) {
        addAsSet("methodCall", methodCalls, mMethodCallFilter, mMethodCallFVG, results);
    }

    protected void addLocalVars(List<LocalVariable> localVars, InstanceDataElement results) {
        addAsSet("localVar", localVars, mLocalVarFilter, mLocalVarFVG, results);
    }
}
