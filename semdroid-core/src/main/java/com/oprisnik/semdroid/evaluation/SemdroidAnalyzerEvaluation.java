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

import com.oprisnik.semdroid.SemdroidAnalyzer;
import com.oprisnik.semdroid.analysis.AppAnalysisPlugin;
import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.SemdroidReport;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluation test suite.
 * Evaluate analysis performance on given expected results.
 */
public class SemdroidAnalyzerEvaluation extends SemdroidAnalyzer {


    private AppAnalysisPlugin mExpectedResultsPlugin;

    private Map<String, Evaluator> mEvaluators;

    private String mNormalLabel;


    @Override
    public void init(Config semdroidConfig) throws BadConfigException {
        super.init(semdroidConfig);
        mEvaluators = new HashMap<String, Evaluator>();
    }

    public void setNormalLabel(String normalLabel) {
        mNormalLabel = normalLabel;
    }

    public void setExpectedResultsPlugin(AppAnalysisPlugin expected) {
        mExpectedResultsPlugin = expected;
    }

    @Override
    public SemdroidReport analyze(App app) throws Exception {
        AppAnalysisReport expected = mExpectedResultsPlugin.analyze(app);
        SemdroidReport results = super.analyze(app);

        // evaluate results
        for (AppAnalysisReport report : results.getReports()) {
            Evaluator evaluator = mEvaluators.get(report.getName());
            if (evaluator == null) {
                if (mNormalLabel == null) {
                    evaluator = new BasicEvaluator(report.getName());
                } else {
                    evaluator = new SingleLabelEvaluator(report.getName(), mNormalLabel);
                }
                mEvaluators.put(report.getName(), evaluator);
            }
            evaluator.evaluate(expected, report);
        }
        results.addReport(expected);
        return results;
    }

    public List<EvaluationResults> getEvaluationResults() {
        List<EvaluationResults> results = new ArrayList<EvaluationResults>();
        for (Evaluator e : mEvaluators.values()) {
            results.add(e.getResults());
        }
        return results;
    }

    public List<EvaluationResults> evaluate(String evaluationData) throws Exception {
        return evaluate(new File(evaluationData));
    }

    public List<EvaluationResults> evaluate(File evaluationData) throws Exception {
        analyzeEvaluationData(evaluationData);
        return getEvaluationResults();
    }

    protected void analyzeEvaluationData(File evaluationData) throws Exception {
        if (evaluationData.isDirectory()) {
            for (File f : evaluationData.listFiles()) {
                analyzeEvaluationData(f);
            }
        } else {
            try {
                analyze(evaluationData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
