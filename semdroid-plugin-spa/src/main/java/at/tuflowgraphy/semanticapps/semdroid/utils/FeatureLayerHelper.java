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

import com.oprisnik.semdroid.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import at.tuflowgraphy.semantic.base.domain.data.DatasetDataElement;
import at.tuflowgraphy.semantic.base.domain.data.DistanceBasedFeatureDataElement;
import at.tuflowgraphy.semantic.base.domain.data.IDataElement;
import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;
import at.tuflowgraphy.semantic.base.domain.data.InstanceReferenceDataElement;
import at.tuflowgraphy.semantic.base.domain.data.SymbolicFeatureDataElement;

/**
 * Feature layer helper.
 */
public class FeatureLayerHelper {

    public static String getFeatures(File featureLayerFile) throws IOException, ClassNotFoundException {
        DatasetDataElement data = (DatasetDataElement) FileUtils.loadObjectFromZipFile(featureLayerFile);
        return getFeatures(data);
    }

    public static String getFeatures(DatasetDataElement featureLayers) {
        StringBuilder sb = new StringBuilder();
        sb.append("DatasetDataElement ").append(featureLayers.getName());
        sb.append('\n');
        for (InstanceDataElement instance : featureLayers.getValue()) {
            sb.append(getFeatures(instance));
        }
        DatasetDataElement next = featureLayers.getNextDataSet();
        if (next != null) {
            sb.append('\n');
            sb.append(getFeatures(next));
        }
        return sb.toString();
    }

    public static String getFeatures(InstanceDataElement instance) {
        StringBuilder sb = new StringBuilder();
        for (IDataElement element : instance.getValue()) {
            if (element instanceof DistanceBasedFeatureDataElement) {
                sb.append(element.getName());
                sb.append(" = ");
                sb.append(Arrays.toString(((DistanceBasedFeatureDataElement) element).getValue()));
                sb.append('\n');
            } else if (element instanceof SymbolicFeatureDataElement) {
                sb.append(element.getName());
                sb.append(" = ");
                sb.append(((SymbolicFeatureDataElement) element).getValue());
                sb.append('\n');
            } else if (element instanceof InstanceReferenceDataElement) {
                DistanceBasedFeatureDataElement temp = ((InstanceReferenceDataElement) element).getValue();
                sb.append(element.getName());
                sb.append(" = ");
                sb.append(Arrays.toString(temp.getValue()));
                sb.append('\n');
            } else {
                System.err.println("Type not recognized: " + instance);
                sb.append("UNKNOWN ").append(instance.getName());
                sb.append('\n');
            }
        }
        return sb.toString();
    }
}
