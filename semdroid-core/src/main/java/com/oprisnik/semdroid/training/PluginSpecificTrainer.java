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
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;

/**
 * Plugin specific trainer.
 */
public interface PluginSpecificTrainer {

    /**
     * Initialize the plugin specific trainer.
     *
     * @param trainer the parent trainer
     * @param config  the plugin specific configuration to used
     * @throws BadConfigException if the configuration is not valid
     */
    public void init(PluginTrainer trainer, Config config) throws BadConfigException;

    /**
     * New training data is available.
     * The target data has been attached to the components of the app object
     * and can also be found in the given AppAnalysisReport.
     *
     * @param targetResults the target results
     * @param app           the training application with labeled components according to targetResults
     */
    public void onTrainingDataAvailable(AppAnalysisReport targetResults, App app);

    /**
     * Generate the analysis plugin.
     * All training applications have been supplied via {@link #onTrainingDataAvailable(com.oprisnik.semdroid.analysis.results.AppAnalysisReport, com.oprisnik.semdroid.app.App)}
     * Now, the final plugin has to be generated.
     *
     * @throws Exception
     */
    public void generateAnalysisPlugin() throws Exception;
}
