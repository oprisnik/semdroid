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

import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.app.LocalVariable;
import com.oprisnik.semdroid.app.MethodCall;
import com.oprisnik.semdroid.app.Opcode;
import com.oprisnik.semdroid.grouper.opcode.OpcodeGrouper;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;
import java.util.Map.Entry;

/**
 * Log application statistics (opcode count etc.) and write to a file /
 * use for feature vector extraction.
 */
public class StatisticsCollector implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private SparseHistogram<String> mMethodCalls = new SparseHistogram<String>();
    private SparseHistogram<String> mMethodCallClasses = new SparseHistogram<String>();
    private SparseHistogram<String> mOpcodes = new SparseHistogram<String>();
    private SparseHistogram<String> mPermissions = new SparseHistogram<String>();
    private SparseHistogram<String> mLocalVars = new SparseHistogram<String>();
    private SparseHistogram<String> mLocalVarTypes = new SparseHistogram<String>();

    private SparseHistogram<String> mClassNames = new SparseHistogram<String>();
    private SparseHistogram<String> mMethodNames = new SparseHistogram<String>();

    private OpcodeGrouper mOpcodeGrouper = null;

    public void clear() {
        mMethodCalls.clear();
        mMethodCallClasses.clear();
        mOpcodes.clear();
        mPermissions.clear();
        mLocalVars.clear();
        mLocalVarTypes.clear();
        mClassNames.clear();
        mMethodNames.clear();
        // we keep the opcodeGrouper!
    }

    public int getmMethodCallCount(String methodCall) {
        return mMethodCalls.getValue(methodCall);
    }

    public int getMethodCallClassCount(String className) {
        return mMethodCallClasses.getValue(className);
    }

    public int getOpcodeCount(String opcode) {
        return mOpcodes.getValue(opcode);
    }

    public int getPermissionCount(String permission) {
        return mPermissions.getValue(permission);
    }

    public int getLocalVarNameCount(String localVar) {
        return mLocalVars.getValue(localVar);
    }

    public int getLocalVarTypeCount(String localVarType) {
        return mLocalVarTypes.getValue(localVarType);
    }

    public int getClassNameCount(String className) {
        return mClassNames.getValue(className);
    }

    public int getMethodNameCount(String methodName) {
        return mMethodNames.getValue(methodName);
    }

    private int mAppCount = 0;

    public int getAppCount() {
        return mAppCount;
    }

    public void setOpcodeGrouper(OpcodeGrouper grouper) {
        mOpcodeGrouper = grouper;
    }

    public void analyze(App app) {
        mAppCount++;
        List<DexMethod> methods = app.getMethods();
        for (DexMethod m : methods) {
            mMethodNames.increase(m.getMethodName());
            List<MethodCall> mc = m.getMethodCalls();
            for (MethodCall c : mc) {
                mMethodCalls.increase(c.getName());
                mMethodCallClasses.increase(c.getClassName());
            }
            if (mOpcodeGrouper == null) {
                for (Opcode o : m.getOpcodes()) {
                    mOpcodes.increase(o.getName());
                }
            } else {
                for (Opcode o : m.getOpcodes()) {
                    String opgroup = mOpcodeGrouper.getOpcodeGroupName(o);
                    if (opgroup != null)
                        mOpcodes.increase(opgroup);
                }
            }

            for (String s : m.getPermissions()) {
                mPermissions.increase(s);
            }

            for (LocalVariable l : m.getLocalVariables()) {
                mLocalVars.increase(l.getName());
                mLocalVarTypes.increase(l.getType());
            }

        }

        for (DexClass c : app.getClasses()) {
            mClassNames.increase(c.getFullName());
        }
    }

    public void analyze(List<App> apps) {
        for (App a : apps) {
            analyze(a);
        }
    }

    public void writeTo(PrintWriter output) {

        appendAll("Invokes", mMethodCalls, output);
        appendAll("Opcodes", mOpcodes, output);
        appendAll("Permissions", mPermissions, output);
        appendAll("Local variable names", mLocalVars, output);
        appendAll("Local variable types", mLocalVarTypes, output);
        appendAll("Class names", mClassNames, output);

        appendAll("Method names", mMethodNames, output);
    }

    private void appendAll(String title, SparseHistogram<String> histo,
                           PrintWriter output) {
        List<Entry<String, Integer>> sortedm = histo.getSortedListDescending();
        output.append("--------------------\n");
        output.append(title);
        output.append('\n');
        for (Entry<String, Integer> e : sortedm) {
            output.append(e.getKey()).append("	").append(Long.toString(e.getValue())).append('\n');
        }
        output.append("--------------------\n\n\n\n\n");
    }

}
