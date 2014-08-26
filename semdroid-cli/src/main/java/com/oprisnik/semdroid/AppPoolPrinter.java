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

import com.oprisnik.semdroid.app.App;
import com.oprisnik.semdroid.utils.FileUtils;

import java.io.IOException;
import java.util.List;

/**
 * Prints a given app pool.
 *
 */
public class AppPoolPrinter {

    public static void print(String fileName) {
        System.out.println("Loading app pool from: " + fileName);
        try {
            @SuppressWarnings("unchecked")
            List<App> mAppPool = (List<App>) FileUtils.loadObjectFromFile(fileName);
            for (App a : mAppPool) {
                System.out.println("-------------------------------------------");
                System.out.println(a.toString());
                System.out.println("-------------------------------------------");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done.");
    }

    public static void main(String[] args) {
        print(args[0]);
    }
}
