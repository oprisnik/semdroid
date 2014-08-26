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

package com.oprisnik.semdroid.results;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;

import com.oprisnik.semdroid.analysis.results.lite.LiteSemdroidReport;
import com.oprisnik.semdroid.utils.FileUtils;

/**
 * Cache for Semdroid reports.
 * Similar to the bitmap caching of http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
 */
public class SemdroidReportCache {

    private static final String TAG = "SemdroidReportCache";

    private static final int DEFAULT_CACHE_SIZE = 8 * 1024 * 1024; // 8MiB;

    private LruCache<String, LiteSemdroidReport> mMemoryCache;

    private SemdroidReportCache(int maxSize) {
        mMemoryCache = new LruCache<String, LiteSemdroidReport>(maxSize);
    }

    public static SemdroidReportCache getInstance(FragmentManager fragmentManager) {
        final RetainFragment mRetainFragment = findOrCreateRetainFragment(fragmentManager);

        SemdroidReportCache imageCache = (SemdroidReportCache) mRetainFragment.getObject();

        if (imageCache == null) {
            imageCache = new SemdroidReportCache(DEFAULT_CACHE_SIZE);
            mRetainFragment.setObject(imageCache);
        }

        return imageCache;
    }

    public void put(String key, LiteSemdroidReport object) {

        synchronized (mMemoryCache) {
            mMemoryCache.put(key, object);
        }
    }

    public LiteSemdroidReport get(String key) {
        LiteSemdroidReport results = null;
        synchronized (mMemoryCache) {
            results = mMemoryCache.get(key);
        }
        if (results == null) {
            try {
                results = (LiteSemdroidReport) FileUtils.loadObjectFromZipFile(key);
                synchronized (mMemoryCache) {
                    if (results != null) {
                        mMemoryCache.put(key, results);
                    }
                }
            } catch (Exception e) {
//                Log.e(TAG, e.getMessage());
            }
        }
        return results;
    }


    /**
     * Locate an existing instance of this Fragment or if not found, create and
     * add it using FragmentManager.
     *
     * @param fm The FragmentManager manager to use.
     * @return The existing instance of the Fragment or the new instance if just
     * created.
     */
    private static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
        //BEGIN_INCLUDE(find_create_retain_fragment)
        // Check to see if we have retained the worker fragment.
        RetainFragment mRetainFragment = (RetainFragment) fm.findFragmentByTag(TAG);

        // If not retained (or first time running), we need to create and add it.
        if (mRetainFragment == null) {
            mRetainFragment = new RetainFragment();
            fm.beginTransaction().add(mRetainFragment, TAG).commitAllowingStateLoss();
        }

        return mRetainFragment;
        //END_INCLUDE(find_create_retain_fragment)
    }

    /**
     * A simple non-UI Fragment that stores a single Object and is retained over configuration
     * changes. It will be used to retain the ImageCache object.
     */
    public static class RetainFragment extends Fragment {
        private Object mObject;

        /**
         * Empty constructor as per the Fragment documentation
         */
        public RetainFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Make sure this Fragment is retained over a configuration change
            setRetainInstance(true);
        }

        /**
         * Store a single object in this Fragment.
         *
         * @param object The object to store
         */
        public void setObject(Object object) {
            mObject = object;
        }

        /**
         * Get the stored object.
         *
         * @return The stored object
         */
        public Object getObject() {
            return mObject;
        }
    }
}
