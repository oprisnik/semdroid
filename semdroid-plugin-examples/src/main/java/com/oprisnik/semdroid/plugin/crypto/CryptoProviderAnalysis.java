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

package com.oprisnik.semdroid.plugin.crypto;

import com.oprisnik.semdroid.analysis.AppAnalysisPlugin;
import com.oprisnik.semdroid.analysis.BaseAnalysisPlugin;
import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.app.MethodCall;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;

/**
 * Crypto provider analysis.
 * <p/>
 * This example directly implements the AppAnalysisPlugin interface, without using the BaseAnalysisPlugin.
 * <p/>
 * <analysis-plugin class="com.oprisnik.semdroid.plugin.crypto.CryptoProviderAnalysis">
 * <name>Crypto plugin</name>
 * </analysis-plugin>
 */
public class CryptoProviderAnalysis implements AppAnalysisPlugin {

    private static final String TAG = "CryptoProviderAnalysis";

    public static final String KEY_NAME = BaseAnalysisPlugin.KEY_NAME;

    private String mName;

    @Override
    public void init(Config config) throws BadConfigException {
        if (config != null) {
            mName = config.getProperty(KEY_NAME, TAG);
        }
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public AppAnalysisReport analyze(App app) throws Exception {
        AppAnalysisReport report = new AppAnalysisReport(getName(), app);

        for (DexClass clazz : app.getClasses()) {
            for (DexMethod method : clazz.getMethods()) {
                for (MethodCall mc : method.getMethodCalls()) {
                    String methodName = mc.getMethodName();
                    if (methodName.contains("getInstance")) {
                        String className = mc.getClassName();
                        if (className.contains("Cipher")) {
                            report.label(method, "Cipher.getInstance()");
                        } else if (className.contains("Mac")) {
                            report.label(method, "Mac.getInstance()");
                        } else if (className.contains("MessageDigest")) {
                            report.label(method, "MessageDigest.getInstance()");
                        } else if (className.contains("Signature")) {
                            report.label(method, "Signature.getInstance()");
                        } else if (className.contains("SecureRandom")) {
                            report.label(method, "Signature.getInstance()");
                        }
                    } else if (methodName.contains("addAsProvider")) {
                        report.label(method, "addAsProvider");
                    } else if (methodName.contains("addProvider")) {
                        String className = mc.getClassName();
                        if (className.contains("Security")) {
                            report.label(method, "Security.addProvider()");
                        } else {
                            report.label(method, mc.getMethodName());
                        }
                    }
                }
            }
        }

        return report;
    }
}
