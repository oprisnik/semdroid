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

import org.apache.commons.io.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * File utilities.
 */
public class FileUtils {

    private FileUtils() {
    }

    public static void writeObjectToZipFile(Object object, File file)
            throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        writeObjectToZipStream(object, new FileOutputStream(file));
    }

    public static void writeObjectToZipStream(Object object, OutputStream output) throws IOException {
        writeObjectToStream(object, new GZIPOutputStream(output));
    }

    public static void writeObjectToZipFile(Object object, String file)
            throws FileNotFoundException, IOException {
        writeObjectToZipFile(object, new File(file));
    }

    public static void writeObjectToStream(Object object, OutputStream output) throws IOException {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(
                    output));
            out.writeObject(object);
            out.flush();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public static void writeObjectToFile(Object object, File file)
            throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(
                    new FileOutputStream(file)));
            out.writeObject(object);
            out.flush();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public static void writeObjectToFile(Object object, String file)
            throws FileNotFoundException, IOException {
        writeObjectToFile(object, new File(file));
    }

    public static Object loadObjectFromZipFile(String inputFile)
            throws ClassNotFoundException, IOException {
        return loadObjectFromZipFile(new File(inputFile));
    }

    public static Object loadObjectFromZipFile(File inputFile)
            throws ClassNotFoundException, IOException {
        return loadObjectFromZipStream(new FileInputStream(inputFile));
    }

    public static Object loadObjectFromZipStream(InputStream input)
            throws ClassNotFoundException, IOException {
        return loadObjectFromStream(new GZIPInputStream(input));
    }

    public static Object loadObjectFromFile(String inputFile)
            throws ClassNotFoundException, IOException {
        return loadObjectFromFile(new File(inputFile));
    }

    public static Object loadObjectFromFile(File inputFile)
            throws ClassNotFoundException, IOException {
        return loadObjectFromStream(new FileInputStream(inputFile));
    }

    public static Object loadObjectFromStream(InputStream input)
            throws ClassNotFoundException, IOException {
        ObjectInputStream ois = null;
        Object obj = null;
        try {
            ois = new ObjectInputStream(input);
            obj = ois.readObject();
        } finally {
            IOUtils.closeQuietly(ois);
        }
        return obj;
    }

    public static void writeToFile(String filename, byte[] data) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filename);
            fos.write(data);
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    public static void copy(File src, File dest) throws IOException {
        // Guava: Files.copy(src, dest);
        org.apache.commons.io.FileUtils.copyFile(src, dest);
    }

    public static void copy(InputStream is, OutputStream os) throws IOException {
        try {
            IOUtils.copyLarge(is, os);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

}
