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

package com.oprisnik.semdroid.analysis.results;

import com.oprisnik.semdroid.app.App;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * AppAnalysisReport returned by analysis plugins.
 * All findings of an analysis plugin are collected in this report.
 * The plugins can add data by calling calling {@link #label(com.oprisnik.semdroid.analysis.results.Labelable, String)}.
 */
public class AppAnalysisReport implements Serializable {

    private String mName;

    private Map<String, Label> mLabels;
    private Map<Labelable, Set<Label>> mComponentMap;

    private App mApp;

    /**
     * Create a new, empty AppAnalysisReport.
     */
    public AppAnalysisReport() {
        mLabels = new HashMap<String, Label>();
        mComponentMap = new HashMap<Labelable, Set<Label>>();
    }

    /**
     * Create a new AppAnalysisReport with the given name for the given application.
     *
     * @param name the name of the report, e.g. the name of the plugin that created the report
     * @param app  the application the report is for
     */
    public AppAnalysisReport(String name, App app) {
        this();
        mName = name;
        mApp = app;
    }

    /**
     * Labels the given component according to the label string.
     * This method should be called by analysis plugins.
     *
     * @param component the component to label
     * @param label     the label to attach to the component
     */
    public void label(Labelable component, String label) {
        Label l = mLabels.get(label);
        if (l == null) {
            l = new Label(label, mName);
            mLabels.put(label, l);
        }
        component.addLabel(l);
        Set<Label> set = mComponentMap.get(component);
        if (set == null) {
            set = new HashSet<Label>();
            mComponentMap.put(component, set);
        }
        set.add(l);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Collection<Label> getLabels() {
        return mLabels.values();
    }

    public Label getLabel(String label) {
        return mLabels.get(label);
    }

    public Set<Labelable> getComponents() {
        return mComponentMap.keySet();

    }

    public Set<Label> getLabels(Labelable component) {
        return mComponentMap.get(component);
    }

    public App getApp() {
        return mApp;
    }

    public void setApp(App app) {
        mApp = app;
    }

}
