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

import com.googlecode.dex2jar.Field;
import com.googlecode.dex2jar.Method;
import com.googlecode.dex2jar.visitors.DexAnnotationVisitor;
import com.googlecode.dex2jar.visitors.DexClassVisitor;
import com.googlecode.dex2jar.visitors.DexFieldVisitor;
import com.googlecode.dex2jar.visitors.DexMethodVisitor;
import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.app.parser.dex.SourceDecompiler;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.filter.StringFilter;

/**
 * Class visitor.
 */
public class DexCodeClassVisitor extends BasicVisitor implements
        DexClassVisitor {


    public static final String KEY_METHOD_FILTER = "method-filter";

    public static final String KEY_LOG_SOURCECODE = "log-source-code";
    public static final String KEY_SOURCE_DECOMPILER = "source-decompiler";

    private boolean mLogSourceCode = false;

    private SourceDecompiler mSourceDecompiler;

    private DexCodeMethodVisitor mMethodVisitor;

    private DexClass mClass;

    private StringFilter mMethodFilter;

    public DexCodeClassVisitor(BasicVisitor parent) {
        super(parent);
    }


    @Override
    public void init(Config config) throws BadConfigException {
        super.init(config);
        if (config != null) {
            mMethodFilter = config.getComponentAndInit(KEY_METHOD_FILTER, StringFilter.class, null);
            mLogSourceCode = config.getBoolean(KEY_LOG_SOURCECODE, mLogSourceCode);
        }
        if (mLogSourceCode) {
            mSourceDecompiler = config.getComponentAndInit(KEY_SOURCE_DECOMPILER, SourceDecompiler.class);
        }

        mMethodVisitor = new DexCodeMethodVisitor(this);
        mMethodVisitor.init(config);
        addChild(mMethodVisitor);
    }

    @Override
    public DexAnnotationVisitor visitAnnotation(String name, boolean visible) {
//        Log.v(TAG, "Visit class annotation: " + name);
        // return new DexCodeAnnotationVisitor(this);
        return null;
    }

    @Override
    public void visitEnd() {
//        Log.v(TAG, "Visit class end!");
    }

    @Override
    public DexFieldVisitor visitField(int accessFlags, Field field, Object value) {
//        Log.v(TAG, "Visit field: " + field.getName());
        if (mClass != null) {
            mClass.addField(new com.oprisnik.semdroid.app.Field(field.getName(),
                    field.getType(), mClass, accessFlags));
        }
        return null;
    }

    @Override
    public DexMethodVisitor visitMethod(int accessFlags, Method method) {
//        Log.v(TAG, "Visit method: " + method.getName());
        if (mMethodFilter == null || mMethodFilter.checkPrefix(method.getName())) {

            DexMethod dexmethod = new DexMethod(method.getName(), mClass,
                    method.getReturnType(), method.getParameterTypes(), accessFlags);
            getApp().addMethod(dexmethod);
            mClass.addMethod(dexmethod);

            if (mLogSourceCode) {
                dexmethod.setJavaCode(mSourceDecompiler.getJavaCode(
                        getApp().getApkFile(),
                        mClass.getClassName(), method.getName()));
            }

            // new DexCodeMethodVisitor(this, report)
            mMethodVisitor.reset();
            mMethodVisitor.setMethod(dexmethod);
            return mMethodVisitor;
        }
        return null;
    }

    @Override
    public void visitSource(String file) {
//        Log.v(TAG, "Visit source: " + file);
    }

    public DexClass getDexClass() {
        return mClass;
    }

    public void setDexClass(DexClass clazz) {
        mClass = clazz;
    }

    @Override
    public void reset() {
        setDexClass(null);
    }


}
