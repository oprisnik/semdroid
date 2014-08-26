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

package com.oprisnik.semdroid.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.oprisnik.semdroid.BuildConfig;
import com.oprisnik.semdroid.SemdroidAnalyzer;
import com.oprisnik.semdroid.analysis.AnalysisProgressListener;
import com.oprisnik.semdroid.analysis.results.SemdroidReport;
import com.oprisnik.semdroid.analysis.results.lite.LiteSemdroidReport;
import com.oprisnik.semdroid.permissions.AndroidPermissionMapFactory;
import com.oprisnik.semdroid.permissions.PermissionMapFactory;
import com.oprisnik.semdroid.plugins.PluginCardEntry;
import com.oprisnik.semdroid.plugins.PluginCardManager;
import com.oprisnik.semdroid.util.AndroidUtils;
import com.oprisnik.semdroid.utils.FileUtils;

import java.io.File;
import java.util.Arrays;


public class AnalysisIntentService extends IntentService {

    public static final String ACTION_ANALYSIS_FINISHED = "com.oprisnik.semdroid.ANALYSIS_FINISHED";
    public static final String ACTION_ANALYSIS_STATUS_UPDATE = "com.oprisnik.semdroid.ANALYSIS_STATUS_UPDATE";
    public static final String PERMISSION_ACCESS_ANALYSIS_RESULTS = "com.oprisnik.semdroid.ACCESS_ANALYSIS_RESULTS";


    public static final String KEY_PACKAGE_NAME = "package";
    public static final String KEY_RESULTS_KEY = "results-key";
    public static final String KEY_PLUGINS = "plugins";

    public static final String KEY_ANALYSIS_STATUS = "status";
    public static final String KEY_ANALYSIS_STATUS_PERCENTAGE = "percentage";

    private static final String TAG = "AnalysisIntentService";

    private SemdroidAnalyzer mSemdroid = null;
    private boolean mFactorySet = false;


    public AnalysisIntentService(String name) {
        super(name);
    }

    public AnalysisIntentService() {
        super(TAG);
    }


    public static String getResultsKey(String packageName) {
        return packageName + ".sr";
    }

    public static String getResultsKey(String packageName, String[] plugins) {
        if (plugins == null || plugins.length <= 0) {
            return getResultsKey(packageName);
        }
        return packageName + "-" + Arrays.toString(plugins) + ".sr";
    }


    protected SemdroidAnalyzer createInstance() {
        if (!mFactorySet) {
            PermissionMapFactory.setFactory(new AndroidPermissionMapFactory(getApplicationContext()));
            mFactorySet = true;
        }

        SemdroidAnalyzer semdroid = new SemdroidAnalyzer();
        semdroid.init(); // use default values
        // TODO: maybe init with semdroid.xml instead of using default values
        return semdroid;
    }

    protected SemdroidAnalyzer getAnalyzer() {
        if (mSemdroid != null) {
            return mSemdroid; // already initialized
        }
        mSemdroid = createInstance();

        for (PluginCardEntry e : PluginCardManager.DEFAULT_PLUGINS.getPlugins()) {
            try {
                if (e.isClassOnly()) {
                    mSemdroid.addAnalysisPlugin(e.getPluginClass());
                } else {
                    mSemdroid.addAnalysisPlugin(e.getConfig());
                }
            } catch (Exception ex) {
                Log.e(TAG, "Could not load plugin " +
                        e.getName(getApplicationContext()) +
                        " " + ex.getMessage());
            }
        }
        return mSemdroid;
    }

    protected SemdroidAnalyzer getAnalyzer(String[] plugins) {
        SemdroidAnalyzer semdroid = createInstance();

        for (String s : plugins) {
            try {
                try {
                    semdroid.addAnalysisPlugin(s);
                } catch (Exception e) {
                    semdroid.addAnalysisPluginFromClassName(s);
                }
            } catch (Exception ex) {
                Log.e(TAG, "Could not load plugin " +
                        s + " " + ex.getMessage());
            }
        }
        return semdroid;
    }

    protected SemdroidAnalyzer getSemdroid(final boolean useAllPlugins, final String[] plugins) {

        // Starting with ART, the stack is big enough to load all plugins
        // (ObjectInputStream for Semantic Pattern Network etc.)
        // Dalvik needs bigger stack
        // https://developer.android.com/guide/practices/verifying-apps-art.html#Stack_Size
        // or we optimize the Semantic Pattern Framework:
        // at.tuflowgraphy.semanticapps.dalvikcode.DalvikBaseAnalyzer#loadNet
        if (AndroidUtils.hasAtLeastArtRuntime()) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Device uses ART runtime -> adding plugins directly");
            }
            return useAllPlugins ? getAnalyzer() : getAnalyzer(plugins);
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Device does not use ART runtime -> adding plugins via thread");
            }
            try {
                ThreadGroup group = new ThreadGroup("SemdroidPlugins");
                SemdroidAnalyzerLoaderRunnable runnable =
                        new SemdroidAnalyzerLoaderRunnable(useAllPlugins, plugins);
                Thread thread = new Thread(group, runnable, "PluginLoader", 2000000);
                thread.start();
                if (thread.isAlive()) {
                    thread.join();
                }
                return runnable.getNewAnalyzer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String packageName = intent.getStringExtra(KEY_PACKAGE_NAME);
        final String[] plugins = intent.getStringArrayExtra(KEY_PLUGINS);
        final boolean useAllPlugins = plugins == null;

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Got intent for package: " + packageName);
        }

        String cacheFileName = useAllPlugins ? getResultsKey(packageName) :
                getResultsKey(packageName, plugins);
        try {

            File resultFile = new File(getExternalCacheDir(), cacheFileName);
            if (!resultFile.exists()) {
                PackageManager pm = getPackageManager();
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);

                long start = System.currentTimeMillis();
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "ApplicationInfo.publicSourceDir: " + applicationInfo.publicSourceDir);
                }

                SemdroidAnalyzer semdroid = getSemdroid(useAllPlugins, plugins);
                BroadcastAnalysisProgressListener listener = new BroadcastAnalysisProgressListener(packageName);

                // register listener
                semdroid.registerProgressListener(listener);

                // analyze app
                SemdroidReport report = semdroid.analyze(new File(applicationInfo.publicSourceDir));
                LiteSemdroidReport results = LiteSemdroidReport.fromTestSuiteReport(report);

                // unregister listener since the analysis is done
                semdroid.unregisterProgressListener(listener);

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Done. " + packageName + " Time: "
                            + (System.currentTimeMillis() - start));
                }

                // we write the object to the result file - even if it already exists
                // since we could have newer results
                FileUtils.writeObjectToZipFile(results, resultFile);

            }
            Intent broadcastIntent = new Intent(ACTION_ANALYSIS_FINISHED)
                    .putExtra(KEY_PACKAGE_NAME, packageName)
                    .putExtra(KEY_RESULTS_KEY, resultFile.getAbsolutePath());
            sendOrderedBroadcast(broadcastIntent, PERMISSION_ACCESS_ANALYSIS_RESULTS);
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            }
            e.printStackTrace();
        }
    }

    protected class BroadcastAnalysisProgressListener implements AnalysisProgressListener {

        private String mPackageName;

        public BroadcastAnalysisProgressListener(String packageName) {
            mPackageName = packageName;
        }

        @Override
        public void onStatusUpdated(int status, int percentageCompleted) {
            Intent intent = new Intent(ACTION_ANALYSIS_STATUS_UPDATE)
                    .putExtra(KEY_PACKAGE_NAME, mPackageName)
                    .putExtra(KEY_ANALYSIS_STATUS, status)
                    .putExtra(KEY_ANALYSIS_STATUS_PERCENTAGE, percentageCompleted);
            sendOrderedBroadcast(intent, PERMISSION_ACCESS_ANALYSIS_RESULTS);
        }
    }

    protected class SemdroidAnalyzerLoaderRunnable implements Runnable {

        private boolean mUseAllPlugins;
        private String[] mPlugins;
        private SemdroidAnalyzer newAnalyzer = null;

        public SemdroidAnalyzerLoaderRunnable(boolean useAllPlugins, String[] plugins) {
            mUseAllPlugins = useAllPlugins;
            mPlugins = plugins;
        }

        @Override
        public void run() {
            newAnalyzer = mUseAllPlugins ? getAnalyzer() : getAnalyzer(mPlugins);
        }

        public SemdroidAnalyzer getNewAnalyzer() {
            return newAnalyzer;
        }
    }

}
