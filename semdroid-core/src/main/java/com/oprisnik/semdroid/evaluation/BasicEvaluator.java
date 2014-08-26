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

import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.Label;
import com.oprisnik.semdroid.analysis.results.Labelable;

import java.util.HashSet;
import java.util.Set;

/**
 * Evaluator. Compares two plugin reports.
 */
public class BasicEvaluator implements Evaluator {


    private static final String TAG = "BasicEvaluator";

    private String mName;
    private EvaluationResults mEvaluationResults;

    public BasicEvaluator(String name) {
        mName = name;
        mEvaluationResults = new EvaluationResults(name);
    }

    @Override
    public void evaluate(AppAnalysisReport expectedResults, AppAnalysisReport results) throws EvaluationException {

        if (!expectedResults.getApp().equals(results.getApp())) {
            throw new EvaluationException("Apps not equal!");
        }
        Set<Labelable> allComponents = new HashSet<Labelable>();
        allComponents.addAll(results.getComponents());

        for (Labelable labelable : expectedResults.getComponents()) {
            mEvaluationResults.increaseTotal(labelable);
            Set<Label> expectedLabels = expectedResults.getLabels(labelable);
            Set<String> resultingLabels = new HashSet<String>();
            for (Label l : results.getLabels(labelable)) {
                resultingLabels.add(l.getName());
            }
            boolean ok = true;
            for (Label shouldBe : expectedLabels) {
                EvaluationResult current = getResult(shouldBe.getName());
                current.increaseTotal(labelable);
                if (resultingLabels.contains(shouldBe.getName())) {
                    current.increaseCorrect(labelable);
                    resultingLabels.remove(shouldBe.getName());
                } else {
                    ok = false;
                    current.increaseFalseNegatives(labelable);
                    current.increaseWrong(labelable);
                }
            }
            if (resultingLabels.size() > 0) {
                ok = false;
                for (String falsePositive : resultingLabels) {
                    EvaluationResult current = getResult(falsePositive);
                    current.increaseFalsePositives(labelable);
                    current.increaseWrong(labelable);
                }
            }
            if (ok) {
                mEvaluationResults.increaseCorrect(labelable);
            } else {
                mEvaluationResults.increaseWrong(labelable);
            }

            allComponents.remove(labelable);
        }
        if (allComponents.size() > 0) {
            for (Labelable falseLabelable : allComponents) {
                for (Label wrong : results.getLabels(falseLabelable)) {
                    EvaluationResult current = getResult(wrong.getName());
                    current.increaseFalsePositives(falseLabelable);
                    current.increaseWrong(falseLabelable);
                }
                mEvaluationResults.increaseWrong(falseLabelable);
            }
        }

    }

    @Override
    public EvaluationResults getResults() {
        return mEvaluationResults;
    }

    protected EvaluationResult getResult(String labelName) {
        EvaluationResult current = mEvaluationResults.getResult(labelName);
        if (current == null) {
            current = new EvaluationResult(labelName);
            mEvaluationResults.addResult(labelName, current);
        }
        return current;
    }
}
