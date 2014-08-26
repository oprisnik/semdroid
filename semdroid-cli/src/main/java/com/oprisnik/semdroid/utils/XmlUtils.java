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

package com.oprisnik.semdroid.utils;

import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.Label;
import com.oprisnik.semdroid.analysis.results.Labelable;
import com.oprisnik.semdroid.analysis.results.SemdroidReport;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.evaluation.EvaluationResult;
import com.oprisnik.semdroid.evaluation.EvaluationResults;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * XML utilities.
 *
 */
public class XmlUtils {

    private static final String TAG = "XmlUtils";

    public static void toXMLFile(List<AppAnalysisReport> results, File xmlFile) {
        toXMLFileAndTransform(results, xmlFile, false, null, null);
    }

    public static void toXMLFileAndTransform(List<AppAnalysisReport> results, File xmlFile, boolean outputTransformed, OutputStream transformedOutput, InputStream transformationStyle) {
        try {
            Document doc = createDocument();
            Element rootElement = doc.createElement("AnalysisResults");
            doc.appendChild(rootElement);

            addResults(results, doc, rootElement);

            writeXml(doc, xmlFile);
            if (outputTransformed) {
                writeTransformedXml(doc, transformedOutput, transformationStyle);
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public static String getResults(List<AppAnalysisReport> results, File transformationStyle) {
        try {
            Document doc = XmlUtils.createDocument();
            Element rootElement = doc.createElement("AnalysisResults");
            doc.appendChild(rootElement);

            StringWriter writer = new StringWriter();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            StreamSource stylesource = new StreamSource(transformationStyle);
            Transformer transformer = transformerFactory.newTransformer(stylesource);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);

            return writer.toString();

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void reportsToXML(List<SemdroidReport> results, File xmlFile) {
        reportsToXMLAndTransform(results, xmlFile, false, null, null);
    }

    public static void reportsToXMLAndTransform(List<SemdroidReport> results, File xmlFile, boolean outputTransformed, OutputStream transformedOutput, InputStream transformationStyle) {
        try {
            Document doc = createDocument();
            Element rootElement = doc.createElement("AnalysisResults");
            doc.appendChild(rootElement);
            for (SemdroidReport result : results) {
                addResults(result, doc, rootElement);
            }

            writeXml(doc, xmlFile);
            if (outputTransformed) {
                writeTransformedXml(doc, transformedOutput, transformationStyle);
            }
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public static void toXMLFile(EvaluationResults results, File xmlFile) {
        toXMLFileAndTransform(results, xmlFile, false, null, null);
    }

    public static void toXMLFileAndTransform(EvaluationResults results, File xmlFile, boolean outputTransformed, OutputStream transformedOutput, InputStream transformationStyle) {
        try {
            Document doc = createDocument();
            Element rootElement = doc.createElement("EvaluationResults");
            setAttribute(rootElement, "name", results.getName());
            doc.appendChild(rootElement);
            addPerformanceResult(results, doc, rootElement, false);
            XmlUtils.addPerformanceDetails(results.getTotalComponents(), "Total", doc, rootElement);
            XmlUtils.addPerformanceDetails(results.getCorrectComponents(), "Correct", doc, rootElement);
            XmlUtils.addPerformanceDetails(results.getWrongComponents(), "Wrong", doc, rootElement);
            for (EvaluationResult r : results.results()) {
                addEvaluationResult(r, doc, rootElement);
            }

            writeXml(doc, xmlFile);
            if (outputTransformed) {
                writeTransformedXml(doc, transformedOutput, transformationStyle);
            }

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public static void addEvaluationResult(EvaluationResult result, Document doc, Element rootElement) {
        Element res = doc.createElement("EvaluationResult");
        setAttribute(res, "name", result.getName());
        rootElement.appendChild(res);
        addPerformanceResult(result, doc, res, true);
        XmlUtils.addPerformanceDetails(result.getTotalComponents(), "Expected", doc, res);
        XmlUtils.addPerformanceDetails(result.getCorrectComponents(), "Correct", doc, res);
        XmlUtils.addPerformanceDetails(result.getWrongComponents(), "Wrong", doc, res);
        XmlUtils.addPerformanceDetails(result.getFalsePositivesComponents(), "FalsePositives", doc, res);
        XmlUtils.addPerformanceDetails(result.getFalseNegativesComponents(), "FalseNegatives", doc, res);
    }

    public static void addPerformanceResult(EvaluationResult result, Document doc, Element rootElement, boolean addFalsePosNeg) {
        Element res = doc.createElement("Performance");
        rootElement.appendChild(res);
        setAttribute(res, "total", result.getTotal() + "");
        setAttribute(res, "correct", result.getCorrect() + "");
        setAttribute(res, "percentageCorrect", result.getPercentageCorrect() + "%");
        setAttribute(res, "wrong", result.getWrong() + "");
        if (addFalsePosNeg) {
            setAttribute(res, "falsePositives", result.getFalsePositives() + "");
            setAttribute(res, "falseNegatives", result.getFalseNegatives() + "");
        }
    }


    public static Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.newDocument();
    }


    public static void writeXml(Document doc, File output) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "2");

        transformer.transform(source, result);
    }

    public static void writeTransformedXml(Document doc, OutputStream output, InputStream style) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        StreamSource stylesource = new StreamSource(style);
        Transformer transformer = transformerFactory.newTransformer(stylesource);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);
        transformer.transform(source, result);
    }


    public static void addLabelable(Labelable labelable, Document doc, Element rootElement) {
        Element labelableEntry = doc.createElement("Labelable");
        rootElement.appendChild(labelableEntry);
        String name = labelable
                .getName();
        if (name == null) {
            name = "null";
        }
        labelableEntry.appendChild(doc.createTextNode(name));
    }

    public static void addPerformanceDetails(Collection<Labelable> entries, String name, Document doc, Element rootElement) {
        Element res = doc.createElement(name);
        rootElement.appendChild(res);
        setAttribute(res, "size", entries.size() + "");

        for (Labelable l : entries) {
            addLabelable(l, doc, res);
        }
    }

    public static void addLabel(Label label, Document doc, Element rootElement) {
        Element labelEntry = doc.createElement("Label");
        rootElement.appendChild(labelEntry);
        setAttribute(labelEntry, "name", label.getName());
        setAttribute(labelEntry, "size", label.getObjects().size() + "");
        for (Labelable labelable : label.getObjects()) {
            XmlUtils.addLabelable(labelable, doc, labelEntry);
        }
    }


    public static void addResults(List<AppAnalysisReport> results,
                                  Document doc, Element root) {

        Map<App, Element> alreadyAnalyzed = new HashMap<App, Element>();
        for (AppAnalysisReport r : results) {
            Element appEntry = alreadyAnalyzed.get(r.getApp());
            if (appEntry == null) {
                appEntry = doc.createElement("App");
                root.appendChild(appEntry);
                setAttribute(appEntry, "apkFile", r.getApp().getName());
                setAttribute(appEntry, "hashValue", r.getApp().getHashValue());
                alreadyAnalyzed.put(r.getApp(), appEntry);
            }
            Element appReport = doc.createElement("AppAnalysisReport");
            appEntry.appendChild(appReport);
            setAttribute(appReport, "name", r.getName());
            for (Label l : r.getLabels()) {
                XmlUtils.addLabel(l, doc, appReport);
            }
        }
    }

    public static void addResults(SemdroidReport report,
                                  Document doc, Element root) {
        App app = report.getApp();
        Element appEntry = getAppEntry(app, doc);
        root.appendChild(appEntry);

        for (AppAnalysisReport r : report.getReports()) {
            if (!app.equals(r.getApp())) {
                Log.w(TAG, "Wrong app specified in TestSuiteReport! Skipping!");
                continue;
            }
            Element appReport = getAppAnalysisReportEntry(r, doc);
            appEntry.appendChild(appReport);

        }
    }

    public static Element getAppAnalysisReportEntry(AppAnalysisReport report, Document doc) {
        Element entry = doc.createElement("AppAnalysisReport");
        setAttribute(entry, "name", report.getName());
        for (Label l : report.getLabels()) {
            XmlUtils.addLabel(l, doc, entry);
        }
        return entry;
    }

    public static Element getAppEntry(App app, Document doc) {
        Element appEntry = doc.createElement("App");
        setAttribute(appEntry, "apkFile", app.getApkFileName());
        setAttribute(appEntry, "name", app.getName());
        setAttribute(appEntry, "hashValue", app.getHashValue());
        return appEntry;
    }

    protected static void setAttribute(Element element, String attribute, String attributeValue, String attributeDefaultValue) {
        element.setAttribute(attribute, attributeValue == null ? attributeDefaultValue : attributeValue);
    }

    protected static void setAttribute(Element element, String attribute, String attributeValue) {
        setAttribute(element, attribute, attributeValue, "null");
    }


    private XmlUtils() {
    }
}
