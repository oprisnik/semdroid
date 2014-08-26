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
 * Dalvik Access flags according to https://source.android.com/devices/tech/dalvik/dex-format.html
 */
public class AccessFlags {

    public static final int PUBLIC = 0x1;
    public static final int PRIVATE = 0x2;
    public static final int PROTECTED = 0x4;
    public static final int STATIC = 0x8;
    public static final int FINAL = 0x10;
    public static final int SYNCHRONIZED = 0x20;
    public static final int VOLATILE = 0x40;
    public static final int BRIDGE = 0x40;
    public static final int TRANSIENT = 0x80;
    public static final int VARARGS = 0x80;
    public static final int NATIVE = 0x100;
    public static final int INTERFACE = 0x200;
    public static final int ABSTRACT = 0x400;
    public static final int STRICT = 0x800;
    public static final int SYNTHETIC = 0x1000;
    public static final int ANNOTATION = 0x2000;
    public static final int ENUM = 0x4000;
    public static final int CONSTRUCTOR = 0x10000;
    public static final int DECLARED_SYNCHRONIZED = 0x20000;

}
