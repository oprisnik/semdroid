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

import com.oprisnik.semdroid.utils.Log;

import java.util.Arrays;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;

/**
 * Generate Weka models using given training data
 */
public class WekaClassifier {

    private static final String TAG = "WekaClassifier";


    private Classifier mCls;

    public WekaClassifier() throws Exception {
        this(null);
    }

    public WekaClassifier(String wekaClassifierConfig) throws Exception {
        if (wekaClassifierConfig == null) {
            mCls = new SMO();
            return;
        }
        String classifier = wekaClassifierConfig;
        String[] options = null;
        int firstSpaceIndex = wekaClassifierConfig.indexOf(' ');
        if (firstSpaceIndex > 0) {
            classifier = wekaClassifierConfig.substring(0, firstSpaceIndex);
            options = wekaClassifierConfig.substring(firstSpaceIndex + 1).split(" ");
            Log.d(TAG, "Using classifier: " + classifier);
            Log.d(TAG, "options:" + Arrays.toString(options));
        } else {
            Log.d(TAG, "Using default classifier.");
        }
        mCls = Classifier.forName(classifier, options);
    }

    public void train(Instances trainingData) throws Exception {
        mCls.buildClassifier(trainingData);
    }

    public void evaluateModel(Instances trainingData, Instances testData) throws Exception {
        // evaluate classifier and print some statistics
        Evaluation eval = new Evaluation(trainingData);
        eval.evaluateModel(mCls, testData);
        Log.d(TAG, eval.toSummaryString("Results:\n", false));
    }

    public Classifier getClassifier() {
        return mCls;
    }
}
