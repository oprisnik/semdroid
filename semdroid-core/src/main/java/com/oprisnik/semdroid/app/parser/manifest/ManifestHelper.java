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

import com.oprisnik.semdroid.app.manifest.AndroidManifest;

import java.io.File;

/**
 * Manifest helper. Parses the Manifest from a given .apk file.
 *
 * @author Alexander Oprisnik
 */
public class ManifestHelper {

    public interface ManifestParser {
        public AndroidManifest parse(File apk);

        public AndroidManifest parse(byte[] apk);
    }

    private static ManifestParser PARSER = new DefaultParser();


    public static AndroidManifest parseManifest(File apk) {
        AndroidManifest manifest = null;

        try {
            manifest = PARSER.parse(apk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return manifest;
    }

    public static AndroidManifest parseManifest(byte[] apk) {
        AndroidManifest manifest = null;

        try {
            manifest = PARSER.parse(apk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return manifest;
    }


    /**
     * Set a different manifest parser, e.g. for Android on-device analysis.
     *
     * @param parser the manifest parser to use
     */
    public static void setManifestParser(ManifestParser parser) {
        PARSER = parser;
    }

    /**
     * Default parser.
     */
    private static class DefaultParser implements ManifestParser {

        @Override
        public AndroidManifest parse(File apk) {
            return ManifestXmlParser.parse(AXMLConverter.getManifestString(apk));
        }

        @Override
        public AndroidManifest parse(byte[] apk) {
            return ManifestXmlParser.parse(AXMLConverter.getManifestString(apk));
        }
    }
}