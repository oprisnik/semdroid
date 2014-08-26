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

import java.util.Arrays;

public class MethodCall implements CodeElement {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private DexMethod mMethod;
    private String mMethodName;
    private String[] mParameterTypes;
    private App mApp;

    public MethodCall(String fullMethodName, String[] parameterTypes, App app) {
        mMethodName = fullMethodName;
        mParameterTypes = parameterTypes;
        mApp = app;
    }

    private boolean lookForMethodInApp() {
        if (mMethod != null)
            return true;
        if (mApp == null) {
            return false;
        }
        // TODO: optimize the lookup process
        mMethod = mApp.getMethod(getLocalMethodString());

        if (mMethod != null) {
            // We do not need the method name any more since we already have it
            mMethodName = null;
            mParameterTypes = null;
            return true;
        }
        return false;
    }

    public DexMethod getDexMethod() {
        lookForMethodInApp();
        return mMethod;
    }

    public boolean isSystemMethod() {
        return getDexMethod() == null;
    }

    private String getLocalMethodString() {
        return mMethodName
                + Arrays.toString(mParameterTypes).replace(';', ',');
    }

    @Override
    public String getName() {
        return lookForMethodInApp() ? mMethod.toString() : getLocalMethodString();
    }

    public String getClassName() {
        if (lookForMethodInApp()) {
            return mMethod.getDexClass().getClassName();
        } else {
            int index = mMethodName.lastIndexOf('-');
            if (index >= 0) {
                String className = mMethodName.substring(0, index);
                return className;
            }
        }
        return null;
    }

    public String getMethodName() {
        if (lookForMethodInApp()) {
            return mMethod.getMethodName();
        } else {
            int index = mMethodName.lastIndexOf('-');
            if (index >= 0) {
                String packageName = mMethodName.substring(index + 1);
                return packageName;
            }
        }
        return null;
    }

    public String getPackage() {
        if (lookForMethodInApp()) {
            return mMethod.getPackage();
        } else {
            int index = mMethodName.lastIndexOf('.');
            if (index >= 0) {
                String packageName = mMethodName.substring(0, index);
                return packageName;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MethodCall) {
            MethodCall mc = (MethodCall) obj;
            return mc.getName().equalsIgnoreCase(getName());
        }
        return super.equals(obj);
    }

}
