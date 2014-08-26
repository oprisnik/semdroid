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

package at.tuflowgraphy.semanticapps.semdroid;

import at.tuflowgraphy.semantic.analysis.questions.Analyses;
import at.tuflowgraphy.semantic.spreadingactivation.fanoutcalculators.EmergingLinkFanoutCalculator;
import at.tuflowgraphy.semantic.spreadingactivation.linkweightnormalizer.LocalMaxNormalizer;
import at.tuflowgraphy.semantic.spreadingactivation.networkactivator.combineactivation.MultiplicationSumCombinationFunction;
import at.tuflowgraphy.semanticconfig.CustomConfig;

/**
 * Simple Dalvik Analyzer.
 */
public class DeepLearningDalvikAnalyzer extends DalvikBaseAnalyzer {

    private double decay = 0.5;
    private double preSpreadSigma = 0.2;
    private double mdl = 3;
    private double clusterLayerMdl = 1.5;
    private double fanoutSigma = 1.6;
    private Boolean beforeSpreadFanout = false;
    private Boolean duringSpreadFanout = false;
    private Boolean afterSpreadFanout = false;
    private Boolean removeSpreadingAfter = false;
    private Class fanoutCalculator = EmergingLinkFanoutCalculator.class;
    private Class combinationFunction = MultiplicationSumCombinationFunction.class;
    private Class normalizerFunction = LocalMaxNormalizer.class;
    private double missingValuesBeforeSemanticNetworkBuilding = -1;
    private double[] missingValuesAfterSemanticNetworkBuildingArray = new double[]{-1};

    @Override
    public CustomConfig initCustomConfig() {
        return new CustomConfig(decay, preSpreadSigma, fanoutCalculator,
                beforeSpreadFanout, duringSpreadFanout, afterSpreadFanout,
                fanoutSigma, mdl, clusterLayerMdl, removeSpreadingAfter, normalizerFunction,
                combinationFunction,
                missingValuesBeforeSemanticNetworkBuilding,
                missingValuesAfterSemanticNetworkBuildingArray);
    }

    @Override
    public ConfigurationAnalysisDalvik initAnalysisConfig() {
        ConfigurationAnalysisDalvik analysisConfig = new ConfigurationAnalysisDalvik();
        analysisConfig.setPosModelLocation("blabla");
        analysisConfig.addAnalysis(Analyses.ANALYSIS_INSTANCES);
        return analysisConfig;
    }

}
