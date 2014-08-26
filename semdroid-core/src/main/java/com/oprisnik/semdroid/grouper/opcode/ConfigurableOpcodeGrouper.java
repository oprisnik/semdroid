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
import com.oprisnik.semdroid.utils.StringList;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Configurable opcode grouper.
 * The configuration can be defined via XML, similar to {@link com.oprisnik.semdroid.utils.StringList}.
 */
public class ConfigurableOpcodeGrouper extends OpcodeGrouper {

    // we only calculate map once
    private static Map<String, Integer> sOpcodeNameMap;

    private Map<Integer, OpcodeGroup> mMapping;
    private Map<String, OpcodeGroup> mGroups;
    private SortedSet<Integer> mPossibleGroups;

    private void setupOpcodeNameMap() {
        if (sOpcodeNameMap == null) {
            sOpcodeNameMap = new HashMap<String, Integer>();
            for (Map.Entry<Integer, String> e : getDexOpcodeMap().entrySet()) {
                sOpcodeNameMap.put(e.getValue(), e.getKey());
            }
        }
    }

    @Override
    public void init(Config config) throws BadConfigException {
        mGroups = new HashMap<String, OpcodeGroup>();
        mMapping = new HashMap<Integer, OpcodeGroup>();
        mPossibleGroups = new TreeSet<Integer>();

        setupOpcodeNameMap();
        StringList list = new StringList();
        list.init(config);
        for (String s : list.values()) {
            String[] data = s.split("=");
            String opcode = data[0].replace(" ", "");
            String group = data[1].replace(" ", "");
//          Log.d(TAG, "Mapping '" + opcode + "' = '" + group+"'");
            if (!OP_IGNORE_STRING.equalsIgnoreCase(group)) {

                Integer opcodeInt = sOpcodeNameMap.get(opcode);
                OpcodeGroup g = mGroups.get(group);
                if (g == null) {

                    g = new OpcodeGroup();
                    g.name = group;
                    // we use the opcode integer for group name since opcode guaranteed to be used only once
                    g.group = opcodeInt;
                    mGroups.put(group, g);
                    mPossibleGroups.add(g.group);
                }
//              Log.d(TAG, "ADDED " + opcodeInt);
                mMapping.put(opcodeInt, g);
            }
            //else {
//            we do nothing
//                Log.d(TAG, "IGNORE");
//        }
        }
    }

    @Override
    public int getOpcodeGroup(int opcode) {
        OpcodeGroup g = mMapping.get(opcode);
        if (g == null)
            return OP_IGNORE;
        return g.group;
    }

    @Override
    public String getOpcodeGroupName(int opcode) {
        OpcodeGroup g = mMapping.get(opcode);
        if (g == null)
            return null;
        return g.name;
    }

    @Override
    public SortedSet<Integer> getPossibleOpcodeGroups() {
        return mPossibleGroups;
    }

    public class OpcodeGroup {
        int group;
        String name;
    }
}
