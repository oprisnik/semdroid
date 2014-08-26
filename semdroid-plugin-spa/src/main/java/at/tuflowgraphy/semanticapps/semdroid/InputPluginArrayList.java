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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import at.tuflowgraphy.semantic.base.domain.data.DAnalysisPackage;
import at.tuflowgraphy.semantic.base.domain.data.DatasetDataElement;
import at.tuflowgraphy.semantic.base.domain.data.DistanceBasedFeatureDataElement;
import at.tuflowgraphy.semantic.base.domain.data.IDataElement;
import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;
import at.tuflowgraphy.semantic.base.domain.data.SymbolicFeatureDataElement;
import at.tuflowgraphy.semantic.base.domain.metadata.DSimpleStringMetaData;
import at.tuflowgraphy.semantic.inputlayer.BaseInputPlugin;
import at.tuflowgraphy.semanticapps.semdroid.utils.SemanticPatternUtils;

/**
 * Input plugin that uses a List<String> as data source.
 */
public class InputPluginArrayList extends BaseInputPlugin {

    protected List<String> inputDataStringList;
    protected DatasetDataElement inputData;

    protected boolean addInstanceNameAsFeature;

    public List<String> getInputDataStringList() {
        return inputDataStringList;
    }

    public void setInputDataFile(String file) throws IOException {
        setInputDataFile(new File(file));
    }

    public void setInputDataFile(File file) throws IOException {
        setInputData(SemanticPatternUtils.loadStringList(file));
    }

    public void setInputData(List<String> inputData) {
        this.inputDataStringList = inputData;
        this.inputData = null;
    }

    public void setInputData(DatasetDataElement inputData) {
        this.inputData = inputData;
        this.inputDataStringList = null;
    }

    public boolean isAddInstanceNameAsFeature() {
        return addInstanceNameAsFeature;
    }

    public void setAddInstanceNameAsFeature(boolean addInstanceNameAsFeature) {
        this.addInstanceNameAsFeature = addInstanceNameAsFeature;
    }

    @Override
    public void parseData() {
        // logger.log(Level.INFO, "Parsing raw data from " + inputData);
        analysisPackage = new DAnalysisPackage();
        if (inputData == null) {
            inputData = new DatasetDataElement();

            int index = 0;
            for (String sensorReadingsString : inputDataStringList) {
                String instanceName = "I" + index;
                convertRawData(inputData, sensorReadingsString, instanceName);
                index++;
            }
        }
        analysisPackage.addDataSet(inputData);
    }

    protected void convertRawData(DatasetDataElement model,
                                  String rawSensorReadingString, String instanceName) {
        logger.log(Level.FINEST, "Reading raw sensor input: "
                + rawSensorReadingString);
        StringTokenizer tokenizer = new StringTokenizer(rawSensorReadingString,
                ";");

        InstanceDataElement dInstance = new InstanceDataElement();

        String instanceFeatureValue = "";
        while (tokenizer.hasMoreTokens()) {
            String sensorReadingString = tokenizer.nextToken();
            String[] featureDataStrings = sensorReadingString.split(":");
            String feature = featureDataStrings[0];
            String type = featureDataStrings[1];
            String value = "";
            IDataElement dFeature = null;
            if ("R".equals(type)) {
                dFeature = new DistanceBasedFeatureDataElement();
                // check for groupID
                if (featureDataStrings.length == 3) {
                    value = featureDataStrings[2];
                } else {
                    value = featureDataStrings[3];
                }
            } else {
                dFeature = new SymbolicFeatureDataElement();
                value = featureDataStrings[2];
                value = value.replace(" ", "_");
            }

            if (instanceFeatureName != null) {
                if (feature.equals(instanceFeatureName)) {
                    instanceFeatureValue = value;
                }
            }
            boolean add = false;
            if (!feature.equals(instanceFeatureName)) {
                add = true;
            } else {
                add = addInstanceNameAsFeature;
            }
            if (add) {
                dFeature.setName(feature);
                dFeature.setValue(value);
                dInstance.addValue(dFeature);
            }
        }
        if (instanceFeatureName != null) {
            DSimpleStringMetaData simpleStringMetaData = new DSimpleStringMetaData();
            simpleStringMetaData.setBasicInformation(instanceFeatureValue);
            dInstance.setMetaData(simpleStringMetaData);
        } else {
            DSimpleStringMetaData simpleStringMetaData = new DSimpleStringMetaData();
            simpleStringMetaData.setBasicInformation(instanceName);
            dInstance.setMetaData(simpleStringMetaData);
        }

        InstanceDataElement instance = applyFilters(dInstance);
        if (instance != null) {
            model.addInstance(instance);
        }

    }

}
