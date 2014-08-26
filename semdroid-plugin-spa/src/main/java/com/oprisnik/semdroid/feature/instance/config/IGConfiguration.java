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

package com.oprisnik.semdroid.feature.instance.config;

import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.config.Configurable;

/**
 * Configuration for a configurable instance generator.
 * <p/>
 * If values are added to this config, all three instance generators have to be updated as well:
 * ConfigurableAIG, ConfigurableCIG and ConfigurableMIG;
 */
public class IGConfiguration implements Configurable {

    public static final String ADD_OPCODE_HISTOGRAM = "add-opcode-histogram";
    public static final String NORMALIZE_OPCODE_HISTOGRAM = "normalize-opcode-histogram";
    public static final String ADD_OPCODES = "add-opcodes";
    public static final String ADD_OPCODE_COUNT = "add-opcode-count";

    public static final String ADD_METHOD_CALLS = "add-method-calls";
    public static final String ADD_METHOD_CALL_COUNT = "add-method-call-count";

    public static final String ADD_BASIC_LOCAL_VAR_HISTOGRAM = "add-basic-local-var-histogram";
    public static final String NORMALIZE_BASIC_LOCAL_VAR_HISTOGRAM = "normalize-basic-local-var-histogram";
    public static final String ADD_LOCAL_VARS = "add-local-vars";
    public static final String ADD_LOCAL_VAR_COUNT = "add-local-var-count";

    public static final String ADD_PERMISSIONS = "add-permissions";

    public boolean addOpcodeHistogram = false;
    public boolean normalizeOpcodeHistogram = true;
    public boolean addOpcodes = false;
    public boolean addOpcodeCount = false;


    public boolean addMethodCalls = false;
    public boolean addMethodCallCount = false;


    public boolean addBasicLocalVarHistogram = false;
    public boolean normalizeBasicLocalVarHistogram = false;
    public boolean addLocalVars = false;
    public boolean addLocalVarCount = false;

    public boolean addPermissions = false;

    @Override
    public void init(Config config) throws BadConfigException {
        addOpcodeHistogram = config.getBoolean(ADD_OPCODE_HISTOGRAM, addOpcodeHistogram);
        normalizeOpcodeHistogram = config.getBoolean(NORMALIZE_OPCODE_HISTOGRAM, normalizeOpcodeHistogram);
        addOpcodes = config.getBoolean(ADD_OPCODES, addOpcodes);
        addOpcodeCount = config.getBoolean(ADD_OPCODE_COUNT, addOpcodeCount);

        addMethodCalls = config.getBoolean(ADD_METHOD_CALLS, addMethodCalls);
        addMethodCallCount = config.getBoolean(ADD_METHOD_CALL_COUNT, addMethodCallCount);

        addBasicLocalVarHistogram = config.getBoolean(ADD_BASIC_LOCAL_VAR_HISTOGRAM, addBasicLocalVarHistogram);
        normalizeBasicLocalVarHistogram = config.getBoolean(NORMALIZE_BASIC_LOCAL_VAR_HISTOGRAM, normalizeBasicLocalVarHistogram);
        addLocalVars = config.getBoolean(ADD_LOCAL_VARS, addLocalVars);
        addLocalVarCount = config.getBoolean(ADD_LOCAL_VAR_COUNT, addLocalVarCount);

        addPermissions = config.getBoolean(ADD_PERMISSIONS, addPermissions);
    }
}
