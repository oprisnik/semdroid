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
import com.oprisnik.semdroid.utils.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Expected SMS results plugin. Uses given expected results from a file to analyze applications.
 */
public class ExpectedSMSResultsAnalysisPlugin extends BaseAnalysisPlugin {

    private static final String TAG = "ExpectedSMSResultsAnalysisPlugin";

    private static final String SEPARATOR = " -> ";
    private final Map<String, List<String>> mSet;

    private static final String EXPECTED_RESULTS_FILE = "sms_expected.txt";

    //for now we do not care whether static or dynamic
    public static final String LABEL_STATIC = "SMS_SNIFFER";
    public static final String LABEL_DYNAMIC = "SMS_SNIFFER";

    public ExpectedSMSResultsAnalysisPlugin() {
        mSet = new HashMap<String, List<String>>();

        BufferedReader br = null;
        try {
            String data;
            br = new BufferedReader(new FileReader(new File(EXPECTED_RESULTS_FILE)));
            while ((data = br.readLine()) != null) {
                int index = data.indexOf(SEPARATOR);
                String appName = data.substring(0, index);
                String classification = data.substring(index + SEPARATOR.length());
                List<String> list = mSet.get(appName);
                if (list == null) {
                    list = new ArrayList<String>();
                    mSet.put(appName, list);
                }
                list.add(classification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }

    }

    @Override
    public String getName() {
        String name = super.getName();
        if (name == null)
            return TAG;
        return name;
    }


    @Override
    public void analyze(AppAnalysisReport report, App app) {
        List<String> data = mSet.get(app.getApkFileName().substring(0, app.getApkFileName().indexOf(".apk")));
//        System.out.println(app.getFileName() + app);
        if (data != null) {
            for (String s : data) {
                String[] temp = s.split(";");
                if (temp[0] != null && temp[0].length() > 0) {
                    // dyn receiver found!
                    labelReceiver(report, app, temp[0], false);
                    if (temp.length >= 3 && temp[2] != null && temp[2].length() > 0) {
                        // stat. receiver found
                        labelReceiver(report, app, temp[2], true);
                    }
                } else {
                    if (temp[1] != null && temp[1].length() > 0) {
                        // stat. receiver found
                        labelReceiver(report, app, temp[1], true);
                    }
                }
            }
        } //else {
        // normal app
        for (DexClass c : app.getClasses()) {
            if (c.getFirstExternalSuperClass().contains("BroadcastReceiver")) {
                for (DexMethod m : c.getMethods()) {
                    if (m.getName().contains("onReceive")) {
                        Log.d(TAG, "RECEIVER " + app.getApkFileName() + " " + c.getFullName() + " onReceive");
                    }
                }
            }
//            }
        }
    }

    private void labelReceiver(AppAnalysisReport report, App app, String source, boolean isStatic) {
        if (isStatic) {
            Log.v(TAG, "Static SMS receiver: " + source);
        } else {
            Log.v(TAG, "Dynamic SMS receiver: " + source);
        }
        List<DexClass> classes = app.getClassesByClassName(source);
        if (classes.size() <= 0) {
            DexClass c = app.getClass(source.substring(1).replace('/', '.'));
            if (c == null) {
                Log.e(TAG, "Class " + source + " not found!");
            } else {
                classes = new ArrayList<DexClass>();
                classes.add(c);
            }
        } else if (classes.size() > 1) {
            Log.e(TAG, "Multiple classes for " + source + " found! App: " + app.getApkFileName());
        }
        for (DexClass c : classes) {
            Log.v(TAG, "Found " + c.getFullName());
            if (isStatic) {
                for (DexMethod method : c.getMethods()) {
                    if (method.getName().contains("onReceive")) {

                        String s = "STATIC_";
                        String name = app.getApkFile().getAbsolutePath();
                        if (name.contains("SNIFFER_D")) {
                            s += "SNIFFER_D";
                        } else if (name.contains("SNIFFER_R")) {
                            s += "SNIFFER_R";
                        } else if (name.contains("CATCHER")) {
                            s += "CATCHER";
                        } else {
                            s += "UNKNOWN";
                        }
                        System.out.println(s + " " + app.getApkFileName() + " " + c.getFullName() + " onReceive");
//                        method.addLabel(LABEL_STATIC);
                        report.label(method, s);
                    }
                }
            } else {
                for (DexMethod method : c.getMethods()) {
                    if (method.getName().contains("onReceive")) {
                        String s = "DYNAMIC_";
                        String name = app.getApkFile().getAbsolutePath();
                        if (name.contains("SNIFFER_D")) {
                            s += "SNIFFER_D";
                        } else if (name.contains("SNIFFER_R")) {
                            s += "SNIFFER_R";
                        } else if (name.contains("CATCHER")) {
                            s += "CATCHER";
                        } else {
                            s += "UNKNOWN";
                        }
                        System.out.println(s + " " + app.getApkFileName() + " " + c.getFullName() + " onReceive");
//                        method.addLabel(LABEL_DYNAMIC);
                        report.label(method, LABEL_DYNAMIC);
                    }
                }
            }
        }
    }
}
