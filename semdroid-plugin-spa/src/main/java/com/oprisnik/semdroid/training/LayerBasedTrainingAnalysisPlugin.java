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

package com.oprisnik.semdroid.training;

import com.oprisnik.semdroid.analysis.BaseAnalysisPlugin;
import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.feature.layer.AppSingleFeatureLayerGenerator;
import com.oprisnik.semdroid.feature.layer.ClassSingleFeatureLayerGenerator;
import com.oprisnik.semdroid.feature.layer.FeatureLayerGenerator;
import com.oprisnik.semdroid.feature.layer.MethodSingleFeatureLayerGenerator;
import com.oprisnik.semdroid.feature.layer.MultiFeatureLayerGenerator;
import com.oprisnik.semdroid.filter.ClassFilter;
import com.oprisnik.semdroid.filter.DefaultClassFilter;
import com.oprisnik.semdroid.filter.DefaultMethodFilter;
import com.oprisnik.semdroid.filter.MethodFilter;

/**
 * Training classifier.
 */
public abstract class LayerBasedTrainingAnalysisPlugin extends BaseAnalysisPlugin {


    public static final String KEY_CLASS_FILTER = "class-filter";
    public static final String KEY_METHOD_FILTER = "method-filter";
    public static final String KEY_FEATURE_LAYER_GENERATOR = "feature-layer-generator";


    private ClassFilter mClassFilter;
    private MethodFilter mMethodFilter;

    private boolean mLabelClasses;
    private boolean mLabelMethods;
    private boolean mLabelApp;

    @Override
    public void init(Config config) throws BadConfigException {
        super.init(config);

        mClassFilter = config.getComponentAndInit(KEY_CLASS_FILTER, ClassFilter.class, DefaultClassFilter.class);
        mMethodFilter = config.getComponentAndInit(KEY_METHOD_FILTER, MethodFilter.class, DefaultMethodFilter.class);

        mLabelClasses = true;
        mLabelMethods = true;
        mLabelApp = true;
        FeatureLayerGenerator generator = config.getComponentAndInit(KEY_FEATURE_LAYER_GENERATOR, FeatureLayerGenerator.class);
        if (generator instanceof MethodSingleFeatureLayerGenerator) {
            mLabelClasses = false;
            mLabelApp = false;
        } else if (generator instanceof ClassSingleFeatureLayerGenerator) {
            mLabelMethods = false;
            mLabelApp = false;
        } else if (generator instanceof AppSingleFeatureLayerGenerator) {
            mLabelMethods = false;
            mLabelClasses = false;
        } else if (generator instanceof MultiFeatureLayerGenerator) {
            // everything is already set to true
        } else {
            throw new BadConfigException("Used feature layer generator unknown. Labeling all layers (app, class, method).");
        }
    }

    @Override
    public void analyze(AppAnalysisReport report, App app) {
        if (mLabelClasses || mLabelMethods) {
            for (DexClass c : app.getClasses()) {
                if (use(c)) {
                    if (mLabelMethods) {
                        for (DexMethod m : c.getMethods()) {
                            if (use(m)) {
                                classifyMethod(report, m);
                            }
                        }
                    }
                    if (mLabelClasses) {
                        classifyClass(report, c);
                    }
                }
            }
        }
        if (mLabelApp) {
            classifyApp(report, app);
        }
    }

    protected boolean use(DexClass clazz) {
        return mClassFilter.use(clazz);
    }

    protected boolean use(DexMethod method) {
        return mMethodFilter.use(method);
    }


    protected abstract void classifyApp(AppAnalysisReport report, App app);

    protected abstract void classifyMethod(AppAnalysisReport report, DexMethod method);

    protected abstract void classifyClass(AppAnalysisReport report, DexClass clazz);
}
