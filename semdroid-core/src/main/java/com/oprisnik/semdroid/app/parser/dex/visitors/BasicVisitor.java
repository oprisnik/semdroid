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

package com.oprisnik.semdroid.app.parser.dex.visitors;

import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.manifest.AndroidManifest;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.config.Configurable;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic class for common functions of all dex.
 */
public abstract class BasicVisitor implements Configurable {

    private App mApp;
    private BasicVisitor mParent;
    private List<BasicVisitor> mChildren;

    public BasicVisitor(BasicVisitor parent) {
        mParent = parent;

        mChildren = new ArrayList<BasicVisitor>();
        if (parent == null)
            return;
        mApp = parent.mApp;
    }

    @Override
    public void init(Config config) throws BadConfigException {
        // nothing to do
    }

    /**
     * Add a child in the hierarchy. Needed for configuration changes (new
     * application to analyze etc.)
     *
     * @param visitor
     */
    protected void addChild(BasicVisitor visitor) {
        mChildren.add(visitor);
    }

    protected void removeChild(BasicVisitor visitor) {
        mChildren.remove(visitor);
    }

    public AndroidManifest getManifest() {
        return mApp.getManifest();
    }

    public String getAppName() {
        return mApp.getName();
    }

    public App getApp() {
        return mApp;
    }

    public void setApp(App app) {
        reset();
        mApp = app;
        for (BasicVisitor v : mChildren) {
            v.setApp(app);
        }
    }

    protected BasicVisitor getParent() {
        return mParent;
    }

    /**
     * Will be called before a new instance is analyzed. Can be used for
     * cleanup.
     */
    public abstract void reset();

}
