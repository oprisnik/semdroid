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

import android.app.Application;
import android.util.Log;

import com.oprisnik.semdroid.plugins.PluginCardManager;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Main application that loads all available plugins.
 */
public class Semdroid extends Application {

    public static final String PLUGINS_FOLDER = "plugins";

    private static final String TAG = "Semdroid";

    private static final int BUFFER = 4096;

    @Override
    public void onCreate() {
        super.onCreate();

        String[] plugins = null;
        try {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Looking for plugins");
            }
            plugins = getAssets().list(PLUGINS_FOLDER);
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "No plugins");
            }
        }
        try {
            if (plugins != null) {
                File cache = getCacheDir();
                File extractedPluginsFolder = new File(cache, PLUGINS_FOLDER);
                if (!extractedPluginsFolder.exists()) {
                    extractedPluginsFolder.mkdirs();
                    extractPlugins(plugins, extractedPluginsFolder);
                } else {
                    File[] extractedPlugins = extractedPluginsFolder.listFiles();
                    if (extractedPlugins.length != plugins.length) {
                        // new plugin -> we remove everything and extract plugins
                        // TODO: also remove cached results
                        boolean deleted = extractedPluginsFolder.delete();
                        if (deleted) {
                            extractedPluginsFolder.mkdirs();
                        } else {
                            Log.e(TAG, "Could not delete folder " + extractedPluginsFolder);
                        }
                        extractPlugins(plugins, extractedPluginsFolder);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        setupPlugins();
    }

    protected void setupPlugins() {
            if (PluginCardManager.DEFAULT_PLUGINS.size() <= 0) {
//              PluginManager.addFromExternalStorage();
                PluginCardManager.addFromPath(new File(getCacheDir(), PLUGINS_FOLDER));
                PluginCardManager.addFromClassName();
            }

    }

    protected void extractPlugins(String[] plugins, File extractedPluginsFolder) throws IOException {
        for (String plugin : plugins) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Extracting " + plugin + " to " + extractedPluginsFolder);
            }
            extract(getAssets().open(PLUGINS_FOLDER + File.separator + plugin),
                    new File(extractedPluginsFolder, plugin.substring(0, plugin.lastIndexOf("."))));
        }
    }

    protected void extract(InputStream source, File targetDir) {
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Extracting to " + targetDir);
        }

        try {
            ZipInputStream zip = new ZipInputStream(source);
            ZipEntry entry = null;

            while ((entry = zip.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    File dir = new File(targetDir, entry.getName());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                } else {
                    File newFile = new File(targetDir, entry.getName());
                    FileOutputStream fout = new FileOutputStream(newFile);
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Extracting " + entry.getName() + " to " + newFile);
                    }

                    byte[] data = new byte[BUFFER];
                    int count;
                    while ((count = zip.read(data, 0, BUFFER)) != -1) {
                        fout.write(data, 0, count);
                    }
                    zip.closeEntry();
                    fout.close();
                }

            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            IOUtils.closeQuietly(source);
        }
    }
}
