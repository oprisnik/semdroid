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

import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.utils.Log;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;

/**
 * Classify given data using the Weka framework.
 */
public class WekaDataClassifier implements DataClassifier {

    public static final String KEY_WEKA_MODEL_FILE = "weka-model";

    private static final String TAG = "WekaDataClassifier";

    private Classifier mClassifier;

    public WekaDataClassifier() {
    }

    public void loadClassificationModel(InputStream modelPath) throws BadConfigException {
        try {
            mClassifier = (Classifier) SerializationHelper.read(modelPath);
        } catch (Exception e) {
            throw new BadConfigException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(modelPath);
        }
    }

    @Override
    public void init(Config config) throws BadConfigException {
        if (config.hasProperty(KEY_WEKA_MODEL_FILE)) {
            loadClassificationModel(config.getNestedInputStream(KEY_WEKA_MODEL_FILE));
        } else {
            throw new BadConfigException("Classification model not set!");
        }
    }

    @Override
    public String[] classify(Instances data) {

        String[] res = new String[data.numInstances()];
        for (int i = 0; i < data.numInstances(); i++) {
            try {
                double clsLabel = mClassifier.classifyInstance(data.instance(i));
                res[i] = data.classAttribute().value((int) clsLabel);
            } catch (Exception e) {
                Log.e(TAG, "Could not label instance " + i + ": " + e.getMessage());
            }
        }
        return res;
    }

    public Classifier getClassifier() {
        return mClassifier;
    }

    public void setClassifier(Classifier classifier) {
        mClassifier = classifier;
    }
}
