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

import com.oprisnik.semdroid.analysis.BaseAnalysisPlugin;
import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.config.XmlConfig;
import com.oprisnik.semdroid.feature.extractor.SemanticPatternAnalysisFeatureExtractor;
import com.oprisnik.semdroid.feature.layer.FeatureLayerGenerator;
import com.oprisnik.semdroid.plugin.weka.WekaClassifier;
import com.oprisnik.semdroid.utils.FileUtils;
import com.oprisnik.semdroid.utils.Log;

import at.tuflowgraphy.semantic.base.domain.data.DatasetDataElement;
import at.tuflowgraphy.semantic.base.domain.metadata.DSimpleStringMetaData;
import at.tuflowgraphy.semanticapps.semdroid.DalvikBaseAnalyzer;
import at.tuflowgraphy.semanticapps.semdroid.SimpleDalvikAnalyzer;
import at.tuflowgraphy.semanticapps.semdroid.utils.ArffHelper;
import weka.core.Instances;

public class SPASpecificTrainer implements PluginSpecificTrainer {

    public static final String KEY_FEATURE_LAYERS_FILE = "feature-layers-file";
    public static final String KEY_DESTINATION_SEMANTIC_NET = "destination-semantic-net";
    public static final String KEY_DESTINATION_ARFF = "destination-arff";
    public static final String KEY_DESTINATION_ARFF_HEADER = "destination-arff-header";
    public static final String KEY_DESTINATION_WEKA_MODEL = "destination-weka-model";
    public static final String KEY_DESTINATION_PLUGIN_CONFIG = "destination-plugin-config";

    public static final String KEY_WEKA_CLASSIFIER = "weka-classifier";

    public final static String TAG = SPASpecificTrainer.class.getSimpleName();

    private FeatureLayerGenerator mFeatureLayerGenerator;
    private DalvikBaseAnalyzer mSemanticPatternFramework;


    private Config mConfig;

    private DatasetDataElement mFeatureLayers;

    @Override
    public void init(PluginTrainer trainer, Config config) throws BadConfigException {

        mConfig = config;

        String analysisName = config.getProperty(BaseAnalysisPlugin.KEY_NAME, "");

        // init feature layer generator
        mFeatureLayerGenerator = config.getComponentAndInit(SemanticPatternAnalysisFeatureExtractor.KEY_FEATURE_LAYER_GENERATOR,
                FeatureLayerGenerator.class);
        mFeatureLayerGenerator.setStatistics(trainer.getStatisticsCollector());
        mFeatureLayerGenerator.addLabels(true);

        // init semantic pattern framework
        mSemanticPatternFramework = config.getComponentAndInit(
                SemanticPatternAnalysisFeatureExtractor.KEY_SEMANTIC_PATTERN_ANALYZER,
                DalvikBaseAnalyzer.class, SimpleDalvikAnalyzer.class);
        mSemanticPatternFramework.init(analysisName, analysisName);

        mFeatureLayers = new DatasetDataElement();
        DSimpleStringMetaData metaData = new DSimpleStringMetaData();
        metaData.setBasicInformation(analysisName);
        mFeatureLayers.setName(analysisName); // not really needed but we do it anyway :)
        mFeatureLayers.setMetaData(metaData);
    }

    @Override
    public void onTrainingDataAvailable(AppAnalysisReport targetResults, App app) {
        // TODO: maybe use targetResults - here wo only use the App since results are also attached
        // directly to the app object

        // add feature layers for current app to mFeatureLayers
        mFeatureLayerGenerator.generateFeatureLayers(app, mFeatureLayers);
    }

    @Override
    public void generateAnalysisPlugin() throws Exception {
        // generate semantic pattern framework stuff
        generateSemanticPatternFrameworkFiles();

        // machine learning files
        generateMachineLearningModel();

        // plugin configuration
        savePluginConfig();

        Log.d(TAG, "DONE!");
    }

    protected void generateSemanticPatternFrameworkFiles() {
        // first we save our feature layers
        if (mConfig.hasProperty(KEY_FEATURE_LAYERS_FILE)) {
            String path = mConfig.getProperty(KEY_FEATURE_LAYERS_FILE);
            Log.d(TAG, "Saving feature layers to " + path);
            try {
                FileUtils.writeObjectToZipStream(mFeatureLayers, mConfig.getNestedOutputStream(KEY_FEATURE_LAYERS_FILE));
            } catch (Exception e) {
                Log.e(TAG, "Could not save feature layers to " + path);
                e.printStackTrace();
            }
        }

        // then we train the Semantic Pattern network using the generated feature layers
        mSemanticPatternFramework.train(mFeatureLayers);

        // now we save the semantic patterns network
        try {
            mSemanticPatternFramework.saveNet(mConfig.getNestedOutputStream(KEY_DESTINATION_SEMANTIC_NET));
        } catch (Exception e) {
            Log.e(TAG, "Could not save semantic pattern net to "
                    + mConfig.getProperty(KEY_DESTINATION_SEMANTIC_NET));
            e.printStackTrace();
        }
    }

    protected void generateMachineLearningModel() {
        // first we get the weka instances
        Instances wekaData = mSemanticPatternFramework.getWekaInstances();

        // then we save the arff file if we have an entry in the config
        if (mConfig.hasProperty(KEY_DESTINATION_ARFF)) {
            String path = mConfig.getProperty(KEY_DESTINATION_ARFF);
            try {
                Log.d(TAG, "Saving .arff file to " + path);
                ArffHelper.saveWekaInstances(wekaData, mConfig.getNestedOutputStream(KEY_DESTINATION_ARFF));
            } catch (Exception e) {
                Log.e(TAG, "Could not save arff file to " + path);
                e.printStackTrace();
            }
        }

        // save the weka header required to analyze new data
        //TODO: see if possible without header
        if (mConfig.hasProperty(KEY_DESTINATION_ARFF_HEADER)) {
            String path = mConfig.getProperty(KEY_DESTINATION_ARFF_HEADER);
            Instances wekaHeader = mSemanticPatternFramework.generateInstancesHeaderFirstActivationPatternPackage();
            try {
                Log.d(TAG, "Saving .arff header file to " + path);
                ArffHelper.saveWekaInstances(wekaHeader, mConfig.getNestedOutputStream(KEY_DESTINATION_ARFF_HEADER));
            } catch (Exception e) {
                Log.e(TAG, "Could not save arff header file to " + path);
                e.printStackTrace();
            }
        }

        // machine learning
        String wekaConfig = mConfig.getProperty(KEY_WEKA_CLASSIFIER);
        try {
            WekaClassifier c = new WekaClassifier(wekaConfig);
            c.train(wekaData);

            // save model
            String path = mConfig.getProperty(KEY_DESTINATION_WEKA_MODEL);
            Log.d(TAG, "Saving Weka model to " + path);
            FileUtils.writeObjectToStream(c.getClassifier(), mConfig.getNestedOutputStream(KEY_DESTINATION_WEKA_MODEL));
        } catch (Exception e) {
            Log.e(TAG, "Could not create Weka classifier: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void savePluginConfig() {
        String path = mConfig.getProperty(KEY_DESTINATION_PLUGIN_CONFIG);
        Log.d(TAG, "Saving plugin configuration to " + path);
        try {
            Config newConfig = generatePluginConfig();
            newConfig.saveTo(mConfig.getNestedOutputStream(KEY_DESTINATION_PLUGIN_CONFIG));
        } catch (Exception e) {
            Log.e(TAG, "Could not create plugin configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected Config generatePluginConfig() throws Exception {
        Config config = new XmlConfig();

        // TODO: implement - and also copy the required files like opcode grouper, whitelists etc.
        Log.e(TAG, "TODO: Plugin configuration generation not updated to new XML format yet! " +
        "Please create the plugin.xml manually and copy all required files from the training directory!");
        // Copy config
//        for (AnalysisConfig s : AnalysisConfig.values()) {
//            prop.copyProperty(mTrainingConfig, s);
//        }
//
//
//        newConfig.setProperty(AnalysisConfig.CLASSIFICATION_MODEL, wekaModelFile);
//        newConfig.setProperty(AnalysisConfig.MODEL_ARFF_FILE, wekaHeaderFile);
//
//        // copy files
//        for (AnalysisConfig key : AnalysisConfig.values()) {
//            if (key.isFileToCopy()) {
//                try {
//                    File src = mTrainingConfig.getNestedFile(key);
//
//                    if (src != null) {
//                        File dest = new File(classifierFolder, src.getName());
//                        if (!dest.exists()) {
//                            dest.createNewFile();
//                        }
//                        Log.d(TAG, "Copying " + src + " to " + dest);
//                        FileUtils.copy(src, dest);
//                        prop.setProperty(key, dest.getName());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                }
//            }
//        }
//
//
//        prop.setProperty(AnalysisConfig.SEMANTIC_PATTERNS_NET,
//                DefaultValues.SEMANTIC_PATTERNS_FILE_NAME);
//
//        // New properties
//        prop.setProperty(AnalysisConfig.NEEDS_LABELABLE_OBJECT_LINKS, "true");
//        // we analyze everything now
//        prop.setProperty(AnalysisConfig.ANALYZE_ONLY_LABELED_COMPONENTS, "false");
//
//        prop.setProperty(AnalysisConfig.PLUGIN_TYPE, SemanticPatternAnalysisPlugin.class.getName());
        return config;
    }


}
