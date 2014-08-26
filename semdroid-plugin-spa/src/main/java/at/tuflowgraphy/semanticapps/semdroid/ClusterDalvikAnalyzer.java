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

import java.util.ArrayList;
import java.util.List;

import at.tuflowgraphy.semantic.analysis.questions.Analyses;
import at.tuflowgraphy.semantic.base.domain.activationpattern.DActivationPattern;
import at.tuflowgraphy.semantic.base.domain.activationpattern.DActivationPatternPackage;
import at.tuflowgraphy.semantic.base.domain.data.DatasetDataElement;
import at.tuflowgraphy.semantic.base.domain.metadata.DBasicMetaData;
import at.tuflowgraphy.semantic.base.domain.metadata.DObjectLinkMetaData;
import at.tuflowgraphy.semantic.spreadingactivation.fanoutcalculators.EmergingLinkFanoutCalculator;
import at.tuflowgraphy.semantic.spreadingactivation.linkweightnormalizer.LocalMaxNormalizer;
import at.tuflowgraphy.semantic.spreadingactivation.networkactivator.combineactivation.MultiplicationSumCombinationFunction;
import at.tuflowgraphy.semanticconfig.CustomConfig;

/**
 * Cluster Dalvik Analyzer used for anomaly detection.
 */
public class ClusterDalvikAnalyzer extends DalvikBaseAnalyzer {

    private static double decay = 0.5;
    private static double preSpreadSigma = 0.2;
    private static double mdl = 1.5;
    private static double clusterLayerMdl = 1.5;
    private static double fanoutSigma = 1.6;
    private static Boolean beforeSpreadFanout = false;
    private static Boolean duringSpreadFanout = false;
    private static Boolean afterSpreadFanout = false;
    private static Boolean removeSpreadingAfter = false;
    private static Class fanoutCalculator = EmergingLinkFanoutCalculator.class;
    private static Class combinationFunction = MultiplicationSumCombinationFunction.class;
    private static Class normalizerFunction = LocalMaxNormalizer.class;
    private static double missingValuesBeforeSemanticNetworkBuilding = -1;
    private static double[] missingValuesAfterSemanticNetworkBuildingArray = new double[]{-1};

    @Override
    public CustomConfig initCustomConfig() {
        return new CustomConfig(decay, preSpreadSigma, fanoutCalculator,
                beforeSpreadFanout, duringSpreadFanout, afterSpreadFanout,
                fanoutSigma, mdl, clusterLayerMdl, removeSpreadingAfter,
                normalizerFunction, combinationFunction,
                missingValuesBeforeSemanticNetworkBuilding,
                missingValuesAfterSemanticNetworkBuildingArray);
    }

    @Override
    public ConfigurationAnalysisDalvik initAnalysisConfig() {
        ConfigurationAnalysisDalvik analysisConfig = new ConfigurationAnalysisDalvik();
        analysisConfig.setPosModelLocation("blabla");
        analysisConfig.addAnalysis(Analyses.ANALYSIS_INSTANCE_CLUSTERS);
        return analysisConfig;
    }

    public List<RawCluster> trainClusters(List<String> features) {
        analyze(features);
        return getResults();
    }

    public List<RawCluster> trainClusters(DatasetDataElement data) {
        analyze(data);
        return getResults();
    }

    protected List<RawCluster> getResults() {
        List<RawCluster> results = new ArrayList<RawCluster>();
        List<DActivationPatternPackage> dActivationPatternPackages = getAnalysisChain()
                .getFinalLayers().get(0).getResultAnalysisPackage()
                .getActivationPatternPackages();
        int counter = 0;
        int instanceCounter = 0;
        for (DActivationPatternPackage dActivationPatternPackage : dActivationPatternPackages) {
            if (counter == 0) {
                System.out.println("First cluster... skipping!");
                counter++;
                continue;
            }
            System.out.println("Cluster " + counter);

            RawCluster cluster = new RawCluster();

            for (int i = 0; i < dActivationPatternPackage
                    .getActivationPatterns().size(); i++) {
                DActivationPattern activationPattern = dActivationPatternPackage
                        .getActivationPatterns().get(i);

                instanceCounter++;
                Object link = null;
                DBasicMetaData d = activationPattern.getMetaData();
                if (d instanceof DObjectLinkMetaData) {
                    link = ((DObjectLinkMetaData) d).getLinkedObject();
                } else {
                    System.out.println("No object attached for instance " + i
                            + " in cluster " + counter);
                }
                cluster.add(new ResultEntry(link, activationPattern
                        .getRawPattern()));

            }
            results.add(cluster);
            counter++;

        }
        System.out.println("Instances in clusters: " + instanceCounter);

        return results;
    }
}
