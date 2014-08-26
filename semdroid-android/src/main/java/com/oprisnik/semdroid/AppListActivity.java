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

package com.oprisnik.semdroid;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.oprisnik.semdroid.plugins.PluginCardManager;

/**
 * List all installed applications.
 *
 */
public class AppListActivity extends BaseActivity implements AppListFragment.Callbacks {


    private PluginCardManager mPluginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        mPluginManager = PluginCardManager.DEFAULT_PLUGINS;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }


    @Override
    public void onAppSelected(final ApplicationInfo app) {
        Intent i = new Intent(AppListActivity.this, AnalysisResultsOverviewActivity.class);
        i.putExtra(AnalysisResultsOverviewFragment.KEY_PACKAGE_NAME,
                app.packageName);
        startActivity(i);
    }
}
