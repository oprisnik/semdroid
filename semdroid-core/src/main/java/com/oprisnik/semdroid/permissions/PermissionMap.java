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

import com.oprisnik.semdroid.config.BadConfigException;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.config.Configurable;
import com.oprisnik.semdroid.utils.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Android permission map helper using the data provided by
 * http://www.android-permissions.org/
 */
public class PermissionMap implements Configurable {

    public static final String KEY_PERMISSION_MAP_API_CALLS_FILE = "permission-map-file";

    public static final String SEE_INTENT_KEY = "see Intent";

    private static final String TAG = "PermissionMap";
    private Map<String, String> mMap;

    public PermissionMap() {
        mMap = new HashMap<String, String>();
    }

    @Override
    public void init(Config config) throws BadConfigException {
        mMap.clear();
        BufferedReader br = null;
        try {
            InputStream permissionMap = null;
            if (config == null || !config.hasProperty(KEY_PERMISSION_MAP_API_CALLS_FILE)) {
                Log.w(TAG, "Permission map not set up correctly! Ignoring...");
                return;
            } else {
                permissionMap = config.getNestedInputStream(KEY_PERMISSION_MAP_API_CALLS_FILE);
            }

            br = new BufferedReader(new InputStreamReader(permissionMap));
            createMap(br);

        } catch (Exception e) {
            Log.w(TAG, "Permission map not set up correctly! Ignoring...");
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public void createMap(BufferedReader br) throws Exception {
        String data;

        // skip header info... we do not need this
        br.readLine();

        while ((data = br.readLine()) != null) {
            String[] args = data.split("	");
            // System.out.println(args[1]);
            if (args[1].length() < 2) {
                // TODO: also include these permissions
//					System.out.println(args[1] + "=>" + args[2]);
                mMap.put(args[0], SEE_INTENT_KEY);
            } else {
                mMap.put(args[0], args[1]);
            }
        }
    }

    public String getPermission(String apiCall) {
        return mMap.get(apiCall);
    }

    public boolean usesPermission(String apiCall) {
        return mMap.containsKey(apiCall);
    }

}
