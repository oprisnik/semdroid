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

package com.oprisnik.semdroid.analysis.results;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Label for given labelable objects.
 */
public class Label implements Serializable {

    public static final String NULL = "null";

    private String mName;
    private String mOwner;

    private Set<Labelable> mObjects;

    public Label(String name, String owner) {
        mName = name;
        mOwner = owner;
        mObjects = new HashSet<Labelable>();
    }

    public String getName() {
        return mName;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setName(String name) {
        mName = name;
    }

    public Set<Labelable> getObjects() {
        return mObjects;
    }

    public int size() {
        return mObjects.size();
    }

    public boolean isNullLabel() {
        return NULL.equals(mName);
    }

    @Override
    public String toString() {
        return mName + ". " + mObjects.size() + " objects.";
    }


    /**
     * Add a labelable to the given label.
     * Only package visibility since this method should not be used by analyses to add objects.
     *
     * @param object the object to add
     */
    void addObjectInternal(Labelable object) {
        mObjects.add(object);
    }

}
