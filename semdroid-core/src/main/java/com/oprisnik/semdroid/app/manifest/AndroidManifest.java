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

package com.oprisnik.semdroid.app.manifest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AndroidManifest implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String mPackageName;

    private List<AndroidActivity> mActivities;
    private List<AndroidService> mServices;
    private List<AndroidReceiver> mReceivers;
    private List<AndroidContentProvider> mContentProviders;
    private List<Permission> mPermissions;

    public AndroidManifest(String packageName) {
        mPackageName = packageName;
        mActivities = new ArrayList<AndroidActivity>();
        mServices = new ArrayList<AndroidService>();
        mReceivers = new ArrayList<AndroidReceiver>();
        mContentProviders = new ArrayList<AndroidContentProvider>();
        mPermissions = new ArrayList<Permission>();
    }

    public String getPackageName() {
        return mPackageName;
    }

    public List<AndroidActivity> getActivities() {
        return mActivities;
    }

    public List<AndroidService> getServices() {
        return mServices;
    }

    public List<AndroidReceiver> getReceivers() {
        return mReceivers;
    }

    public List<AndroidContentProvider> getContentProviders() {
        return mContentProviders;
    }

    public List<Permission> getPermissions() {
        return mPermissions;
    }

    public void addActivity(AndroidActivity a) {
        mActivities.add(a);
    }

    public void addService(AndroidService s) {
        mServices.add(s);
    }

    public void addReceiver(AndroidReceiver r) {
        mReceivers.add(r);
    }

    public void addContentProvider(AndroidContentProvider p) {
        mContentProviders.add(p);
    }

    public void addPermission(Permission permission) {
        mPermissions.add(permission);
    }

}