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

package com.oprisnik.semdroid.training;

import com.oprisnik.semdroid.analysis.AppAnalysisPlugin;
import com.oprisnik.semdroid.analysis.AppAnalysisPluginFactory;
import com.oprisnik.semdroid.analysis.BaseAnalysisPlugin;
import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.parser.AppParser;
import com.oprisnik.semdroid.app.parser.DefaultAndroidAppParser;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.utils.FileUtils;
import com.oprisnik.semdroid.utils.Log;
import com.oprisnik.semdroid.utils.StatisticsCollector;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * App trainer. Load APK files, perform pre-classification and train and export models.
 * models.
 */
public class PluginTrainer {

    public static final String KEY_PLUGIN_SPECIFIC_TRAINER = "plugin-specific-trainer";
    public static final String KEY_TRAINING_PLUGIN = "training-plugin";

    private static final String TAG = PluginTrainer.class.getName();

    private String mAnalysisName;
    private AppParser mAppParser;
    private StatisticsCollector mStatisticsCollector;
    private AppAnalysisPlugin mTrainingPlugin;
    private PluginSpecificTrainer mTrainer;

    public void init(Config semdroidConfig, Config analysisConfig) throws BadConfigException {
        mAnalysisName = analysisConfig.getProperty(BaseAnalysisPlugin.KEY_NAME);

        // App parser (APK file => App object)
        mAppParser = new DefaultAndroidAppParser();
        mAppParser.init(semdroidConfig);

        // Statistics collector (count opcodes, function calls... for app set)
        mStatisticsCollector = new StatisticsCollector();

        // Training plugin => label apps for training (e.g. according to
        // folder name / app name, broadcast receivers...)
        mTrainingPlugin = AppAnalysisPluginFactory.fromConfig(analysisConfig.getSubconfig(KEY_TRAINING_PLUGIN));

        try {
            mTrainer = analysisConfig.getComponent(KEY_PLUGIN_SPECIFIC_TRAINER, PluginSpecificTrainer.class);
        } catch (Exception e) {
            throw new BadConfigException("Could not load plugin specific trainer: " + e.getMessage());
        }
        mTrainer.init(this, analysisConfig.getSubconfig(KEY_PLUGIN_SPECIFIC_TRAINER));
    }

    public void generateAnalysisPlugin() throws Exception {
        mTrainer.generateAnalysisPlugin();
    }

    public AppParser getAppParser() {
        return mAppParser;
    }

    public StatisticsCollector getStatisticsCollector() {
        return mStatisticsCollector;
    }

    public AppAnalysisPlugin getTrainingPlugin() {
        return mTrainingPlugin;
    }

    public PluginSpecificTrainer getPluginSpecificTrainer() {
        return mTrainer;
    }

    public void setPluginSpecificTrainer(PluginSpecificTrainer trainer) {
        mTrainer = trainer;
    }

    public void addTrainingFolder(File folder) throws Exception {
        File[] files = folder.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                addTrainingFolder(f);
            } else {
                addTrainingData(f);
            }
        }
    }

    public void addTrainingData(File apkFile) throws Exception {
        String name = apkFile.getName();
        if (name.endsWith(".apk") || name.endsWith(".dex")
                || name.endsWith(".odex")) {
            try {
                Log.d(TAG, "Adding to app pool: " + apkFile.getAbsolutePath());
                addTrainingData(mAppParser.parse(apkFile));
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }

    public synchronized void addTrainingData(List<App> apps) {
        for (App app : apps) {
            addTrainingData(app);
        }
    }

    public synchronized void addTrainingData(App app) {
        if (mStatisticsCollector != null) {
            mStatisticsCollector.analyze(app);
        }
        AppAnalysisReport target = null;
        if (mTrainingPlugin != null) {
            Log.d(TAG, "Applying training plugin...");
            try {
                target = mTrainingPlugin.analyze(app);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        mTrainer.onTrainingDataAvailable(target, app);
    }

    @SuppressWarnings("unchecked")
    public synchronized void loadAppPool(File inputFile)
            throws ClassNotFoundException, IOException {
        Log.d(TAG, "Loading app pool from " + inputFile);
        List<App> appPool = (List<App>) FileUtils.loadObjectFromZipFile(inputFile);
        Log.d(TAG, "Loaded " + appPool.size() + " apps!");
        addTrainingData(appPool);
    }
}
