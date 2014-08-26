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

import com.oprisnik.semdroid.DefaultValues;
import com.oprisnik.semdroid.analysis.AppAnalysisPlugin;
import com.oprisnik.semdroid.analysis.AppAnalysisPluginFactory;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;

import java.io.File;
import java.util.List;

/**
 * Evaluation helper class
 */
public class EvaluationHelper {


    public static List<EvaluationResults> evaluate(Config semdroidConfig, String trainingPluginConfig,
                                                   String[] evaluationPluginConfigurations,
                                                   String evaluationData)
            throws BadConfigException {
        return evaluate(semdroidConfig, trainingPluginConfig,
                evaluationPluginConfigurations, evaluationData, null);
    }

    public static List<EvaluationResults> evaluate(Config semdroidConfig, String trainingPluginConfig,
                                                   String[] evaluationPluginConfigs,
                                                   String evaluationData,
                                                   String normalLabel) throws BadConfigException {
        try {
            SemdroidAnalyzerEvaluation evaluation = new SemdroidAnalyzerEvaluation();
            evaluation.setNormalLabel(normalLabel);
            evaluation.init(semdroidConfig);
            AppAnalysisPlugin expected = null;
            File trainingPluginFile = new File(trainingPluginConfig);
            if (trainingPluginFile.isDirectory()) {
                // folder => check if classification config in folder
                File temp = new File(trainingPluginFile, DefaultValues.PLUGIN_CONFIG_FILE_NAME);
                if (temp.exists()) {
                    trainingPluginFile = temp;
                } else {
                    // check if training config in folder
                    temp = new File(trainingPluginFile, DefaultValues.TRAINING_CONFIG_FILE_NAME);
                    if (temp.exists()) {
                        trainingPluginFile = temp;
                    }
                }
            }
            expected = AppAnalysisPluginFactory.fromFile(trainingPluginFile);

            evaluation.setExpectedResultsPlugin(expected);

            for (String s : evaluationPluginConfigs) {
                evaluation.addAnalysisPlugin(s);
            }

            return evaluation.evaluate(evaluationData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
