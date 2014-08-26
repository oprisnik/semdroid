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

package com.oprisnik.semdroid.filter;

/**
 * String filter interface.
 */
public interface StringFilter extends Filter<String> {

    /**
     * Checks if the given string starts with a certain prefix.
     * Example: We only want strings that start with "android".
     * <p/>
     * checkPrefix("android.util") returns true
     * checkPrefix("com.android") returns false
     *
     * @param data the string to check
     * @return boolean if the string should be used
     */
    public boolean checkPrefix(String data);
}
