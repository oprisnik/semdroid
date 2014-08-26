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

import com.googlecode.dex2jar.util.DexOpcodeDump;
import com.oprisnik.semdroid.config.Config;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Default opcode grouper which performs no grouping.
 */
public class DefaultOpcodeGrouper extends OpcodeGrouper {

    protected SortedSet<Integer> mSortedSet;

    @Override
    public void init(Config config) {
        mSortedSet = new TreeSet<Integer>();
        Map<Integer, String> map = getDexOpcodeMap();
        mSortedSet.addAll(map.keySet());
    }

    @Override
    public int getOpcodeGroup(int opcode) {
        return opcode;
    }

    @Override
    public String getOpcodeGroupName(int opcode) {
        return DexOpcodeDump.dump(opcode);
    }

    @Override
    public SortedSet<Integer> getPossibleOpcodeGroups() {
        return mSortedSet;
    }
}
