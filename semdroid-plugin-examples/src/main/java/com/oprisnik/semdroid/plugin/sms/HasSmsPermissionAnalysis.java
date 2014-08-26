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

package com.oprisnik.semdroid.plugin.sms;

import com.oprisnik.semdroid.analysis.BaseAnalysisPlugin;
import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.app.App;

/**
 * Very simple analysis plugin that checks whether an
 * application has the android.permission.RECEIVE_SMS permission.
 *
 * Since this plugin extends {@link com.oprisnik.semdroid.analysis.BaseAnalysisPlugin}, you can
 * set the name of the plugin in the XML configuration file for the plugin:
 *
 * XML config:
 * <analysis-plugin class="com.oprisnik.semdroid.plugin.sms.HasSmsPermissionAnalysis">
 *     <name>Has SMS permission?</name>
 * </analysis-plugin>
 *
 */
public class HasSmsPermissionAnalysis extends BaseAnalysisPlugin {

    public static final String PERMISSION_RECEIVE_SMS = "android.permission.RECEIVE_SMS";
    public static final String YES = "YES";
    public static final String NO = "NO";

    @Override
    public void analyze(AppAnalysisReport report, App app) {
        if (app.hasPermission(PERMISSION_RECEIVE_SMS)) {
            report.label(app, YES);
        } else {
            report.label(app, NO);
        }
    }
}
