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

package com.oprisnik.semdroid.plugins;

import android.content.Context;

import com.oprisnik.semdroid.analysis.AppAnalysisPlugin;

/**
 * Plugin card entry. For each plugin, a card is displayed in the results.
 * The contents of these cards can be customized with this class.
 * <p/>
 * The {@link com.oprisnik.semdroid.plugins.PluginCardEntry.Builder} can be used to create
 * new plugin card entries.
 */
public class PluginCardEntry {

    // always update this number!
    public static final int NUMBER_OF_TYPES = 6;

    public static final int TYPE_LIST = 0;
    public static final int TYPE_LIST_AND_MORE = 1;
    public static final int TYPE_SIMPLE_TEXT = 2;
    public static final int TYPE_SIMPLE_COUNT = 3;
    public static final int TYPE_COUNT = 4;
    public static final int TYPE_YES_NO = 5;


    private int mNameResId;
    private String mConfig;
    private boolean mIsClassOnly;
    private Class<? extends AppAnalysisPlugin> mClass;
    private String mName;
    private int mType;

    private String mTargetLabel;
    private int mUnitTypeResId;
    private boolean mHasUnit;

    private PluginCardEntry() {
    }

    public String getName(Context context) {
        if (mName == null) {
            return context.getString(mNameResId);
        }
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getConfig() {
        return mConfig;
    }

    public boolean isClassOnly() {
        return mIsClassOnly;
    }

    public Class<? extends AppAnalysisPlugin> getPluginClass() {
        return mClass;
    }

    public int getType() {
        return mType;
    }

    public String getTargetLabel() {
        return mTargetLabel;
    }

    public int getUnitTypeResId() {
        return mUnitTypeResId;
    }

    public boolean hasUnit() {
        return mHasUnit;
    }

    /**
     * Builder for plugin card entries.
     *
     * @see com.oprisnik.semdroid.plugins.PluginCardEntry
     */
    public static class Builder {
        private int mNameResId;
        private String mConfig;
        private boolean mIsClassOnly;
        private Class<? extends AppAnalysisPlugin> mClass;
        private String mName;
        private int mType = -1;
        private int mUnitTypeResId;
        private boolean mHasUnit = false;

        private String mTargetLabel;

        public Builder() {
        }

        /**
         * Set the name resource ID for the given plugin.
         *
         * @param res the resource ID
         * @return the builder
         */
        public Builder setNameResId(int res) {
            mNameResId = res;
            return this;
        }

        /**
         * Set the name of the given plugin.
         *
         * @param name the plugin name
         * @return the builder
         */
        public Builder setName(String name) {
            mName = name;
            return this;
        }

        /**
         * Set the config path for the given plugin.
         * You can either specify the config path here or set the class directly with
         * {@link #setClass(Class)}.
         *
         * @param configPath the config path
         * @return the builder
         * @see #setClass(Class)
         */
        public Builder setConfig(String configPath) {
            mConfig = configPath;
            mIsClassOnly = false;
            return this;
        }

        /**
         * Set the plugin class.
         * You can either set the class directly or specify a config path for the plugin with
         * {@link #setConfig(String)}.
         *
         * @param clazz the plugin class
         * @return the builder
         * @see #setConfig(String)
         */
        public Builder setClass(Class<? extends AppAnalysisPlugin> clazz) {
            mClass = clazz;
            mIsClassOnly = true;
            return this;
        }

        /**
         * Set the card type to "List".
         *
         * @return the builder
         */
        public Builder setTypeList() {
            mType = TYPE_LIST;
            return this;
        }

        /**
         * Set the card type to "Simple Text".
         *
         * @return the builder
         */
        public Builder setTypeSimpleText() {
            mType = TYPE_SIMPLE_TEXT;
            return this;
        }

        /**
         * Set the card type to "Simple Count".
         *
         * @return the builder
         */
        public Builder setTypeSimpleCount() {
            mType = TYPE_SIMPLE_COUNT;
            return this;
        }

        /**
         * Set the type to "Count".
         * The number of components labeled according to the supplied targetLabel is displayed.
         *
         * @param targetLabel the label to use
         * @return the builder
         */
        public Builder setTypeCount(String targetLabel) {
            mTargetLabel = targetLabel;
            mType = TYPE_COUNT;
            return this;
        }

        /**
         * Set the type to "Yes or No".
         * If at least one component has the given targetLabel, "Yes" will be displayed.
         * If no component has the given label, "No" will be displayed.
         *
         * @param targetLabel the label to use
         * @return the builder
         */
        public Builder setTypeYesNo(String targetLabel) {
            mTargetLabel = targetLabel;
            mType = TYPE_YES_NO;
            return this;
        }

        /**
         * Set the unit type (e.g., "methods" or "classes").
         *
         * @param stringRes the type resource ID
         * @return the builder
         */
        public Builder setUnitTypeResId(int stringRes) {
            mUnitTypeResId = stringRes;
            mHasUnit = true;
            return this;
        }

        /**
         * Build the plugin card entry.
         *
         * @return the final PluginCardEntry.
         * @see com.oprisnik.semdroid.plugins.PluginCardEntry
         */
        public PluginCardEntry build() {
            if (mType == -1) {
                throw new RuntimeException("Plugin type not set!");
            }
            PluginCardEntry entry = new PluginCardEntry();
            entry.mNameResId = mNameResId;
            entry.mConfig = mConfig;
            entry.mIsClassOnly = mIsClassOnly;
            entry.mClass = mClass;
            entry.mName = mName;
            entry.mType = mType;
            entry.mTargetLabel = mTargetLabel;
            entry.mUnitTypeResId = mUnitTypeResId;
            entry.mHasUnit = mHasUnit;
            return entry;
        }


    }
}
