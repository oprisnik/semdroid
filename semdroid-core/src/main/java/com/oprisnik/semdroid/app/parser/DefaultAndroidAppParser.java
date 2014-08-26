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

package com.oprisnik.semdroid.app.parser;

import com.googlecode.dex2jar.reader.DexFileReader;
import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.app.manifest.AndroidManifest;
import com.oprisnik.semdroid.app.parser.dex.visitors.DexCodeFileVisitor;
import com.oprisnik.semdroid.app.parser.manifest.ManifestHelper;
import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.utils.Log;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Extract the App object from the APK file.
 * Parses the Dalvik bytecode (classes.dex) and the AndroidManifest.xml.
 *

 */
public class DefaultAndroidAppParser extends BaseAppParser {

    protected static final int MANIFEST_PARSING_TIME_PERCENTAGE = 20;
    protected static final int PROGRESS_UPDATE_THRESHOLD_PERCENTAGE = 5;

    private static final String TAG = "DefaultAndroidAppParser";

    protected DexCodeFileVisitor mVisitor;
    protected int mLastProgress = -1;

    protected DexCodeFileVisitor.ProgressListener mProgressListener = new DexCodeFileVisitor.ProgressListener() {
        @Override
        public void onProgressUpdated(int currentClass, int totalClasses) {
            if (totalClasses < 0) {
                // TODO: if we do not know the number of classes, we cannot report back
//                publishProgress(-currentClass);
            } else {
                float percentage = (float) currentClass / (float) totalClasses;
                int progress = MANIFEST_PARSING_TIME_PERCENTAGE +
                        Math.round(percentage * (100 - MANIFEST_PARSING_TIME_PERCENTAGE));
                if (Math.abs(progress - mLastProgress) >= PROGRESS_UPDATE_THRESHOLD_PERCENTAGE) {
                    publishProgress(progress);
                    mLastProgress = progress;
                }
            }
        }
    };

    public DefaultAndroidAppParser() {
    }

    @Override
    public synchronized void init(Config config) throws BadConfigException {
        mVisitor = new DexCodeFileVisitor();
        mVisitor.init(config);
        mVisitor.setProgressListener(mProgressListener);
    }

    protected synchronized void extract(App app, DexFileReader dexFileReader) {
        try {
            // get the number of classes
            Field f = dexFileReader.getClass().getDeclaredField("class_defs_size");
            f.setAccessible(true);
            mVisitor.setNumberOfClasses(f.getInt(dexFileReader));
        } catch (Exception e) {
            Log.e(TAG, "Could not retrieve number of classes. Ignoring... Exception:" + e.getMessage());
        }
        mLastProgress = -1;
        // set app for visitor and extract data
        mVisitor.setApp(app);
        dexFileReader.accept(mVisitor);
    }

    @Override
    public App parse(File apk) throws Exception {
        App app = parse(org.apache.commons.io.FileUtils.readFileToByteArray(apk));
        app.setApkFile(apk);
        return app;
    }

    @Override
    public synchronized App parse(byte[] apk) throws Exception {
        long start = System.currentTimeMillis();
        // update progress
        publishProgress(0);
        App app = new App();
        AndroidManifest manifest = ManifestHelper.parseManifest(apk);
        publishProgress(MANIFEST_PARSING_TIME_PERCENTAGE);
        if (manifest != null) {
            app.setManifest(manifest);
            app.setName(manifest.getPackageName());
        }
        extract(app, new DexFileReader(DexFileReader.readDex(apk)));
        Log.d(TAG, "App parsing: " + (System.currentTimeMillis() - start) + "ms.");
        publishProgress(100);
        return app;
    }
}
