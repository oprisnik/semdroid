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

package com.oprisnik.semdroid.grouper.opcode;

import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Opcode grouper.
 */
public abstract class FieldBasedOpcodeGrouper extends OpcodeGrouper {

    private Map<Integer, String> mMap;
    private SortedSet<Integer> mSortedSet;

    @Override
    public void init(Config config) throws BadConfigException {
        if (mMap == null) {
            try {
                java.lang.reflect.Field[] fs = this.getClass().getFields();
                mMap = new HashMap<Integer, String>(fs.length);
                mSortedSet = new TreeSet<Integer>();
                for (java.lang.reflect.Field f : fs) {
                    f.setAccessible(true);
                    if (f.getName().startsWith("OP_")
                            && !f.getName().equalsIgnoreCase("OP_IGNORE")) {
                        Integer value = f.getInt(null);
                        mMap.put(value, f.getName()
                                .substring(3));
                        mSortedSet.add(value);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new BadConfigException(e.getMessage());
            }
        }
    }

    public SortedSet<Integer> getPossibleOpcodeGroups() {
        return mSortedSet;
    }

    @Override
    public String getOpcodeGroupName(int opcode) {
        return mMap.get(opcode);
    }
}
