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

package com.oprisnik.semdroid;

import com.oprisnik.semdroid.analysis.results.SemdroidReport;
import com.oprisnik.semdroid.utils.Log;
import com.oprisnik.semdroid.utils.ReportUtils;
import com.oprisnik.semdroid.utils.XmlUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Analysis demo main file. It shows how given applications can be analyzed.
 */
public class AnalysisMain {

    private static final String TAG = AnalysisMain.class.getSimpleName();

    public static void main(String[] args) {

        SemdroidAnalyzer semdroid = new SemdroidAnalyzer();
        try {
            semdroid.init(CliConfig.getSemdroidConfig());

            // add your analysis plugins
            semdroid.addAnalysisPlugin("path/to/plugin.config");

            // specify what apps you would like to analyze.
            // can be a single APK or a folder containing multiple applications
            String file = "apps/myApplication.apk";

            // where do we want to store the analysis results?
            String resultFile = "analysis.xml";

            // verbose output?
            boolean verbose = false;

            // let's go
            List<SemdroidReport> results = analyze(semdroid, new File(file), verbose);
            XmlUtils.reportsToXMLAndTransform(results, new File(resultFile), false, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<SemdroidReport> analyze(SemdroidAnalyzer semdroid, File file, boolean verbose) throws Exception {
        List<SemdroidReport> allResults = new ArrayList<SemdroidReport>();

        // check if the input file is a single application or a folder
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    // we recurse
                    allResults.addAll(analyze(semdroid, f, verbose));
                }
            }
            return allResults;
        }
        Log.d(TAG, "-------------------------------------------");
        Log.d(TAG, "Analyzing: " + file.getAbsolutePath());
        long start = System.currentTimeMillis();

        // this is where the magic happens :)
        SemdroidReport results = semdroid
                .analyze(file);
        allResults.add(results);

        Log.d(TAG,
                "Done. " + file + " Time: "
                        + (System.currentTimeMillis() - start) + " ms."
        );
        if (verbose) {
            ReportUtils.printResults(results.getReports());
        }
        return allResults;
    }

}
