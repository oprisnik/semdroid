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

import com.oprisnik.semdroid.analysis.results.Label;
import com.oprisnik.semdroid.analysis.results.Labelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Super group.
 */
public abstract class SuperGroup {

    private List<StatisticsGroup> mGroups;
    private String mName;

    private Condition mCondition;

    public SuperGroup(String name) {
        mName = name;
        mGroups = new ArrayList<StatisticsGroup>();
        setup();
    }

    public Condition getCondition() {
        return mCondition;
    }

    protected abstract void setup();


    public void addIfConditionMet(Label l) {
        if (mCondition.isTrue(l.getName())) {
            for (Labelable labelable : l.getObjects()) {
                for (StatisticsGroup g : mGroups)
                    g.addIfConditionMet(labelable);
            }

        }
    }

    public void setCondition(Condition condition) {
        mCondition = condition;
    }


    public void addGroup(StatisticsGroup group) {
        mGroups.add(group);
    }

    public List<StatisticsGroup> getGroups() {
        return mGroups;
    }

    public void setGroups(List<StatisticsGroup> groups) {
        mGroups = groups;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
