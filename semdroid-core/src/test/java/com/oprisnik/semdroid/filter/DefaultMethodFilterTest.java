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
import com.oprisnik.semdroid.app.Opcode;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DefaultMethodFilterTest extends BaseComponentTest {

    @Test
    public void testDefaultMethodFilter() throws BadConfigException, IOException {
        Config config = getXmlConfig("/default-method-filter.xml");

        DefaultMethodFilter filter = config.getComponentAndInit(DefaultMethodFilter.class);
        assertNotNull(filter);

        // threshold = 10
        assertFalse(filter.use(getMethod(0)));
        assertFalse(filter.use(getMethod(8)));
        assertFalse(filter.use(getMethod(1)));
        assertFalse(filter.use(getMethod(10)));
        assertTrue(filter.use(getMethod(22)));
        assertTrue(filter.use(getMethod(14123)));
    }

    public DexMethod getMethod(int numberOfOpcodes) {
        App app = new App();
        DexClass clazz = new DexClass(app, "MyClass");
        DexMethod method = new DexMethod("doSomething", clazz, "V",
                null, AccessFlags.PUBLIC);
        clazz.addMethod(method);
        app.addMethod(method);
        for (int i = 0; i < numberOfOpcodes; i++) {
            method.addOpcode(Opcode.get(0));
        }
        return method;
    }
}
