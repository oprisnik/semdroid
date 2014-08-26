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

package com.oprisnik.semdroid.analysis.results.statistics;

import com.oprisnik.semdroid.analysis.results.Labelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Statistics group
 */
public class StatisticsGroup {

    private String mName;

    private List<String> mEntries;

    private Condition mCondition;

    public StatisticsGroup(String name, Condition condition) {
        mName = name;
        mEntries = new ArrayList<String>();
        setCondition(condition);
    }

    public StatisticsGroup(String name) {
        mName = name;
        mEntries = new ArrayList<String>();
    }

    public void addIfConditionMet(Labelable labelable) {
        if (mCondition.isTrue(labelable.getName())) {
            mEntries.add(labelable.getName());
        }
    }

    public Condition getCondition() {
        return mCondition;
    }

    public void setCondition(Condition condition) {
        mCondition = condition;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<String> getEntries() {
        return mEntries;
    }

    public void setEntries(List<String> entries) {
        mEntries = entries;
    }

    public void addEntry(String entry) {
        mEntries.add(entry);
    }
}
