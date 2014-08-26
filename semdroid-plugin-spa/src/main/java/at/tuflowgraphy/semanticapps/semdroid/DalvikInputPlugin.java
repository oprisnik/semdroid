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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.tuflowgraphy.semantic.base.domain.data.IDataElement;
import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;
import at.tuflowgraphy.semantic.base.domain.metadata.DSimpleStringMetaData;

/**
 * Dalvik input plugin that uses a List<String> as data source.
 */
public class DalvikInputPlugin extends InputPluginArrayList {

    public static final String TAG_LABEL = "label";

    private File metadata;

    public void setMetadataFile(File file) {
        metadata = file;
    }

    @Override
    public void parseData() {
        super.parseData();
        try {

            if (metadata != null) {
                // OLD FORMAT
                HashMap<Integer, String> codeTable = null;

                ObjectInputStream objectInputStream = new ObjectInputStream(
                        new BufferedInputStream(new FileInputStream(metadata)));
                codeTable = (HashMap<Integer, String>) objectInputStream
                        .readObject();
                objectInputStream.close();

                List<InstanceDataElement> instances = getAnalysisPackage()
                        .getDatasets().get(0).getValue();

                for (int i = 0; i < instances.size(); i++) {
                    InstanceDataElement dInstance = instances.get(i);
                    List<IDataElement> foundLabels = new ArrayList<IDataElement>();

                    DSimpleStringMetaData metaData = (DSimpleStringMetaData) dInstance
                            .getMetaData();

                    for (IDataElement v : dInstance.getValue()) {
                        if (v.getName().equals(TAG_LABEL)) {
                            // if (labels != null && !added) {
                            // added = true;
                            // // we only add one label
                            // labels.add(v.getValueAsString());
                            // }
                            metaData.addMetaDataEntry(TAG_LABEL,
                                    v.getValueAsString());
                            foundLabels.add(v);
                        }
                    }
                    for (IDataElement label : foundLabels) {
                        dInstance.getValue().remove(label);
                    }
                    if (codeTable != null) {
                        String string = codeTable.get(i + 1);
                        metaData.addMetaDataEntry("code", string);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
