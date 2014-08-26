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
import com.oprisnik.semdroid.utils.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads the given mapping from a file.
 * Structure (separated by tabs): label type app className methodPart
 * E.g. "malware m myApp.apk com.some.malware.Class onReceive"
 */
public class MappingFileAnalysisPlugin extends BaseAnalysisPlugin {

    private static final String TAG = "MappingFileAnalysisPlugin";

    public static final String KEY_MAPPING_FILE = "mapping-file";

    protected Map<String, List<LabelEntry>> mLabels = new HashMap<String, List<LabelEntry>>();

    protected List<LabelEntry> mGlobalLabels = new ArrayList<LabelEntry>();

    @Override
    public void init(Config config) throws BadConfigException {
        super.init(config);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(config.getNestedInputStream(KEY_MAPPING_FILE)));
            String data;
            while ((data = br.readLine()) != null) {
                if (data.startsWith("//") || data.isEmpty() || data.equals(" ")) {
                    continue;
                }
                String[] parts = data.split("\t");
                LabelEntry e = new LabelEntry();
                if (parts[0].equalsIgnoreCase("m")) {
                    e.labelMethod = true;
                } else if (parts[0].equalsIgnoreCase("c")) {
                    e.labelClass = true;
                } else if (parts[0].equalsIgnoreCase("a")) {
                    e.labelApp = true;
                } else {
                    throw new BadConfigException("Type not recognized: " + parts[0]);
                }
                e.label = parts[1];
                e.app = parts[2];
                if (e.labelClass || e.labelMethod) {
                    e.className = parts[3];
                }
                if (e.labelMethod) {
                    e.methodPart = parts[4];
                }

//               Log.d(TAG, e.toString());

                if (e.app.equals("*")) {
                    mGlobalLabels.add(e);
                } else {
                    List<LabelEntry> entries = mLabels.get(e.app);
                    if (entries == null) {
                        entries = new ArrayList<LabelEntry>();
                        mLabels.put(e.app, entries);
                    }
                    entries.add(e);
                }

            }
        } catch (Exception e) {
            throw new BadConfigException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(br);
        }
    }

    @Override
    public void analyze(AppAnalysisReport report, App app) {
        Log.d(TAG, " " + app.getApkFileName());
        List<LabelEntry> labelEntries = mLabels.get(app.getName());
        if (labelEntries == null) {
            labelEntries = mLabels.get(app.getApkFileName());
        }
        if (labelEntries != null) {
            Log.d(TAG, "Analyzing " + app.getName());
            for (LabelEntry labelEntry : labelEntries) {
                check(labelEntry, report, app);
            }
        }

        // global label entries
        if (mGlobalLabels.size() > 0) {
            for (LabelEntry labelEntry : mGlobalLabels) {
                check(labelEntry, report, app);
            }
        }

    }

    protected boolean check(LabelEntry labelEntry, AppAnalysisReport report, App app) {
        boolean labeled = false;
        if (labelEntry.labelApp) {
            Log.d(TAG, "  Labeled " + app.getName() + " " + labelEntry.label);
            report.label(app, labelEntry.label);
            labeled = true;
        }
        if (labelEntry.labelClass || labelEntry.labelMethod) {
            DexClass c = app.getClass(labelEntry.className);
            if (c != null) {
//                        Log.d(TAG, "Analyzing " + c.getFullName());
                if (labelEntry.labelClass) {
                    Log.d(TAG, "  Labeled " + c.getFullName() + " " + labelEntry.label);
                    report.label(c, labelEntry.label);
                    labeled = true;
                }
                if (labelEntry.labelMethod) {
                    boolean found = false;
                    for (DexMethod m : c.getMethods()) {

                        if (m.getName().contains(labelEntry.methodPart)) {
                            Log.d(TAG, "  Labeled " + m.getName() + " " + labelEntry.label);
                            report.label(m, labelEntry.label);
                            labeled = true;
                            found = true;
                        }
                    }
                    if (!found) {
                        Log.e(TAG, "Could not find method " + labelEntry.methodPart + " in " + labelEntry.app + " " + labelEntry.className);
                    }
                }
            } else {
                Log.e(TAG, "Could not find class " + labelEntry.className + " in " + labelEntry.app);
                for (DexClass cll : app.getClasses()) {
                    System.out.println(cll.getFullName());
                }
            }
        }
        return labeled;
    }

    public class LabelEntry {

        boolean labelApp;
        boolean labelClass;
        boolean labelMethod;
        String app;
        String label;
        String className;
        String methodPart;

        @Override
        public String toString() {
            return "Label: " + label + ". App: " + app + ". Class: " + className + "" +
                    ". Method: " + methodPart + ". Label app/class/method: " + labelApp + " " + labelClass + " " + labelMethod;
        }
    }
}
