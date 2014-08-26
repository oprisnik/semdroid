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

/**
 * Result entry. Contains the raw double array as well as the linked object.
 */
public class ResultEntry {

    private Object mObject;
    private double[] mRawData;

    public ResultEntry(Object object, double[] rawData) {
        super();
        mObject = object;
        mRawData = rawData;
    }

    public Object getLinkedObject() {
        return mObject;
    }

    public void setLinkedObject(Object object) {
        mObject = object;
    }

    public double[] getRawData() {
        return mRawData;
    }

    public void setRawData(double[] rawData) {
        mRawData = rawData;
    }

}
