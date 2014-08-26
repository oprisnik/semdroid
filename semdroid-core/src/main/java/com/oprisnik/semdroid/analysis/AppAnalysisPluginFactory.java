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

import com.oprisnik.semdroid.DefaultValues;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.config.ConfigFactory;

import java.io.File;

/**
 * App analysis plugin factory.
 * Creates analysis plugins from the given configuration files.
 */
public class AppAnalysisPluginFactory {

    /**
     * Load the given AppAnalysisPlugin from a plugin configuration.
     *
     * @param analysisConfig the analysis configuration
     * @return the analysis plugin
     * @throws BadConfigException if the analysisConfig is not valid
     */
    public static AppAnalysisPlugin fromConfig(Config analysisConfig)
            throws BadConfigException {
        return analysisConfig.getComponentAndInit(AppAnalysisPlugin.class);
    }

    /**
     * Load the given AppAnalysisPlugin from a file or a folder.
     * The supplied file can either be a folder that contains the plugin configuration file
     * (with the file name specified in @link{com.oprisnik.semdroid.DefaultValues}),
     * or a plugin configuration file.
     *
     * @param file plugin file or folder
     * @return the analysis plugin
     * @throws Exception if the plugin cannot be loaded
     * @see com.oprisnik.semdroid.DefaultValues
     */
    public static AppAnalysisPlugin fromFile(File file) throws Exception {
        if (file.isDirectory()) {
            file = new File(file, DefaultValues.PLUGIN_CONFIG_FILE_NAME);
            // if it does not exist, an exception will be thrown
        }
        Config conf = ConfigFactory.fromFile(file);
        return fromConfig(conf);
    }
}
