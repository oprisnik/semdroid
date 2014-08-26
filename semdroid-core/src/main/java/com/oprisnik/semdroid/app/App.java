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
import com.oprisnik.semdroid.app.manifest.AndroidManifest;
import com.oprisnik.semdroid.app.manifest.Permission;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Android application representation. Holds the components of the Android application:
 * {@link com.oprisnik.semdroid.app.DexClass}: all classes of the Dalvik executable.
 * {@link com.oprisnik.semdroid.app.DexMethod}: all methods of all classes.
 * <p/>
 * {@link com.oprisnik.semdroid.app.manifest.AndroidManifest}: the contents of the AndroidManifest.xml
 */
public class App extends BasicLabelable implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String mName;
    private String mHashValue;

    private File mApkFile;
    private AndroidManifest mManifest;

    private List<DexMethod> mMethods;
    private Map<String, DexMethod> mMethodMap;
    private Map<String, DexClass> mClassMap;
    private List<DexClass> mClasses;


    public App() {
        mMethods = new ArrayList<DexMethod>();
        mClasses = new ArrayList<DexClass>();
        mMethodMap = new HashMap<String, DexMethod>();
        mClassMap = new HashMap<String, DexClass>();
    }

    @Override
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getHashValue() {
        return mHashValue;
    }

    // TODO: calculate and set hash value
    public void setHashValue(String hashValue) {
        mHashValue = hashValue;
    }

    /**
     * Returns the APK file.
     *
     * @return the APK file or null if not stored
     */
    public File getApkFile() {
        return mApkFile;
    }

    public void setApkFile(File apk) {
        mApkFile = apk;
    }

    /**
     * Returns the filename of the apk.
     *
     * @return the filename or null if the APK is not stored
     */
    public String getApkFileName() {
        if (mApkFile == null)
            return null;
        return mApkFile.getName();
    }

    public AndroidManifest getManifest() {
        return mManifest;
    }

    public void setManifest(AndroidManifest manifest) {
        mManifest = manifest;
    }

    public List<DexMethod> getMethods() {
        return mMethods;
    }

    public void setMethods(List<DexMethod> methods) {
        mMethods = methods;
    }

    public void addMethod(DexMethod method) {
        mMethods.add(method);
        mMethodMap.put(method.toString(), method);
    }

    public DexMethod getMethod(String fullMethodName) {
        if (!mMethodMap.containsKey(fullMethodName)) {
            // use super class
            int index = fullMethodName.indexOf('-');
            String clazz = fullMethodName.substring(0, index);
            DexClass c = getClass(clazz);
            if (c != null) {
                DexClass superClass = c.getSuperClass();
                if (superClass != null) {
                    return getMethod(superClass.getFullName() + fullMethodName.substring(index));
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
        return mMethodMap.get(fullMethodName);
    }

    public void removeMethod(DexMethod method) {
        mMethods.remove(method);
    }

    public List<DexClass> getClasses() {
        return mClasses;
    }

    public void setClasses(List<DexClass> classes) {
        mClasses = classes;
    }

    public void addClass(DexClass clazz) {
        mClasses.add(clazz);
        mClassMap.put(clazz.getFullName(), clazz);
    }

    public DexClass getClass(String fullClassName) {
        return mClassMap.get(fullClassName);
    }

    public List<DexClass> getClassesByClassName(String className) {
        List<DexClass> classes = new ArrayList<DexClass>();
        for (DexClass c : mClasses) {
            if (c.getClassName().equals(className))
                classes.add(c);
        }
        return classes;
    }


    public List<Permission> getPermissions() {
        if (mManifest == null) {
            return null;
        }
        return mManifest.getPermissions();
    }

    public boolean hasPermission(String permission) {
        if (mManifest == null || mManifest.getPermissions() == null) {
            return false;
        }
        for (Permission p : mManifest.getPermissions()) {
            if (p.getName() != null && p.getName().equalsIgnoreCase(permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(Permission permission) {
        if (mManifest == null || mManifest.getPermissions() == null) {
            return false;
        }
        for (Permission p : mManifest.getPermissions()) {
            if (p.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("App: ");
        sb.append(getName());
        sb.append("\n  Classes:\n");
        for (DexClass c : mClasses) {
            sb.append("\n  ");
            sb.append(c.toString());
            sb.append("\n\n  ");
        }

        return sb.toString();
    }

    @Override
    public boolean hasLabels() {
        boolean hl = super.hasLabels();
        if (hl) {
            return true;
        }

        for (DexClass c : mClasses) {
            if (c.hasLabels())
                return true;
        }

        // for(DexMethod m : mMethods) {
        // if(m.hasLabels())
        // return true;
        // }
        return false;
    }

}
