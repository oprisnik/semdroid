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

package com.oprisnik.semdroid.plugins;

import com.oprisnik.semdroid.R;
import com.oprisnik.semdroid.plugin.crypto.CryptoProviderAnalysis;
import com.oprisnik.semdroid.plugin.general.ListBroadcastReceiversPlugin;
import com.oprisnik.semdroid.plugin.general.ListManifestPermissionsPlugin;
import com.oprisnik.semdroid.plugin.sms.HasSmsPermissionAnalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Plugin card manager. All used plugins are stored in {@link #DEFAULT_PLUGINS}.
 */
public class PluginCardManager {

    public static final PluginCardManager DEFAULT_PLUGINS = new PluginCardManager();
    private List<PluginCardEntry> mPlugins = new ArrayList<PluginCardEntry>();

    public static void addFromClassName() {

        PluginCardManager.DEFAULT_PLUGINS.addPlugin(
                new PluginCardEntry.Builder()
                        .setNameResId(R.string.plugin_crypto_apis)
                        .setClass(CryptoProviderAnalysis.class)
                        .setTypeList()
                        .build()
        );

        PluginCardManager.DEFAULT_PLUGINS.addPlugin(
                new PluginCardEntry.Builder()
                        .setNameResId(R.string.plugin_broadcast_receivers)
                        .setClass(ListBroadcastReceiversPlugin.class)
                        .setTypeSimpleCount()
                        .setUnitTypeResId(R.plurals.plural_receivers)
                        .build()
        );

        PluginCardManager.DEFAULT_PLUGINS.addPlugin(
                new PluginCardEntry.Builder()
                        .setNameResId(R.string.plugin_declared_permissions)
                        .setClass(ListManifestPermissionsPlugin.class)
                        .setTypeList()
                        .build()
        );


        PluginCardManager.DEFAULT_PLUGINS.addPlugin(
                new PluginCardEntry.Builder()
                        .setNameResId(R.string.plugin_sms_permission)
                        .setClass(HasSmsPermissionAnalysis.class)
                        .setTypeYesNo("YES")
                        .build()
        );
    }

    public static void addFromPath(File dir) {

        PluginCardManager.DEFAULT_PLUGINS.addPlugin(
                new PluginCardEntry.Builder()
                        .setNameResId(R.string.plugin_custom_crypto_symm)
                        .setConfig(new File(dir, "custom_symm_crypto").getAbsolutePath())
                        .setTypeCount("CRYPTO")
                        .setUnitTypeResId(R.plurals.plural_methods)
                        .build()
        );

        PluginCardManager.DEFAULT_PLUGINS.addPlugin(
                new PluginCardEntry.Builder()
                        .setNameResId(R.string.plugin_sms_receiver)
                        .setConfig(new File(dir, "sms_receiver").getAbsolutePath())
                        .setTypeYesNo("SMS_RECEIVER")
                        .build()
        );
    }

    public void addPlugin(PluginCardEntry plugin) {
        mPlugins.add(plugin);
    }

    public void removePlugin(PluginCardEntry plugin) {
        mPlugins.remove(plugin);
    }

    public List<PluginCardEntry> getPlugins() {
        return mPlugins;
    }

    public int size() {
        return mPlugins.size();
    }

    public PluginCardEntry get(int index) {
        return mPlugins.get(index);
    }

}
