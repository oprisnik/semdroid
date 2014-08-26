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

package at.tuflowgraphy.semanticapps.semdroid.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.tuflowgraphy.semantic.analysis.analysischains.AnalysisChain;
import at.tuflowgraphy.semantic.base.domain.activationpattern.DActivationPattern;
import at.tuflowgraphy.semantic.base.domain.activationpattern.DActivationPatternPackage;
import at.tuflowgraphy.semantic.base.domain.metadata.DSimpleStringMetaData;
import at.tuflowgraphy.semanticapps.semdroid.DalvikInputPlugin;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * Arff helper. Create arff files from Semantic Patterns.
 */
public class ArffHelper {

    public static void saveWekaInstances(Instances instances, File arffFile) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(instances);
        saver.setFile(arffFile);
        saver.writeBatch();
    }

    public static void saveWekaInstances(Instances instances, String arffFile) throws IOException {
        saveWekaInstances(instances, new File(arffFile));
    }

    public static void saveWekaInstances(Instances instances, OutputStream arffFile) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(instances);
        saver.setDestination(arffFile);
        saver.writeBatch();
    }


    public Instances getWekaInstances(AnalysisChain analysisChain, String name) {
        Instances instances = null;
        List<DActivationPatternPackage> dActivationPatternPackages = analysisChain
                .getFinalLayers().get(0).getResultAnalysisPackage()
                .getActivationPatternPackages();
        int counter = 0;
        for (DActivationPatternPackage dActivationPatternPackage : dActivationPatternPackages) {
            if (counter > 0) {
//                String resultFileName = arffFile.getName();
//                String newName = resultFileName.split("_")[0];
//                int index = resultFileName.indexOf("_");
//                newName += "-MISSING-" + counter + "-"
//                        + resultFileName.substring(index);
//                arffFileToWriteTo = new File(arffFile.getParentFile(), newName);
                System.err.println("ERROR: Multiple activation pattern packages found! Should not happen...");
            }
            counter++;
            DActivationPattern activationPatternTemp = dActivationPatternPackage
                    .getActivationPatterns().get(0);
            FastVector fvWekaAttributes = new FastVector(
                    activationPatternTemp.getRawPattern().length);
            for (int j = 0; j < activationPatternTemp.getRawPattern().length; j++) {
                Attribute attribute = new Attribute(j + "");
                fvWekaAttributes.addElement(attribute);
            }

            Set<String> labelSet = getLabelSet(dActivationPatternPackage);

            FastVector classValues = new FastVector(labelSet.size());
            for (String label : labelSet) {
                classValues.addElement(label);
            }

            Attribute classAttribute = new Attribute("Class", classValues);
            fvWekaAttributes.addElement(classAttribute);

            instances = new Instances(
                    name,
                    fvWekaAttributes, dActivationPatternPackage
                    .getActivationPatterns().size());
            instances.setClassIndex(instances.numAttributes() - 1);

            for (int i = 0; i < dActivationPatternPackage
                    .getActivationPatterns().size(); i++) {
                DActivationPattern activationPattern = dActivationPatternPackage
                        .getActivationPatterns().get(i);
                Instance instance = new Instance(fvWekaAttributes.size());

                for (int j = 0; j < activationPattern.getRawPattern().length; j++) {
                    instance.setValue(
                            (Attribute) fvWekaAttributes.elementAt(j),
                            activationPattern.getRawPattern()[j]);
                }

                instance.setDataset(instances);

                DSimpleStringMetaData metadata = (DSimpleStringMetaData) activationPattern
                        .getMetaData();
                List<String> keys = metadata.getMetaDataKeys();
                for (int k = 0; k < keys.size(); k++) {
                    if (keys.get(k).equals(DalvikInputPlugin.TAG_LABEL)) {
                        String label = metadata.getMetaDataEntries().get(k);

                        // TODO: dynamically add new labels to instances so that getLabelSet for-loop is not required

                        // System.out.println(label);
                        // if(!labelSet.contains(label)) {
                        // labelSet.add(label);
                        // // classValues.addElement(label);
                        // classAttribute.addStringValue(label);
                        // instances.attribute(instances.classIndex()).addValue(label);
                        // System.out.println("ADDED " + label);
                        // }
                        instance.setClassValue(label);
                        // TODO: only first class value used
                        break;
                    }
                }
                instances.add(instance);
            }

        }
        return instances;
    }

    protected static Set<String> getLabelSet(
            DActivationPatternPackage dActivationPatternPackage) {
        Set<String> labelSet = new HashSet<String>();

        for (int i = 0; i < dActivationPatternPackage.getActivationPatterns()
                .size(); i++) {
            DActivationPattern activationPattern = dActivationPatternPackage
                    .getActivationPatterns().get(i);
            DSimpleStringMetaData metadata = (DSimpleStringMetaData) activationPattern
                    .getMetaData();
            List<String> keys = metadata.getMetaDataKeys();
            for (int k = 0; k < keys.size(); k++) {
                if (keys.get(k).equals(DalvikInputPlugin.TAG_LABEL)) {
                    String label = metadata.getMetaDataEntries().get(k);
                    labelSet.add(label);
                }
            }
        }
        return labelSet;
    }

}
