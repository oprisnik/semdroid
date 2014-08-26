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

package com.oprisnik.semdroid;

import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.config.ConfigFactory;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * CLI config parameters.
 *
 */
public class CliConfig {

    public static final String DEFAULT_CLI_CONFIG_FILE = "config"+ File.separator + "cli.xml";

    public static final String SEMDROID_CONFIG_FILE_NAME = "config" + File.separator + "semdroid.xml";


    public class Analysis {
        public static final String SUBCONFIG_TAG = "analysis";
        public static final String XSL = "xsl";
        public static final String RESULTS = "results";

        private Analysis() {
        }
    }

    public class Evaluation {
        public static final String SUBCONFIG_TAG = "evaluation";
        public static final String XSL = "xsl";
        public static final String RESULTS = "results";

        private Evaluation() {
        }
    }

    public static Config getSemdroidConfig() throws Exception {
        return ConfigFactory.fromFile(SEMDROID_CONFIG_FILE_NAME);
    }

    public static Config getCliConfig() throws FileNotFoundException, BadConfigException {
        return ConfigFactory.fromFile(DEFAULT_CLI_CONFIG_FILE);
    }

    private CliConfig() {
    }
}
