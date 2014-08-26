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

package com.oprisnik.semdroid.app.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Base app parser that manages all progress listeners.
 */
public abstract class BaseAppParser implements AppParser {

    public List<ProgressListener> mProgressListeners;

    public BaseAppParser() {
    }

    @Override
    public void registerProgressListener(ProgressListener listener) {
        if (mProgressListeners == null) {
            mProgressListeners = new ArrayList<ProgressListener>();
        }
        mProgressListeners.add(listener);
    }

    @Override
    public void unregisterProgressListener(ProgressListener listener) {
        if (mProgressListeners != null) {
            mProgressListeners.remove(listener);
        }
    }

    protected void publishProgress(int percentage) {
        if (mProgressListeners != null) {
            for (ProgressListener listener : mProgressListeners) {
                listener.onProgressUpdated(percentage);
            }
        }
    }
}
