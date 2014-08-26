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
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.feature.instance.AppInstanceGenerator;
import com.oprisnik.semdroid.feature.instance.ClassInstanceGenerator;
import com.oprisnik.semdroid.feature.instance.MethodInstanceGenerator;
import com.oprisnik.semdroid.filter.ClassFilter;
import com.oprisnik.semdroid.filter.DefaultClassFilter;
import com.oprisnik.semdroid.filter.DefaultMethodFilter;
import com.oprisnik.semdroid.filter.MethodFilter;
import com.oprisnik.semdroid.grouper.opcode.DefaultOpcodeGrouper;
import com.oprisnik.semdroid.grouper.opcode.OpcodeGrouper;
import com.oprisnik.semdroid.utils.StatisticsCollector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.tuflowgraphy.semantic.base.domain.data.DatasetDataElement;
import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;
import at.tuflowgraphy.semantic.base.domain.data.InstanceReferenceDataElement;
import at.tuflowgraphy.semantic.base.domain.metadata.DSimpleStringMetaData;

/**
 * Basic feature layer generator.
 */
public abstract class BasicFeatureLayerGenerator implements
        FeatureLayerGenerator {

    public static final String KEY_CLASS_FILTER = "class-filter";
    public static final String KEY_METHOD_FILTER = "method-filter";

    public static final String KEY_OPCODE_GROUPER = "opcode-grouper";

    public static final String KEY_APP_INSTANCE_GENERATOR = "app-instance-generator";
    public static final String KEY_CLASS_INSTANCE_GENERATOR = "class-instance-generator";
    public static final String KEY_METHOD_INSTANCE_GENERATOR = "method-instance-generator";


    // special values
    public static final String KEY_NEEDS_LABELABLE_OBJECT_LINKS = "needs-labelable-object-links";
    public static final String KEY_ANALYZE_ONLY_LABELED_COMPONENTS = "analyze-only-labeled-components";


    private StatisticsCollector mStatistics;
    private OpcodeGrouper mOpcodeGrouper;

    private Map<DexMethod, InstanceDataElement> mMethodMap = new HashMap<DexMethod, InstanceDataElement>();
    private Map<DexClass, InstanceDataElement> mClassMap = new HashMap<DexClass, InstanceDataElement>();
    private Map<App, InstanceDataElement> mAppMap = new HashMap<App, InstanceDataElement>();

    private MethodInstanceGenerator mMethodInstanceGenerator;
    private ClassInstanceGenerator mClassInstanceGenerator;
    private AppInstanceGenerator mAppInstanceGenerator;

    private boolean mAddLabels = false;
    private boolean mNeedsLabelableObjectLinks = true;
    private boolean mAddOnlyLabeledData = false;

    private ClassFilter mClassFilter;
    private MethodFilter mMethodFilter;

    @Override
    public void init(Config config)
            throws BadConfigException {

        if (config == null) {
            throw new BadConfigException("Feature layer generator needs a config file!");
        }

        // special values for training
        mNeedsLabelableObjectLinks = config.getBoolean(KEY_NEEDS_LABELABLE_OBJECT_LINKS,
                mNeedsLabelableObjectLinks);
        mAddOnlyLabeledData = config.getBoolean(KEY_ANALYZE_ONLY_LABELED_COMPONENTS, mAddOnlyLabeledData);

        // if the values are not set in the config file, we use the default implementation
        mClassFilter = config.getComponentAndInit(
                KEY_CLASS_FILTER, ClassFilter.class, DefaultClassFilter.class);
        mMethodFilter = config.getComponentAndInit(
                KEY_METHOD_FILTER, MethodFilter.class, DefaultMethodFilter.class);
        mOpcodeGrouper = config.getComponentAndInit(
                KEY_OPCODE_GROUPER, OpcodeGrouper.class, DefaultOpcodeGrouper.class);


        if (needsAppInstanceGenerator()) {
            mAppInstanceGenerator = config.getComponentAndInit(
                    KEY_APP_INSTANCE_GENERATOR, AppInstanceGenerator.class);
            mAppInstanceGenerator.setFeatureLayerGenerator(this);
        }
        if (needsClassInstanceGenerator()) {
            mClassInstanceGenerator = config.getComponentAndInit(
                    KEY_CLASS_INSTANCE_GENERATOR, ClassInstanceGenerator.class);
            mClassInstanceGenerator.setFeatureLayerGenerator(this);
        }
        if (needsMethodInstanceGenerator()) {
            mMethodInstanceGenerator = config.getComponentAndInit(
                    KEY_METHOD_INSTANCE_GENERATOR, MethodInstanceGenerator.class);
            mMethodInstanceGenerator.setFeatureLayerGenerator(this);
        }
    }

    @Override
    public DatasetDataElement generateFeatureLayers(String datasetName,
                                                    List<App> apps) {
        DatasetDataElement appData = new DatasetDataElement();
        DSimpleStringMetaData metaData = new DSimpleStringMetaData();
        metaData.setBasicInformation(datasetName);
        appData.setName(datasetName); // not really needed...
        appData.setMetaData(metaData);

        for (App a : apps) {
            generateFeatureLayers(a, appData);
        }
        return appData;
    }

    @Override
    public DatasetDataElement generateFeatureLayers(App app) {
        DatasetDataElement appData = new DatasetDataElement();
        DSimpleStringMetaData metaData = new DSimpleStringMetaData();
        metaData.setBasicInformation(app.getName() + " " + app.getApkFileName());
        appData.setName(app.getName()); // not really needed...
        appData.setMetaData(metaData);

        generateFeatureLayers(app, appData);

        return appData;
    }

    @Override
    public boolean needsLabelableObjectLinks() {
        return mNeedsLabelableObjectLinks;
    }

    public boolean shouldAddMethod(DexMethod method) {
        if (mAddOnlyLabeledData) {
            if (!method.hasLabels()) {
                return false;
            }
        }
        return mMethodFilter.use(method);
    }

    public boolean shouldAddClass(DexClass clazz) {
        if (mAddOnlyLabeledData) {
            if (!clazz.hasLabels()) {
                return false;
            }
        }
        return mClassFilter.use(clazz);
    }

    @Override
    public ClassFilter getClassFilter() {
        return mClassFilter;
    }

    @Override
    public MethodFilter getMethodFilter() {
        return mMethodFilter;
    }

    @Override
    public void addLabels(boolean addLabels) {
        mAddLabels = addLabels;
    }

    @Override
    public StatisticsCollector getStatistics() {
        return mStatistics;
    }

    @Override
    public void setStatistics(StatisticsCollector stats) {
        mStatistics = stats;
    }

    @Override
    public OpcodeGrouper getOpcodeGrouper() {
        return mOpcodeGrouper;
    }

    @Override
    public void setOpcodeGrouper(OpcodeGrouper grouper) {
        mOpcodeGrouper = grouper;
    }

    public InstanceDataElement getMethodInstance(DexMethod method) {
        InstanceDataElement instanceDataElement = mMethodMap.get(method);
        // add to method
        if (instanceDataElement != null) {
            // already analyzed => link
            return instanceDataElement;
        }
        if (mMethodInstanceGenerator != null) {
            instanceDataElement = mMethodInstanceGenerator.getMethodInstance(
                    method, mAddLabels);
            mMethodMap.put(method, instanceDataElement);
        }
        return instanceDataElement;
    }

    public InstanceDataElement getClassInstance(DexClass clazz) {
        InstanceDataElement instanceDataElement = mClassMap.get(clazz);
        // add to method
        if (instanceDataElement != null) {
            // already analyzed => link
            return instanceDataElement;
        }
        if (mClassInstanceGenerator != null) {
            instanceDataElement = mClassInstanceGenerator.getClassInstance(
                    clazz, mAddLabels);
            mClassMap.put(clazz, instanceDataElement);
        }
        return instanceDataElement;
    }

    public InstanceDataElement getAppInstance(App app) {
        InstanceDataElement instanceDataElement = mAppMap.get(app);
        // add to method
        if (instanceDataElement != null) {
            // already analyzed => link
            return instanceDataElement;
        }
        if (mAppInstanceGenerator != null) {
            instanceDataElement = mAppInstanceGenerator.getAppInstance(app,
                    mAddLabels);
            mAppMap.put(app, instanceDataElement);
        }
        return instanceDataElement;
    }

    @Override
    public InstanceReferenceDataElement getInstanceReferenceDataElement(
            DexClass clazz) {
        InstanceReferenceDataElement ref = new InstanceReferenceDataElement();
        ref.setReferenceInstanceDataElement(getClassInstance(clazz));
        return ref;
    }

    @Override
    public InstanceReferenceDataElement getInstanceReferenceDataElement(
            DexMethod method) {
        InstanceReferenceDataElement ref = new InstanceReferenceDataElement();
        ref.setReferenceInstanceDataElement(getMethodInstance(method));
        return ref;
    }

    @Override
    public InstanceReferenceDataElement getInstanceReferenceDataElement(App app) {
        InstanceReferenceDataElement ref = new InstanceReferenceDataElement();
        ref.setReferenceInstanceDataElement(getAppInstance(app));
        return ref;
    }

    public abstract boolean needsMethodInstanceGenerator();

    public abstract boolean needsClassInstanceGenerator();

    public abstract boolean needsAppInstanceGenerator();
}
