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

package com.oprisnik.semdroid.utils;

import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.Label;
import com.oprisnik.semdroid.analysis.results.Labelable;

import java.util.List;
import java.util.Set;

/**
 * Report utilities
 *
 */
public class ReportUtils {

    private static final String TAG = "ReportUtils";

    public static void printResults(List<AppAnalysisReport> results, Set<String> ignoredLabels) {
        printResults(results, ignoredLabels, -1);
    }

    /**
     * Print the analysis results.
     *
     * @param results the results to print
     * @param ignoredLabels the labels to ignore.
     * @param labelableLimit limit the number of printed labelables. -1 for all labelables
     */
    public static void printResults(List<AppAnalysisReport> results, Set<String> ignoredLabels, int labelableLimit) {
        for (AppAnalysisReport report : results) {
            Log.d(TAG, "Report: " + report.getName());
            for (Label l : report.getLabels()) {

                Log.d(TAG, "  Label: " + l);
                if (!l.getName().equals(Label.NULL) && (ignoredLabels == null || !ignoredLabels.contains(l.getName()))) {
                    for (Labelable r : l.getObjects()) {
                        Log.d(TAG, "          " + r.getName());
                    }
                } else {
                    int size = l.getObjects().size();
                    if (labelableLimit < 0  || size < labelableLimit) {

                        for (Labelable r : l.getObjects()) {
                            Log.d(TAG, "          " + r.getName());
                        }
                    } else {
//                        Log.d(TAG, "          " + size + " objects!");
                    }
                }
            }
        }
    }

    public static void printResults(List<AppAnalysisReport> results) {
        printResults(results, null);
    }

    private ReportUtils() {
    }

}
