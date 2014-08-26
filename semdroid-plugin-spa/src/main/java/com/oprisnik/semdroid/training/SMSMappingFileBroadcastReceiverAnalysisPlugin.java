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
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.filter.BroadcastReceiverClassFilter;
import com.oprisnik.semdroid.filter.ClassFilter;
import com.oprisnik.semdroid.filter.MethodFilter;
import com.oprisnik.semdroid.filter.OnReceiveMethodFilter;
import com.oprisnik.semdroid.utils.Log;

import java.util.List;

/**
 * Loads the given SMS mapping from a file.
 * If no mapping is given for a BroadcastReceiver, the onReceive method is labeled as normal
 * Structure (separated by tabs): label type app className methodPart
 * E.g. "malware m myApp.apk com.some.malware.Class onReceive"
 */
public class SMSMappingFileBroadcastReceiverAnalysisPlugin extends MappingFileAnalysisPlugin {

    public final static String NORMAL_LABEL = "NORMAL_RECEIVER";

    private static final String TAG = "SMSMappingFileBroadcastReceiverAnalysisPlugin";

    private ClassFilter mClassFilter;
    private MethodFilter mMethodFilter;

    @Override
    public void init(Config config) throws BadConfigException {
        super.init(config);
        mClassFilter = new BroadcastReceiverClassFilter();
        mClassFilter.init(config);
        mMethodFilter = new OnReceiveMethodFilter();
        mMethodFilter.init(config);
    }

    @Override
    public void analyze(AppAnalysisReport report, App app) {
        List<LabelEntry> labelEntries = mLabels.get(app.getName());
        if (labelEntries == null) {
            labelEntries = mLabels.get(app.getApkFileName());
        }
        boolean labeled = false;
        if (labelEntries != null) {
            Log.d(TAG, "Classifying " + app.getName());
            for (LabelEntry labelEntry : labelEntries) {
                if (check(labelEntry, report, app)) {
                    labeled = true;
                }
            }
        }

        // global label entries
        if (mGlobalLabels.size() > 0) {
            for (LabelEntry labelEntry : mGlobalLabels) {
                if (check(labelEntry, report, app)) {
                    labeled = true;
                }
            }
        }

        if (!labeled) {
            for (DexClass c : app.getClasses()) {
                if (c.getClassName().toLowerCase().contains("sms")) {
                    Log.d(TAG, "...!... SMS: " + c.getFullName());
                }
            }
        }

        if (app.hasPermission("android.permission.RECEIVE_SMS")) {
            Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!! HAS SMS PERMISSION !!!!!!!!!!!!!!!!!!!");
        } else {
            Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!! NOO SMS PERMISSION !!!!!!!!!!!!!!!!!!!");

            if (!labeled) {
                for (DexClass c : app.getClasses()) {
                    if (mClassFilter.use(c)) {
                        for (DexMethod m : c.getMethods()) {
                            if (mMethodFilter.use(m)) {
                                report.label(m, NORMAL_LABEL);
                                Log.d(TAG, "  Labeled " + m.getName() + " " + NORMAL_LABEL);
                            }
                        }
                    }
                }
            }
        }

    }


}
