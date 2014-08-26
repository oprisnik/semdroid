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
 * Utils for Dex parsing.
 */
public class DexUtils {

    public static final String DEX_TYPE_COMPOSITE = "L";
    public static final String[] BASIC_DEX_TYPE_DESCRIPTORS_AND_OTHERS = {"Z", "B", "S", "C", "I", "J", "F", "D", "V", DEX_TYPE_COMPOSITE};
    public static final String[] BASIC_DEX_TYPE_DESCRIPTORS = {"Z", "B", "S", "C", "I", "J", "F", "D", "V"};


    public static String getJavaTypeDescriptor(String descriptor) {
        if (descriptor.equals("Z")) {
            return "boolean";
        } else if (descriptor.equals("B")) {
            return "byte";
        } else if (descriptor.equals("S")) {
            return "short";
        } else if (descriptor.equals("C")) {
            return "char";
        } else if (descriptor.equals("I")) {
            return "int";
        } else if (descriptor.equals("J")) {
            return "long";
        } else if (descriptor.equals("F")) {
            return "float";
        } else if (descriptor.equals("D")) {
            return "double";
        } else if (descriptor.equals("V")) {
            return "void";
        } else if (descriptor.startsWith(DEX_TYPE_COMPOSITE)) {
            return MethodUtils.cleanClassName(descriptor);
        } else if (descriptor.startsWith("[")) {
            return getJavaTypeDescriptor(descriptor.substring(1)) + "[]";
        }
        return "unknown";

    }

    public static boolean isBasicDataType(String dataType) {
        if (dataType == null)
            return false;
        // filter arrays
        if (dataType.startsWith("[")) {
            return isBasicDataType(dataType.substring(1));
        }
        return !dataType.startsWith(DEX_TYPE_COMPOSITE);
    }

    public static boolean isArray(String dataType) {
        return dataType.startsWith("[");
    }

    public static String getTypeWithoutArray(String dataType) {
        if (dataType.startsWith("[")) {
            return getTypeWithoutArray(dataType.substring(1));
        }
        return dataType;
    }

    public static boolean dataTypeStartsWith(String dataType, String startsWith) {
        if (dataType == null || startsWith == null)
            return false;
        if (dataType.startsWith("[")) {
            return dataTypeStartsWith((dataType.substring(1)), startsWith);
        }
        if (dataType.startsWith(DEX_TYPE_COMPOSITE + startsWith)
                || dataType.startsWith(startsWith))
            return true;
        return false;
    }

    public static String getJavaTypeDescriptorWithTrailingChars(
            String descriptor) {
        if (descriptor.equals("Z")) {
            return "boolean";
        } else if (descriptor.equals("B")) {
            return "byte";
        } else if (descriptor.equals("S")) {
            return "short";
        } else if (descriptor.equals("C")) {
            return "char";
        } else if (descriptor.equals("I")) {
            return "int";
        } else if (descriptor.equals("J")) {
            return "long";
        } else if (descriptor.equals("F")) {
            return "float";
        } else if (descriptor.equals("D")) {
            return "double";
        } else if (descriptor.equals("V")) {
            return "void";
        } else if (descriptor.startsWith(DEX_TYPE_COMPOSITE)) {
            return descriptor.substring(1).replace('/', '.');
        } else if (descriptor.startsWith("[")) {
            return getJavaTypeDescriptor(descriptor.substring(1)) + "[]";
        }
        return "unknown";

    }
}
