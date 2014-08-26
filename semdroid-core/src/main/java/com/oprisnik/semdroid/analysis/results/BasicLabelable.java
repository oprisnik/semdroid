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

import java.util.HashSet;
import java.util.Set;

/**
 * Basic labelable implementation that manages a list of labels
 */
public abstract class BasicLabelable implements Labelable {

    private Set<Label> mLabels = new HashSet<Label>();

    @Override
    public void addLabel(Label label) {
        mLabels.add(label);
        label.addObjectInternal(this);
    }

    @Override
    public void removeLabel(Label label) {
        mLabels.remove(label);
    }

    @Override
    public Set<Label> getLabels() {
        return mLabels;
    }

    /**
     * Checks whether the app component has valid labels (which are not "null").
     *
     * @return true if the component has valid labels.
     */
    public boolean hasLabels() {
        if (mLabels.size() > 0) {
            for (Label label : mLabels) {
                String s = label.getName();
                if (s != null && !s.equalsIgnoreCase(Label.NULL))
                    return true;
            }
        }
        return false;
    }

}
