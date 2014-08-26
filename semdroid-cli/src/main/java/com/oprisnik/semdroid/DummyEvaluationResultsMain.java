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

package com.oprisnik.semdroid;

import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.Label;
import com.oprisnik.semdroid.analysis.results.Labelable;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.evaluation.EvaluationResults;
import com.oprisnik.semdroid.evaluation.Evaluator;
import com.oprisnik.semdroid.evaluation.SingleLabelEvaluator;
import com.oprisnik.semdroid.utils.XmlUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

public class DummyEvaluationResultsMain {

    public static void main(String[] args) {
        App app = new App();

        Evaluator mEvaluator;

        AppAnalysisReport mExpected, mReport;


        mEvaluator = new SingleLabelEvaluator("TEST", "NORMAL");
        Labelable c1 = new DebugLabelable("com.oprisnik.test.crypto1");
        Labelable c2 = new DebugLabelable("com.oprisnik.test.crypto2");
        Labelable c3 = new DebugLabelable("com.oprisnik.a.crypto3");
        Labelable c4 = new DebugLabelable("com.oprisnik.a.crypto4");
        Labelable c5 = new DebugLabelable("com.oprisnik.a.crypto5");
        Labelable c6 = new DebugLabelable("com.oprisnik.a.crypto6");
        Labelable c7 = new DebugLabelable("com.oprisnik.test.crypto7");
        Labelable c8 = new DebugLabelable("com.oprisnik.a.crypto8");
        Labelable n1 = new DebugLabelable("com.oprisnik.test.normal1");
        Labelable n2 = new DebugLabelable("com.oprisnik.b.normal2");
        Labelable n3 = new DebugLabelable("com.oprisnik.test.normal3");
        Labelable n4 = new DebugLabelable("com.oprisnik.test.normal4");
        Labelable n5 = new DebugLabelable("com.oprisnik.a.normal5");
        Labelable n6 = new DebugLabelable("com.oprisnik.b.normal6");
        Labelable n7 = new DebugLabelable("com.oprisnik.b.normal7");
        Labelable n8 = new DebugLabelable("com.oprisnik.b.normal8");
        Labelable n9 = new DebugLabelable("com.oprisnik.b.normal8");
        Labelable n10 = new DebugLabelable("com.oprisnik.a.normal8");


        mExpected = new AppAnalysisReport();
        mExpected.setApp(app);

        mExpected.label(c1, "CRYPTO");
        mExpected.label(c2, "CRYPTO");
        mExpected.label(c3, "CRYPTO");
        mExpected.label(c4, "CRYPTO");
        mExpected.label(c5, "CRYPTO");
        mExpected.label(c6, "CRYPTO");
        mExpected.label(c7, "CRYPTO");
        mExpected.label(c8, "CRYPTO");


        mExpected.label(n1, "NORMAL");
        mExpected.label(n2, "NORMAL");
        mExpected.label(n3, "NORMAL");
        mExpected.label(n4, "NORMAL");
        mExpected.label(n5, "NORMAL");
        mExpected.label(n6, "NORMAL");
        mExpected.label(n7, "NORMAL");
        mExpected.label(n8, "NORMAL");
        mExpected.label(n9, "NORMAL");
        mExpected.label(n10, "NORMAL");

        mReport = new AppAnalysisReport("Symmetric cryptography demo", app);
        mReport.setApp(app);


        mReport.label(c1, "CRYPTO");
        mReport.label(c2, "CRYPTO");
        mReport.label(c3, "NORMAL");
        mReport.label(c4, "CRYPTO");
        mReport.label(c5, "NORMAL");
        mReport.label(c6, "CRYPTO");
        mReport.label(c7, "CRYPTO");
        mReport.label(c8, "CRYPTO");


        mReport.label(n1, "CRYPTO");
        mReport.label(n2, "NORMAL");
        mReport.label(n3, "NORMAL");
        mReport.label(n4, "NORMAL");
        mReport.label(n5, "NORMAL");
        mReport.label(n6, "NORMAL");
        mReport.label(n7, "NORMAL");
        mReport.label(n8, "NORMAL");
        mReport.label(n9, "NORMAL");
        mReport.label(n10, "NORMAL");

        try {
            mEvaluator.evaluate(mExpected, mReport);
            EvaluationResults results = mEvaluator.getResults();

            File out = new File("demo", "evaluation-demo.xml");
            File outHTML = new File("demo", "evaluation-demo.html");
//              XmlUtils.toXMLFile(r, out);
            Config semdroidConfig = CliConfig.getSemdroidConfig();
            XmlUtils.toXMLFileAndTransform(results, out, true, new FileOutputStream(outHTML), semdroidConfig.getNestedInputStream(CliConfig.Evaluation.XSL));
            System.out.println("Results saved in '" + out + "'.");
            System.out.println("HTML file saved in '" + outHTML + "'.");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();

        }
    }

    static class DebugLabelable implements Labelable {

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
