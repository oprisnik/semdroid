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
 * Vector math helper methods.
 */
public class VecMath {

    public static double[] subtract(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors not same length!");
        }
        double[] result = new double[a.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = a[i] - b[i];
        }

        return result;
    }

    public static double[] add(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors not same length!");
        }
        double[] result = new double[a.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = a[i] + b[i];
        }

        return result;
    }

    public static double euclideanDistance(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors not same length! " + a.length + " : " + b.length);
        }
        double result = 0;

        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            result += diff * diff;
        }
        return Math.sqrt(result);
    }

}
