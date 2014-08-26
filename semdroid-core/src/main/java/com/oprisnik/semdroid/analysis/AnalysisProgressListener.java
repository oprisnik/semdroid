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

package com.oprisnik.semdroid.analysis;

/**
 * Analysis progress listener.
 * Listeners can be registered with the Semdroid framework by calling
 * {@link com.oprisnik.semdroid.SemdroidAnalyzer#registerProgressListener(AnalysisProgressListener)}.
 * <p/>
 * Once registered, you will receive progress updates for the current analysis.
 * <p/>
 * To remove a listener, call
 * {@link com.oprisnik.semdroid.SemdroidAnalyzer#unregisterProgressListener(AnalysisProgressListener)}.
 */
public interface AnalysisProgressListener {

    public static final int STATUS_APP_PARSING = 0;
    public static final int STATUS_ANALYZING = 1;
    public static final int STATUS_DONE = 2;

    /**
     * Analysis progress update.
     *
     * @param status              the current status of the analysis process, e.g.{@link #STATUS_APP_PARSING},
     *                            {@link #STATUS_ANALYZING} or {@link #STATUS_DONE}
     * @param percentageCompleted the current percentage
     */
    public void onStatusUpdated(int status, int percentageCompleted);
}
