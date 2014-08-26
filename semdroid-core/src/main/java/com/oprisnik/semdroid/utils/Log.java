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

/**
 * Basic logger similar to Android Log functionality.
 *
 */
public class Log {


    public static final int VERBOSE = 2;

    public static final int DEBUG = 3;

    public static final int INFO = 4;

    public static final int WARN = 5;

    public static final int ERROR = 6;

    public static final int ASSERT = 7;


    /**
     * Current log level.
     */
    public static final int LOG_LEVEL = DEBUG;

    public static void v(String tag, String message) {
        println(VERBOSE, tag, message);
    }

    public static void d(String tag, String message) {
        println(DEBUG, tag, message);
    }

    public static void i(String tag, String message) {
        println(INFO, tag, message);
    }

    public static void w(String tag, String message) {
        println(WARN, tag, message);
    }

    public static void e(String tag, String message) {
        println(ERROR, tag, message);
    }

    public static void a(String tag, String message) {
        println(ASSERT, tag, message);
    }

    private static void println(int priority, String tag, String message) {
        if (priority < LOG_LEVEL)
            return;
        if (priority >= WARN)
            System.err.println(tag + ": " + message);
        else
            System.out.println(tag + ": " + message);
    }

}