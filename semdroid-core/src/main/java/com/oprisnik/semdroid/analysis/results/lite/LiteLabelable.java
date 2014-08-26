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

package com.oprisnik.semdroid.analysis.results.lite;

import com.oprisnik.semdroid.analysis.results.Labelable;
import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.app.DexMethod;

import java.io.Serializable;

/**
 * Lightweight labelable
 */
public class LiteLabelable implements Serializable {

    private String mName;

    private String mClassName;

    private String mJavaMethodSignature;

    private boolean isClass;
    private boolean isMethod;

    public LiteLabelable(Labelable parent) {
        mName = parent.getName();

        if (parent instanceof DexMethod) {
            DexMethod data = (DexMethod) parent;
            mClassName = data.getDexClass().getFullName();
            mJavaMethodSignature = data.toJavaString();
            isMethod = true;
        } else if (parent instanceof DexClass) {
            DexClass data = (DexClass) parent;
            mClassName = data.getFullName();
            isClass = true;
        }
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getClassName() {
        return mClassName;
    }

    public void setClassName(String className) {
        mClassName = className;
    }

    public String getJavaMethodSignature() {
        return mJavaMethodSignature;
    }

    public void setJavaMethodSignature(String javaMethodSignature) {
        mJavaMethodSignature = javaMethodSignature;
    }

    public boolean isClass() {
        return isClass;
    }

    public void setClass(boolean isClass) {
        this.isClass = isClass;
    }

    public boolean isMethod() {
        return isMethod;
    }

    public void setMethod(boolean isMethod) {
        this.isMethod = isMethod;
    }
}
