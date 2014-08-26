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
 * Evaluator. Compares two plugin reports. The normal label does not have to be present in
 * the evaluation results in order to be recognized correctly.
 */
public class NormalEvaluator implements Evaluator {

    private String mName;
    private EvaluationResults mEvaluationResults;

    private String mNormalLabel;

    public NormalEvaluator(String name) {
        mName = name;
        mEvaluationResults = new EvaluationResults(name);
    }

    public NormalEvaluator(String name, String normalLabel) {
        mName = name;
        mNormalLabel = normalLabel;
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
            boolean addToTotal = true;
            Set<Label> expectedLabels = expectedResults.getLabels(labelable);
            Set<String> resultingLabels = new HashSet<String>();
            Set<Label> allResultingLabels = results.getLabels(labelable);
            if (allResultingLabels != null) {
                for (Label l : allResultingLabels) {
                    resultingLabels.add(l.getName());
                }
            }
            boolean ok = true;
            boolean hadNormal = false;
            boolean hasNoLabels = resultingLabels.size() == 0;
            for (Label shouldBe : expectedLabels) {
                EvaluationResult current = getResult(shouldBe.getName());

                if (resultingLabels.contains(shouldBe.getName())) {
                    current.increaseTotal(labelable);
                    current.increaseCorrect(labelable);
                    resultingLabels.remove(shouldBe.getName());
                } else {

                    // custom mNormalLabel set
                    if (mNormalLabel != null && shouldBe.getName().equals(mNormalLabel)) {
                        hadNormal = true;

                        // we do not care if we do not have a normal label attached

                        if (allResultingLabels == null || allResultingLabels.size() == 0) {
                            // we are correct, but since not analyzed by evaluation analysis we do nothing
                            // and we do not add it to total!
                            if (expectedLabels.size() == 1) {
                                addToTotal = false;
                            }
                        } else {
                            // we are wrong!
                            ok = false;

                            current.increaseTotal(labelable);
                            current.increaseFalseNegatives(labelable);
                            current.increaseWrong(labelable);
                        }
                    } else {

                        ok = false;
                        current.increaseTotal(labelable);
                        current.increaseFalseNegatives(labelable);
                        current.increaseWrong(labelable);
                    }
                }
            }

            if (mNormalLabel != null && hasNoLabels && !hadNormal && expectedLabels.size() >= 1) {
                // we recognized as normal, but is not normal => false positive for normal!
                EvaluationResult normal = getResult(mNormalLabel);
                normal.increaseFalsePositives(labelable);
                normal.increaseWrong(labelable);
            }

            if (resultingLabels.size() > 0) {
                if (mNormalLabel != null) {
                    // custom mNormalLabel set
                    EvaluationResult normal = null;
                    for (String falsePositive : resultingLabels) {
                        EvaluationResult current = getResult(falsePositive);
                        if (falsePositive.equals(mNormalLabel)) {
                            normal = current;
                        } else {
                            ok = false;
                            current.increaseFalsePositives(labelable);
                            current.increaseWrong(labelable);
                        }
                    }
                    if (normal != null) {

                        if (ok) {
                            normal.increaseCorrect(labelable);
                            normal.increaseTotal(labelable);
                        } else {
//                            normal.increaseTotal(labelable);
                            normal.increaseFalsePositives(labelable);
                            normal.increaseWrong(labelable);
                        }
                    }
                } else {
                    ok = false;
                    for (String falsePositive : resultingLabels) {
                        EvaluationResult current = getResult(falsePositive);
                        current.increaseFalsePositives(labelable);
                        current.increaseWrong(labelable);
                    }
                }
            }

            if (addToTotal) {
                mEvaluationResults.increaseTotal(labelable);

                if (ok) {
                    mEvaluationResults.increaseCorrect(labelable);
                } else {
                    mEvaluationResults.increaseWrong(labelable);
                }
            }

            allComponents.remove(labelable);
        }
        if (allComponents.size() > 0) {
            if (mNormalLabel != null) {


                for (Labelable falseLabelable : allComponents) {
                    Set<Label> resultLabels = results.getLabels(falseLabelable);
                    boolean ok = true;
                    boolean addedToNormalLabel = false;
                    for (Label wrong : resultLabels) {
                        EvaluationResult current = getResult(wrong.getName());
                        if (wrong.getName().equals(mNormalLabel)) {
                            if (mNormalLabel != null && resultLabels.size() == 1) {
                                addedToNormalLabel = true;
                                current.increaseTotal(falseLabelable);
                                current.increaseCorrect(falseLabelable);
                            } else {
                                ok = false;

                                current.increaseFalsePositives(falseLabelable);
                                current.increaseWrong(falseLabelable);
                            }
                        } else {
                            ok = false;
                            current.increaseFalsePositives(falseLabelable);
                            current.increaseWrong(falseLabelable);
                            if (mNormalLabel != null && !addedToNormalLabel) {
                                EvaluationResult normal = getResult(mNormalLabel);
                                normal.increaseTotal(falseLabelable);
                                normal.increaseFalseNegatives(falseLabelable);
                                normal.increaseWrong(falseLabelable);
                            }
                        }
                    }

                    mEvaluationResults.increaseTotal(falseLabelable);
                    if (ok) {
                        mEvaluationResults.increaseCorrect(falseLabelable);
                    } else {
                        mEvaluationResults.increaseWrong(falseLabelable);
                    }
                }

            } else {
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
