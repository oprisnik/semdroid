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

 */
public class SingleLabelEvaluator implements Evaluator {

    private String mName;
    private EvaluationResults mEvaluationResults;

    private String mNormalLabel;

    public SingleLabelEvaluator(String name) {
        mName = name;
        mEvaluationResults = new EvaluationResults(name);
    }

    public SingleLabelEvaluator(String name, String normalLabel) {
        mName = name;
        mNormalLabel = normalLabel;
        mEvaluationResults = new EvaluationResults(name);
    }


    @Override
    public void evaluate(AppAnalysisReport expectedResults, AppAnalysisReport results) throws EvaluationException {

        Set<Labelable> referenceComponents = new HashSet<Labelable>();
        referenceComponents.addAll(expectedResults.getComponents());

        for (Labelable labelable : results.getComponents()) {
            Set<Label> resultingLabels = results.getLabels(labelable);
            if (resultingLabels.size() != 1) {
                throw new EvaluationException("Multiple labels for " + labelable.getName());
            }
            String resultingLabel = resultingLabels.iterator().next().getName();

            Set<Label> referenceLabels = expectedResults.getLabels(labelable);
            String referenceLabel = null;
            if (referenceLabels == null || referenceLabels.isEmpty()) {
                referenceLabel = mNormalLabel;
            } else if (referenceLabels.size() != 1) {
                throw new EvaluationException("Multiple reference labels for " + labelable.getName());
            } else {
                referenceLabel = referenceLabels.iterator().next().getName();
            }

            mEvaluationResults.increaseTotal(labelable);
            if (resultingLabel.equals(referenceLabel)) {

                mEvaluationResults.increaseCorrect(labelable);

                EvaluationResult label = getResult(resultingLabel);
                label.increaseCorrect(labelable);
                label.increaseTotal(labelable);
            } else {

                mEvaluationResults.increaseWrong(labelable);

                EvaluationResult label = getResult(resultingLabel);
                label.increaseFalsePositives(labelable);
//                label.increaseTotal(labelable);
                label.increaseWrong(labelable);

                label = getResult(referenceLabel);
                label.increaseFalseNegatives(labelable);
                label.increaseTotal(labelable);
                label.increaseWrong(labelable);
            }
            referenceComponents.remove(labelable);
        }

        for (Labelable labelable : referenceComponents) {
            Set<Label> referenceLabels = expectedResults.getLabels(labelable);
            String referenceLabel = null;
            if (referenceLabels == null || referenceLabels.isEmpty()) {
                referenceLabel = mNormalLabel;
            } else if (referenceLabels.size() != 1) {
                throw new EvaluationException("Multiple reference labels for " + labelable.getName());
            } else {
                referenceLabel = referenceLabels.iterator().next().getName();
            }
            if (referenceLabel.equals(mNormalLabel)) {
                continue; // since not analyzed => considered normal
            } else {
                mEvaluationResults.increaseTotal(labelable);
                mEvaluationResults.increaseWrong(labelable);

                EvaluationResult label = getResult(referenceLabel);
                label.increaseWrong(labelable);
                label.increaseTotal(labelable);
                label.increaseFalseNegatives(labelable);
                if (mNormalLabel != null) {
                    label = getResult(mNormalLabel);
                    label.increaseWrong(labelable);
                    label.increaseFalsePositives(labelable);
                }
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
