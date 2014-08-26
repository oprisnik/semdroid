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

import java.util.Iterator;
import java.util.SortedSet;

/**
 * Histogram helper. Used to convert SparseHistogram to other formats
 */
public class HistogramHelper {

    /**
     * Converts a given SparseIntHistogram to a double array containing all possible values.
     * Possible values have to be supplied sorted in order to guarantee compatible double[] arrays.
     *
     * @param possibleValues the possible values used for the mapping
     * @param histogram      the histogram containing the data
     * @param norm           true, if the data should be normed (divided by max value)
     * @return the double histogram
     */
    public static double[] getDoubleHistogram(SortedSet<Integer> possibleValues, SparseIntHistogram histogram, boolean norm) {
        double[] res = new double[possibleValues.size()];
        Iterator<Integer> ks = possibleValues.iterator();

        double max = histogram.getAbsoluteMax().doubleValue();
        // use if everything 0...
        if (max == 0) {
            return res;
        }
        int i = 0;
        while (ks.hasNext()) {
            Integer count = histogram.getValue(ks.next());
            if (count == null) {
                res[i] = 0;
            } else {
                if (norm) {
                    res[i] = count.doubleValue() / max;
                } else {
                    res[i] = count.doubleValue();
                }
            }
            i++;
        }

        return res;
    }

}
