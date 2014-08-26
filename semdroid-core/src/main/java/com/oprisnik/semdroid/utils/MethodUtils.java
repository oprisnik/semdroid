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

import com.googlecode.dex2jar.Method;


/**
 * Utils for Java/Dex methods.
 */
public class MethodUtils {

    public static String cleanClassName(String className) {
        String classNameCleaned = className;
        if (className != null && className.length() > 1) {
            classNameCleaned = className.substring(1, className.length() - 1)
                    .replace('/', '.');
        }
        return classNameCleaned;
    }

    public static String getJavaDefinitionWithoutRetVal(Method method) {
        StringBuilder str = new StringBuilder();
        str.append(cleanClassName(method.getOwner()));
        str.append('.');
        str.append(method.getName());

        str.append('(');
        // + method.getDesc().substring(0,
        // method.getDesc().lastIndexOf(')') + 1);
//		System.out.println(Arrays.toString(method.getParameterTypes()));
        String[] params = method.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            str.append(DexUtils.getJavaTypeDescriptor(params[i]));
            if (i != params.length - 1) {
                str.append(',');
            }
        }
        str.append(')');
        // TODO: modify map instead => faster
        return str.toString();
    }
}
