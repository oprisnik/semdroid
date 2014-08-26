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

import com.oprisnik.semdroid.BaseComponentTest;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class WhiteAndBlackListTest extends BaseComponentTest {

    @Test
    public void testDefaultFilter() throws BadConfigException, IOException {
        Config config = getXmlConfig("/white-and-blacklist.xml");

        WhiteAndBlackList filter = config.getComponentAndInit("default", WhiteAndBlackList.class);
        assertNotNull(filter);

        assertFalse(filter.usesBlacklist());
        assertFalse(filter.usesWhitelist());

        String[] data = new String[]{"a", "", " ", "1", "abc", "Hello", "World"};

        for (String s : data) {
            assertTrue(filter.checkPrefix(s));
            assertTrue(filter.use(s));
        }
    }

    @Test
    public void testWhitelist() throws BadConfigException, IOException {
        Config config = getXmlConfig("/white-and-blacklist.xml");

        WhiteAndBlackList filter = config.getComponentAndInit("wl", WhiteAndBlackList.class);
        assertNotNull(filter);

        assertFalse(filter.usesBlacklist());
        assertTrue(filter.usesWhitelist());

        String[] data = new String[]{"a", "", " ", "1", "abc", "Hello", "World"};

        for (String s : data) {
            assertFalse(filter.checkPrefix(s));
            assertFalse(filter.use(s));
        }
        assertTrue(filter.use("Test"));
        assertFalse(filter.use("test"));
        assertTrue(filter.checkPrefix("Test"));
        assertFalse(filter.checkPrefix("test"));
        assertTrue(filter.checkPrefix("Testtest"));

        assertTrue(filter.use("Hello World!"));
        assertTrue(filter.checkPrefix("Hello World! 123"));
        assertFalse(filter.checkPrefix("Hello world! 123"));
    }

    @Test
    public void testBlacklist() throws BadConfigException, IOException {
        Config config = getXmlConfig("/white-and-blacklist.xml");

        WhiteAndBlackList filter = config.getComponentAndInit("bl", WhiteAndBlackList.class);
        assertNotNull(filter);

        assertTrue(filter.usesBlacklist());
        assertFalse(filter.usesWhitelist());

        String[] data = new String[]{"a", "", " ", "1", "abc", "Hello", "World"};

        for (String s : data) {
            assertTrue(filter.checkPrefix(s));
            assertTrue(filter.use(s));
        }
        assertFalse(filter.use("It works! :)"));

        assertTrue(filter.use("Hello World"));
        assertFalse(filter.use("Hello World!"));

        assertFalse(filter.use("Hello World!"));
        assertFalse(filter.checkPrefix("Hello World!"));
        assertFalse(filter.checkPrefix("Hello World! 123"));
        assertTrue(filter.checkPrefix("Hello World test"));
        assertTrue(filter.checkPrefix("Hello world! 123"));
    }

    @Test
    public void testWhiteAndBlacklist() throws BadConfigException, IOException {
        Config config = getXmlConfig("/white-and-blacklist.xml");

        WhiteAndBlackList filter = config.getComponentAndInit("wbl", WhiteAndBlackList.class);
        assertNotNull(filter);

        assertTrue(filter.usesBlacklist());
        assertTrue(filter.usesWhitelist());

        String[] data = new String[]{"a", "", " ", "1", "abc", "Hello", "World"};

        for (String s : data) {
            assertFalse(filter.checkPrefix(s));
            assertFalse(filter.use(s));
        }
        assertTrue(filter.use("whitelisted"));
        assertTrue(filter.use("test is whitelisted"));

        assertFalse(filter.use("test is whitelisted except for this"));
        assertFalse(filter.use("It works! :)"));

        assertTrue(filter.checkPrefix("test is whitelisted and we can write anything here"));
        assertFalse(filter.checkPrefix("test is whitelisted except for this"));
        assertFalse(filter.checkPrefix("test is whitelisted except for this :)"));
    }
}
