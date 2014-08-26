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
import com.oprisnik.semdroid.utils.DexUtils;
import com.oprisnik.semdroid.utils.SparseIntHistogram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Dex method.
 */
public class DexMethod extends BasicLabelable implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String mJavaCode;
    private String mMethodName;
    private DexClass mClass;
    private String mReturnType;
    private String[] mParameterTypes;

    private List<String> mPermissions;
    private App mApp;

    private List<CodeElement> mCode;
    private List<Opcode> mOpcodes;
    private List<MethodCall> mCalledMethods;
    private List<LocalVariable> mLocalVariables;
    // TODO: annotations

    private int mAccessFlags;

    public DexMethod(String methodName, DexClass clazz, String returnType,
                     String[] parameterTypes, int accessFlags) {
        mClass = clazz;
        if (clazz != null) {
            mApp = clazz.getApp();
        }
        mMethodName = methodName;
        mReturnType = returnType;
        mParameterTypes = parameterTypes;

        mCode = new ArrayList<CodeElement>();
        mOpcodes = new ArrayList<Opcode>();
        mCalledMethods = new ArrayList<MethodCall>();
        mLocalVariables = new ArrayList<LocalVariable>();
        mPermissions = new ArrayList<String>();

        mAccessFlags = accessFlags;
    }

    public List<CodeElement> getCode() {
        return mCode;
    }

    public List<MethodCall> getMethodCalls() {
        return mCalledMethods;
    }

    public List<MethodCall> getMethodCalls(int methodCallDepth) {
        if (methodCallDepth <= 0) {
            return getMethodCalls();
        }
        List<MethodCall> methodCalls = new ArrayList<MethodCall>();
        methodCalls.addAll(getMethodCalls());
        for (MethodCall c : getMethodCalls()) {
            if (!c.isSystemMethod()) {
                methodCalls.addAll(c.getDexMethod().getMethodCalls(methodCallDepth - 1));
            }
        }
        return methodCalls;
    }

    public void addCalledMethod(MethodCall method) {
        mCode.add(method);
        mCalledMethods.add(method);
    }

    public List<Opcode> getOpcodes() {
        return mOpcodes;
    }

    public List<Opcode> getOpcodes(int methodCallDepth) {
        if (methodCallDepth <= 0) {
            return getOpcodes();
        }
        List<Opcode> op = new ArrayList<Opcode>();
        op.addAll(getOpcodes());
        for (MethodCall c : getMethodCalls()) {
            if (!c.isSystemMethod()) {
                op.addAll(c.getDexMethod().getOpcodes(methodCallDepth - 1));
            }
        }
        return op;
    }

    public void addOpcode(Opcode op) {
        mCode.add(op);
        mOpcodes.add(op);
    }

    public List<LocalVariable> getLocalVariables() {
        return mLocalVariables;
    }

    public List<LocalVariable> getLocalVariables(int methodCallDepth) {
        if (methodCallDepth <= 0) {
            return getLocalVariables();
        }
        List<LocalVariable> vars = new ArrayList<LocalVariable>();
        vars.addAll(getLocalVariables());
        for (MethodCall c : getMethodCalls()) {
            if (!c.isSystemMethod()) {
                vars.addAll(c.getDexMethod().getLocalVariables(methodCallDepth - 1));
            }
        }
        return vars;
    }

    public void addLocalVariable(LocalVariable var) {
        mCode.add(var);
        mLocalVariables.add(var);
    }

    public String getJavaCode() {
        return mJavaCode;
    }

    public void setJavaCode(String javaCode) {
        mJavaCode = javaCode;
    }

    public String getMethodName() {
        return mMethodName;
    }

    public void setMethodName(String methodName) {
        mMethodName = methodName;
    }

    public DexClass getDexClass() {
        return mClass;
    }

    public void setDexClass(DexClass clazz) {
        mClass = clazz;
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

    public List<String> getPermissions() {
        return mPermissions;
    }

    public void setPermissions(List<String> permissions) {
        mPermissions = permissions;
    }

    public List<String> getPermissions(int methodCallDepth) {
        if (methodCallDepth <= 0) {
            return getPermissions();
        }
        List<String> permissions = new ArrayList<String>();
        permissions.addAll(getPermissions());
        for (MethodCall c : getMethodCalls()) {
            if (!c.isSystemMethod()) {
                permissions.addAll(c.getDexMethod().getPermissions(methodCallDepth - 1));
            }
        }
        return permissions;
    }

    public void addPermission(String permission) {
        mPermissions.add(permission);
    }

    public String getReturnType() {
        return mReturnType;
    }

    public String[] getParameterTypes() {
        return mParameterTypes;
    }

    public String getPackage() {
        return mClass.getPackage();
    }

    public App getApp() {
        return mApp;
    }

    public SparseIntHistogram getOpcodeHistogram() {

        SparseIntHistogram sparseIntHistogram = new SparseIntHistogram();
        for (Opcode op : mOpcodes) {
            sparseIntHistogram.increase(op.getInt());
        }
        return sparseIntHistogram;
    }

    public SparseIntHistogram getOpcodeHistogram(int methodCallDepth) {
        SparseIntHistogram sparseIntHistogram = new SparseIntHistogram();
        List<Opcode> data = getOpcodes(methodCallDepth);
        for (Opcode op : data) {
            sparseIntHistogram.increase(op.getInt());
        }
        return sparseIntHistogram;
    }


    @Override
    public String toString() {
        // TODO: faster method
        return mClass.getFullName() + "-" + mMethodName
                + Arrays.toString(mParameterTypes).replace(';', ',');
    }

    public String toJavaString() {
        StringBuilder name = new StringBuilder();
        name.append(DexUtils.getJavaTypeDescriptor(mReturnType));
        name.append(" ");
        name.append(mMethodName);
        name.append("(");
        for (int i = 0; i < mParameterTypes.length; i++) {
            name.append(DexUtils.getJavaTypeDescriptor(mParameterTypes[i]));
            if (i != mParameterTypes.length - 1) {
                name.append(", ");
            }
        }
        name.append(")");

        // to debug opcode size uncomment:
//        name.append(" ");
//        name.append(getOpcodes().size());
//        name.append(" d1 ");
//        name.append(getOpcodes(1).size());

        return name.toString();
    }

    @Override
    public String getName() {
        return mClass.getFullName() + ": " + toJavaString();
    }

}