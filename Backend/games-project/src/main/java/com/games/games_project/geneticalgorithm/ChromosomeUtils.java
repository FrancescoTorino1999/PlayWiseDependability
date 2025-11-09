package com.games.games_project.geneticalgorithm;

import java.util.Random;

//@ pure
public final class ChromosomeUtils {

    private ChromosomeUtils() {}

    //@ requires genes != null && genes.length > 0;
    //@ ensures \result >= 0;
    public static double computeDiversity(double[] genes) {
        double mean = 0;
        for (double g : genes) mean += g;
        mean /= genes.length;

        double var = 0;
        for (double g : genes) var += (g - mean) * (g - mean);
        return Math.sqrt(var / genes.length);
    }

    //@ requires genes != null;
    //@ ensures \result.length == genes.length;
    public static double[] cloneChromosome(double[] genes) {
        double[] copy = new double[genes.length];
        System.arraycopy(genes, 0, copy, 0, genes.length);
        return copy;
    }

    //@ requires r != null;
    //@ ensures \result >= 0 && \result <= genes.length;
    public static int randomIndex(Random r, double[] genes) {
        return r.nextInt(genes.length);
    }
}
