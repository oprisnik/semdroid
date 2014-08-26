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
import com.oprisnik.semdroid.BaseComponentTest;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class ConfigurableOpcodeGrouperTest extends BaseComponentTest {

    @Test
    public void testConfigurableGrouper1() throws BadConfigException, IOException {
        Config config = getXmlConfig("/configurable-opcode-grouper.xml");

        OpcodeGrouper grouper = config.getComponentAndInit("grouper", OpcodeGrouper.class);

        assertNotNull(grouper);

        // not defined in file -> ignore
        assertEquals(OpcodeGrouper.OP_IGNORE, grouper.getOpcodeGroup(DexOpcodes.OP_NOP));
        // defined in file as IGNORE
        assertEquals(OpcodeGrouper.OP_IGNORE, grouper.getOpcodeGroup(DexOpcodes.OP_THROW));
        assertEquals(OpcodeGrouper.OP_IGNORE, grouper.getOpcodeGroup(DexOpcodes.OP_RETURN));

        // for mapped groups, we do not know the exact opcode int -> all but IGNORE possible
        assertNotEquals(OpcodeGrouper.OP_IGNORE, grouper.getOpcodeGroup(DexOpcodes.OP_IF_EQ));
        assertNotEquals(OpcodeGrouper.OP_IGNORE, grouper.getOpcodeGroup(DexOpcodes.OP_IF_NE));
        assertNotEquals(OpcodeGrouper.OP_IGNORE, grouper.getOpcodeGroup(DexOpcodes.OP_USHR_INT_LIT_X));
        assertNotEquals(OpcodeGrouper.OP_IGNORE, grouper.getOpcodeGroup(DexOpcodes.OP_NEG));

        // check names
        assertEquals("IF", grouper.getOpcodeGroupName(DexOpcodes.OP_IF_EQ));
        assertEquals("IF", grouper.getOpcodeGroupName(DexOpcodes.OP_IF_NE));
        assertEquals("ADD_INT_LIT_X", grouper.getOpcodeGroupName(DexOpcodes.OP_ADD_INT_LIT_X));

        assertNull(grouper.getOpcodeGroupName(DexOpcodes.OP_NOP));
        assertNull(grouper.getOpcodeGroupName(DexOpcodes.OP_THROW));
    }

    @Test
    public void testConfigurableGrouper2() throws BadConfigException, IOException {
        Config config = getXmlConfig("/configurable-opcode-grouper.xml");

        OpcodeGrouper grouper = config.getComponentAndInit("grouper2", OpcodeGrouper.class);

        assertNotNull(grouper);

        // not defined in file -> ignore
        assertEquals(OpcodeGrouper.OP_IGNORE, grouper.getOpcodeGroup(DexOpcodes.OP_NOP));
        // defined in file as IGNORE
        assertEquals(OpcodeGrouper.OP_IGNORE, grouper.getOpcodeGroup(DexOpcodes.OP_THROW));
        assertEquals(OpcodeGrouper.OP_IGNORE, grouper.getOpcodeGroup(DexOpcodes.OP_AGET));

        // for mapped groups, we do not know the exact opcode int -> all but IGNORE possible
        assertNotEquals(OpcodeGrouper.OP_IGNORE, grouper.getOpcodeGroup(DexOpcodes.OP_IF_EQ));
        assertNotEquals(OpcodeGrouper.OP_IGNORE, grouper.getOpcodeGroup(DexOpcodes.OP_IF_NE));
        assertNotEquals(OpcodeGrouper.OP_IGNORE, grouper.getOpcodeGroup(DexOpcodes.OP_NEG));

        // check names
        assertEquals("HELLO", grouper.getOpcodeGroupName(DexOpcodes.OP_IF_EQ));
        assertEquals("HELLO", grouper.getOpcodeGroupName(DexOpcodes.OP_IF_NE));
        assertEquals("NEG", grouper.getOpcodeGroupName(DexOpcodes.OP_NEG));

        assertNull(grouper.getOpcodeGroupName(DexOpcodes.OP_NOP));
        assertNull(grouper.getOpcodeGroupName(DexOpcodes.OP_THROW));
    }
}
