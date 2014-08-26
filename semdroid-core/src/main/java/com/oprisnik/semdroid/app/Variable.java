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

import com.oprisnik.semdroid.utils.DexUtils;

import java.io.Serializable;

/**
 * Basic variable (e.g., a field, or a local variable).
 */
public class Variable implements CodeElement, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String mName;
    private String mType;

    public Variable(String name, String type) {
        super();
        mName = name;
        mType = type;
    }

    @Override
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getTypeWithoutArray() {
        return DexUtils.getTypeWithoutArray(mType);
    }

    public boolean isBasicType() {
        return DexUtils.isBasicDataType(mType);
    }

    @Override
    public String toString() {
        return mType + " " + mName;
    }

    public String toJavaString() {
        return DexUtils.getJavaTypeDescriptor(mType) + " " + mName;
    }
}
