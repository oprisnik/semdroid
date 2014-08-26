    Copyright 2014 Alexander Oprisnik

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
# Plugins

Semdroid manages several plugins that analyze the contents of Android applications.
The plugins can be based on any static analysis approach.

Some example plugins can be found in [semdroid-plugin-examples](../semdroid-plugin-examples):

* [ListManifestPermissionsPlugin](../semdroid-plugin-examples/src/main/java/com/oprisnik/semdroid/plugin/general/ListManifestPermissionsPlugin.java)
* [ListBroadcastReceiversPlugin](../semdroid-plugin-examples/src/main/java/com/oprisnik/semdroid/plugin/general/ListBroadcastReceiversPlugin.java)
* [MethodCallDetector](../semdroid-plugin-examples/src/main/java/com/oprisnik/semdroid/plugin/general/MethodCallDetector.java)
* [CryptoProviderAnalysis](../semdroid-plugin-examples/src/main/java/com/oprisnik/semdroid/plugin/crypto/CryptoProviderAnalysis.java)
* ...


In [semdroid-plugin-spa](../semdroid-plugin-spa) you can find the implementation of the *Semantic Pattern Analysis*.
This analysis approach is based on machine learning and on the so-called *Semantic Pattern Transformation* developed
by Peter Teufl.

Currently, there are three plugins available that are based on the Semantic Pattern Analysis:

* Detecting custom symmetric cryptography 
([Plugin configuration](../semdroid-server/src/main/webapp/WEB-INF/plugins/semdroid/plugin/custom_symm_crypto/))
* Detecting custom asymmetric cryptography 
([Plugin configuration](../semdroid-server/src/main/webapp/WEB-INF/plugins/semdroid/plugin/custom_asymm_crypto/))
* Detecting SMS broadcast receivers
([Plugin configuration](../semdroid-server/src/main/webapp/WEB-INF/plugins/semdroid/plugin/sms_receiver/))




## Implementing new plugins

In order to create new plugins, you have to implement the [AppAnalysisPlugin]
(../semdroid-core/src/main/java/com/oprisnik/semdroid/analysis/AppAnalysisPlugin.java) interface.
You can also extend the abstract [BaseAnalysisPlugin]
(../semdroid-core/src/main/java/com/oprisnik/semdroid/analysis/BaseAnalysisPlugin.java), 
which already handles some basic tasks for you.

Each plugin receives the parsed Android application (the *Dalvik bytecode* and the *AndroidManifest.xml*).
The plugin then examines the contents of this [App]
(../semdroid-core/src/main/java/com/oprisnik/semdroid/app/App.java) object and labels the application 
according to its functionality.
It is possible to label the whole application, or to only label specific components, like certain
methods.

For example, if the plugin finds a method that performs cryptographic operations, it can label
this method as *CRYPTO*.

The plugin has to return a so-called [AppAnalysisReport]
(../semdroid-core/src/main/java/com/oprisnik/semdroid/analysis/results/AppAnalysisReport.java)
 that contains all findings.
For our crypto example this means that we have to call the ``label`` method in order to add the *CRYPTO*
label to the report:

    AppAnalysisReport report = ... //our report
    DexMethod method = ... //our method
    report.label(method, "CRYPTO); // label the method as CRYPTO
    
### Extending BaseAnalysisPlugin


A very simple plugin that displays all permissions defined in the *AndroidManifest.xml* looks as follows
(see [ListManifestPermissionsPlugin.java](../semdroid-plugin-examples/src/main/java/com/oprisnik/semdroid/plugin/general/ListManifestPermissionsPlugin.java)):

    public class ListManifestPermissionsPlugin extends BaseAnalysisPlugin {
    
        @Override
        public void analyze(AppAnalysisReport report, App app) {
            for (Permission permission : app.getPermissions()) {
                report.label(app, permission.getName()); // add labels to the app according to the permission name
            }
        }
    }

As you can see, we have to implement `public void analyze(AppAnalysisReport report, App app)`.
In the example above, we just label the application according to the defined permissions.


### XML configuration

Plugins can be configured via XML files.
If we take a look at the [MethodCallDetector](../semdroid-plugin-examples/src/main/java/com/oprisnik/semdroid/plugin/general/MethodCallDetector.java),
we can configure the plugin as follows:

    <analysis-plugin class="com.oprisnik.semdroid.plugin.general.MethodCallDetector">
        <name>My plugin</name>

        <class-filter class="com.oprisnik.semdroid.filter.BroadcastReceiverClassFilter" />
        <method-filter class="com.oprisnik.semdroid.filter.OnReceiveMethodFilter" />
        
        ...
        
        <method-call-contains>
            <list>
                <string>crypto</string>
                <string>sms</string>
            </list>
        </method-call-contains>

        ...
        
    </analysis-plugin>
    
For example, we can specify the plugin name, the used class- and method filters 
and certain strings we are interested in.

You can also use this XML config for your own plugins.
All plugins and all classes that implement the 
[Configurable](../semdroid-core/src/main/java/com/oprisnik/semdroid/config/Configurable.java) 
interface can be configured through XML files.
The [Config](../semdroid-core/src/main/java/com/oprisnik/semdroid/config/Config.java) object supplied
 in `public void init(Config config)` can be used to retrieve the values defined in the XML file.

For example, it is possible to get the value of the `<name>` tag by calling:

    String name = config.getProperty("name"); // name = "My plugin"
    
You can also specify a *class* XML attribute, like `<filter class="com.my.SpecialFilter"/>`.
Then, it is possible to create a new instance of the filter by calling

    Filter filter = config.getComponent("filter", Filter.class);
    
The filter will be an instance of *com.my.SpecialFilter*.

It is also possible to define nested configuration information for components:

    <container>
        <filter class="com.oprisnik.semdroid.filter.DefaultMethodFilter">
            <min-opcodes>10</min-opcodes>
        </filter>
    <container>
    
Again, we create the filter by calling

    Filter filter = config.getComponentAndInit("filter", Filter.class);

The method `init(Config config)` of the *DefaultMethodFilter* will be called and we can access
the value of *min-opcodes* by calling

    config.getInt("min-opcodes");


