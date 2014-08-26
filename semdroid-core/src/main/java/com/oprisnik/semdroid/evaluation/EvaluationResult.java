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

import com.oprisnik.semdroid.analysis.results.Labelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluation results.
 */
public class EvaluationResult {

    private String mName;

    private List<Labelable> mTotal = new ArrayList<Labelable>();
    private List<Labelable> mFalsePositives = new ArrayList<Labelable>();
    private List<Labelable> mFalseNegatives = new ArrayList<Labelable>();
    private List<Labelable> mCorrect = new ArrayList<Labelable>();
    private List<Labelable> mWrong = new ArrayList<Labelable>();

//    private Set<EvaluationResult> mParents;

    public EvaluationResult(String name) {
        mName = name;
//        mParents = new HashSet<EvaluationResult>();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<Labelable> getTotalComponents() {
        return mTotal;
    }

    public List<Labelable> getFalsePositivesComponents() {
        return mFalsePositives;
    }

    public List<Labelable> getFalseNegativesComponents() {
        return mFalseNegatives;
    }

    public List<Labelable> getCorrectComponents() {
        return mCorrect;
    }

    public List<Labelable> getWrongComponents() {
        return mWrong;
    }

    public void increaseTotal(Labelable labelable) {
        mTotal.add(labelable);
    }

    public void decreaseTotal(Labelable labelable) {
        mTotal.remove(labelable);
    }

    public void increaseCorrect(Labelable labelable) {
        mCorrect.add(labelable);
//        if (mParents.size() > 0) {
//            for (EvaluationResult r : mParents) {
//                r.increaseCorrect(labelable);
//            }
//        }
    }

    public void decreaseCorrect(Labelable labelable) {
        mCorrect.remove(labelable);
//        if (mParents.size() > 0) {
//            for (EvaluationResult r : mParents) {
//                r.decreaseCorrect(labelable);
//            }
//        }
    }

    public void increaseFalsePositives(Labelable labelable) {
        mFalsePositives.add(labelable);
//        mWrong.add(labelable);
//        if (mParents.size() > 0) {
//            for (EvaluationResult r : mParents) {
//                r.increaseFalsePositives(labelable);
//            }
//        }
    }

    public void decreaseFalsePositives(Labelable labelable) {
        mFalsePositives.remove(labelable);

//        mWrong.remove(labelable);
//        if (mParents.size() > 0) {
//            for (EvaluationResult r : mParents) {
//                r.decreaseFalsePositives(labelable);
//            }
//        }
    }

    public void increaseFalseNegatives(Labelable labelable) {
        mFalseNegatives.add(labelable);
//        mWrong.add(labelable);
//        if (mParents.size() > 0) {
//            for (EvaluationResult r : mParents) {
//                r.increaseFalseNegatives(labelable);
//            }
//        }
    }

    public void decreaseFalseNegatives(Labelable labelable) {
        mFalseNegatives.remove(labelable);
//        mWrong.remove(labelable);
//        if (mParents.size() > 0) {
//            for (EvaluationResult r : mParents) {
//                r.decreaseFalseNegatives(labelable);
//            }
//        }
    }

    public void increaseWrong(Labelable labelable) {
        mWrong.add(labelable);
    }

    public int getTotal() {
        return mTotal.size();
    }

    public int getFalsePositives() {
        return mFalsePositives.size();
    }

    public int getFalseNegatives() {
        return mFalseNegatives.size();
    }

    public int getCorrect() {
        return mCorrect.size();
    }

    public int getWrong() {
        return mWrong.size();
    }

    public int getPercentageCorrect() {
        return Math.round(getPercentCorrectFloat() * 100f);
    }

    public float getPercentCorrectFloat() {
        return (float) getCorrect() / (float) getTotal();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(mName);
        sb.append("\nTotal: ").append(getTotal());
        sb.append("\nCorrect: ").append(getCorrect());
        sb.append('\n');
        for (Labelable l : mCorrect) {
            sb.append("  ").append(l.getName()).append("\n");
        }
        sb.append(" => ").append(getPercentageCorrect()).append("%");
        sb.append("\nWrong: ").append(getWrong());
        sb.append("\nFalse positives: ").append(getFalsePositives());
        sb.append('\n');
        for (Labelable l : mFalsePositives) {
            sb.append("  ").append(l.getName()).append("\n");
        }
        sb.append("\nFalse negatives: ").append(getFalseNegatives());
        sb.append('\n');
        for (Labelable l : mFalseNegatives) {
            sb.append("  ").append(l.getName()).append("\n");
        }
        return sb.toString();
    }

//    protected void attach(EvaluationResult parent) {
//        mParents.add(parent);
//        for (Labelable l : mCorrect) {
//            parent.increaseCorrect(l);
//        }
//        for (Labelable l : mFalseNegatives) {
//            parent.increaseFalseNegatives(l);
//        }
//        for (Labelable l : mFalsePositives) {
//            parent.increaseFalsePositives(l);
//        }
//    }
//
//    protected void detach(EvaluationResult parent) {
//        mParents.remove(parent);
//        for (Labelable l : mCorrect) {
//            parent.decreaseCorrect(l);
//        }
//        for (Labelable l : mFalseNegatives) {
//            parent.decreaseFalseNegatives(l);
//        }
//        for (Labelable l : mFalsePositives) {
//            parent.decreaseFalsePositives(l);
//        }
//    }
}
