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

import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.utils.Log;

/**
 * Application classification by folder name.
 * For example, if the application is in folder SMS, components will be labeled as SMS.
 */
public class FolderNameAnalysisPlugin extends LayerBasedTrainingAnalysisPlugin {

    private static final String TAG = "FolderNameAnalysisPlugin";

    @Override
    protected void classifyApp(AppAnalysisReport report, App app) {
        String label = app.getApkFile().getParentFile().getName();
        Log.d(TAG, "Labeling " + app.getApkFileName() + " as " + label);
        report.label(app, label);
    }

    @Override
    protected void classifyMethod(AppAnalysisReport report, DexMethod method) {
        String label = method.getApp().getApkFile().getParentFile().getName();
        report.label(method, label);
    }

    @Override
    protected void classifyClass(AppAnalysisReport report, DexClass clazz) {
        String label = clazz.getApp().getApkFile().getParentFile().getName();
        report.label(clazz, label);
    }
}
