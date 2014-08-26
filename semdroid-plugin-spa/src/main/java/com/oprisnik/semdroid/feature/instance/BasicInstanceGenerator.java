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

package com.oprisnik.semdroid.feature.instance;

import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.app.LocalVariable;
import com.oprisnik.semdroid.app.MethodCall;
import com.oprisnik.semdroid.app.Opcode;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.config.Configurable;
import com.oprisnik.semdroid.feature.layer.FeatureLayerGenerator;
import com.oprisnik.semdroid.feature.value.FeatureValueGenerator;
import com.oprisnik.semdroid.feature.value.LocalVarFeatureValueGenerator;
import com.oprisnik.semdroid.feature.value.LocalVarTypeFVG;
import com.oprisnik.semdroid.feature.value.MethodCallFeatureValueGenerator;
import com.oprisnik.semdroid.feature.value.MethodCallNameFVG;
import com.oprisnik.semdroid.filter.ClassFilter;
import com.oprisnik.semdroid.filter.DefaultLocalVarFilter;
import com.oprisnik.semdroid.filter.DefaultMethodCallFilter;
import com.oprisnik.semdroid.filter.Filter;
import com.oprisnik.semdroid.filter.LocalVarFilter;
import com.oprisnik.semdroid.filter.MethodCallFilter;
import com.oprisnik.semdroid.filter.MethodFilter;
import com.oprisnik.semdroid.grouper.opcode.OpcodeGrouper;
import com.oprisnik.semdroid.utils.DexUtils;
import com.oprisnik.semdroid.utils.HistogramHelper;
import com.oprisnik.semdroid.utils.SparseHistogram;
import com.oprisnik.semdroid.utils.SparseIntHistogram;
import com.oprisnik.semdroid.utils.StatisticsCollector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.tuflowgraphy.semantic.base.domain.data.DistanceBasedFeatureDataElement;
import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;
import at.tuflowgraphy.semantic.base.domain.data.InstanceReferenceDataElement;
import at.tuflowgraphy.semantic.base.domain.data.SymbolicFeatureDataElement;
import at.tuflowgraphy.semantic.base.domain.metadata.DSimpleStringMetaData;
import at.tuflowgraphy.semanticapps.semdroid.DalvikInputPlugin;

/**
 * Basic instance generator.
 */
public abstract class BasicInstanceGenerator implements Configurable {

    public static final String KEY_METHOD_CALL_INCLUSION_DEPTH = "method-call-inclusion-depth";

    public static final String KEY_METHOD_CALL_FILTER = "method-call-filter";
    public static final String KEY_METHOD_CALL_VALUE_GENERATOR = "method-call-value-generator";

    public static final String KEY_LOCAL_VAR_FILTER = "local-var-filter";
    public static final String KEY_LOCAL_VAR_VALUE_GENERATOR = "local-var-value-generator";

    private FeatureLayerGenerator mParent;

    private MethodCallFilter mMethodCallFilter;
    private LocalVarFilter mLocalVarFilter;

    private MethodCallFeatureValueGenerator mMethodCallFeatureValueGenerator;
    private LocalVarFeatureValueGenerator mLocalVarFeatureValueGenerator;

    private int mMethodCallInclusionDepth = 0;

    @Override
    public void init(Config config) throws BadConfigException {

        if (config != null) {

            mMethodCallInclusionDepth = config.getInt(
                    KEY_METHOD_CALL_INCLUSION_DEPTH, mMethodCallInclusionDepth);

            mMethodCallFilter = config.getComponentAndInit(
                    KEY_METHOD_CALL_FILTER, MethodCallFilter.class, null);
            mMethodCallFeatureValueGenerator = config.getComponentAndInit(
                    KEY_METHOD_CALL_VALUE_GENERATOR, MethodCallFeatureValueGenerator.class, null);

            mLocalVarFilter = config.getComponentAndInit(
                    KEY_LOCAL_VAR_FILTER, LocalVarFilter.class, null);
            mLocalVarFeatureValueGenerator = config.getComponentAndInit(
                    KEY_LOCAL_VAR_VALUE_GENERATOR, LocalVarFeatureValueGenerator.class, null);
        }
        if (mMethodCallFilter == null) {
            mMethodCallFilter = new DefaultMethodCallFilter();
        }
        if (mMethodCallFeatureValueGenerator == null) {
            mMethodCallFeatureValueGenerator = new MethodCallNameFVG();
        }
        if (mLocalVarFilter == null) {
            mLocalVarFilter = new DefaultLocalVarFilter();
        }
        if (mLocalVarFeatureValueGenerator == null) {
            mLocalVarFeatureValueGenerator = new LocalVarTypeFVG();
        }
    }

    public FeatureLayerGenerator getFeatureLayerGenerator() {
        return mParent;
    }

    public void setFeatureLayerGenerator(FeatureLayerGenerator generator) {
        mParent = generator;
    }

    public StatisticsCollector getStatistics() {
        return mParent.getStatistics();
    }

    public boolean hasStatistics() {
        return mParent.getStatistics() != null;
    }

    public OpcodeGrouper getOpcodeGrouper() {
        return mParent.getOpcodeGrouper();
    }

    public String getOpcodeGroupName(int opcode) {
        return getOpcodeGrouper().getOpcodeGroupName(opcode);
    }

    public String getOpcodeGroupName(Opcode opcode) {
        return getOpcodeGrouper().getOpcodeGroupName(opcode);
    }

    public int getOpcodeGroup(int opcode) {
        return getOpcodeGrouper().getOpcodeGroup(opcode);
    }

    public int getOpcodeGroup(Opcode opcode) {
        return getOpcodeGrouper().getOpcodeGroup(opcode);
    }

    public SymbolicFeatureDataElement getSymbolicFeatureDataElement(
            String name, String value) {
        SymbolicFeatureDataElement symbolicFeatureDataElement = new SymbolicFeatureDataElement();
        symbolicFeatureDataElement.setName(name);
        symbolicFeatureDataElement.setValue(value);
        return symbolicFeatureDataElement;
    }

    public DistanceBasedFeatureDataElement getDistanceBasedFeatureDataElement(
            String name, double value) {
        DistanceBasedFeatureDataElement distanceBasedFeatureDataElement = new DistanceBasedFeatureDataElement();
        distanceBasedFeatureDataElement.setName(name);
        distanceBasedFeatureDataElement.setValue(value);
        return distanceBasedFeatureDataElement;
    }

    public DistanceBasedFeatureDataElement getDistanceBasedFeatureDataElement(
            String name, double[] values) {
        DistanceBasedFeatureDataElement distanceBasedFeatureDataElement = new DistanceBasedFeatureDataElement();
        distanceBasedFeatureDataElement.setName(name);
        distanceBasedFeatureDataElement.setValue(values);
        return distanceBasedFeatureDataElement;
    }

    public SparseIntHistogram getOpcodeGroupHistogram(SparseIntHistogram opcodeHistogram) {
        SparseIntHistogram h = new SparseIntHistogram();
        for (Integer key : opcodeHistogram.getKeySet()) {
            int grp = getOpcodeGroup(key);
            if (grp != OpcodeGrouper.OP_IGNORE) {
                h.increase(grp, opcodeHistogram.getValue(key));
            }
        }
        return h;
    }

    public SparseIntHistogram getOpcodeGroupHistogram(List<Opcode> opcodes) {
        SparseIntHistogram h = new SparseIntHistogram();
        for (Opcode opcode : opcodes) {
            int grp = getOpcodeGroup(opcode.getInt());
            if (grp != OpcodeGrouper.OP_IGNORE) {
                h.increase(grp);
            }
        }
        return h;
    }

    public double[] getNormedOpcodeArray(
            SparseIntHistogram opcodeGroupHistogram) {
        OpcodeGrouper g = getOpcodeGrouper();
        return HistogramHelper.getDoubleHistogram(g.getPossibleOpcodeGroups(), opcodeGroupHistogram, true);
    }

    public double[] getDoubleOpcodeArray(SparseIntHistogram opcodeGroupHistogram) {
        OpcodeGrouper g = getOpcodeGrouper();
        return HistogramHelper.getDoubleHistogram(g.getPossibleOpcodeGroups(), opcodeGroupHistogram, false);
    }

    public InstanceReferenceDataElement getInstanceReferenceDataElement(
            DexMethod method) {
        return mParent.getInstanceReferenceDataElement(method);
    }

    public InstanceReferenceDataElement getInstanceReferenceDataElement(
            DexClass clazz) {
        return mParent.getInstanceReferenceDataElement(clazz);
    }

    public InstanceReferenceDataElement getInstanceReferenceDataElement(App app) {
        return mParent.getInstanceReferenceDataElement(app);
    }

    public boolean needsLabelableObjectLinks() {
        return mParent.needsLabelableObjectLinks();
    }

    public ClassFilter getClassFilter() {
        return mParent.getClassFilter();
    }

    public MethodFilter getMethodFilter() {
        return mParent.getMethodFilter();
    }

    public MethodCallFilter getMethodCallFilter() {
        return mMethodCallFilter;
    }

    public LocalVarFilter getLocalVarFilter() {
        return mLocalVarFilter;
    }

    public int getMethodCallInclusionDepth() {
        return mMethodCallInclusionDepth;
    }

    public MethodCallFeatureValueGenerator getMethodCallFeatureValueGenerator() {
        return mMethodCallFeatureValueGenerator;
    }

    public LocalVarFeatureValueGenerator getLocalVarFeatureValueGenerator() {
        return mLocalVarFeatureValueGenerator;
    }

    public double[] getOpcodeHistogram(SparseIntHistogram opcodeHistogram, boolean normalized) {
        // apply grouping and get double array
        SparseIntHistogram groupHisto = getOpcodeGroupHistogram(opcodeHistogram);
        if (normalized) {
            return getNormedOpcodeArray(groupHisto);
        } else {
            return getDoubleOpcodeArray(groupHisto);
        }
    }

    public double[] getOpcodeHistogram(List<Opcode> opcodes, boolean normalized) {
        // apply grouping and get double array
        SparseIntHistogram groupHisto = getOpcodeGroupHistogram(opcodes);
        if (normalized) {
            return getNormedOpcodeArray(groupHisto);
        } else {
            return getDoubleOpcodeArray(groupHisto);
        }
    }

    public double[] getBasicLocalVarHistogram(List<LocalVariable> localVariables, boolean normalize) {
        SparseHistogram<String> localVarHisto = new SparseHistogram<String>();

        for (LocalVariable v : localVariables) {
            if (v.isBasicType()) {
                localVarHisto.increase(v.getTypeWithoutArray());
            }
        }
        return localVarHisto.getHistogram(DexUtils.BASIC_DEX_TYPE_DESCRIPTORS, normalize);
    }

    public double[] getBasicLocalVarHistogramAndOther(List<LocalVariable> localVariables, boolean normalize) {
        SparseHistogram<String> localVarHisto = new SparseHistogram<String>();

        for (LocalVariable v : localVariables) {
            if (v.isBasicType()) {
                localVarHisto.increase(v.getTypeWithoutArray());
            } else {
                localVarHisto.increase(DexUtils.DEX_TYPE_COMPOSITE);
            }
        }
        return localVarHisto.getHistogram(DexUtils.BASIC_DEX_TYPE_DESCRIPTORS_AND_OTHERS, normalize);
    }

    public void addOpcodes(String featureName, List<Opcode> opcodes, InstanceDataElement results) {
        Set<String> set = new HashSet<String>();
        for (Opcode data : opcodes) {
            if (getOpcodeGroup(data) != OpcodeGrouper.OP_IGNORE) {
                String name = getOpcodeGroupName(data);
                set.add(name);
            }
        }
        addSet(set, featureName, results);
    }

    protected void addSet(Set<String> set, String featureName, InstanceDataElement results) {
        for (String value : set) {
            results.addValue(getSymbolicFeatureDataElement(featureName, value));
//            Log.v(TAG, "added: " + featureName +" = " + value);
        }
    }

    protected <T> void addAsSet(String featureName, Collection<T> collection, Filter<T> filter, FeatureValueGenerator<T> generator, InstanceDataElement results) {
        Set<String> set = new HashSet<String>();
        for (T data : collection) {
            if (filter.use(data)) {
                String value = generator.getFeatureValue(data);
                set.add(value);
            }
        }
        addSet(set, featureName, results);
    }


    protected List<Opcode> getAllOpcodes(App app) {
        List<Opcode> data = new ArrayList<Opcode>();
        for (DexClass clazz : app.getClasses()) {
            if (getClassFilter().use(clazz)) {
                for (DexMethod method : clazz.getMethods()) {
                    if (getMethodFilter().use(method)) {
                        data.addAll(method.getOpcodes(getMethodCallInclusionDepth()));
                    }
                }
            }
        }
        return data;
    }

    protected List<MethodCall> getAllMethodCalls(App app) {
        List<MethodCall> data = new ArrayList<MethodCall>();
        for (DexClass clazz : app.getClasses()) {
            if (getClassFilter().use(clazz)) {
                for (DexMethod method : clazz.getMethods()) {
                    if (getMethodFilter().use(method)) {
                        data.addAll(method.getMethodCalls(getMethodCallInclusionDepth()));
                    }
                }
            }
        }
        return data;
    }

    protected List<LocalVariable> getAllLocalVars(App app) {
        List<LocalVariable> data = new ArrayList<LocalVariable>();
        for (DexClass clazz : app.getClasses()) {
            if (getClassFilter().use(clazz)) {
                for (DexMethod method : clazz.getMethods()) {
                    if (getMethodFilter().use(method)) {
                        data.addAll(method.getLocalVariables(getMethodCallInclusionDepth()));
                    }
                }
            }
        }
        return data;
    }

    protected List<Opcode> getAllOpcodes(DexClass clazz) {
        List<Opcode> data = new ArrayList<Opcode>();

        for (DexMethod method : clazz.getMethods()) {
            if (getMethodFilter().use(method)) {
                data.addAll(method.getOpcodes(getMethodCallInclusionDepth()));
            }
        }
        return data;
    }

    protected List<MethodCall> getAllMethodCalls(DexClass clazz) {
        List<MethodCall> data = new ArrayList<MethodCall>();
        for (DexMethod method : clazz.getMethods()) {
            if (getMethodFilter().use(method)) {
                data.addAll(method.getMethodCalls(getMethodCallInclusionDepth()));
            }
        }

        return data;
    }

    protected List<LocalVariable> getAllLocalVars(DexClass clazz) {
        List<LocalVariable> data = new ArrayList<LocalVariable>();
        for (DexMethod method : clazz.getMethods()) {
            if (getMethodFilter().use(method)) {
                data.addAll(method.getLocalVariables(getMethodCallInclusionDepth()));
            }
        }
        return data;
    }

    protected void addLabel(InstanceDataElement instanceDataElement, String label) {
        DSimpleStringMetaData metadata = (DSimpleStringMetaData) instanceDataElement.getMetaData();
        metadata.addMetaDataEntry(DalvikInputPlugin.TAG_LABEL, label);
//		instanceDataElement.addValue(getSymbolicFeatureDataElement(
//				DalvikInputPlugin.TAG_LABEL, label));
    }

}
