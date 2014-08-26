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

package com.oprisnik.semdroid.app;

/**
 * Class field representation.
 */
public class Field extends Variable {

    private DexClass mClass;
    private int mAccessFlags;

    public Field(String name, String type, DexClass clazz, int accessFlags) {
        super(name, type);
        mClass = clazz;
        mAccessFlags = accessFlags;
    }

    /**
     * Get the access flags for the method.
     *
     * @return the access flags
     * @see com.oprisnik.semdroid.app.AccessFlags
     */
    public int getAccessFlags() {
        return mAccessFlags;
    }

    /**
     * Set the access flags for the method.
     *
     * @param accessFlags the access flags
     * @see com.oprisnik.semdroid.app.AccessFlags
     */
    public void setAccessFlags(int accessFlags) {
        mAccessFlags = accessFlags;
    }

    public DexClass getDexClass() {
        return mClass;
    }

    public void setDexClass(DexClass clazz) {
        mClass = clazz;
    }
}
