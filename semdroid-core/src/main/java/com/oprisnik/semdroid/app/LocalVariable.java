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

/**
 * Local variable.
 *

 */
public class LocalVariable extends Variable {

    public static final char PACKAGE_DELIMITER = '/';

    private String mSignature;

    public LocalVariable(String name, String type, String signature) {
        super(name, type);
        mSignature = signature;
    }

    public String getSignature() {
        return mSignature;
    }

    public void setSignature(String signature) {
        mSignature = signature;
    }

    @Override
    public String toString() {
        return mSignature + " " + super.toString();
    }
}
