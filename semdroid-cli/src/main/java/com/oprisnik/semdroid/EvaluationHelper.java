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

import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.evaluation.EvaluationResults;
import com.oprisnik.semdroid.utils.XmlUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


public class EvaluationHelper {

    public static void evaluate(Config semdroidConfig,  Config evaluationConfig, String trainingPluginConfig,
                                String[] evaluationPluginConfig, String evaluationData,
                                String normalLabel) throws Exception {

        List<EvaluationResults> results = com.oprisnik.semdroid.evaluation.EvaluationHelper.evaluate(semdroidConfig,
                trainingPluginConfig, evaluationPluginConfig, evaluationData, normalLabel);


        File resultsFolder = new File(evaluationConfig.getProperty(CliConfig.Evaluation.RESULTS));
        resultsFolder.mkdirs();
        for (EvaluationResults r : results) {
            System.out.println("--------------------------");
//                System.out.println(r);
            File out = new File(resultsFolder, "evaluation-" + r.getName() + ".xml");
            File outHTML = new File(resultsFolder, "evaluation-" + r.getName() + ".html");
//              XmlUtils.toXMLFile(r, out);
            XmlUtils.toXMLFileAndTransform(r, out, true, new FileOutputStream(outHTML),
                    evaluationConfig.getNestedInputStream(CliConfig.Evaluation.XSL));
            System.out.println("Results saved in '" + out + "'.");
            System.out.println("HTML file saved in '" + outHTML + "'.");
        }
    }
}
