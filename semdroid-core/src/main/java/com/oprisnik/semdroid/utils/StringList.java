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

import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.config.Configurable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Simple searchable list. Can be used for white-/blacklists...
 * <p/>
 * XML usage:
 * <p/>
 * <string-list id="myId" class="com.oprisnik.semdroid.utils.StringList">
 * <list>
 * <string>crypto</string>
 * <string>sms</string>
 * </list>
 * </string-list>
 * <p/>
 * Or with a file:
 * <p/>
 * <string-list id="myId" class="com.oprisnik.semdroid.utils.StringList">
 * <file>myList.txt</string>
 * </string-list>
 */
public class StringList implements Configurable {

    public static final String KEY_FILE = "file";
    public static final String KEY_LIST = "list";
    public static final String KEY_STRING = "string";

    private Set<String> mList;

    public StringList() {
        mList = new HashSet<String>();
    }

    public StringList(InputStream stringList) throws IOException {
        this();
        loadFromStream(stringList);
    }

    public StringList(File stringList) throws IOException {
        this(new FileInputStream(stringList));
    }


    @Override
    public void init(Config config) throws BadConfigException {
        // check if we have a file-tag
        if (config.hasProperty(KEY_FILE)) {
            try {
                loadFromStream(config.getNestedInputStream(KEY_FILE));
            } catch (Exception e) {
                throw new BadConfigException("Could not init StringList: " + e.getMessage());
            }
        } else {
            Collection<String> strings = config.getCollection(KEY_LIST + "." + KEY_STRING);
            addAll(strings);
        }
    }

    public void addAll(Collection<String> data) {
        if (data != null) {
            mList.addAll(data);
        }
    }

    public void add(String s) {
        if (s != null && s.length() > 0) {
            mList.add(s);
        }
    }

    public void loadFromStream(InputStream input) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(input));
            String data;
            while ((data = br.readLine()) != null) {
                add(data);
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Checks if the string list contains the given string.
     *
     * @param s the string to use
     * @return true if the list contains the string
     */
    public boolean contains(String s) {
        return mList.contains(s);
    }

    /**
     * Checks if the given string starts with at least one string of the StringList.
     *
     * @param string the string to check
     * @return true if the string starts with at least one string of the list
     */
    public boolean checkPrefix(String string) {
        if (mList == null || mList.isEmpty()) {
            return true;
        }
        // TODO: not very efficient implementation
        for (String str : mList) {
            if (string.startsWith(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given string contains at least one string of the StringList.
     *
     * @param string the string to check
     * @return true if the given string contains at least one string of the list
     */
    public boolean checkContains(String string) {
        if (mList == null || mList.isEmpty()) {
            return true;
        }
        // TODO: not very efficient implementation
        for (String str : mList) {
            if (string.contains(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of all strings that the given string contains.
     *
     * Example:
     * Suppose the StringList is {"Hello", "ABC", "World", "Test"}.
     * getAllContainedStrings("Hello World!") returns {"Hello", "World"}.
     *
     * @param string the string to check
     * @return the list of strings contained in the given string
     */
    public List<String> getAllContainedStrings(String string) {
        List<String> list = new ArrayList<String>();
        if (mList == null || mList.isEmpty()) {
            return list;
        }
        // TODO: not very efficient implementation
        for (String str : mList) {
            if (string.contains(str)) {
                list.add(str);
            }
        }
        return list;
    }


    public Collection<String> values() {
        return mList;
    }

}
