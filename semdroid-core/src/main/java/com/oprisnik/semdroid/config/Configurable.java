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

package com.oprisnik.semdroid.config;

/**
 * Configurable interface.
 * Classes can implement this interface and can then configure themselves by querying the given
 * {@link com.oprisnik.semdroid.config.Config} object.
 *
 */
public interface Configurable {

    /**
     * Initialize the analysis plugin with the given configuration.
     *
     * @param config the configuration to use
     * @throws BadConfigException if the configuration is not valid
     */
    public void init(Config config) throws BadConfigException;
}
