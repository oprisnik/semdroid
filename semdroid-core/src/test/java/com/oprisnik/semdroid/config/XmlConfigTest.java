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

package com.oprisnik.semdroid.config;


import com.oprisnik.semdroid.BaseComponentTest;
import com.oprisnik.semdroid.filter.MethodFilter;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class XmlConfigTest extends BaseComponentTest {

    protected Config mConfig;

    @Before
    public void init() throws BadConfigException, FileNotFoundException {
        mConfig = getXmlConfig("/xml-config-test.xml");
    }

    @Test
    public void testGetProperty() throws BadConfigException, FileNotFoundException {
        assertTrue(mConfig.hasProperty("test-property"));
        assertEquals("Hello world!", mConfig.getProperty("test-property"));
    }

    @Test
    public void testSubConfig() throws BadConfigException, FileNotFoundException {
        // subconfig itself is not a property
        assertFalse(mConfig.hasProperty("subconfig"));

        // get it
        Config subconfig = mConfig.getSubconfig("subconfig");
        assertNotNull(subconfig);
        assertEquals("Test", subconfig.getProperty("something"));
        assertEquals("hello", subconfig.getProperty("something-else[@attr]"));
        assertNull(mConfig.getSubconfig("nothing"));
    }

    @Test
    public void testXmlComponents() throws BadConfigException, FileNotFoundException {

        assertTrue(mConfig.hasComponent("filter1"));
        MethodFilter filter1 = mConfig.getComponent("filter1", MethodFilter.class);
        assertNotNull(filter1);

        MethodFilter filter2 = mConfig.getComponentAndInit("filter2", MethodFilter.class);
        assertNotNull(filter2);

        // default value = null -> since not-in-xml is not a component, it should be null
        MethodFilter filter3 = mConfig.getComponentAndInit("not-in-xml", MethodFilter.class, null);
        assertNull(filter3);
    }

    @Test(expected = BadConfigException.class)
    public void testInvalidXmlComponents() throws BadConfigException, FileNotFoundException {

        MethodFilter filter3 = mConfig.getComponent("filter3", MethodFilter.class);
    }

    @Test(expected = BadConfigException.class)
    public void testInvalidXmlComponents2() throws BadConfigException, FileNotFoundException {
        // even if we have a default value, we get an exception since filter3 is defined
        // but does not have a class value
        MethodFilter filter3 = mConfig.getComponent("filter3", MethodFilter.class, null);
    }

    @Test
    public void testNestedFiles() throws BadConfigException, IOException {
        assertTrue(mConfig.hasProperty("file"));
        InputStream is = mConfig.getNestedInputStream("file");

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            // check if the file can be read and if it is correct
            String data = br.readLine();
            assertEquals("This is a nested file.", data);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Test
    public void testCollections() throws BadConfigException, IOException {
        Collection<String> list = mConfig.getCollection("list.string");
        assertNotNull(list);
        assertEquals(4, list.size());
        int cur = 1;
        for (String s : list) {
            assertEquals("data" + cur, s);
            cur++;
        }
    }

    @Test
    public void testCollectionsSingleItem() throws BadConfigException, IOException {
        Collection<String> list = mConfig.getCollection("list1.string");
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("single item", list.iterator().next());
    }
}
