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
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.oprisnik.semdroid.analysis.results.lite.LiteAppAnalysisReport;
import com.oprisnik.semdroid.plugins.PluginCardEntry;

/**
 * Analysis results overview activity.
 */
public class AnalysisResultsOverviewActivity extends BaseActivity implements AnalysisResultsOverviewFragment.Callbacks {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_results_overview);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReportSelected(ApplicationInfo app, LiteAppAnalysisReport report, PluginCardEntry plugin) {
        Intent i = new Intent(this, AnalysisResultsActivity.class);
        i.putExtra(
                AnalysisResultsOverviewFragment.KEY_PACKAGE_NAME,
                app.packageName);
        i.putExtra(AnalysisResultsListFragment.KEY_PLUGINS,
                new String[]{report.getName()});
        i.putExtra(AnalysisResultsListFragment.KEY_ANALYSIS_TITLE, plugin.getName(this));
        startActivity(i);
    }
}
