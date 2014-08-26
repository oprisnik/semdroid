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

package com.oprisnik.semdroid.plugin.sms;

import com.oprisnik.semdroid.BaseComponentTest;
import com.oprisnik.semdroid.analysis.AppAnalysisPlugin;
import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.manifest.AndroidManifest;
import com.oprisnik.semdroid.app.manifest.Permission;
import com.oprisnik.semdroid.config.Config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
public class HasSmsPermissionAnalysisTest extends BaseComponentTest {


    @Test
    public void testHasSmsPermissionAnalysis() throws Exception {
        Config config = getXmlConfig("/has-sms-permission-analysis.xml");

        AppAnalysisPlugin plugin = config.getComponentAndInit(AppAnalysisPlugin.class);
        assertNotNull(plugin);

        App app = new App();
        app.setManifest(new AndroidManifest("com.oprisnik.test"));
        app.getManifest().addPermission(new Permission("com.oprisnik.test.DEMO_PERMISSION"));

        AppAnalysisReport r1 = plugin.analyze(app);

        assertNotNull(r1);
        assertEquals(1, r1.getLabels().size());
        assertEquals(HasSmsPermissionAnalysis.NO, r1.getLabels().iterator().next().getName());

        app.getManifest().addPermission(new Permission(HasSmsPermissionAnalysis.PERMISSION_RECEIVE_SMS));

        AppAnalysisReport r2 = plugin.analyze(app);

        assertNotNull(r2);
        assertEquals(1, r2.getLabels().size());
        assertEquals(HasSmsPermissionAnalysis.YES, r2.getLabels().iterator().next().getName());
    }


}