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

package com.oprisnik.semdroid.analysis.results.lite;

import com.oprisnik.semdroid.analysis.results.Label;
import com.oprisnik.semdroid.analysis.results.Labelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight labelable
 */
public class LiteLabel implements Serializable {

    private String mName;

    private List<LiteLabelable> mObjects;

    public LiteLabel() {
    }

    public LiteLabel(Label original) {
        mName = original.getName();
        mObjects = new ArrayList<LiteLabelable>();
        for (Labelable orig : original.getObjects()) {
            mObjects.add(new LiteLabelable(orig));
        }
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<LiteLabelable> getObjects() {
        return mObjects;
    }

    public void setObjects(List<LiteLabelable> objects) {
        mObjects = objects;
    }

    public int size() {
        return mObjects.size();
    }

    public LiteLabelable get(int index) {
        return mObjects.get(index);
    }

}
