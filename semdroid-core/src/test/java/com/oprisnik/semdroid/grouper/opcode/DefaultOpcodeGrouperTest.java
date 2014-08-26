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

import com.googlecode.dex2jar.DexOpcodes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultOpcodeGrouperTest {

    @Test
    public void testGetOpcodeGroup() throws Exception {
        DefaultOpcodeGrouper grouper = new DefaultOpcodeGrouper();
        for (int i = -1000; i < 10000; i++) {
            assertEquals(i, grouper.getOpcodeGroup(i));
        }
    }

    @Test
    public void testGetOpcodeGroupName() throws Exception {
        DefaultOpcodeGrouper grouper = new DefaultOpcodeGrouper();

        assertEquals("NOP", grouper.getOpcodeGroupName(DexOpcodes.OP_NOP));
        assertEquals("MOVE", grouper.getOpcodeGroupName(DexOpcodes.OP_MOVE));
        assertEquals("IF_EQ", grouper.getOpcodeGroupName(DexOpcodes.OP_IF_EQ));
        assertEquals("IGET", grouper.getOpcodeGroupName(DexOpcodes.OP_IGET));
        assertEquals("CMP", grouper.getOpcodeGroupName(DexOpcodes.OP_CMP));
    }
}