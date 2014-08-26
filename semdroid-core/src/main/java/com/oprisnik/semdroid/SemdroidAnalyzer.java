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

import com.oprisnik.semdroid.analysis.AnalysisProgressListener;
import com.oprisnik.semdroid.analysis.AppAnalysisPlugin;
import com.oprisnik.semdroid.analysis.AppAnalysisPluginFactory;
import com.oprisnik.semdroid.analysis.results.AppAnalysisReport;
import com.oprisnik.semdroid.analysis.results.SemdroidReport;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.parser.AppParser;
import com.oprisnik.semdroid.app.parser.DefaultAndroidAppParser;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.config.Configurable;
import com.oprisnik.semdroid.utils.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Semdroid is a static Android application analysis framework.
 * It manages multiple analysis plugins, collects all analysis results and returns
 * a final report.
 *
 * @author Alexander Oprisnik
 */
public class SemdroidAnalyzer implements Configurable {

    public static final String KEY_APP_PARSER = "app-parser";

    private static final String TAG = "Semdroid";

    private List<AppAnalysisPlugin> mPlugins;

    private AppParser mAppParser;

    private AppParser.ProgressListener mAppParserProgressListener = new AppParser.ProgressListener() {
        @Override
        public void onProgressUpdated(int percentage) {
            publishProgress(AnalysisProgressListener.STATUS_APP_PARSING, percentage);
        }
    };

    private List<AnalysisProgressListener> mListeners = null;
    private boolean mInitialized = false;

    public SemdroidAnalyzer() {
        mPlugins = new ArrayList<AppAnalysisPlugin>();
    }

    /**
     * Initialize Semdroid with the default values.
     */
    public synchronized void init() {
        try {
            init(null);
        } catch (BadConfigException e) {
            Log.e(TAG, "Could not load default config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void init(Config semdroidConfig) throws BadConfigException {
        if (semdroidConfig != null) {
            mAppParser = semdroidConfig.getComponentAndInit(KEY_APP_PARSER,
                    AppParser.class, DefaultAndroidAppParser.class);
        } else {
            mAppParser = new DefaultAndroidAppParser();
            mAppParser.init(null);
        }
        mInitialized = true;
    }

    /**
     * Register a progress listener. The listener will receive analysis progress updates.
     *
     * @see #unregisterProgressListener(com.oprisnik.semdroid.analysis.AnalysisProgressListener)
     * @param listener the listener to register
     */
    public synchronized void registerProgressListener(AnalysisProgressListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<AnalysisProgressListener>();
            // since we have at least one listener
            // we also have to register the internal listeners
            registerInternalListeners();
        }
        mListeners.add(listener);
    }

    /**
     * Remove the given progress listener.
     *
     * @see #registerProgressListener(com.oprisnik.semdroid.analysis.AnalysisProgressListener)
     * @param listener the listener to remove
     */
    public synchronized void unregisterProgressListener(AnalysisProgressListener listener) {
        if (mListeners != null) {
            mListeners.remove(listener);
            if (mListeners.isEmpty()) {
                mListeners = null;
                // no more listeners -> we do not need internal listeners any more
                unregisterInternalListeners();
            }
        }
    }

    protected synchronized void registerInternalListeners() {
        mAppParser.registerProgressListener(mAppParserProgressListener);
    }

    protected synchronized void unregisterInternalListeners() {
        mAppParser.unregisterProgressListener(mAppParserProgressListener);
    }

    /**
     * Add the given analysis plugin.
     *
     * @param plugin the plugin to add
     */
    public synchronized void addAnalysisPlugin(AppAnalysisPlugin plugin) {
        if (plugin.getName() != null) {
            for (AppAnalysisPlugin a : mPlugins) {
                if (plugin.getName().equals(a.getName())) {
                    throw new IllegalArgumentException("App analysis with name " + plugin.getName() +
                            " already added! Choose a different name!");
                }
            }
        }
        mPlugins.add(plugin);
    }

    /**
     * Removes a given analysis plugin.
     * @param plugin the plugin to remove
     */
    public synchronized void removeAnalysisPlugin(AppAnalysisPlugin plugin) {
        mPlugins.remove(plugin);
    }

    /**
     * Add the analysis plugin from the given plugin configuration.
     *
     * @param pluginConfig the configuration to use
     * @throws Exception if something goes wrong
     */
    public synchronized void addAnalysisPlugin(Config pluginConfig) throws Exception {
        addAnalysisPlugin(AppAnalysisPluginFactory.fromConfig(pluginConfig));
    }

    /**
     * Add the analysis plugin from the given plugin configuration file.
     *
     * @param pluginConfig the configuration file
     * @throws Exception if something goes wrong
     */
    public synchronized void addAnalysisPlugin(String pluginConfig) throws Exception {
        addAnalysisPlugin(new File(pluginConfig));
    }

    /**
     * Add the analysis plugin from the given plugin configuration file.
     *
     * @param pluginConfig the configuration file
     * @throws Exception if something goes wrong
     */
    public synchronized void addAnalysisPlugin(File pluginConfig) throws Exception {
        AppAnalysisPlugin plugin = AppAnalysisPluginFactory.fromFile(pluginConfig);
        if (plugin != null) {
            addAnalysisPlugin(plugin);
        }
    }

    /**
     * Add the given analysis plugin.
     * Here, we do not have a plugin configuration we would like to initialize the plugin with.
     *
     * @see #addAnalysisPlugin(Class, com.oprisnik.semdroid.config.Config)
     * @param pluginClass the plugin class to use
     * @throws Exception if someting goes wrong
     */
    public synchronized void addAnalysisPlugin(Class<? extends AppAnalysisPlugin> pluginClass)
            throws Exception {
        addAnalysisPlugin(pluginClass, null);
    }

    /**
     * Add the given analysis plugin and initialize it with the supplied pluginConfig
     *
     * @param pluginClass the plugin class to use
     * @param pluginConfig the configuration for the plugin
     * @throws Exception if someting goes wrong
     */
    public synchronized void addAnalysisPlugin(Class<? extends AppAnalysisPlugin> pluginClass,
                                               Config pluginConfig)
            throws Exception {
        AppAnalysisPlugin plugin = pluginClass.newInstance();
        plugin.init(pluginConfig);
        addAnalysisPlugin(plugin);
    }

    /**
     * Add an analysis plugin from the given class name.
     * Example: addAnalysisPluginFromClassName("com.my.Plugin")
     *
     * @param className the class name of the analysis plugin
     * @return true if the analysis plugin could be added
     */
    public synchronized boolean addAnalysisPluginFromClassName(String className) {
        if (className == null) {
            return false;
        }
        try {
            Class c = Class.forName(className);
            AppAnalysisPlugin p = (AppAnalysisPlugin) c.newInstance();
            addAnalysisPlugin(p);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public synchronized String getName() {
        if (mPlugins.size() <= 0) {
            return "Analyses: none";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(mPlugins.size() == 1 ? "Analysis: " : "Analyses: ");

        for (AppAnalysisPlugin plugin : mPlugins) {
            sb.append(plugin.getName());
            sb.append(", ");
        }

        if (mPlugins.size() > 0) {
            return sb.toString().substring(0, sb.length() - 2);
        }
        return sb.toString();
    }


    /**
     * Analyze the given APK file.
     *
     * @param apk the application to analyze
     * @return the analysis report
     * @throws Exception
     */
    public synchronized SemdroidReport analyze(File apk) throws Exception {
        if (!mInitialized) {
            init();
        }
        App app = mAppParser.parse(apk);
        return analyze(app);
    }

    /**
     * Analyze the given application.
     *
     * @param apk the application to analyze
     * @return the analysis report
     * @throws Exception
     */
    public synchronized SemdroidReport analyze(byte[] apk) throws Exception {
        if (!mInitialized) {
            init();
        }
        App app = mAppParser.parse(apk);
        return analyze(app);
    }

    /**
     * Analyze the given application.
     *
     * @param app the application to analyze
     * @return the analysis report
     * @throws Exception
     */
    public synchronized SemdroidReport analyze(App app) throws Exception {
        if (!mInitialized) {
            init();
        }
        int percentage = 0;
        int increment = mPlugins.size() == 0 ? 100 : 100 / mPlugins.size();
        List<AppAnalysisReport> results = new ArrayList<AppAnalysisReport>();
        for (AppAnalysisPlugin plugin : mPlugins) {
            // TODO: fine-grained percentage by adding ProgressListener to plugin
            publishProgress(AnalysisProgressListener.STATUS_ANALYZING, percentage);
            results.add(plugin.analyze(app));
            percentage += increment;
        }
        publishProgress(AnalysisProgressListener.STATUS_DONE, 100);
        return new SemdroidReport(getName(), app, results);
    }

    protected synchronized void publishProgress(int status, int percentage) {
        if (mListeners == null || mListeners.size() == 0) {
            return;
        }
        for (AnalysisProgressListener l : mListeners) {
            l.onStatusUpdated(status, percentage);
        }
    }
}
