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
import com.oprisnik.semdroid.analysis.results.Label;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Lite app analysis report - does not contain the contents of the App object, only lightweight results.
 */
public class LiteAppAnalysisReport implements Serializable {

    private String mName;

    private List<LiteLabel> mLabels;

    public static LiteAppAnalysisReport fromAnalysisReport(AppAnalysisReport original) {
        return new LiteAppAnalysisReport(original);
    }

    public LiteAppAnalysisReport() {
    }


    protected LiteAppAnalysisReport(AppAnalysisReport original) {
        this();
        mName = original.getName();
        mLabels = new ArrayList<LiteLabel>();
        for (Label orig : original.getLabels()) {
            mLabels.add(new LiteLabel(orig));
        }
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<LiteLabel> getLabels() {
        return mLabels;
    }

    public void setLabels(List<LiteLabel> labels) {
        mLabels = labels;
    }
}
