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

import com.googlecode.dex2jar.visitors.DexAnnotationAble;
import com.googlecode.dex2jar.visitors.DexAnnotationVisitor;
import com.googlecode.dex2jar.visitors.DexCodeVisitor;
import com.googlecode.dex2jar.visitors.DexMethodVisitor;
import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.utils.Log;

/**
 * Method visitor.
 */
public class DexCodeMethodVisitor extends BasicVisitor implements
        DexMethodVisitor {

    private static final String TAG = "DexCodeMethodVisitor";

    private DexMethod mMethodReport;

    private DexCodeOpcodeVisitor mOpcodeVisitor;

    public DexCodeMethodVisitor(BasicVisitor parent) {
        super(parent);
    }

    @Override
    public void init(Config config) throws BadConfigException {
        super.init(config);
        mOpcodeVisitor = new DexCodeOpcodeVisitor(this);
        mOpcodeVisitor.init(config);
        addChild(mOpcodeVisitor);
    }

    public void setMethod(DexMethod report) {
        mMethodReport = report;
    }

    @Override
    public DexAnnotationVisitor visitAnnotation(String name, boolean visible) {
        Log.v(TAG, "Visit method annotation" + name);
//		return new DexCodeAnnotationVisitor(this);
        return null;
    }

    @Override
    public DexCodeVisitor visitCode() {
        Log.v(TAG, "Visit method code!");
        mOpcodeVisitor.reset();
        mOpcodeVisitor.setMethodFeatureReport(mMethodReport);
        return mOpcodeVisitor;
    }

    @Override
    public void visitEnd() {
        Log.v(TAG, "Visit method end!");
    }

    @Override
    public DexAnnotationAble visitParameterAnnotation(int index) {
        Log.v(TAG, "Visit method parameter annotation " + index);
//		return new DexCodeParamAnnotationVisitor(this);
        return null;
    }

    @Override
    public void reset() {
        setMethod(null);
    }
}
