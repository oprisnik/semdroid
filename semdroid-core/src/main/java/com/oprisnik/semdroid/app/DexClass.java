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

import com.oprisnik.semdroid.analysis.results.BasicLabelable;
import com.oprisnik.semdroid.analysis.results.Label;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Android application class.
 */
public class DexClass extends BasicLabelable implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private List<DexMethod> mMethods;
    private List<Field> mFields;
    // TODO: Annotations
    private DexClass mSuperClass;
    private String mSuperClassString;

    private String mName;
    private String mPackage;

    private App mApp;

    public DexClass(App app, String fullName) {
        mApp = app;
        mMethods = new ArrayList<DexMethod>();
        mFields = new ArrayList<Field>();
        setFullName(fullName);
    }

    public DexClass getSuperClass() {
        // use if superclass available
        if (mSuperClass == null && mSuperClassString != null) {
            setSuperClass(mApp.getClass(mSuperClassString));
        }
        return mSuperClass;
    }

    /**
     * Returns the name of the first superclass not defined by the application.
     *
     * @return the name of the first superclass not defined by the application.
     */
    public String getFirstExternalSuperClass() {
        DexClass superClass = getSuperClass();
        // to prevent infinite loops
        if (superClass != null) {
            return superClass.getFirstExternalSuperClass();
        }
        return getSuperClassString();
    }

    public void setSuperClass(DexClass superClass) {
        mSuperClass = superClass;
        if (superClass != null)
            mSuperClassString = null;
    }

    public void setSuperClass(String superClass) {
        mSuperClassString = superClass;
    }

    public String getSuperClassString() {
        if (mSuperClass != null) {
            return mSuperClass.getClassName();
        }
        return mSuperClassString;
    }

    public List<DexMethod> getMethods() {
        return mMethods;
    }

    public void setMethods(List<DexMethod> methods) {
        mMethods = methods;
    }

    public void addMethod(DexMethod method) {
        mMethods.add(method);
    }

    public String getFullName() {
        return mPackage + "." + mName;
    }

    public String getClassName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setFullName(String fullName) {
        int index = fullName.lastIndexOf('.');
        if (index != -1) {
            setName(fullName.substring(index + 1));
            setPackage(fullName.substring(0, index));
        } else {
            setName(fullName);
            // Log.e(TAG, "Default package: " + fullName);
        }
    }

    public String getPackage() {
        return mPackage;
    }

    public void setPackage(String package1) {
        mPackage = package1;
    }

    public List<Field> getFields() {
        return mFields;
    }

    public void addField(Field field) {
        mFields.add(field);
    }

    public void setFields(List<Field> fields) {
        mFields = fields;
    }

    public App getApp() {
        return mApp;
    }

    public void setApp(App app) {
        mApp = app;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Class: ");
        sb.append(getFullName());
        sb.append("\n  Super: ");
        sb.append(getSuperClassString());
        sb.append("\n  Fields:");
        for (Field f : mFields) {
            sb.append("\n   ");
            sb.append(f.toJavaString());
        }
        sb.append("\n  Methods:");
        for (DexMethod m : mMethods) {
            sb.append("\n   ");
            sb.append(m.toJavaString());
            for (String s : m.getPermissions()) {
                sb.append("\n     Uses permission: ").append(s);
            }
            if (m.getLabels() != null)
                for (Label l : m.getLabels()) {
                    sb.append("\n     Label: ").append(l);
                }
        }
        return sb.toString();
    }

    @Override
    public String getName() {
        return getFullName();
    }

    @Override
    public boolean hasLabels() {
        boolean hl = super.hasLabels();
        if (hl) {
            return true;
        }

        for (DexMethod m : mMethods) {
            if (m.hasLabels())
                return true;
        }
        return false;
    }

    public int getOpcodeCount() {
        int count = 0;
        for (DexMethod m : mMethods) {
            count += m.getOpcodes().size();
        }
        return count;
    }

}
