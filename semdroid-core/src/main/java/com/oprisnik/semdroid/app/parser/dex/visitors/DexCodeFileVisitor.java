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


import com.googlecode.dex2jar.visitors.DexClassVisitor;
import com.googlecode.dex2jar.visitors.DexFileVisitor;
import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.filter.StringFilter;
import com.oprisnik.semdroid.utils.Log;
import com.oprisnik.semdroid.utils.MethodUtils;

/**
 * Dex file visitor.
 */
public class DexCodeFileVisitor extends BasicVisitor implements DexFileVisitor {

    public static final String KEY_CLASS_FILTER = "class-filter";

    private static final String TAG = "DexCodeFileVisitor";

    private DexCodeClassVisitor mClassVisitor;
    private int mNumberOfClasses = -1;
    private int mCurrentClass = 1;
    private ProgressListener mProgressListener = null;

    private StringFilter mClassFilter;

    public DexCodeFileVisitor() {
        super(null);
    }

    @Override
    public void init(Config config) throws BadConfigException {
        super.init(config);
        if (config != null) {
            mClassFilter = config.getComponentAndInit(KEY_CLASS_FILTER, StringFilter.class, null);
        }
        mClassVisitor = new DexCodeClassVisitor(this);
        mClassVisitor.init(config);
        addChild(mClassVisitor);
    }

    public void setProgressListener(ProgressListener progressListener) {
        mProgressListener = progressListener;
    }

    @Override
    public DexClassVisitor visit(int access_flags, String className,
                                 String superClass, String[] interfaceNames) {
        if (mProgressListener != null) {
            mProgressListener.onProgressUpdated(mCurrentClass, mNumberOfClasses);
        }
//        Log.v(TAG, "Class " + className + " found!");
        // appendLine("Class: " + className);

        if (mClassFilter != null && !mClassFilter.checkPrefix(className)) {
            Log.v(TAG, "Ignoring " + className);
            return null;
        }
        mClassVisitor.reset();
        String cn = MethodUtils.cleanClassName(className);
        DexClass mClass = new DexClass(getApp(), cn);
        if (superClass != null) {
            mClass.setSuperClass(MethodUtils.cleanClassName(superClass));
        }
        getApp().addClass(mClass);
        mClassVisitor.setDexClass(mClass);
        mCurrentClass++;
        return mClassVisitor;
    }

    @Override
    public void visitEnd() {
        mNumberOfClasses = -1;
    }

    @Override
    public void reset() {
//        mNumberOfClasses = -1;
        mCurrentClass = 1;
    }

    public int getNumberOfClasses() {
        return mNumberOfClasses;
    }

    public void setNumberOfClasses(int numberOfClasses) {
        mNumberOfClasses = numberOfClasses;
    }

    public interface ProgressListener {
        public void onProgressUpdated(int currentClass, int totalClasses);
    }

}
