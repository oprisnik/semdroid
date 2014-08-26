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

package com.oprisnik.semdroid.analysis;

import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;

/**
 * Abstract base analysis plugin that implements the
 * {@link com.oprisnik.semdroid.analysis.AppAnalysisPlugin} interface.
 * <p/>
 * The BaseAnalysisPlugin reads a <name> XML tag from the plugin configuration.
 * Furthermore, it creates an AppAnalysisReport and sets everything up accordingly.
 * <p/>
 * Plugins that extend the BaseAnalysisPlugin have to implement
 * {@link #analyze(com.oprisnik.semdroid.analysis.results.AppAnalysisReport, com.oprisnik.semdroid.app.App)}.
 * In this method, the plugin should analyze the contents of the given application under analysis
 * and add the analysis results to the {@link com.oprisnik.semdroid.analysis.results.AppAnalysisReport}
 * by calling {@link AppAnalysisReport#label(com.oprisnik.semdroid.analysis.results.Labelable, String)}
 * <p/>
 * <p/>
 * XML config:
 * <analysis-plugin class="com.oprisnik.semdroid.plugin.MyPlugin">
 *     <name>My plugin name</name>
 * </analysis-plugin>
 *
 * @see com.oprisnik.semdroid.analysis.AppAnalysisPlugin
 * @see com.oprisnik.semdroid.analysis.results.AppAnalysisReport
 */
public abstract class BaseAnalysisPlugin implements AppAnalysisPlugin {

    public static final String KEY_NAME = "name";

    private String mName = null;

    public BaseAnalysisPlugin() {
    }

    public BaseAnalysisPlugin(String name) {
        this();
        setName(name);
    }

    @Override
    public void init(Config config) throws BadConfigException {
        if (config != null) {
            mName = config.getProperty(KEY_NAME, mName);
        }
        if (mName == null) {
            mName = this.getClass().getSimpleName();
        }
    }

    @Override
    public AppAnalysisReport analyze(App app) throws Exception {
        AppAnalysisReport report = new AppAnalysisReport(getName(), app);
        analyze(report, app);
        return report;
    }

    @Override
    public String getName() {
        return mName == null ? this.getClass().getSimpleName() : mName;
    }

    public void setName(String name) {
        mName = name;
    }

    /**
     * Analyzes the given App object and adds the results to the AppAnalysisReport
     *
     * @param report the app report where the findings are added
     * @param app    the application under analysis
     */
    public abstract void analyze(AppAnalysisReport report, App app);
}
