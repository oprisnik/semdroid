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

package com.oprisnik.semdroid.evaluation.integer;

import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.Label;
import com.oprisnik.semdroid.analysis.results.Labelable;
import com.oprisnik.semdroid.utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluator. Compares two plugin reports.
 */
public class Evaluator {

    private static final String TAG = "Evaluator";

    private String mName;
    private EvaluationResults mEvaluationResults;

    public Evaluator(String name) {
        mName = name;
        mEvaluationResults = new EvaluationResults(name);
    }

    public void evaluate(AppAnalysisReport expectedResults, AppAnalysisReport results) {

        List<Label> uncheckedLabels = new ArrayList<Label>(results.getLabels());
        for (Label shouldBe : expectedResults.getLabels()) {
            Label result = results.getLabel(shouldBe.getName());
            uncheckedLabels.remove(result);
            EvaluationResult current = mEvaluationResults.getResult(shouldBe.getName());
            if (current == null) {
                current = new EvaluationResult(shouldBe.getName());
                mEvaluationResults.addResult(shouldBe.getName(), current);
            }
            if (result == null) {
                Log.w(TAG, "Did not find label " + shouldBe.getName() + ". => " + shouldBe.size() + " wrong entries added.");
                current.increaseFalseNegatives(shouldBe.size());
            } else {
                List<Labelable> unchecked = new ArrayList<Labelable>(result.getObjects());
                for (Labelable obj : shouldBe.getObjects()) {
                    if (unchecked.remove(obj)) {
                        // in list => ok
                        current.increaseCorrect();
                    } else {
                        // not in list => false negative
                        current.increaseFalseNegatives();
                    }
                }
                if (unchecked.size() > 0) {
                    current.increaseFalsePositives(unchecked.size());
                }
            }
        }

        for (Label l : uncheckedLabels) {
            EvaluationResult current = mEvaluationResults.getResult(l.getName());
            if (current == null) {
                current = new EvaluationResult(l.getName());
                mEvaluationResults.addResult(l.getName(), current);
            }
            current.increaseFalsePositives(l.size());
        }
    }

    public EvaluationResults getResults() {
        return mEvaluationResults;
    }
}
