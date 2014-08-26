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

package com.oprisnik.semdroid.app.parser.dex.visitors;

import com.googlecode.dex2jar.DexLabel;
import com.googlecode.dex2jar.Field;
import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.visitors.DexCodeVisitor;
import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.app.LocalVariable;
import com.oprisnik.semdroid.app.MethodCall;
import com.oprisnik.semdroid.app.Opcode;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.filter.StringFilter;
import com.oprisnik.semdroid.permissions.PermissionMap;
import com.oprisnik.semdroid.permissions.PermissionMapFactory;
import com.oprisnik.semdroid.utils.MethodUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Dex code visitor
 */
public class DexCodeOpcodeVisitor extends BasicVisitor implements
        DexCodeVisitor {

    public static final String KEY_METHOD_CALL_FILTER = "method-call-filter";
    public static final String KEY_SAVE_UNIQUE_DATA_ONLY = "save-unique-data-only";

    private static final String TAG = "DexCodeOpcodeVisitor";

    private DexMethod mMethodReport;

    private PermissionMap mPermissionMap;

    // each Opcode, method call... only recorded once
    private boolean mUniqueDataOnly = false;

    // what do we want to log
    private boolean logArrayStmt = true;
    private boolean logBinopLitXStmt = true;
    private boolean logBinopStmt = true;
    private boolean logClassStmt = true;
    private boolean logCmpStmt = true;
    private boolean logConstStmt = true;
    private boolean logFieldStmt = true;
    private boolean logFillArrayStmt = true;
    private boolean logFilledNewArrayStmt = true;
    private boolean logJumpStmt = true;
    private boolean logLookupSwitchStmt = true;
    private boolean logMethodStmt = true;
    private boolean logMonitorStmt = true;
    private boolean logMoveStmt = true;
    private boolean logReturnStmt = true;
    private boolean logTableSwitchStmt = true;
    private boolean logUnopStmt = true;
    private boolean logTryCatchStmt = true;
    private boolean logVisitArguments = true;
    private boolean logVisitLabel = true;
    private boolean logVisitLineNumber = true;
    private boolean logVisitLocalVariable = true;

    private boolean logMethodPermissions = true;

    private Set<Opcode> mUsedOPCodes = new HashSet<Opcode>();
    private Set<MethodCall> mUsedMethods = new HashSet<MethodCall>();
    private Set<LocalVariable> mLocalVariables = new HashSet<LocalVariable>();

    private StringFilter mMethodCallFilter;

    public DexCodeOpcodeVisitor(BasicVisitor parent) {
        super(parent);
    }

    @Override
    public void init(Config config) throws BadConfigException {
        super.init(config);
        if (config != null) {

            mMethodCallFilter = config.getComponentAndInit(KEY_METHOD_CALL_FILTER, StringFilter.class, null);

            mUniqueDataOnly = config.getBoolean(KEY_SAVE_UNIQUE_DATA_ONLY, mUniqueDataOnly);

            logArrayStmt = config.getBoolean("logArrayStmt", logArrayStmt);
            logBinopLitXStmt = config.getBoolean("logBinopLitXStmt", logBinopLitXStmt);
            logBinopStmt = config.getBoolean("logBinopStmt", logBinopStmt);
            logClassStmt = config.getBoolean("logClassStmt", logClassStmt);
            logCmpStmt = config.getBoolean("logCmpStmt", logCmpStmt);
            logConstStmt = config.getBoolean("logConstStmt", logConstStmt);
            logFieldStmt = config.getBoolean("logFieldStmt", logFieldStmt);
            logFillArrayStmt = config.getBoolean("logFillArrayStmt", logFillArrayStmt);
            logFilledNewArrayStmt = config.getBoolean(
                    "logFilledNewArrayStmt", logFilledNewArrayStmt);
            logJumpStmt = config.getBoolean("logJumpStmt", logJumpStmt);
            logLookupSwitchStmt = config.getBoolean("logLookupSwitchStmt",
                    logLookupSwitchStmt);
            logMethodStmt = config.getBoolean("logMethodStmt", logMethodStmt);
            logMonitorStmt = config.getBoolean("logMonitorStmt", logMonitorStmt);
            logMoveStmt = config.getBoolean("logMoveStmt", logMoveStmt);
            logReturnStmt = config.getBoolean("logReturnStmt", logReturnStmt);
            logTableSwitchStmt = config.getBoolean("logTableSwitchStmt",
                    logTableSwitchStmt);
            logUnopStmt = config.getBoolean("logUnopStmt", logUnopStmt);
            logTryCatchStmt = config.getBoolean("logTryCatchStmt", logTryCatchStmt);
            logVisitArguments = config.getBoolean("logVisitArguments",
                    logVisitArguments);
            logVisitLabel = config.getBoolean("logVisitLabel", logVisitLabel);
            logVisitLineNumber = config.getBoolean("logVisitLineNumber",
                    logVisitLineNumber);
            logVisitLocalVariable = config.getBoolean(
                    "logVisitLocalVariable", logVisitLocalVariable);

            logMethodPermissions = config.getBoolean(
                    "logMethodPermissions", logMethodPermissions);
        }

        if (logMethodPermissions) {
            mPermissionMap = PermissionMapFactory.getPermissionMap();
            mPermissionMap.init(config);
        }
    }

    public void setMethodFeatureReport(DexMethod report) {
        mMethodReport = report;
    }

    public void logOpcode(int opcode) {
        Opcode op = Opcode.get(opcode);
        if (mUniqueDataOnly) {
            if (!mUsedOPCodes.contains(op)) {
                mUsedOPCodes.add(op);
                mMethodReport.addOpcode(op);
                //} else {
                //    System.out.println("OPCODE ALREADY ADDED " + opcodeS);
            }
        } else {
            mMethodReport.addOpcode(op);
        }
    }

    public void logMethod(int opcode, Method method) {
        // log the opcode
        logOpcode(opcode);
        // TODO: better convert permission map so that MethodUtils not needed
        if (logMethodPermissions) {
            String javaMethod = MethodUtils
                    .getJavaDefinitionWithoutRetVal(method);
            if (mPermissionMap.usesPermission(javaMethod)) {
                String permission = mPermissionMap.getPermission(javaMethod);
                if (permission != null) {
                    mMethodReport.addPermission(permission);
                }
            }
        }
        String owner = method.getOwner();
        if (mMethodCallFilter == null || mMethodCallFilter.checkPrefix(owner)) {
            // if(owner != null) {
            String s = owner.substring(1, owner.length() - 1).replace('/', '.')
                    + "-" + method.getName();
            MethodCall mc = new MethodCall(s, method.getParameterTypes(),
                    getApp());
            if (mUniqueDataOnly) {
                if (!mUsedMethods.contains(mc)) {
                    mMethodReport.addCalledMethod(mc);
                    mUsedMethods.add(mc);
                }
            } else {
                mMethodReport.addCalledMethod(mc);
            }
        }
    }

    @Override
    public void visitArrayStmt(int opcode, int formOrToReg, int arrayReg,
                               int indexReg, int xt) {
//        Log.v(TAG, "visitArrayStmt " + opcode);
        if (logArrayStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitBinopLitXStmt(int opcode, int distReg, int srcReg,
                                   int content) {
//        Log.v(TAG, "visitBinopLitXStmt " + opcode);
        if (logBinopLitXStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitBinopStmt(int opcode, int toReg, int r1, int r2, int xt) {
//        Log.v(TAG, "visitBinopStmt " + opcode);
        if (logBinopStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitClassStmt(int opcode, int a, int b, String type) {
//        Log.v(TAG, "visitClassStmt " + opcode);
        if (logClassStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitClassStmt(int opcode, int saveTo, String type) {
//        Log.v(TAG, "visitClassStmt " + opcode);
        if (logClassStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitCmpStmt(int opcode, int distReg, int bB, int cC, int xt) {
//        Log.v(TAG, "visitCmpStmt " + opcode);
        if (logCmpStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitConstStmt(int opcode, int toReg, Object value, int xt) {
//        Log.v(TAG, "visitConstStmt " + opcode);
        // System.err.println(value);
        if (logConstStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitFieldStmt(int opcode, int fromOrToReg, Field field, int xt) {
//        Log.v(TAG, "visitFieldStmt " + opcode);
        if (logFieldStmt) {
            logOpcode(opcode);
        }
    }

    @Override
    public void visitFieldStmt(int opcode, int fromOrToReg, int objReg,
                               Field field, int xt) {
//        Log.v(TAG, "visitFieldStmt " + opcode);
        if (logFieldStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitFillArrayStmt(int opcode, int aA, int elemWidth,
                                   int initLength, Object[] values) {
//        Log.v(TAG, "isitFillArrayStmt " + opcode);
        if (logFillArrayStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitFilledNewArrayStmt(int opcode, int[] args, String type) {
//        Log.v(TAG, "visitFilledNewArrayStmt " + opcode);
        if (logFilledNewArrayStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitJumpStmt(int opcode, int a, int b, DexLabel label) {
//        Log.v(TAG, "visitJumpStmt " + opcode);
        if (logJumpStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitJumpStmt(int opcode, int reg, DexLabel label) {
//        Log.v(TAG, "visitJumpStmt " + opcode);
        if (logJumpStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitJumpStmt(int opcode, DexLabel label) {
//        Log.v(TAG, "visitJumpStmt " + opcode);
        if (logJumpStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitLookupSwitchStmt(int opcode, int aA, DexLabel label,
                                      int[] cases, DexLabel[] labels) {
//        Log.v(TAG, "visitLookupSwitchStmt " + opcode);
        if (logLookupSwitchStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitMethodStmt(int opcode, int[] args, Method method) {
//        Log.v(TAG, "visitMethodStmt " + opcode);
        // logOpcode(opcode);
        if (logMethodStmt)
            logMethod(opcode, method);
    }

    @Override
    public void visitMonitorStmt(int opcode, int reg) {
//        Log.v(TAG, "visitMonitorStmt " + opcode);
        if (logMonitorStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitMoveStmt(int opcode, int toReg, int xt) {
//        Log.v(TAG, "visitMoveStmt " + opcode);
        if (logMoveStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitMoveStmt(int opcode, int toReg, int fromReg, int xt) {
//        Log.v(TAG, "visitMoveStmt " + opcode);
        if (logMoveStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitReturnStmt(int opcode) {
//        Log.v(TAG, "visitReturnStmt " + opcode);
        if (logReturnStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitReturnStmt(int opcode, int reg, int xt) {
//        Log.v(TAG, "visitReturnStmt " + opcode);
        if (logReturnStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitTableSwitchStmt(int opcode, int aA, DexLabel label,
                                     int first_case, int last_case, DexLabel[] labels) {
//        Log.v(TAG, "visitTableSwitchStmt " + opcode);
        if (logTableSwitchStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitUnopStmt(int opcode, int toReg, int fromReg, int xt) {
//        Log.v(TAG, "visitUnopStmt " + opcode);
        if (logUnopStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitUnopStmt(int opcode, int toReg, int fromReg, int xta,
                              int xtb) {
//        Log.v(TAG, "visitUnopStmt " + opcode);
        if (logUnopStmt)
            logOpcode(opcode);
    }

    @Override
    public void visitTryCatch(DexLabel start, DexLabel end, DexLabel[] handler, String[] type) {
//        Log.v(TAG, "visitTryCatch " + type);
        if (logTryCatchStmt) {
            // TODO: does nothing
        }
    }

    @Override
    public void visitArguments(int total, int[] args) {
//        Log.v(TAG, "visitArguments " + total);
        if (logVisitArguments) {
            // TODO: does nothing
        }
    }

    @Override
    public void visitEnd() {
    }

    @Override
    public void visitLabel(DexLabel label) {
//        Log.v(TAG, "visitLabel " + label);
        if (logVisitLabel) {
            // TODO: does nothing
        }
    }

    @Override
    public void visitLineNumber(int line, DexLabel label) {
//        Log.v(TAG, "visitLineNumber " + line + " " + label);
        if (logVisitLineNumber) {
            // TODO: does nothing
        }
    }

    @Override
    public void visitLocalVariable(String name, String type, String signature,
                                   DexLabel start, DexLabel end, int reg) {
//        Log.v(TAG, "visitLocalVariable " + name);

        if (logVisitLocalVariable) {
            // TODO: maybe also add other params
            if (type != null && type.length() > 0) {

                String s = type.replaceAll(";", "");
                LocalVariable local = new LocalVariable(name, s, signature);
                if (mUniqueDataOnly) {
                    if (!mLocalVariables.contains(local)) {
                        mLocalVariables.add(local);
                        mMethodReport.addLocalVariable(local);
                    }
                } else {
                    mMethodReport.addLocalVariable(local);
                }
            }

        }
    }

    @Override
    public void reset() {
        // cleanup
        mUsedOPCodes.clear();
        mUsedMethods.clear();
        mLocalVariables.clear();
    }
}
