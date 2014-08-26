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

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertNotNull;

public class ConfigFactoryTest extends BaseComponentTest {

    @Test
    public void testFromFile() throws Exception {
        Config conf = ConfigFactory.fromFile(getFileString("/xml-config-test.xml"));
        assertNotNull(conf);
    }

    @Test
    public void testFromFile1() throws Exception {
        Config conf = ConfigFactory.fromFile(getFile("/xml-config-test.xml"));
        assertNotNull(conf);
    }

    @Test(expected = FileNotFoundException.class)
    public void testInvalidFileString() throws Exception {
        Config conf = ConfigFactory.fromFile("does-not-exist");
    }

    @Test(expected = FileNotFoundException.class)
    public void testInvalidFile() throws Exception {
        Config conf = ConfigFactory.fromFile(new File("does-not-exist"));
    }
}