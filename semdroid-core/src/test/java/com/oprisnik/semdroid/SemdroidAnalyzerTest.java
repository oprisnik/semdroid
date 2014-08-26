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

import com.oprisnik.semdroid.analysis.AppAnalysisPlugin;
import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.Label;
import com.oprisnik.semdroid.analysis.results.SemdroidReport;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;

import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SemdroidAnalyzerTest {


    @Test
    public void testSimplePlugin() throws Exception {
        SemdroidAnalyzer semdroid = new SemdroidAnalyzer();
        semdroid.init();

        semdroid.addAnalysisPlugin(new AppAnalysisPlugin() {
            @Override
            public String getName() {
                return "Test";
            }

            @Override
            public AppAnalysisReport analyze(App app) throws Exception {
                AppAnalysisReport report = new AppAnalysisReport(getName(), app);
                report.label(app, "Demo app");
                report.label(app, "Does nothing");
                return report;
            }

            @Override
            public void init(Config config) throws BadConfigException {
                // nothing to do here
            }
        });

        App dummy = new App();

        SemdroidReport report = semdroid.analyze(dummy);

        assertNotNull(report);

        List<AppAnalysisReport> all = report.getReports();
        assertNotNull(all);
        assertEquals(1, all.size());

        AppAnalysisReport r = all.get(0);
        assertEquals("Test", r.getName());

        assertEquals(dummy, r.getApp());

        assertEquals(1, r.getComponents().size());

        assertTrue(r.getComponents().contains(dummy));


        assertEquals(2, r.getLabels().size());
        assertEquals(2, r.getLabels(dummy).size());

        Collection<Label> labels = r.getLabels();
        Set<String> expected = new HashSet<String>();
        expected.add("Demo app");
        expected.add("Does nothing");

        for (Label l : labels) {
            assertEquals(1, l.size());
            assertTrue(l.getObjects().contains(dummy));
            assertTrue(expected.contains(l.getName()));
            expected.remove(l.getName());
        }

        assertTrue(expected.isEmpty());
    }


}
