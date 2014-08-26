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

package com.oprisnik.semdroid.feature.extractor;

import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.feature.layer.FeatureLayerGenerator;
import com.oprisnik.semdroid.plugin.weka.WekaData;
import com.oprisnik.semdroid.plugin.weka.WekaFeatureExtractor;
import com.oprisnik.semdroid.utils.Log;

import java.util.ArrayList;
import java.util.List;

import at.tuflowgraphy.semantic.base.domain.data.DatasetDataElement;
import at.tuflowgraphy.semanticapps.semdroid.DalvikBaseAnalyzer;
import at.tuflowgraphy.semanticapps.semdroid.SimpleDalvikAnalyzer;
import at.tuflowgraphy.semanticapps.semdroid.utils.FeatureLayerHelper;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Weka feature extractor for the Semantic Pattern Analysis.
 * Feature layers will be extracted, converted to Semantic Patterns and then stored as Weka instances.
 */
public class SemanticPatternAnalysisFeatureExtractor implements WekaFeatureExtractor {

    public static final String KEY_SEMANTIC_PATTERNS_NET = "semantic-patterns-net";
    public static final String KEY_SEMANTIC_PATTERN_ANALYZER = "semantic-pattern-analyzer";


    public static final String KEY_FEATURE_LAYER_GENERATOR = "feature-layer-generator";
    public static final String KEY_ARFF_HEADER_FILE = "arff-header-file";

    private static final String TAG = SemanticPatternAnalysisFeatureExtractor.class.getSimpleName();

    private Instances mInstances;
    private DalvikBaseAnalyzer mSemanticPatternFramework;

    private FeatureLayerGenerator mFeatureLayerGenerator;

    @Override
    public void init(Config config) throws BadConfigException {
        DataSource source = new DataSource(
                config.getNestedInputStream(KEY_ARFF_HEADER_FILE));
        try {
            mInstances = source.getDataSet();
        } catch (Exception e) {
            throw new BadConfigException(e.getMessage());
        }
        mInstances.setClassIndex(mInstances.numAttributes() - 1);
        mSemanticPatternFramework = config.getComponentAndInit(KEY_SEMANTIC_PATTERN_ANALYZER,
                DalvikBaseAnalyzer.class, SimpleDalvikAnalyzer.class);
        mSemanticPatternFramework.init("", "");
        try {
            mSemanticPatternFramework.loadNet(config.getNestedInputStream(KEY_SEMANTIC_PATTERNS_NET));
        } catch (Exception e) {
            throw new BadConfigException("Could not load Semantic Pattern Net from: "
                    + config.getProperty(KEY_SEMANTIC_PATTERNS_NET) + ": " + e.getMessage());
        }

        mFeatureLayerGenerator = config.getComponentAndInit(KEY_FEATURE_LAYER_GENERATOR,
                FeatureLayerGenerator.class);
        mFeatureLayerGenerator.addLabels(false);
    }

    @Override
    public WekaData extract(App app) {
        Instances i = new Instances(mInstances, app.getMethods().size());
        List<Object> linkedObjects = new ArrayList<Object>();
        if (mFeatureLayerGenerator != null) {
            long start = System.currentTimeMillis();
            DatasetDataElement d = mFeatureLayerGenerator
                    .generateFeatureLayers(app);
            Log.d(TAG, FeatureLayerHelper.getFeatures(d));
            long featureLayers = System.currentTimeMillis();
            Log.d(TAG,
                    "Feature layer generation: "
                            + (featureLayers - start) + " ms."
            );
            mSemanticPatternFramework.getWekaData(d, i, linkedObjects);
            long done = System.currentTimeMillis();
            Log.d(TAG,
                    "Semantic Pattern framework: "
                            + (done - featureLayers) + " ms."
            );

            Log.d(TAG,
                    "Feature extractor total: "
                            + (done - start) + " ms."
            );
        }
        return new WekaData(i, linkedObjects);
    }

}
