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

package at.tuflowgraphy.semanticapps.semdroid.demo;

import com.oprisnik.semdroid.utils.FileUtils;

import java.io.File;
import java.util.Arrays;

import at.tuflowgraphy.semantic.base.domain.data.DatasetDataElement;
import at.tuflowgraphy.semantic.base.domain.data.IDataElement;
import at.tuflowgraphy.semantic.base.domain.data.InstanceDataElement;
import at.tuflowgraphy.semanticapps.semdroid.DalvikBaseAnalyzer;
import at.tuflowgraphy.semanticapps.semdroid.SimpleDalvikAnalyzer;

/**
 * Feature analyzer demo.
 * Input: a given compressed DatasetDataElement.
 * Output: Resulting semantic pattern net and .arff file.
 */
public class FeatureAnalyzer {

    public static void main(String[] args) {

        // Input file (gzipped DatasetDataElement)
        String input = "results.dataset";

        // Output files
        String sem = "results/semantic-pattern.net";
        String arff = "results/results.arff";

        try {

            DalvikBaseAnalyzer analyzer = new SimpleDalvikAnalyzer();
            analyzer.init("Test", "Test");

            DatasetDataElement data = (DatasetDataElement) FileUtils.loadObjectFromZipFile(new File(input));
            for (InstanceDataElement e : data.getValue()) {
                StringBuilder sb = new StringBuilder();
                sb.append("InstanceDataElement ");
                for (IDataElement element : e.getValue()) {
                    // we assume that we have just double arrays as IDataElement
                    try {
                        sb.append(Arrays.toString((double[]) element.getValue()));
                    } catch (Exception ex) {
                        System.err.println(ex.getMessage());
                    }
                }
                System.out.println(sb.toString());
            }
            analyzer.analyze(data);
            analyzer.saveNet(sem);
            analyzer.saveArff(arff);

            System.out.println("Semantic pattern net in " + sem);
            System.out.println("Arff file in " + arff);
            System.out.println("DONE!");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
