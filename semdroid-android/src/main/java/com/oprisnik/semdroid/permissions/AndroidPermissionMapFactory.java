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

package com.oprisnik.semdroid.permissions;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Android-specific permission map factory since we load the permission map from the assets folder.
 */
public class AndroidPermissionMapFactory extends PermissionMapFactory {

    private static final String TAG = "AndroidPermissionMapFactory";

    private PermissionMap mPermissionMap;

    public AndroidPermissionMapFactory(Context context) {
        mPermissionMap = new PermissionMap();
        InputStream is = null;

        try {
            is = context.getAssets().open("config" + File.separator +
                    "permissionmap" + File.separator + "APICalls.txt");
            mPermissionMap.createMap(new BufferedReader(new InputStreamReader(is)));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override
    public PermissionMap generate() {
        return mPermissionMap;
    }
}
