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

import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.config.Config;

/**
 * Class size class filter. Only uses classes with a minimum number of opcodes.
 */
public class ClassSizeClassFilter implements ClassFilter {

    public static final String KEY_MIN_OPCODES = "min-opcodes";

    private int mMinOpcodeCount = 0;

    @Override
    public void init(Config config) {
        if (config != null) {
            mMinOpcodeCount = config.getInt(KEY_MIN_OPCODES, mMinOpcodeCount);
        }
    }

    @Override
    public boolean use(DexClass data) {
        if (mMinOpcodeCount > 0) {
            if (data.getOpcodeCount() <= mMinOpcodeCount)
                return false;
        }
        return true;
    }
}
