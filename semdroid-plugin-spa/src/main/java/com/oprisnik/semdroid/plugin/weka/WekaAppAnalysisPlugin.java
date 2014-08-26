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

package com.oprisnik.semdroid.plugin.weka;

import com.oprisnik.semdroid.analysis.BaseAnalysisPlugin;
import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.Labelable;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.utils.Log;

import java.util.List;

/**
 * Application analysis plugin that uses the Weka machine learning framework.
 */
public class WekaAppAnalysisPlugin extends BaseAnalysisPlugin {

    public static final String KEY_FEATURE_EXTRACTOR = "feature-extractor";
    public static final String KEY_DATA_CLASSIFIER = "data-classifier";

    private static final String TAG = "WekaAppAnalysisPlugin";

    protected DataClassifier mDataClassifier;

    protected WekaFeatureExtractor mFeatureExtractor;

    public WekaAppAnalysisPlugin() {
    }

    @Override
    public void init(Config config) throws BadConfigException {
        super.init(config);
        if (config == null) {
            throw new BadConfigException("Config null...");
        }
        // check if the feature extractor has already been set and load if not
        if (mFeatureExtractor == null) {
            mFeatureExtractor = config.getComponentAndInit(KEY_FEATURE_EXTRACTOR, WekaFeatureExtractor.class);
        }

        // check if the dataClassifier has already been set and load if not
        if (mDataClassifier == null) {
            mDataClassifier = config.getComponentAndInit(KEY_DATA_CLASSIFIER, DataClassifier.class);
        }
    }

    public DataClassifier getDataClassifier() {
        return mDataClassifier;
    }

    public WekaFeatureExtractor getFeatureExtractor() {
        return mFeatureExtractor;
    }

    @Override
    public void analyze(AppAnalysisReport report, App app) {
        WekaData data = mFeatureExtractor.extract(app);

        long start = System.currentTimeMillis();
        String[] labelsS = mDataClassifier.classify(data.getInstances());
        Log.d(TAG, "Data classifier: " + (System.currentTimeMillis() - start) + "ms.");

        List<Object> linkedObjects = data.getLinkedObjects();

        for (int i = 0; i < labelsS.length; i++) {

            Object obj = linkedObjects.get(i);
            if (obj instanceof Labelable) {
                report.label((Labelable) obj, labelsS[i]);
            } else {
                if (obj == null) {
                    Log.e(TAG, "Linked object null! Label: " + labelsS[i]);
                } else {
                    Log.e(TAG, "Invalid linked object! " + obj.toString());
                }
            }

        }
    }
}
