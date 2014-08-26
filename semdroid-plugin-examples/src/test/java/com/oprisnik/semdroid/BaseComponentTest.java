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
import com.oprisnik.semdroid.config.XmlConfig;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * Base component test with helper methods to retrieve files.
 */
public class BaseComponentTest {

    public File getFile(String relativeFileName) throws FileNotFoundException {
        return new File(getFileString(relativeFileName));
    }

    public String getFileString(String relativeFileName) throws FileNotFoundException {
        return getClass().getResource(relativeFileName).getFile();
    }

    public Config getXmlConfig(String filename) throws BadConfigException, FileNotFoundException {
        return new XmlConfig(getFile(filename));
    }
}
