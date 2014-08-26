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

package com.oprisnik.semdroid.feature.instance.app;

import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.app.MethodCall;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.feature.instance.AppInstanceGenerator;
import com.oprisnik.semdroid.filter.ClassFilter;
import com.oprisnik.semdroid.filter.MethodCallFilter;
import com.oprisnik.semdroid.filter.MethodFilter;
import com.oprisnik.semdroid.utils.SparseIntHistogram;

import java.util.HashSet;
import java.util.Set;

import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;

/**
 * App category app instance generator.
 */
public class AppCategoryAIG extends AppInstanceGenerator {

    private ClassFilter mClassFilter;
    private MethodFilter mMethodFilter;
    private MethodCallFilter mMethodCallFilter;

    @Override
    public void init(Config config) throws BadConfigException {
        super.init(config);
        mClassFilter = getClassFilter();
        mMethodFilter = getMethodFilter();
        mMethodCallFilter = getMethodCallFilter();
    }

    @Override
    protected void getAppInstance(App app, InstanceDataElement results) {
        SparseIntHistogram histo = new SparseIntHistogram();

        Set<String> invokesSet = new HashSet<String>();

        for (DexClass clazz : app.getClasses()) {
            if (mClassFilter.use(clazz)) {
                for (DexMethod method : clazz.getMethods()) {

                    if (mMethodFilter.use(method)) {
                        histo.increaseAll(method.getOpcodeHistogram());

                        for (MethodCall mc : method.getMethodCalls()) {
                            if (mMethodCallFilter.use(mc)) {
                                String package1 = mc.getPackage();
                                if (package1 != null) {
                                    invokesSet.add(package1);
                                }
                            }
                        }
                    }
                }
            }
        }

        // apply grouping and get double array
        SparseIntHistogram groupHisto = getOpcodeGroupHistogram(histo);
        double[] val = getNormedOpcodeArray(groupHisto);
        results.addValue(getDistanceBasedFeatureDataElement("opcodes", val));
        for (String name : invokesSet) {
            results.addValue(getSymbolicFeatureDataElement("invokes", name));
        }

    }
}
