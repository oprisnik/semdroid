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

import com.oprisnik.semdroid.analysis.results.SemdroidReport;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.config.ConfigFactory;
import com.oprisnik.semdroid.plugin.crypto.CryptoProviderAnalysis;
import com.oprisnik.semdroid.plugin.general.ListBroadcastReceiversPlugin;
import com.oprisnik.semdroid.plugin.general.ListManifestPermissionsPlugin;
import com.oprisnik.semdroid.plugin.general.ListUsedPermissionsPlugin;
import com.oprisnik.semdroid.utils.XmlUtils;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class SemdroidServlet extends HttpServlet {

    public static final String SEMDROID_CONFIG = "/WEB-INF/global-config/semdroid.xml";
    public static final String PLUGIN_FOLDER = "/WEB-INF/plugins";
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(SemdroidServlet.class
            .getName());
    private Config mSemdroidConfig = null;

    public synchronized SemdroidAnalyzer getAvailableTestSuite() throws BadConfigException {
        init();

        // TODO for now we create a new analyzer for each APK
        // it would be better to have a pool of TestSuites and re-use them (parallel requests etc.)

        SemdroidAnalyzer ts = new SemdroidAnalyzer();
        ts.init(mSemdroidConfig);

        // load plugins from plugin folder
        try {
            File pluginFolder = new File(getServletContext().getRealPath(PLUGIN_FOLDER));
            if (pluginFolder.isDirectory()) {
                File[] files = pluginFolder.listFiles();
                if (files != null) {
                    for (File plugin : files) {
                        try {
                            ts.addAnalysisPlugin(plugin);
                        } catch (Exception e) {
                            log.warning(e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warning(e.getMessage());
        }

        // load other plugins
        try {
            ts.addAnalysisPlugin(ListBroadcastReceiversPlugin.class);
            ts.addAnalysisPlugin(CryptoProviderAnalysis.class);
            ts.addAnalysisPlugin(ListManifestPermissionsPlugin.class);
            ts.addAnalysisPlugin(ListUsedPermissionsPlugin.class);

        } catch (Exception e) {
            log.warning("Could not init plugins: " + e.getMessage());
            e.printStackTrace();
        }

        return ts;
    }

    public synchronized void init() {
        if (mSemdroidConfig != null) {
            return; // already initialized;
        }
        try {
            log.info("Initializing test suite");
            File f = new File(getServletContext().getRealPath(SEMDROID_CONFIG));
            mSemdroidConfig = ConfigFactory.fromFile(f);

        } catch (Exception e) {
            log.warning("Exception: " + e.getMessage());
            log.throwing(this.getClass().getName(), "init", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        log.info("doPost");
        StringBuilder sb = new StringBuilder();
        try {
            ServletFileUpload upload = new ServletFileUpload();
            // set max size (-1 for unlimited size)
            upload.setSizeMax(1024 * 1024 * 30); // 30MB
            upload.setHeaderEncoding("UTF-8");

            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                if (item.isFormField()) {
                    // Process regular form fields
                    // String fieldname = item.getFieldName();
                    // String fieldvalue = item.getString();
                    // log.info("Got form field: " + fieldname + " " + fieldvalue);
                } else {
                    // Process form file field (input type="file").
                    String fieldname = item.getFieldName();
                    String filename = FilenameUtils.getBaseName(item.getName());
                    log.info("Got file: " + filename);
                    InputStream filecontent = null;
                    try {
                        filecontent = item.openStream();
                        // analyze
                        String txt = analyzeApk(filecontent);
                        if (txt != null) {
                            sb.append(txt);
                        } else {
                            sb.append("Error. Could not analyze ").append(filename);
                        }
                        log.info("Analysis done!");
                    } finally {
                        if (filecontent != null) {
                            filecontent.close();
                        }
                    }
                }
            }
            response.getWriter().print(sb.toString());
        } catch (FileUploadException e) {
            throw new ServletException("Cannot parse multipart request.", e);
        } catch (Exception ex) {
            log.warning("Exception: " + ex.getMessage());
            log.throwing(this.getClass().getName(), "doPost", ex);
        }

    }

    public String analyzeApk(InputStream apk) {
        try {
            SemdroidAnalyzer ts = getAvailableTestSuite();
            log.info("Starting analysis: " + ts.getName());

            byte[] bytes = IOUtils.toByteArray(apk);
            long start = System.currentTimeMillis();
            SemdroidReport results = ts.analyze(bytes);
            long end = System.currentTimeMillis();
            log.info("Done after " + (end - start) + "ms. Report: "
                    + results.getName());
            return getResults(
                    results,
                    mSemdroidConfig.getNestedInputStream(CliConfig.Analysis.XSL));
        } catch (Exception e) {
            log.warning("Exception: " + e.getMessage());
            log.throwing(this.getClass().getName(), "analyzeApk", e);
        }
        return null;
    }

    public String getResults(SemdroidReport results,
                             InputStream transformationStyle) {
        try {
            Document doc = XmlUtils.createDocument();
            Element rootElement = doc.createElement("AnalysisResults");
            doc.appendChild(rootElement);
            XmlUtils.addResults(results, doc, rootElement);

            StringWriter writer = new StringWriter();

            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            StreamSource stylesource = new StreamSource(transformationStyle);
            Transformer transformer = transformerFactory
                    .newTransformer(stylesource);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            return writer.toString();

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            log.warning("Exception: " + pce.getMessage());
            log.throwing(this.getClass().getName(), "getResults", pce);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            log.warning("Exception: " + e.getMessage());
            log.throwing(this.getClass().getName(), "getResults", e);
        } catch (TransformerException e) {
            e.printStackTrace();
            log.warning("Exception: " + e.getMessage());
            log.throwing(this.getClass().getName(), "getResults", e);
        }
        return null;
    }

}