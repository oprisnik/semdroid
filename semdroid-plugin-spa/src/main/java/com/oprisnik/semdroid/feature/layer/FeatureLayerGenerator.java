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

package com.oprisnik.semdroid.feature.layer;

import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.config.Configurable;
import com.oprisnik.semdroid.filter.ClassFilter;
import com.oprisnik.semdroid.filter.MethodFilter;
import com.oprisnik.semdroid.grouper.opcode.OpcodeGrouper;
import com.oprisnik.semdroid.utils.StatisticsCollector;

import java.util.List;

import at.tuflowgraphy.semantic.base.domain.data.DatasetDataElement;
import at.tuflowgraphy.semantic.base.domain.data.InstanceReferenceDataElement;

/**
 * Feature layer generator. This feature vector layers will be used by the
 * Semantic Patterns framework.
 */
public interface FeatureLayerGenerator extends Configurable {

    public DatasetDataElement generateFeatureLayers(String datasetName,
                                                    List<App> apps);

    public void generateFeatureLayers(App app, DatasetDataElement results);

    public DatasetDataElement generateFeatureLayers(App app);

    public void addLabels(boolean addLabels);

    public boolean needsLabelableObjectLinks();

    public StatisticsCollector getStatistics();

    public void setStatistics(StatisticsCollector stats);

    public OpcodeGrouper getOpcodeGrouper();

    public void setOpcodeGrouper(OpcodeGrouper grouper);

    public InstanceReferenceDataElement getInstanceReferenceDataElement(
            DexMethod method);

    public InstanceReferenceDataElement getInstanceReferenceDataElement(
            DexClass clazz);

    public InstanceReferenceDataElement getInstanceReferenceDataElement(App app);

    public ClassFilter getClassFilter();

    public MethodFilter getMethodFilter();

}
