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

package com.oprisnik.semdroid.analysis.results.lite;

import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.SemdroidReport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight test suite report. Instead of storing a reference to the {@link com.oprisnik.semdroid.app.App}
 * components, like {@link com.oprisnik.semdroid.app.DexClass} or
 * {@link com.oprisnik.semdroid.app.DexMethod}, we only store the names of the components.
 */
public class LiteSemdroidReport implements Serializable {

    private String mAnalysisName;
    private String mAppName;
    private String mApkFile;

    private List<LiteAppAnalysisReport> mAnalysisReports;

    public static LiteSemdroidReport fromTestSuiteReport(SemdroidReport original) {
        return new LiteSemdroidReport(original);
    }

    protected LiteSemdroidReport() {
    }

    protected LiteSemdroidReport(SemdroidReport original) {
        this();
        mAnalysisName = original.getName();
        mAppName = original.getApp().getName();
        mApkFile = original.getApp().getApkFileName();
        mAnalysisReports = new ArrayList<LiteAppAnalysisReport>();
        for (AppAnalysisReport report : original.getReports()) {
            mAnalysisReports.add(LiteAppAnalysisReport.fromAnalysisReport(report));
        }
    }

    public String getAnalysisName() {
        return mAnalysisName;
    }

    public void setAnalysisName(String analysisName) {
        mAnalysisName = analysisName;
    }

    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String appName) {
        mAppName = appName;
    }

    public String getApkFile() {
        return mApkFile;
    }

    public void setApkFile(String apkFile) {
        mApkFile = apkFile;
    }

    public List<LiteAppAnalysisReport> getReports() {
        return mAnalysisReports;
    }

    public void setReports(List<LiteAppAnalysisReport> analysisReports) {
        mAnalysisReports = analysisReports;
    }
}
