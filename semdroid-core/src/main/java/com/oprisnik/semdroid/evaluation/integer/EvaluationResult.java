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

import java.util.HashSet;
import java.util.Set;

/**
 * Evaluation results.
 */
public class EvaluationResult {

    private String mName;

    private int mTotal = 0;
    private int mFalsePositives = 0;
    private int mFalseNegatives = 0;
    private int mCorrect = 0;
    private int mWrong = 0;

    private Set<EvaluationResult> mParents;

    public EvaluationResult(String name) {
        mName = name;
        mParents = new HashSet<EvaluationResult>();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void increaseCorrect() {
        increaseCorrect(1);
    }

    public void increaseCorrect(int amount) {
        mCorrect += amount;
        mTotal += amount;
        if (mParents.size() > 0) {
            for (EvaluationResult r : mParents) {
                r.increaseCorrect(amount);
            }
        }
    }

    public void increaseFalsePositives() {
        increaseFalsePositives(1);
    }

    public void increaseFalsePositives(int amount) {
        mFalsePositives += amount;
//        mTotal += amount;
        mWrong += amount;
        if (mParents.size() > 0) {
            for (EvaluationResult r : mParents) {
                r.increaseFalsePositives(amount);
            }
        }
    }

    public void increaseFalseNegatives() {
        increaseFalseNegatives(1);
    }

    public void increaseFalseNegatives(int amount) {
        mFalseNegatives += amount;
        mTotal += amount;
        mWrong += amount;
        if (mParents.size() > 0) {
            for (EvaluationResult r : mParents) {
                r.increaseFalseNegatives(amount);
            }
        }
    }

    public int getTotal() {
        return mTotal;
    }

    public int getFalsePositives() {
        return mFalsePositives;
    }

    public int getFalseNegatives() {
        return mFalseNegatives;
    }

    public int getCorrect() {
        return mCorrect;
    }

    public int getWrong() {
        return mWrong;
    }

    public int getPercentCorrect() {
        return Math.round(getPercentCorrectFloat() * 100f);
    }

    public float getPercentCorrectFloat() {
        return (float) mCorrect / (float) mTotal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: " + mName);
        sb.append("\nTotal: " + mTotal);
        sb.append("\nCorrect: " + mCorrect);
        sb.append(" => " + getPercentCorrect() + "%");
        sb.append("\nWrong: " + mWrong);
        sb.append("\nFalse positives: " + mFalsePositives);
        sb.append("\nFalse negatives: " + mFalseNegatives);
        return sb.toString();
    }

    protected void attach(EvaluationResult parent) {
        mParents.add(parent);
        parent.increaseCorrect(getCorrect());
        parent.increaseFalseNegatives(getFalseNegatives());
        parent.increaseFalsePositives(getFalsePositives());
    }

    protected void detach(EvaluationResult parent) {
        mParents.remove(parent);
        parent.increaseCorrect(-getCorrect());
        parent.increaseFalseNegatives(-getFalseNegatives());
        parent.increaseFalsePositives(-getFalsePositives());
    }
}
