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

package com.oprisnik.semdroid.plugin.general;

import com.oprisnik.semdroid.analysis.BaseAnalysisPlugin;
import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.DexClass;
import com.oprisnik.semdroid.app.DexMethod;
import com.oprisnik.semdroid.app.MethodCall;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.filter.ClassFilter;
import com.oprisnik.semdroid.filter.DefaultClassFilter;
import com.oprisnik.semdroid.filter.DefaultMethodFilter;
import com.oprisnik.semdroid.filter.MethodFilter;
import com.oprisnik.semdroid.utils.StringList;

import java.util.List;

/**
 * API call detector. You can specify API calls that should be detected.
 *
 * <p/>
 * This example extends the BaseAnalysisPlugin.
 *
 * XML usage:
 *
 * <analysis-plugin class="com.oprisnik.semdroid.plugin.general.MethodCallDetector">
 *     <name>API call detector</name>
 *
 *     <class-filter class="com.oprisnik.semdroid.filter.BroadcastReceiverClassFilter" />
 *     <method-filter class="com.oprisnik.semdroid.filter.OnReceiveMethodFilter" />
 *
 *     <api-call-list>
 *         <list>
 *             <string></string>
 *         </list>
 *     </api-call-list>
 *
 *     <method-call-contains>
 *         <list>
 *             <string>crypto</string>
 *             <string>sms</string>
 *         </list>
 *     </method-call-contains>
 *
 *     <method-call-inclusion-depth>2</method-call-inclusion-depth>
 * </analysis-plugin>
 *
 * @see com.oprisnik.semdroid.utils.StringList
 */
public class MethodCallDetector extends BaseAnalysisPlugin {

    public static final String KEY_API_CALL_LIST = "api-call-list";
    public static final String KEY_METHOD_CALL_CONTAINS = "method-call-contains";
    public static final String KEY_CLASS_FILTER = "class-filter";
    public static final String KEY_METHOD_FILTER = "method-filter";
    public static final String KEY_METHOD_CALL_INCLUSION_DEPTH = "method-call-inclusion-depth";

    private ClassFilter mClassFilter;
    private MethodFilter mMethodFilter;

    private StringList mCallList;
    private StringList mContainsList;

    private int mMethodCallInclusionDepth = 0;

    @Override
    public void init(Config config) throws BadConfigException {
        super.init(config);
        mCallList = new StringList();
        mContainsList = new StringList();
        if (config != null) {
            mCallList.init(config.getSubconfig(KEY_API_CALL_LIST));
            mContainsList.init(config.getSubconfig(KEY_METHOD_CALL_CONTAINS));
            mClassFilter = config.getComponentAndInit(KEY_CLASS_FILTER,
                    ClassFilter.class, DefaultClassFilter.class);
            mMethodFilter = config.getComponentAndInit(KEY_METHOD_FILTER,
                    MethodFilter.class, DefaultMethodFilter.class);
            mMethodCallInclusionDepth = config.getInt(KEY_METHOD_CALL_INCLUSION_DEPTH,
                    mMethodCallInclusionDepth);
        } else {
            mClassFilter = new DefaultClassFilter();
            mMethodFilter = new DefaultMethodFilter();
        }
    }

    @Override
    public void analyze(AppAnalysisReport report, App app) {
        for (DexClass clazz : app.getClasses()) {
            if (mClassFilter.use(clazz)) {
                for (DexMethod method : clazz.getMethods()) {
                    if (mMethodFilter.use(method)) {
                        for (MethodCall mc : method.getMethodCalls(mMethodCallInclusionDepth)) {
                            String mcName = mc.getMethodName();
                            if (mcName == null) {
                                continue;
                            }
                            // check if we match an API call
                            if (mc.isSystemMethod() && mCallList.contains(mcName)) {
                                report.label(method, mc.getMethodName());
                            }
                            // check if the method call contains a given string
                            // we convert the method call name to lower case first
                            List<String> contained = mContainsList.getAllContainedStrings(
                                   mcName.toLowerCase());
                            if (!contained.isEmpty()) {
                                for (String s : contained) {
                                    report.label(method, s);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public StringList getCallList() {
        return mCallList;
    }

    public void setCallList(StringList callList) {
        mCallList = callList;
    }

    public int getMethodCallInclusionDepth() {
        return mMethodCallInclusionDepth;
    }

    public void setMethodCallInclusionDepth(int methodCallInclusionDepth) {
        mMethodCallInclusionDepth = methodCallInclusionDepth;
    }

    public ClassFilter getClassFilter() {
        return mClassFilter;
    }

    public void setClassFilter(ClassFilter classFilter) {
        mClassFilter = classFilter;
    }

    public MethodFilter getMethodFilter() {
        return mMethodFilter;
    }

    public void setMethodFilter(MethodFilter methodFilter) {
        mMethodFilter = methodFilter;
    }
}
