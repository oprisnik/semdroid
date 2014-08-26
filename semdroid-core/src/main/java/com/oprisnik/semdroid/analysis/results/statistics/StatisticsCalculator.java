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

package com.oprisnik.semdroid.analysis.results.statistics;

import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * Statistics calculator interface.
 */
public abstract class StatisticsCalculator {

    private String mName;

    private List<SuperGroup> mGroups;

    protected abstract void setup();

    public StatisticsCalculator(String name) {
        mName = name;
        mGroups = new ArrayList<SuperGroup>();
        setup();
    }

    public void addGroup(SuperGroup g) {
        mGroups.add(g);
    }


    public List<SuperGroup> collectStatistics(List<AppAnalysisReport> report) {
        for (AppAnalysisReport r : report) {
            if (r.getName().equals(mName)) {
                for (Label l : r.getLabels()) {
                    for (SuperGroup g : mGroups) {
                        g.addIfConditionMet(l);
                    }
                }
            }
        }
        return mGroups;
    }

}
