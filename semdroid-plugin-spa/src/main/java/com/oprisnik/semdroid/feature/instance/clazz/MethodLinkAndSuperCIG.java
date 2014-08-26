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

package com.oprisnik.semdroid.feature.instance.clazz;

import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.utils.Log;

import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;

/**
 * Class instance generator that creates links to all methods of the class and
 * the superclass if the package name starts with "android".
 */
public class MethodLinkAndSuperCIG extends MethodLinkCIG {

    private static final String TAG = "MethodLinkAndSuperCIG";

    @Override
    protected void getClassInstance(DexClass clazz, InstanceDataElement results) {
        super.getClassInstance(clazz, results);

        // Add first external super class if part of Android
        String superClass = clazz.getFirstExternalSuperClass();
        if (superClass.startsWith("Landroid")) {
            results.addValue(getSymbolicFeatureDataElement("extends",
                    superClass));
            Log.v(TAG, clazz.getClassName() + " extends " + superClass);
        }
    }

}
