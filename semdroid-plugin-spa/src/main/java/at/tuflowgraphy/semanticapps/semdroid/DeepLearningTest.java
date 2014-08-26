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

import at.tuflowgraphy.semantic.base.domain.data.DatasetDataElement;
import at.tuflowgraphy.semantic.base.domain.data.DistanceBasedFeatureDataElement;
import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;
import at.tuflowgraphy.semantic.base.domain.data.InstanceReferenceDataElement;
import at.tuflowgraphy.semantic.base.domain.data.SymbolicFeatureDataElement;
import at.tuflowgraphy.semantic.base.domain.metadata.DSimpleStringMetaData;

/**
 * Created with IntelliJ IDEA.
 * User: pteufl
 * Date: 4/13/13
 * Time: 12:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeepLearningTest {

    public static void main(String[] args) {
        //create dataset
        DatasetDataElement datasetDataElement = new DatasetDataElement();
        DSimpleStringMetaData metaData = new DSimpleStringMetaData();
        metaData.setBasicInformation("Dataset metadata");
        datasetDataElement.setName("Dataset Layer 1");   //not really needed... didn't exist before
        datasetDataElement.setMetaData(metaData);

        //create instances
        List<InstanceDataElement> instances = new ArrayList<InstanceDataElement>();

        //create an instance
        InstanceDataElement instanceDataElement = new InstanceDataElement();
        metaData = new DSimpleStringMetaData();
        metaData.setBasicInformation("Instance metadata 1");
        instanceDataElement.setMetaData(metaData);

        //double feature value
        DistanceBasedFeatureDataElement doubleFeatureValue = new DistanceBasedFeatureDataElement();
        doubleFeatureValue.setName("Temperature");
        doubleFeatureValue.setValue(20); //or set a double[], handled by the same class

        //symbolic feature value
        SymbolicFeatureDataElement symbolicFeatureDataElement = new SymbolicFeatureDataElement();
        symbolicFeatureDataElement.setName("DayOfWeek");
        symbolicFeatureDataElement.setValue("Sunday");

        instanceDataElement.addValue(doubleFeatureValue);
        instanceDataElement.addValue(symbolicFeatureDataElement);


        instances.add(instanceDataElement);

        //create an instance
        instanceDataElement = new InstanceDataElement();
        metaData = new DSimpleStringMetaData();
        metaData.setBasicInformation("Instance metadata 2");
        instanceDataElement.setMetaData(metaData);

        //double feature value
        doubleFeatureValue = new DistanceBasedFeatureDataElement();
        doubleFeatureValue.setName("Temperature");
        doubleFeatureValue.setValue(10); //or set a double[], handled by the same class

        //symbolic feature value
        symbolicFeatureDataElement = new SymbolicFeatureDataElement();
        symbolicFeatureDataElement.setName("DayOfWeek");
        symbolicFeatureDataElement.setValue("Monday");

        instanceDataElement.addValue(doubleFeatureValue);
        instanceDataElement.addValue(symbolicFeatureDataElement);

        instances.add(instanceDataElement);

        datasetDataElement.setValue(instances);


        //create higher level dataset
        DatasetDataElement datasetDataElement2 = new DatasetDataElement();
        metaData = new DSimpleStringMetaData();
        metaData.setBasicInformation("Dataset metadata");
        datasetDataElement.setName("Dataset Layer 0");   //not really needed... didn't exist before
        datasetDataElement.setMetaData(metaData);

        //create instances
        List<InstanceDataElement> instances2 = new ArrayList<InstanceDataElement>();

        //create instance that has a double feature value and a reference to an instance (activation pattern) from the lower level data set
        instanceDataElement = new InstanceDataElement();
        metaData = new DSimpleStringMetaData();
        metaData.setBasicInformation("Super high level metadata");
        instanceDataElement.setMetaData(metaData);

        //add double feature value
        doubleFeatureValue = new DistanceBasedFeatureDataElement();
        doubleFeatureValue.setName("Temperature");
        doubleFeatureValue.setValue(10); //or set a double[], handled by the same class
        instanceDataElement.addValue(doubleFeatureValue);

        //add reference to other instance (activation pattern)
        InstanceReferenceDataElement instanceRefDataElement = new InstanceReferenceDataElement();
        instanceRefDataElement.setReferenceInstanceDataElement(instances.get(0));
        instanceDataElement.addValue(instanceRefDataElement);

        instances2.add(instanceDataElement);

        datasetDataElement2.setValue(instances2);


        //now create link between the two datasets
        datasetDataElement.setNextDataSet(datasetDataElement2);


    }

}
