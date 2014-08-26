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
import com.oprisnik.semdroid.utils.Log;

/**
 * Visitor for annotations.
 */
public class DexCodeAnnotationVisitor extends BasicVisitor implements
        DexAnnotationVisitor {

    private static final String TAG = "DexCodeAnnotationVisitor";

    public DexCodeAnnotationVisitor(BasicVisitor parent) {
        super(parent);
    }


    @Override
    public void visit(String name, Object value) {
        Log.v(TAG, "Visit annotation: " + name);
    }

    @Override
    public DexAnnotationVisitor visitAnnotation(String name, String desc) {
        Log.v(TAG, "Visit nested annotation: " + name);
        return null;//new DexCodeAnnotationVisitor(this);
    }

    @Override
    public DexAnnotationVisitor visitArray(String name) {
        Log.v(TAG, "Visit annotation array: " + name);
        return null;//new DexCodeAnnotationVisitor(this);
    }

    @Override
    public void visitEnd() {
        Log.v(TAG, "Visit annotation end!");
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        Log.v(TAG, "Visit Enum: " + name);
    }

    @Override
    public void reset() {
    }

}
