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
import com.oprisnik.semdroid.app.AccessFlags;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.app.MethodCall;
import com.oprisnik.semdroid.app.Opcode;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class MethodCallWhiteAndBlacklistFilterTest extends BaseComponentTest {

    @Test
    public void testDefaultMethodFilter() throws BadConfigException, IOException {
        Config config = getXmlConfig("/method-call-white-and-blacklist-filter.xml");

        MethodCallFilter filter = config.getComponentAndInit("method-call-filter", MethodCallFilter.class);
        assertNotNull(filter);

        App app = new App();

        assertFalse(filter.use(new MethodCall("com.oprisnik.semdroid.test.Class-onReceive", new String[0], app)));

        assertTrue(filter.use(new MethodCall("java.math.BigInteger-onReceive", new String[0], app)));
    }

}