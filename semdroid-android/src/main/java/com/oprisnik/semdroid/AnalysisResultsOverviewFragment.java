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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.oprisnik.fragments.AbsListFragment;
import com.oprisnik.semdroid.adapters.ResultsOverviewAdapter;
import com.oprisnik.semdroid.analysis.AnalysisProgressListener;
import com.oprisnik.semdroid.analysis.results.lite.LiteAppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.lite.LiteSemdroidReport;
import com.oprisnik.semdroid.plugins.PluginCardEntry;
import com.oprisnik.semdroid.results.SemdroidReportCache;
import com.oprisnik.semdroid.service.AnalysisIntentService;

/**
 * Fragment that displays the analysis results for a given application.
 * The application package has to be specified in the starting intent via
 * {@link #KEY_PACKAGE_NAME}.
 */
public class AnalysisResultsOverviewFragment extends AbsListFragment {

    public static final String KEY_PACKAGE_NAME = "PACKAGE";

    private static final String TAG = "AnalysisResultsOverviewFragment";
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onReportSelected(ApplicationInfo app, LiteAppAnalysisReport report, PluginCardEntry plugin) {

        }
    };
    private Callbacks mCallbacks = sDummyCallbacks;
    private ResultsOverviewAdapter mAdapter;
    private ApplicationInfo mAppInfo;
    private ProgressBar mProgressBar1;
    private ProgressBar mProgressBar2;
    private BroadcastReceiver mAnalysisProgressListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // check if the results are for us
            if (!mPackage.equals(intent.getStringExtra(AnalysisIntentService.KEY_PACKAGE_NAME))) {
                return;
            }
            int status = intent.getIntExtra(AnalysisIntentService.KEY_ANALYSIS_STATUS, -1);
            int percentage = intent.getIntExtra(AnalysisIntentService.KEY_ANALYSIS_STATUS_PERCENTAGE, 0);
//            Toast.makeText(getActivity(), "GOT " + status + " percentage " + percentage, Toast.LENGTH_SHORT).show();
            if (status == AnalysisProgressListener.STATUS_APP_PARSING) {
                if (mProgressBar1 != null) {
                    mProgressBar1.setProgress(percentage);
                }
            } else if (status == AnalysisProgressListener.STATUS_ANALYZING) {
                if (mProgressBar2 != null) {
                    mProgressBar2.setProgress(percentage);
                }
            }
        }
    };
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ResultsOverviewAdapter(getActivity());
        setListAdapter(mAdapter);

        mCache = SemdroidReportCache.getInstance(getActivity().getSupportFragmentManager());

        mPackage = getActivity().getIntent().getStringExtra(AnalysisResultsOverviewFragment.KEY_PACKAGE_NAME);
        if (mPackage == null && savedInstanceState != null) {
            mPackage = savedInstanceState.getString(AnalysisResultsOverviewFragment.KEY_PACKAGE_NAME);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AnalysisResultsOverviewFragment.KEY_PACKAGE_NAME, mPackage);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mAnalysisFinishedReceiver);
        getActivity().unregisterReceiver(mAnalysisProgressListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter finishedFilter = new IntentFilter(AnalysisIntentService.ACTION_ANALYSIS_FINISHED);
        finishedFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        getActivity().registerReceiver(mAnalysisFinishedReceiver, finishedFilter);

        IntentFilter progressFilter = new IntentFilter(AnalysisIntentService.ACTION_ANALYSIS_STATUS_UPDATE);
        progressFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        getActivity().registerReceiver(mAnalysisProgressListener, progressFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analysis_results_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PackageManager pm = getActivity().getPackageManager();

        if (mAppInfo != null) {
            ((TextView) view.findViewById(R.id.title)).setText(mAppInfo.loadLabel(pm));
            ((TextView) view.findViewById(R.id.subtitle)).setText(mAppInfo.packageName);
            ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(mAppInfo.loadIcon(pm));
        }
        mProgressBar1 = (ProgressBar) view.findViewById(R.id.progress1);
        mProgressBar2 = (ProgressBar) view.findViewById(R.id.progress2);
    }

    public void handleResults(LiteSemdroidReport results) {
        if (results != null) {
            mAdapter.addAll(results.getReports());
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Results null");
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(AbsListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mCallbacks.onReportSelected(mAppInfo, mAdapter.getItem(position),
                mAdapter.getPluginEntry(position));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement Callbacks!");
        }
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    public interface Callbacks {
        public void onReportSelected(ApplicationInfo app, LiteAppAnalysisReport report, PluginCardEntry plugin);
    }
}
