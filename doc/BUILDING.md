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

# How to build Semdroid

Semdroid can be used locally on your PC (CLI), on an Android device, or on a Server.
This project is Gradle-based. There are several Gradle tasks available that can be used to
compile, deploy, and test Semdroid.

You can import the project in Android Studio or IntelliJ IDEA or build the modules using the Gradle wrapper.

## Building the Semdroid CLI

The **semdroid-cli** module is used to create a command line version of Semdroid.
In order to build the CLI version, run the following command:

```
./gradlew :semdroid-cli:installApp 
```

This creates all the required files in *semdroid-cli/build/install*.
Alternatively, you can use

```
./gradlew :semdroid-cli:distZip
```

to create a .zip file in *semdroid-cli/build/distributions*.


## Building the Android application

**semdroid-android** is the main module for the Android application.

In order to build a debuggable version of Semdroid, run:

```
./gradlew :semdroid-android:assembleDebug
```

The release .apk can be assembled by calling:

```
./gradlew :semdroid-android:assembleRelease
```

The signing configuration for the release version has to be in *distribution/signing.properties*.
Example *signing.properties*:
    
    keystore=path/to/your/keystore
    keystore.password=password
    keyAlias=yourAlias
    keyPassword=password

The .apk files will be in *semdroid-android/build/outputs/apk*.

## Building the server

The **semdroid-server** module can be used to create a web application that can be deployed onto a
server.

Run the following command to create a .war file in *semdroid-server/build/libs*:

```
:semdroid-server:war
```

