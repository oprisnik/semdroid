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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ExpandableListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.oprisnik.semdroid.adapters.ExpandableResultsAdapter;
import com.oprisnik.semdroid.analysis.results.lite.LiteAppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.lite.LiteSemdroidReport;
import com.oprisnik.semdroid.results.SemdroidReportCache;
import com.oprisnik.semdroid.service.AnalysisIntentService;

import java.util.HashSet;
import java.util.Set;

/**
 * Fragment that displays the analysis results for a given application in list form.
 */
public class AnalysisResultsListFragment extends ExpandableListFragment {

    private static final String TAG = "AnalysisResultsListFragment";

    public static final String KEY_PLUGINS = "PLUGINS";
    public static final String KEY_ANALYSIS_TITLE = "TITLE";

    private ExpandableResultsAdapter mAdapter;
    private Set<String> mPlugins = null;

    private ApplicationInfo mAppInfo;

    private String mPackage;

    private LiteSemdroidReport mAnalysisReport;

    private SemdroidReportCache mCache;

    private BroadcastReceiver mAnalysisFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // check if the results are for us
            if (!mPackage.equals(intent.getStringExtra(AnalysisIntentService.KEY_PACKAGE_NAME))) {
                return;
            }
            if (intent.hasExtra(AnalysisIntentService.KEY_RESULTS_KEY)) {
                if (mCache != null) {
                    mAnalysisReport = mCache.get(intent.getStringExtra(AnalysisIntentService.KEY_RESULTS_KEY));

                    abortBroadcast();
                    handleResults(mAnalysisReport);
                }
            }
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Got results: " + mPackage);
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analysis_results, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ExpandableResultsAdapter(getActivity());
        setListAdapter(mAdapter);

        mCache = SemdroidReportCache.getInstance(getActivity().getSupportFragmentManager());

        mPackage = getActivity().getIntent().getStringExtra(AnalysisResultsOverviewFragment.KEY_PACKAGE_NAME);

        String[] plugins = getActivity().getIntent().getStringArrayExtra(KEY_PLUGINS);
        if (plugins != null) {
            mPlugins = new HashSet<String>();
            for (String s : plugins) {
                mPlugins.add(s);
            }
        }

        try {
            PackageManager pm = getActivity().getPackageManager();
            mAppInfo = pm.getApplicationInfo(mPackage, 0);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error: Application " + mPackage + " not installed!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        String key;
        if (getActivity().getIntent().hasExtra(AnalysisIntentService.KEY_RESULTS_KEY)) {
            key = getActivity().getIntent().getStringExtra(AnalysisIntentService.KEY_RESULTS_KEY);
        } else {
            key = AnalysisIntentService.getResultsKey(mPackage);

        }
        mAnalysisReport = mCache.get(key);

        if (mAnalysisReport == null) {
            // start analysis
            Intent msgIntent = new Intent(getActivity(), AnalysisIntentService.class);
            msgIntent.putExtra(AnalysisIntentService.KEY_PACKAGE_NAME, mPackage);
            getActivity().startService(msgIntent);
        } else {
            handleResults(mAnalysisReport);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mAnalysisFinishedReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(AnalysisIntentService.ACTION_ANALYSIS_FINISHED);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        getActivity().registerReceiver(mAnalysisFinishedReceiver, filter);
    }


    public void handleResults(LiteSemdroidReport results) {
        if (results != null) {
            if (mPlugins != null) {
                for (LiteAppAnalysisReport r : results.getReports()) {
                    if (mPlugins.contains(r.getName())) {
                        mAdapter.addPluginReport(r);
                    }
                }
            } else {
                mAdapter.setReport(results);
            }
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Results null");
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
