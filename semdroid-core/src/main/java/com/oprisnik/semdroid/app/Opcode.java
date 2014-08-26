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

package com.oprisnik.semdroid.app;

import com.googlecode.dex2jar.util.DexOpcodeDump;

/**
 * Opcode.
 *
 */
public class Opcode implements CodeElement {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int mOpcode;

    protected Opcode(int opcode) {
        mOpcode = opcode;
    }

    public static Opcode get(int opcode) {
        final int offset = 128;
        if (opcode >= -128 && opcode <= 127) { // must cache
            return OpcodeCache.cache[opcode + offset];
        }
        return new Opcode(opcode);
    }

    public int getInt() {
        return mOpcode;
    }

    @Override
    public String getName() {

        return DexOpcodeDump.dump(mOpcode);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Opcode) {
            return ((Opcode) obj).mOpcode == mOpcode;
        }
        return super.equals(obj);
    }

    private static class OpcodeCache {
        static final Opcode[] cache = new Opcode[-(-128) + 127 + 1];

        static {
            for (int i = 0; i < cache.length; i++) {
                cache[i] = new Opcode(i - 128);
            }
        }

        private OpcodeCache() {
        }
    }
}