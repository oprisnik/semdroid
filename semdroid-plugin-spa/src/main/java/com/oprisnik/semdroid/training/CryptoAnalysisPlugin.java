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

/**
 * Cryptography training plugin.
 */
public class CryptoAnalysisPlugin extends BaseAnalysisPlugin {

    private static final String TAG = "CryptoAnalysisPlugin";

    @Override
    public void analyze(AppAnalysisReport report, App app) {
        labelMethods(report, app, "org.bouncycastle.crypto.engines.AESEngine", "encryptBlock", "CRYPTO");
        labelMethods(report, app, "org.bouncycastle.crypto.engines.AESFastEngine", "encryptBlock", "CRYPTO");
        labelMethods(report, app, "org.bouncycastle.crypto.engines.AESLightEngine", "encryptBlock", "CRYPTO");
        labelMethods(report, app, "org.bouncycastle.crypto.engines.AESEngine", "decryptBlock", "CRYPTO");
        labelMethods(report, app, "org.bouncycastle.crypto.engines.AESFastEngine", "decryptBlock", "CRYPTO");
        labelMethods(report, app, "org.bouncycastle.crypto.engines.AESLightEngine", "decryptBlock", "CRYPTO");
//        labelMethods(app, "org.bouncycastle.crypto.engines.BlowfishEngine", "decryptBlock"), "CRYPTO";
//        labelMethods(app, "org.bouncycastle.crypto.engines.DESedeEngine", "CRYPTO");
//        labelMethods(app, "org.bouncycastle.crypto.engines.NoekeonEngine", "CRYPTO");
//        labelMethods(app, "org.bouncycastle.crypto.engines.SerpentEngine", "CRYPTO");
//        labelMethods(app, "org.bouncycastle.crypto.engines.TwofishEngine", "CRYPTO");

        for (DexClass c : app.getClasses()) {
            if (!c.getFullName().startsWith("org.bouncycastle")) {
                if (c.getFullName().startsWith("at.iaik.trafficanalyzer")) {
                    Log.d(TAG, "Normal class " + c.getFullName());
                    for (DexMethod m : c.getMethods()) {
                        if (m.getOpcodes().size() > 10 && m.getOpcodeHistogram().getMax() > 0) {
                            Log.d(TAG, "  Using " + app.getApkFileName() + " " + c.getFullName() + " " + m.getMethodName());
                            report.label(m, "NORMAL");
                        }
                    }
                }
            } else {
//                for (DexMethod m : c.getMethods()) {
//                    if (m.getName().contains("encryptBlock")) {
//                        Log.d(TAG, "  Crypto " + m.getName());
//                        m.addLabel("CRYPTO");
//                    }
//                }
            }
        }
    }

    protected void labelMethods(AppAnalysisReport report, App app, String clazz, String methodName, String label) {
        DexClass c = app.getClass(clazz);
        if (c != null) {
            Log.d(TAG, "Analyzing " + c.getFullName());
            for (DexMethod m : c.getMethods()) {

                if (m.getName().contains(methodName)) {
                    Log.d(TAG, "  Found " + m.getName());
                    report.label(m, label);
                }
            }
        }
    }
}
