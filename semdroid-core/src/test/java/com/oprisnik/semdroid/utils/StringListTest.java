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

package com.oprisnik.semdroid.utils;


import com.oprisnik.semdroid.BaseComponentTest;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * {@link com.oprisnik.semdroid.utils.StringList} test.
 */
public class StringListTest extends BaseComponentTest {

    @Test
    public void testStringList1() throws BadConfigException, IOException {
        Config config = getXmlConfig("/string-list.xml");

        // list1 with nested file
        StringList list1 = config.getComponentAndInit("string-list1", StringList.class);

        assertNotNull(list1);

        assertTrue(list1.contains("Hello World!"));
        assertTrue(list1.contains("This is another string 123."));
        assertFalse(list1.contains("Not in list :)"));

        assertTrue(list1.checkPrefix("TestIsAPrefixInTheList"));
        assertFalse(list1.checkPrefix("Hello World is not a prefix since the ! is missing"));
    }

    @Test
    public void testStringList2() throws BadConfigException, IOException {
        Config config = getXmlConfig("/string-list.xml");

        // list 2 with list defined in XML
        StringList list2 = config.getComponentAndInit("string-list2", StringList.class);

        assertNotNull(list2);

        assertTrue(list2.contains("Hello World!"));
        assertTrue(list2.contains("It works! :)"));
        assertFalse(list2.contains("Nothing"));

        assertTrue(list2.checkPrefix("Hello World! is a prefix"));
        assertFalse(list2.checkPrefix("Hello World is not a prefix since the ! is missing"));
    }

    @Test
    public void testStringListSingleItem() throws BadConfigException, IOException {
        Config config = getXmlConfig("/string-list.xml");

        StringList list = config.getComponentAndInit("string-list3", StringList.class);

        assertNotNull(list);

        assertTrue(list.contains("SingleItem"));
        assertFalse(list.contains("It works! :)"));
        assertFalse(list.contains("Nothing"));

        assertTrue(list.checkPrefix("SingleItem Hello"));
        assertFalse(list.checkPrefix("Hello World is not a prefix since the ! is missing"));
    }


}
