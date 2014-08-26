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

import com.oprisnik.semdroid.analysis.results.SemdroidReport;
import com.oprisnik.semdroid.config.Config;
import com.oprisnik.semdroid.config.ConfigFactory;
import com.oprisnik.semdroid.training.TrainingHelper;
import com.oprisnik.semdroid.utils.XmlUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command line interface main.
 *
 */
public class Main {


    private static final Map<String, Command> COMMAND_MAP = new HashMap<String, Command>();


    public static void usage() {
        System.out.println("How to use Semdroid: ");
        System.out.println("----------");
        for (Command c : COMMAND_MAP.values()) {
            c.printHelp();
            System.out.println("----------");
        }
    }

    public static void initCommands() {
        Command CMD_ANALYZE = new Command("analyze", 2) {
            @Override
            public void printHelp() {
                System.out.println("Analyze a given application or a folder:");
                System.out.println("    analyze path/to/plugin1.config ... path/to/pluginN.config fileOrFolderToAnalyze");
                System.out.println("    analyze -plugin-folder path/to/plugin/folder fileOrFolderToAnalyze");
            }

            @Override
            protected void run(Config semdroidConfig, Config cliConfig, String[] args) throws Exception {
                Config analysisConfig = cliConfig.getSubconfig(CliConfig.Analysis.SUBCONFIG_TAG);
                long start = System.currentTimeMillis();
                SemdroidAnalyzer semdroid = new SemdroidAnalyzer();

                semdroid.init(semdroidConfig);
                if (args[0].equalsIgnoreCase("-plugin-folder")) {
                    File folder = new File(args[1]);
                    for (File analysis : folder.listFiles()) {
                        semdroid.addAnalysisPlugin(analysis);
                    }
                } else {
                    for (int i = 0; i < args.length - 1; i++) {
                        File file = new File(args[i]);
                        semdroid.addAnalysisPlugin(file);
                    }
                }
                long initDone = System.currentTimeMillis();
                // file / folder to analyze is last arg
                File app = new File(args[args.length - 1]);
                List<SemdroidReport> results = AnalysisMain.analyze(semdroid, app, false);
                long analysisDone = System.currentTimeMillis();
                File resultsFolder = new File(analysisConfig.getProperty(CliConfig.Analysis.RESULTS));
                resultsFolder.mkdirs();
                XmlUtils.reportsToXMLAndTransform(results,
                        new File(resultsFolder, "analysis-" + app.getName() + ".xml"),
                        true, new FileOutputStream(new File(resultsFolder,
                                "analysis-" + app.getName() + ".html")),
                        analysisConfig.getNestedInputStream(CliConfig.Analysis.XSL));
                long saveDone = System.currentTimeMillis();

                System.out.println("Time: Total: " +
                        ((saveDone - start)) + "ms. Init: " +
                        ((initDone - start)) + "ms. Analysis: " +
                        ((analysisDone - initDone)) + "ms. Saving: " +
                        ((saveDone - analysisDone)) + "ms.");
                long components = 0;
                for (SemdroidReport r : results) {
                    components += r.getComponentCount();
                }
                System.out.println("Components: " + components);
            }
        };

        Command CMD_TRAIN = new Command("train", 2) {

            @Override
            public void printHelp() {
                System.out.println("Create a new plugin:");
                System.out.println("    train path/to/training.config appFolder");
            }

            @Override
            protected void run(Config semdroidConfig, Config cliConfig, String[] args) throws Exception {
                File file = new File(args[0]);
                File appFolder = new File(args[1]);
                if (file.isDirectory()) {
                    file = new File(file, DefaultValues.TRAINING_CONFIG_FILE_NAME);
                    // if it does not exist, .fromFile throws an exception
                }
                Config trainingConfig = ConfigFactory.fromFile(file);
                TrainingHelper.train(semdroidConfig, trainingConfig, appFolder);
            }
        };


        Command CMD_EVALUATE = new Command("evaluate", 3) {

            @Override
            public void printHelp() {
                System.out.println("Evaluate plugins:");
                System.out.println("    evaluate path/to/expectedResultsPlugin.config path/to/plugin1.config ... path/to/pluginN.config fileOrFolderToAnalyze");
            }

            @Override
            protected void run(Config semdroidConfig, Config cliConfig, String[] args) throws Exception {
                int size = args.length - 2;
                int analysisStart = 1;
                String evaluationPlugin = args[0];
                String normalLabel = null;
                if (args[0].equalsIgnoreCase("-normalLabel")) {
                    normalLabel = args[1];
                    evaluationPlugin = args[2];
                    size -= 2;
                    analysisStart += 2;
                }
                String[] plugins = new String[size];

                for (int i = analysisStart; i < args.length - 1; i++) {
                    plugins[i - analysisStart] = args[i];
                }
                EvaluationHelper.evaluate(semdroidConfig,
                        cliConfig.getSubconfig(CliConfig.Evaluation.SUBCONFIG_TAG),
                        evaluationPlugin, plugins, args[args.length - 1], normalLabel);
            }
        };

        Command CMD_HELP = new Command("help", 0) {
            @Override
            public void printHelp() {
                System.out.println("Display this help:");
                System.out.println("    help");
            }

            @Override
            protected void run(Config semdroidConfig, Config cliConfig, String[] args) throws Exception {
                usage();
            }
        };
    }

    public static void main(String[] args) {
        initCommands();
        try {
            if (args.length < 1) {
                usage();
                return;
            }

            String cmd = args[0];
            Command command = COMMAND_MAP.get(cmd);
            if (command == null) {
                usage();
                return;
            }
            String[] cmdargs = null;
            if (args.length - 1 > 0) {
                cmdargs = new String[args.length - 1];
                System.arraycopy(args, 1, cmdargs, 0, cmdargs.length);
            }
            // load semdroid config
            Config semdroidConfig = CliConfig.getSemdroidConfig();
            // load cli config
            Config cliConfig = CliConfig.getCliConfig();

            // run the command
            command.checkAndRun(semdroidConfig, cliConfig, cmdargs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static abstract class Command {
        private String mName;
        private int mMinArgs;

        public Command(String name, int minArgs) {
            this.mName = name;
            this.mMinArgs = minArgs;
            // register our command
            COMMAND_MAP.put(name, this);
        }

        public void checkAndRun(Config semdroidConfig, Config cliConfig, String[] args) throws Exception {
            if ((args == null && mMinArgs > 0) ||
                    args.length < mMinArgs) {
                printHelp();
                return;
            }
            run(semdroidConfig, cliConfig, args);
        }

        public abstract void printHelp();

        protected abstract void run(Config semdroidConfig, Config cliConfig, String[] args) throws Exception;
    }
}
