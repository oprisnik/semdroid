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

package com.oprisnik.semdroid.app.parser.manifest;

import com.oprisnik.semdroid.app.manifest.AndroidActivity;
import com.oprisnik.semdroid.app.manifest.AndroidContentProvider;
import com.oprisnik.semdroid.app.manifest.AndroidManifest;
import com.oprisnik.semdroid.app.manifest.AndroidReceiver;
import com.oprisnik.semdroid.app.manifest.AndroidService;
import com.oprisnik.semdroid.app.manifest.IntentFilter;
import com.oprisnik.semdroid.app.manifest.Permission;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Manifest XML String parser. Takes an XML string and returns the parsed Android Manifest.
 *
 * @author Bernd Bergler
 * @author Alexander Oprisnik
 */
public class ManifestXmlParser {

    private ManifestXmlParser() {
    }

    public static AndroidManifest parse(String xmlString) {
        if (xmlString == null)
            return null;
        AndroidManifest manifest = null;

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();

            Document doc;

            doc = dBuilder
                    .parse(new ByteArrayInputStream(xmlString.getBytes()));
            doc.getDocumentElement().normalize();

            manifest = new AndroidManifest(doc.getDocumentElement().getAttribute(
                    "package"));

            NodeList nList = doc.getElementsByTagName("activity");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    AndroidActivity act = new AndroidActivity(
                            eElement.getAttribute("android:name"), manifest);

                    List<IntentFilter> l = parseIntentFilters(nNode);
                    act.addIntentFilters(l);
                    manifest.addActivity(act);

                }
            }

            nList = doc.getElementsByTagName("service");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    AndroidService service = new AndroidService(
                            eElement.getAttribute("android:name"), manifest);
                    List<IntentFilter> l = parseIntentFilters(nNode);
                    service.addIntentFilters(l);
                    manifest.addService(service);

                }
            }

            nList = doc.getElementsByTagName("receiver");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    AndroidReceiver receiver = new AndroidReceiver(
                            eElement.getAttribute("android:name"), manifest);
                    List<IntentFilter> l = parseIntentFilters(nNode);
                    receiver.addIntentFilters(l);
                    manifest.addReceiver(receiver);

                }
            }

            nList = doc.getElementsByTagName("provider");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    manifest.addContentProvider(new AndroidContentProvider(eElement
                            .getAttribute("android:name"), manifest));

                }
            }

            nList = doc.getElementsByTagName("uses-permission");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    manifest.addPermission(new Permission(eElement
                            .getAttribute("android:name")));

                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return manifest;

    }

    protected static List<IntentFilter> parseIntentFilters(Node parent_node) {
        List<IntentFilter> filters = new ArrayList<IntentFilter>();
        NodeList nList = parent_node.getChildNodes();
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node child_node = nList.item(temp);
            if (child_node.getNodeType() == Node.ELEMENT_NODE) {
                if ("intent-filter".equals(child_node.getNodeName())) {

                    IntentFilter intent_filter = new IntentFilter();
                    Element child_element = (Element) child_node;
                    intent_filter.setLabel(child_element
                            .getAttribute("android:label"));
                    intent_filter.setPriority(child_element
                            .getAttribute("android:priority"));
                    NodeList internt_children = child_node.getChildNodes();
                    for (int temp2 = 0; temp2 < internt_children.getLength(); temp2++) {
                        Node intent_node = internt_children.item(temp2);
                        if (intent_node.getNodeType() == Node.ELEMENT_NODE) {
                            if ("action".equals(intent_node.getNodeName()))
                                intent_filter.setAction(((Element) intent_node)
                                        .getAttribute("android:name"));
                            if ("category".equals(intent_node.getNodeName()))
                                intent_filter
                                        .setCategory(((Element) intent_node)
                                                .getAttribute("android:name"));
                        }

                    }

                    filters.add(intent_filter);

                }
            }
        }
        return filters;
    }

}
