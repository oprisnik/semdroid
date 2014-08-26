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

package com.oprisnik.semdroid.app.manifest;

import java.io.Serializable;

public class IntentFilter implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String mAction;
    private String mLabel;
    private String mCategory;
    private String mPriority;
    private String mData;

    public IntentFilter() {

    }

    public IntentFilter(String action, String label, String category, String priority, String data) {
        this();
        mAction = action;
        mLabel = label;
        mCategory = category;
        mPriority = priority;
        mData = data;
    }

    public String getAction() {
        return mAction;
    }

    public void setAction(String action) {
        this.mAction = action;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }

    public String getPriority() {
        return mPriority;
    }

    public void setPriority(String string) {
        this.mPriority = string;
    }

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        this.mData = data;
    }
}
