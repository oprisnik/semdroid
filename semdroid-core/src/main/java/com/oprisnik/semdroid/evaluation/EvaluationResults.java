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

package com.oprisnik.semdroid.evaluation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Evaluation results. For each label, a EvaluationResult is stored.
 */
public class EvaluationResults extends EvaluationResult {

    private Map<String, EvaluationResult> mEntries;

    public EvaluationResults(String name) {
        super(name);
        mEntries = new HashMap<String, EvaluationResult>();
    }

    public void addResult(String name, EvaluationResult result) {
        mEntries.put(name, result);
//        result.attach(this);
    }

    public EvaluationResult getResult(String name) {
        return mEntries.get(name);
    }

    public Collection<EvaluationResult> results() {
        return mEntries.values();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Evaluation results for ");
        sb.append(super.toString());
        sb.append("\n");
        for (EvaluationResult r : mEntries.values()) {
            sb.append(r.toString());
            sb.append('\n');
        }
        return sb.toString();
    }
}
