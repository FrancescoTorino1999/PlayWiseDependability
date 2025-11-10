package com.games.games_project.benchmark;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {

        // 🔧 Imposta dinamicamente la classpath (workaround per exec:java)
        URLClassLoader cl = (URLClassLoader) BenchmarkRunner.class.getClassLoader();
        StringBuilder cp = new StringBuilder();
        for (URL url : cl.getURLs()) {
            cp.append(url.getPath()).append(File.pathSeparator);
        }
        System.setProperty("java.class.path", cp.toString());

        // 🚀 Avvia JMH
        org.openjdk.jmh.Main.main(args);
    }
}
