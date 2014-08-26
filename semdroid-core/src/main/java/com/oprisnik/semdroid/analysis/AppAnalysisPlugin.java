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
import com.oprisnik.semdroid.config.Configurable;

/**
 * Application analysis plugin interface. Analyze the given application and return a report.
 * <p/>
 * XML configuration:
 * <analysis-plugin class="com.my.package.MyPlugin">...</analysis-plugin>
 * <p/>
 * You can include custom XML tags in the analysis-plugin tag.
 * An example for custom tags can be found in {@link com.oprisnik.semdroid.analysis.BaseAnalysisPlugin}.
 * <p/>
 * You can also extend the BaseAnalysisPlugin instead of implementing AppAnalysisPlugin.
 *
 * @see com.oprisnik.semdroid.analysis.BaseAnalysisPlugin
 * @see com.oprisnik.semdroid.analysis.results.AppAnalysisReport
 */
public interface AppAnalysisPlugin extends Configurable {


    /**
     * Get the plugin name.
     *
     * @return the plugin name.
     */
    public String getName();

    /**
     * Analyze the given application.
     *
     * @param app the application under analysis
     * @return the analysis report
     * @throws Exception
     */
    public AppAnalysisReport analyze(App app)
            throws Exception;

}
