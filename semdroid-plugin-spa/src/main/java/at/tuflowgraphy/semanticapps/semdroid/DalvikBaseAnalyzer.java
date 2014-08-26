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

import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.config.Configurable;
import com.oprisnik.semdroid.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.tuflowgraphy.semantic.analysis.analysischains.AnalysisChain;
import at.tuflowgraphy.semantic.analysis.analysischains.config.patterngeneration.ParameterFactory;
import at.tuflowgraphy.semantic.analysis.analysischains.config.patterngeneration.ParameterGenerator;
import at.tuflowgraphy.semantic.analysis.analysischains.config.patterngeneration.ParametersStandardInstanceAnalysis;
import at.tuflowgraphy.semantic.analysis.layer.SemanticNetworkLayer;
import at.tuflowgraphy.semantic.base.domain.activationpattern.DActivationPattern;
import at.tuflowgraphy.semantic.base.domain.activationpattern.DActivationPatternPackage;
import at.tuflowgraphy.semantic.base.domain.data.DatasetDataElement;
import at.tuflowgraphy.semantic.base.domain.metadata.DBasicMetaData;
import at.tuflowgraphy.semantic.base.domain.metadata.DObjectLinkMetaData;
import at.tuflowgraphy.semantic.base.domain.metadata.DSimpleStringMetaData;
import at.tuflowgraphy.semantic.base.domain.semanticnetwork.raw.RawSemanticNetwork;
import at.tuflowgraphy.semanticapps.semdroid.utils.ArffHelper;
import at.tuflowgraphy.semanticapps.semdroid.utils.SemanticPatternUtils;
import at.tuflowgraphy.semanticconfig.CustomConfig;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Base analyzer for Dalvik bytecode.
 */
public abstract class DalvikBaseAnalyzer implements Configurable {

    private ConfigurationAnalysisDalvik mAnalysisConfig;
    private AnalysisChain mAnalysisChain;
    private List<RawSemanticNetwork> rawSemanticNetworks;

    private CustomConfig mCustomConfig;

    public DalvikBaseAnalyzer() {
        setCustomConfig(initCustomConfig());
        setAnalysisConfig(initAnalysisConfig());
    }

    protected DalvikBaseAnalyzer(CustomConfig config,
                                 ConfigurationAnalysisDalvik analysisConfig) {
        setCustomConfig(config);
        setAnalysisConfig(analysisConfig);
    }

    @Override
    public void init(Config config) throws BadConfigException {
        // TODO: load custom config from file
    }

    public abstract CustomConfig initCustomConfig();

    public abstract ConfigurationAnalysisDalvik initAnalysisConfig();

    public AnalysisChain getAnalysisChain() {
        return mAnalysisChain;
    }

    public void setAnalysisChain(AnalysisChain analysisChain) {
        mAnalysisChain = analysisChain;
    }

    public List<RawSemanticNetwork> getRawSemanticNetworks() {
        return rawSemanticNetworks;
    }

    public void setRawSemanticNetworks(
            List<RawSemanticNetwork> rawSemanticNetworks) {
        this.rawSemanticNetworks = rawSemanticNetworks;
    }

    public void init(String applicationAnalysisName, String specificAnalysisName) {
        setAnalysisName(applicationAnalysisName, specificAnalysisName);
        resetAnalysisChain();
    }

    public void setMetadataLocation(String metadataLocation) {
        mAnalysisConfig.setMetadataLocation(metadataLocation);
    }

    public void setMetadataLocation(File metadataLocation) {
        mAnalysisConfig.setMetadataLocation(metadataLocation);
    }

    public void train(String trainingFile) throws IOException {
        train(SemanticPatternUtils.loadStringList(trainingFile));
    }

    public void train(File trainingFile) throws IOException {
        train(SemanticPatternUtils.loadStringList(trainingFile));
    }

    public void train(List<String> trainingData) {
        mAnalysisConfig.setData(trainingData);
        trainInternal();
    }

    public void train(DatasetDataElement trainingData) {
        mAnalysisConfig.setData(trainingData);
        trainInternal();
    }

    /**
     * Called after training data has been set
     */
    protected void trainInternal() {
        resetAnalysisChain();
        mAnalysisChain.performAnalysis();
    }

    public void analyze(String newDataFile) throws IOException {
        analyze(SemanticPatternUtils.loadStringList(newDataFile));
    }

    public void analyze(File newDataFile) throws IOException {
        analyze(SemanticPatternUtils.loadStringList(newDataFile));
    }

    public void analyze(List<String> newData) {
        mAnalysisConfig.setData(newData);
        analyzeInternal();
    }

    public void analyze(DatasetDataElement newData) {
        mAnalysisConfig.setData(newData);
        analyzeInternal();
    }

    /**
     * Called after data has been set
     */
    protected void analyzeInternal() {
        resetAnalysisChain();
        if (rawSemanticNetworks != null) {
            ((SemanticNetworkLayer) mAnalysisChain.getInitialLayer()
                    .getSubsequentLayers().get(0))
                    .setExistingSemanticNetwork(rawSemanticNetworks);
        }
        mAnalysisChain.performAnalysis();
    }

    public double[][] getRawData(String newDataFile) throws IOException {
        return getRawData(new File(newDataFile));
    }

    public double[][] getRawData(File newDataFile) throws IOException {
        return getRawData(SemanticPatternUtils.loadStringList(newDataFile));
    }

    public double[][] getRawData(List<String> newData) {
        double[][] results = null;

        analyze(newData);

        List<DActivationPatternPackage> dActivationPatternPackages = mAnalysisChain
                .getFinalLayers().get(0).getResultAnalysisPackage()
                .getActivationPatternPackages();
        int counter = 0;
        for (DActivationPatternPackage dActivationPatternPackage : dActivationPatternPackages) {
            DActivationPattern activationPatternTemp = dActivationPatternPackage
                    .getActivationPatterns().get(0);
            if (counter > 0) {
                throw new RuntimeException("More than one DActivationPatternPackage found!");
            }
            counter++;
            results = new double[newData.size()][activationPatternTemp
                    .getRawPattern().length];
            for (int i = newData.size() - 1; i >= 0; i--) {
                DActivationPattern activationPattern = dActivationPatternPackage
                        .getActivationPatterns()
                        .get(dActivationPatternPackage.getActivationPatterns()
                                .size() - 1 - i);
                // TODO: we could also just copy the double[][] array
                System.arraycopy(activationPattern.getRawPattern(), 0,
                        results[newData.size() - 1 - i], 0,
                        activationPattern.getRawPattern().length);
            }
        }
        return results;
    }


    protected void addLastXWekaInstances(Instances instances,
                                         int numberOfInstances, List<Object> linkedObjects) {
        // TODO: use ResultEntry instead of List<Object>
        if (numberOfInstances <= 0) {
            return;
        }
        List<DActivationPatternPackage> dActivationPatternPackages = mAnalysisChain
                .getFinalLayers().get(0).getResultAnalysisPackage()
                .getActivationPatternPackages();
        int counter = 0;
        for (DActivationPatternPackage dActivationPatternPackage : dActivationPatternPackages) {
            DActivationPattern activationPatternTemp = dActivationPatternPackage
                    .getActivationPatterns().get(0);
            if (counter > 0) {
                throw new RuntimeException("More than one DActivationPatternPackage found!");
            }
            counter++;

            for (int i = numberOfInstances - 1; i >= 0; i--) {
                DActivationPattern activationPattern = dActivationPatternPackage
                        .getActivationPatterns()
                        .get(dActivationPatternPackage.getActivationPatterns()
                                .size() - 1 - i);
                Instance instance = new Instance(
                        activationPatternTemp.getRawPattern().length + 1);

                for (int j = 0; j < activationPattern.getRawPattern().length; j++) {
                    instance.setValue(j, activationPattern.getRawPattern()[j]);
                }
                instances.add(instance);
                instance.setDataset(instances);

                if (linkedObjects != null) {
                    DBasicMetaData d = activationPattern.getMetaData();
                    if (d instanceof DObjectLinkMetaData) {
                        Object o = ((DObjectLinkMetaData) d).getLinkedObject();
                        linkedObjects.add(o);
                    } else {
                        throw new IllegalArgumentException(
                                "Wrong metadata type attached! Must be DObjectLinkMetaData!");
                    }
                }
            }

        }
    }

    public void getWekaData(DatasetDataElement data, Instances instances,
                            List<Object> linkedObjects) {
        analyze(data);
        addLastXWekaInstances(instances, data.getValue().size(), linkedObjects);
    }

    public void getWekaData(String newDataFile, Instances instances,
                            List<Object> linkedObjects) throws IOException {
        getWekaData(new File(newDataFile), instances, linkedObjects);
    }

    public void getWekaData(File newDataFile, Instances instances,
                            List<Object> linkedObjects) throws IOException {
        getWekaData(SemanticPatternUtils.loadStringList(newDataFile), instances,
                linkedObjects);
    }

    public void getWekaData(List<String> newData, Instances instances,
                            List<Object> linkedObjects) {
        analyze(newData);
        addLastXWekaInstances(instances, newData.size(), linkedObjects);
    }

    public void resetAnalysisChain() {
        ParameterFactory parameterFactory = ParameterFactory.getInstance();
        ParameterGenerator parameterGenerator = parameterFactory
                .getParameterGenerator(ParametersStandardInstanceAnalysis.class);
        parameterGenerator.setOverriddenProperties(mCustomConfig
                .generateParameters());
        if (mAnalysisConfig != null)
            mAnalysisChain = mAnalysisConfig.constructAnalyzers();
    }

    public CustomConfig getCustomConfig() {
        return mCustomConfig;
    }

    public void setCustomConfig(CustomConfig config) {
        mCustomConfig = config;
        resetAnalysisChain();
    }

    public ConfigurationAnalysisDalvik getAnalysisConfig() {
        return mAnalysisConfig;
    }

    public void setAnalysisConfig(ConfigurationAnalysisDalvik configuration) {
        mAnalysisConfig = configuration;
    }

    public void setPersistenceLocation(String persistenceLocation) {
        mAnalysisConfig.setPersistenceLocation(persistenceLocation);
    }

    public void setAnalysisName(String applicationAnalysisName,
                                String specificAnalysisName) {
        mAnalysisConfig.setApplicationAnalysisName(applicationAnalysisName);
        mAnalysisConfig.setSpecificAnalysisName(specificAnalysisName);
    }

    public void saveNet(String semanticNetworkFile) throws IOException {
        saveNet(new File(semanticNetworkFile));
    }

    public void saveNet(File semanticNetworkFile) throws IOException {
        if (!semanticNetworkFile.getParentFile().exists()) {
            semanticNetworkFile.getParentFile().mkdirs();
        }
        saveNet(new FileOutputStream(
                semanticNetworkFile));
    }

    public void saveNet(OutputStream semanticNetworkFile) throws IOException {
        List<RawSemanticNetwork> rawSemanticNetwork = mAnalysisChain
                .getFinalLayers().get(0).getResultAnalysisPackage()
                .getSemanticNetworks();
        FileUtils.writeObjectToZipStream(rawSemanticNetwork,
                semanticNetworkFile);
    }

    public List<ResultEntry> getResultEntries() {
        List<ResultEntry> results = new ArrayList<ResultEntry>();
        List<DActivationPatternPackage> dActivationPatternPackages = getAnalysisChain()
                .getFinalLayers().get(0).getResultAnalysisPackage()
                .getActivationPatternPackages();
        int counter = 0;
        for (DActivationPatternPackage dActivationPatternPackage : dActivationPatternPackages) {
            if (counter != 0) {
                System.out.println("Multiple dActivationPatternPackages! Should not happen.");
            }

            for (int i = 0; i < dActivationPatternPackage
                    .getActivationPatterns().size(); i++) {
                DActivationPattern activationPattern = dActivationPatternPackage
                        .getActivationPatterns().get(i);

                Object link = null;
                DBasicMetaData d = activationPattern.getMetaData();
                if (d instanceof DObjectLinkMetaData) {
                    link = ((DObjectLinkMetaData) d).getLinkedObject();
                } else {
                    System.out.println("No object attached for instance " + i
                            + " in cluster " + counter);
                }
                results.add(new ResultEntry(link, activationPattern
                        .getRawPattern()));
            }
            counter++;
        }

        return results;
    }

    public Instances getWekaInstances() {
        Instances instances = null;
        List<DActivationPatternPackage> dActivationPatternPackages = mAnalysisChain
                .getFinalLayers().get(0).getResultAnalysisPackage()
                .getActivationPatternPackages();
        int counter = 0;
        for (DActivationPatternPackage dActivationPatternPackage : dActivationPatternPackages) {
            if (counter > 0) {
                throw new RuntimeException("More than one DActivationPatternPackage found!");
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
                    mAnalysisConfig.getApplicationAnalysisName(),
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
                        instance.setClassValue(label);
                        break;
                    }
                }

                instances.add(instance);
            }

        }
        return instances;
    }

    public Instances generateInstancesHeaderFirstActivationPatternPackage() {
        return generateInstancesHeader(mAnalysisChain
                .getFinalLayers().get(0).getResultAnalysisPackage()
                .getActivationPatternPackages().get(0));
    }

    protected Instances generateInstancesHeader(DActivationPatternPackage dActivationPatternPackage) {
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

        Instances instances = new Instances(
                mAnalysisConfig.getApplicationAnalysisName(),
                fvWekaAttributes, dActivationPatternPackage
                .getActivationPatterns().size());
        instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }

    public void saveArff(String arffFile) throws IOException {
        ArffHelper.saveWekaInstances(getWekaInstances(), arffFile);
    }

    protected Set<String> getLabelSet(
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

    public void loadNet(InputStream semanticNetworkStream) throws IOException, ClassNotFoundException {

        rawSemanticNetworks = (List<RawSemanticNetwork>)
                FileUtils.loadObjectFromStream(semanticNetworkStream);

        ((SemanticNetworkLayer) mAnalysisChain.getInitialLayer()
                .getSubsequentLayers().get(0))
                .setExistingSemanticNetwork(rawSemanticNetworks);
    }

    public void loadNet(File semanticNetworkFile) throws IOException, ClassNotFoundException, FileNotFoundException {
        loadNet(new FileInputStream(
                semanticNetworkFile));
    }

}