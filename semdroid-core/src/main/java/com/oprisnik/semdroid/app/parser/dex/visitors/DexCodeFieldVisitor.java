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

import com.googlecode.dex2jar.visitors.DexAnnotationVisitor;
import com.googlecode.dex2jar.visitors.DexFieldVisitor;

/**
 * Field visitor.
 */
public class DexCodeFieldVisitor extends BasicVisitor implements
        DexFieldVisitor {
    private static final String TAG = "DexCodeFieldVisitor";

    public DexCodeFieldVisitor(BasicVisitor parent) {
        super(parent);
    }

    @Override
    public DexAnnotationVisitor visitAnnotation(String name, boolean visible) {
//        Log.v(TAG, "Visit field annotation " + name);
        return null;//;new DexCodeAnnotationVisitor(this);
    }

    @Override
    public void visitEnd() {
//        Log.v(TAG, "Visit field end!");
    }

    @Override
    public void reset() {
    }
}
