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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Abstract config interface.
 * It allows to retrieve various properties, subconfigurations, and components.
 */
public abstract class Config {

    public abstract void saveTo(OutputStream output) throws Exception;

    public abstract String getProperty(String key);

    public abstract boolean hasProperty(String key);

    public abstract boolean getBoolean(String key, boolean defaultValue);

    public abstract int getInt(String key, int defaultValue);

    public abstract Config getSubconfig(String key);

    public abstract void setProperty(String key, String value);

    public abstract InputStream getNestedInputStream(String key) throws BadConfigException;

    public abstract OutputStream getNestedOutputStream(String key) throws BadConfigException;

    /**
     * Get a collection of values.
     * <p/>
     * Example:
     * <p/>
     * <list>
     * <string>data 1</string>
     * <string>data 2</string>
     * </list>
     * <p/>
     * By calling getCollection("list.string"), you get a collection containing "data 1" and "data 2".
     *
     * @param key the key for the collection
     * @return the string collection
     */
    public abstract Collection<String> getCollection(String key);

    public abstract <U> U getComponent(Class<U> baseInterface) throws BadConfigException;

    public abstract <U> U getComponent(String key, Class<U> baseInterface) throws BadConfigException;

    public <U> U getComponent(String key, Class<U> baseInterface, Class<? extends U> defaultImplementation)
            throws BadConfigException {
        if (hasComponent(key)) {
            return getComponent(key, baseInterface);
        }
        try {
            if (defaultImplementation == null) {
                return null;
            }
            return defaultImplementation.newInstance();
        } catch (Exception e) {
            throw new BadConfigException("Could not instantiate default implementation "
                    + defaultImplementation);
        }
    }

    public <U extends Configurable> U getComponentAndInit(Class<U> baseInterface) throws BadConfigException {
        U instance = getComponent(baseInterface);
        instance.init(this);
        return instance;
    }

    public <U extends Configurable> U getComponentAndInit(String key, Class<U> baseInterface)
            throws BadConfigException {
        Config conf = getSubconfig(key);
        if (conf == null) {
            throw new BadConfigException("Could not load component for key: " + key);
        }
        U instance = conf.getComponent(baseInterface);
        instance.init(conf);
        return instance;
    }

    public <U extends Configurable> U getComponentAndInit(String key, Class<U> baseInterface, Class<? extends U> defaultImplementation)
            throws BadConfigException {
        Config conf = getSubconfig(key);
        U instance;
        if (conf == null) {
            try {
                if (defaultImplementation == null) {
                    return null;
                }
                instance = defaultImplementation.newInstance();
            } catch (Exception e) {
                throw new BadConfigException("Could not instantiate default implementation " + defaultImplementation);
            }
        } else {
            instance = conf.getComponent(baseInterface);
        }
        instance.init(conf);
        return instance;
    }


    public boolean hasComponent(String key) {
        Config conf = getSubconfig(key);
        return conf != null;
    }


    public String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        return (val == null) ? defaultValue : val;
    }

    public Object readObjectFromStream(String fileKey)
            throws BadConfigException, ClassNotFoundException, IOException {
        Object o = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(getNestedInputStream(fileKey));
            o = ois.readObject();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (Exception e) {
                }
            }
        }
        return o;
    }
}
