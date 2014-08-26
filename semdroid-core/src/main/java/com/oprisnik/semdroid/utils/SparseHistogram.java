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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Sparse histogram implementation.
 */
// TODO: more efficient implementation
// we could directly update the max when a value is incremented / changed
// Use treemap for getSortedListDescending/Ascending
// Histogram: use localMax (only of keyMapping values)
public class SparseHistogram<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Map<T, Integer> mValues;

    public SparseHistogram() {
        mValues = new HashMap<T, Integer>();
    }

    public void increase(T key) {
        increase(key, 1);
    }

    public void increase(T key, int amount) {

        Integer i = mValues.get(key);
        if (i == null) {
            i = new Integer(0);
        }
        i += amount;
        mValues.put(key, i);
    }

    public Set<T> getKeySet() {
        return mValues.keySet();
    }

    public Integer getValue(T key) {
        return mValues.get(key);
    }

    public void set(T key, int value) {
        mValues.put(key, Integer.valueOf(value));
    }

    public Integer getAbsoluteMax() {

        Integer max = Integer.MIN_VALUE;
        Integer min = Integer.MAX_VALUE;
        for (Integer i : mValues.values()) {
            if (i > max) {
                max = i;
            }
            if (i < min) {
                min = i;
            }
        }
        return Math.max(Math.abs(max), Math.abs(min));
    }


    public Integer getMax() {

        Integer max = Integer.MIN_VALUE;
        for (Integer i : mValues.values()) {
            if (i > max) {
                max = i;
            }
        }
        return max;
    }

    public Integer getMin() {
        Integer min = Integer.MAX_VALUE;
        for (Integer i : mValues.values()) {
            if (i < min) {
                min = i;
            }
        }
        return min;
    }

    public List<Entry<T, Integer>> getSortedListDescending() {
        List<Entry<T, Integer>> list = new ArrayList<Entry<T, Integer>>();
        list.addAll(mValues.entrySet());
        Collections.sort(list, Collections.reverseOrder(new SparseHistogramComparator()));
        return list;

    }

    public List<Entry<T, Integer>> getSortedListAscending() {
        List<Entry<T, Integer>> list = new ArrayList<Entry<T, Integer>>();
        list.addAll(mValues.entrySet());
        Collections.sort(list, new SparseHistogramComparator());
        return list;

    }

    public void increaseAll(SparseHistogram<T> data) {
        for (Entry<T, Integer> entry : data.mValues.entrySet()) {
            increase(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        mValues.clear();
    }

    public int getValueCount() {
        int count = 0;
        for (Integer value : mValues.values()) {
            count += value;
        }
        return count;
    }

    public double[] getHistogram(T[] keyMapping, boolean normalize) {
        double[] results = new double[keyMapping.length];
        Integer max = getMax();
        for (int i = 0; i < keyMapping.length; i++) {
            Integer value = getValue(keyMapping[i]);
            if (value != null) {
                if (normalize && max != 0) {
                    results[i] = value.doubleValue() / max.doubleValue();
                } else {
                    results[i] = value.doubleValue();
                }
            }
        }
        return results;
    }

    @Override
    public String toString() {
        return mValues.toString();
    }

    public class SparseHistogramComparator implements Comparator<Entry<T, Integer>> {

        @Override
        public int compare(Entry<T, Integer> o1, Entry<T, Integer> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }

    }

}
