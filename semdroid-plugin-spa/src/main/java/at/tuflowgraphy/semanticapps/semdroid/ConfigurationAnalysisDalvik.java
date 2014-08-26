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
import java.util.ArrayList;
import java.util.List;

import at.tuflowgraphy.semantic.analysis.analysischains.config.analysis.BaseAnalysisConfiguration;
import at.tuflowgraphy.semantic.base.domain.data.DatasetDataElement;
import at.tuflowgraphy.semantic.inputlayer.IInputPlugin;
import at.tuflowgraphy.semanticapps.semdroid.utils.SemanticPatternUtils;

/**
 * Analysis configuration that uses a List<String> or a file as data source.
 */
public class ConfigurationAnalysisDalvik extends BaseAnalysisConfiguration {

    protected List<String> dataStringList;
    protected File metadataLocation;
    protected DatasetDataElement data;

    public void setData(String dataFile) throws IOException {
        setData(SemanticPatternUtils.loadStringList(dataFile));
    }

    public void setData(File dataFile) throws IOException {
        setData(SemanticPatternUtils.loadStringList(dataFile));
    }

    public void setData(List<String> data) {
        this.dataStringList = data;
        this.data = null;
    }

    public void setData(DatasetDataElement data) {
        this.data = data;
        this.dataStringList = null;
    }

    public void setMetadataLocation(String metadataLocation) {
        if (metadataLocation == null)
            this.metadataLocation = null;
        setMetadataLocation(new File(metadataLocation));
    }

    public void setMetadataLocation(File metadataLocation) {
        this.metadataLocation = metadataLocation;
    }

    public List<String> getDataStringList() {
        return dataStringList;
    }

    public DatasetDataElement getData() {
        return data;
    }

    public File getMetadataLocation() {
        return metadataLocation;
    }

    @Override
    protected List<IInputPlugin> generateApplicationSpecificInputPlugins() {
        List<IInputPlugin> inputPluginChain = new ArrayList<IInputPlugin>();
        DalvikInputPlugin inputPlugin = new DalvikInputPlugin();
        inputPlugin.setInstanceFeatureName("method");
        if (data == null) {
            inputPlugin.setInputData(dataStringList);
            inputPlugin.setMetadataFile(metadataLocation);
        } else {
            inputPlugin.setInputData(data);
        }

        inputPluginChain.add(inputPlugin);
        return inputPluginChain;
    }

    @Override
    protected void initDefaultParameters() {
    }

}
