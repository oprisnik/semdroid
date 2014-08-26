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
import com.oprisnik.semdroid.app.App;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

/**
 * NormalEvaluatorTest
 */
public class NormalEvaluatorTest {

    private Evaluator mEvaluator;

    private AppAnalysisReport mExpected, mReport;

    @Before
    public void setUp() throws Exception {

        App app = new App();

        mEvaluator = new NormalEvaluator("TEST", "NORMAL");
        Labelable c1 = new DebugLabelable("c1");
        Labelable c2 = new DebugLabelable("c2");
        Labelable c3 = new DebugLabelable("c3");
        Labelable c4 = new DebugLabelable("c4");
        Labelable c5 = new DebugLabelable("c5");
        Labelable c6 = new DebugLabelable("c6");
        Labelable c7 = new DebugLabelable("c7");
        Labelable c8 = new DebugLabelable("c8");
        Labelable c9 = new DebugLabelable("c9");
        Labelable c10 = new DebugLabelable("c10");


        mExpected = new AppAnalysisReport();
        mExpected.setApp(app);

        mExpected.label(c1, "A");
        mExpected.label(c2, "A");
        mExpected.label(c3, "A");
        mExpected.label(c4, "B");
        mExpected.label(c5, "NORMAL");
        mExpected.label(c6, "NORMAL");
        mExpected.label(c7, "NORMAL");
//        mExpected.label(c8, "NORMAL");
//        mExpected.label(c9, "NORMAL");
        mExpected.label(c10, "A");

        mReport = new AppAnalysisReport();
        mReport.setApp(app);

        mReport.label(c1, "A");
        mReport.label(c2, "B");
        mReport.label(c3, "NORMAL");
        mReport.label(c4, "A");
        mReport.label(c5, "NORMAL");
        mReport.label(c6, "A");
//        mReport.label(c7, "NORMAL");
        mReport.label(c8, "NORMAL");
        mReport.label(c9, "A");
//        mReport.label(c10, "NORMAL");
    }

    @Test
    public void testEvaluate() throws Exception {
        mEvaluator.evaluate(mExpected, mReport);
        EvaluationResults results = mEvaluator.getResults();

        // TOTAL
        print("TOTAL", results.getTotalComponents());
        assertEquals(9, results.getTotal());
        System.out.println("OK");

        print("CORRECT", results.getCorrectComponents());
        assertEquals(3, results.getCorrect());
        System.out.println("OK");

        print("WRONG", results.getWrongComponents());
        assertEquals(6, results.getWrong());
        System.out.println("OK");


        // Label A

        EvaluationResult a = results.getResult("A");

        assertEvaluationResult(a, 4, 1, 6, 3, 3);

        // Label B
        EvaluationResult b = results.getResult("B");
        assertEvaluationResult(b, 1, 0, 2, 1, 1);

        // NORMAL
        EvaluationResult n = results.getResult("NORMAL");
        assertEvaluationResult(n, 4, 2, 4, 2, 2);

    }

    public static void assertEvaluationResult(EvaluationResult result, int total, int correct, int wrong, int falsePositives, int falseNegatives) {
        print(result.getName() + " TOTAL", result.getTotalComponents());
        assertEquals(total, result.getTotal());

        print(result.getName() + " CORRECT", result.getCorrectComponents());
        assertEquals(correct, result.getCorrect());

        print(result.getName() + " WRONG", result.getWrongComponents());
        assertEquals(wrong, result.getWrong());

        print(result.getName() + " False Positives", result.getFalsePositivesComponents());
        assertEquals(falsePositives, result.getFalsePositives());

        print(result.getName() + " False Negatives", result.getFalseNegativesComponents());
        assertEquals(falseNegatives, result.getFalseNegatives());
        System.out.println(result.getName() + " OK!\n\n");
    }

    public static void print(String text, Collection<Labelable> labels) {
        System.out.println(text + ":");
        for (Labelable l : labels) {
            System.out.print(l.getName() + ", ");
        }
        System.out.println();
    }

    public class DebugLabelable implements Labelable {

        private final String mName;
        Set<Label> mLabels = new HashSet<Label>();

        public DebugLabelable(String name) {
            mName = name;
        }

        @Override
        public String getName() {
            return mName;
        }

        @Override
        public void addLabel(Label label) {
            mLabels.add(label);
        }

        @Override
        public void removeLabel(Label label) {
            mLabels.remove(label);
        }

        @Override
        public Set<Label> getLabels() {
            return mLabels;
        }
    }
}
