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

package com.oprisnik.semdroid.filter;

import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;

/**
 * Default method filter.
 * <p/>
 * <p/>
 * XML usage:
 * <p/>
 * <filter id="myId" class="com.oprisnik.semdroid.filter.DefaultMethodFilter">
 * <min-opcodes>10</min-opcodes>
 * </filter>
 */
public class DefaultMethodFilter implements MethodFilter {

    public static final String KEY_MIN_OPCODES = "min-opcodes";

    private int mMinMethodOpcodeCount = 0;

    @Override
    public void init(Config config) throws BadConfigException {
        if (config != null) {
            mMinMethodOpcodeCount = config.getInt(KEY_MIN_OPCODES, mMinMethodOpcodeCount);
        }
    }

    @Override
    public boolean use(DexMethod data) {
        if (mMinMethodOpcodeCount > 0) {
            if (data.getOpcodes().size() <= mMinMethodOpcodeCount)
                return false;
        }
        return true;
    }
}
