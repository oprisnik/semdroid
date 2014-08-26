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
import com.oprisnik.semdroid.app.LocalVariable;
import com.oprisnik.semdroid.app.MethodCall;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.filter.BroadcastReceiverClassFilter;
import com.oprisnik.semdroid.filter.ClassFilter;
import com.oprisnik.semdroid.filter.ContainsStringClassFilter;
import com.oprisnik.semdroid.filter.MethodFilter;
import com.oprisnik.semdroid.filter.OnReceiveMethodFilter;
import com.oprisnik.semdroid.utils.Log;

/**
 * Classifies broadcast receivers according to their name.
 * If it contains "sms" and the app has the SMS permission, they are labeled as "SMS_RECEIVER",
 * if the app does not have the SMS permission and the class name does not contain SMS,
 * they are labeled "NORMAL_RECEIVER".
 */
public class SMSBroadcastReceiverAnalysisPlugin extends BaseAnalysisPlugin {

    private static final String TAG = "SMSBroadcastReceiverAnalysisPlugin";

    public static final String NORMAL_LABEL = "NORMAL_RECEIVER";
    public static final String SMS_LABEL = "SMS_RECEIVER";

    private ClassFilter mBroadcastReceiverFilter;
    private ClassFilter mContainsSMSFilter;
    private MethodFilter mOnReceiveMethodFilter;

    @Override
    public void init(Config config) throws BadConfigException {
        super.init(config);
        mBroadcastReceiverFilter = new BroadcastReceiverClassFilter();
        mBroadcastReceiverFilter.init(config);
        mOnReceiveMethodFilter = new OnReceiveMethodFilter();
        mOnReceiveMethodFilter.init(config);
        mContainsSMSFilter = new ContainsStringClassFilter("sms");
        mContainsSMSFilter.init(config);
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
        boolean hasSMSPermission = app.hasPermission("android.permission.RECEIVE_SMS");
        for (DexClass c : app.getClasses()) {
            if (mBroadcastReceiverFilter.use(c)) {
                if (mContainsSMSFilter.use(c)) {
                    if (hasSMSPermission) {
                        labelOnReceive(report, c, SMS_LABEL);
                        Log.d(TAG, "SMS\tm\t" + app.getApkFileName() + "\t" + c.getName() + "\t" + "onReceive");
                    }
                } else {
                    // does not contain SMS
                    if (!hasSMSPermission) {
                        // just to be sure, we only label receivers of apps without SMS permissions
                        labelOnReceive(report, c, NORMAL_LABEL);
                    }
                }
            }
        }
    }

    protected void labelOnReceive(AppAnalysisReport report, DexClass c, String label) {
        for (DexMethod m : c.getMethods()) {
            if (mOnReceiveMethodFilter.use(m)) {
                if (label.equals(SMS_LABEL)) {
                    for (MethodCall mc : m.getMethodCalls(1)) {
                        if (mc.getClassName().equals("SmsMessage")) {
                            report.label(m, label);
                            Log.d(TAG, "  Labeled " + m.getName() + " " + label);
                            return;
                        }
                    }
                    for (LocalVariable lv : m.getLocalVariables(1)) {
                        if (lv.getType().contains("SmsMessage")) {
                            report.label(m, label);
                            Log.d(TAG, "  Labeled " + m.getName() + " " + label + " because of " + lv.getType());
                            return;
                        }
                    }
                } else {
                    report.label(m, label);
                    Log.d(TAG, "  Labeled " + m.getName() + " " + label);
                }
            }
        }
    }
}
