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

package com.oprisnik.semdroid.feature.value;

import com.oprisnik.semdroid.app.MethodCall;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;

/**
 * Use the method call name as feature value.
 */
public class MethodCallNameFVG implements MethodCallFeatureValueGenerator {

    @Override
    public void init(Config config) throws BadConfigException {

    }

    @Override
    public String getFeatureValue(MethodCall data) {
        return data.getName();
    }
}
