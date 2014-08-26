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
import com.oprisnik.semdroid.app.Opcode;
import com.oprisnik.semdroid.config.Configurable;
import com.oprisnik.semdroid.utils.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * Opcode grouper.
 */
public abstract class OpcodeGrouper implements Configurable {

    /**
     * Opcode group to ignore given opcodes.
     */
    public static final int OP_IGNORE = -1;
    public static final String OP_IGNORE_STRING = "IGNORE";

    /**
     * Returns the opcode group for the given opcode or OP_IGNORE if the opcode
     * should be ignored.
     *
     * @param opcode the opcode to group
     * @return grouped opcode name or OP_IGNORE if to be ignored
     */
    public abstract int getOpcodeGroup(int opcode);

    /**
     * Returns the opcode group for the given opcode or null if the opcode
     * should be ignored.
     *
     * @param opcode the opcode to group
     * @return grouped opcode name or null if to be ignored
     */
    public abstract String getOpcodeGroupName(int opcode);


    /**
     * Returns the opcode group for the given opcode or OP_IGNORE if the opcode
     * should be ignored.
     *
     * @param opcode the opcode to group
     * @return grouped opcode name or OP_IGNORE if to be ignored
     */
    public int getOpcodeGroup(Opcode opcode) {
        return getOpcodeGroup(opcode.getInt());
    }

    /**
     * Returns the opcode group for the given opcode or null if the opcode
     * should be ignored.
     *
     * @param opcode the opcode to group
     * @return grouped opcode name or null if to be ignored
     */
    public String getOpcodeGroupName(Opcode opcode) {
        return getOpcodeGroupName(opcode.getInt());
    }

    /**
     * Get a set of all possible opcode groups
     *
     * @return all possible opcode groups
     */
    public abstract SortedSet<Integer> getPossibleOpcodeGroups();

    /**
     * Return a grouped list of the given opcode list.
     *
     * @param opcodes the opcode list to group
     * @return grouped opcode list
     */
    public List<Integer> getGroupedList(List<Opcode> opcodes) {
        List<Integer> grouped = new ArrayList<Integer>();
        for (Opcode opcode : opcodes) {
            grouped.add(getOpcodeGroup(opcode));
        }
        return grouped;
    }

    protected static Map<Integer, String> getDexOpcodeMap() {
        try {
            // TODO: since DexOpcodeDump.map is private, we need to use reflection - maybe use own impl.
            Field field = DexOpcodeDump.class.getDeclaredField("map");
            field.setAccessible(true);
            Map<Integer, String> map = (Map<Integer, String>) field.get(null);
            field.setAccessible(false);
            return map;
        } catch (IllegalAccessException e) {
            Log.e(DefaultOpcodeGrouper.class.getName(), e.getMessage());
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            Log.e(DefaultOpcodeGrouper.class.getName(), e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
