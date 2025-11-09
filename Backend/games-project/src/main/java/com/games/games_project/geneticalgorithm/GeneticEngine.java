package com.games.games_project.geneticalgorithm;

import java.util.*;

//@ pure
public class GeneticEngine {

    //@ ensures \result != null && \result.length == 7;
    //@ assignable \nothing;
    public double[] evolve() {
        Random r = new Random(42);
        int n = 7, pop = 25, gens = 20;
        List<double[]> population = new ArrayList<>();
        for (int i = 0; i < pop; i++) population.add(rand(r, n));
        for (int g = 0; g < gens; g++) {
            population.sort(Comparator.comparingDouble((double[] w) -> -fitness(w)));
            List<double[]> next = new ArrayList<>(population.subList(0, 5));
            while (next.size() < pop) {
                double[] c = crossover(population.get(r.nextInt(pop)), population.get(r.nextInt(pop)), r);
                mutate(c, r, 0.25);
                next.add(c);
            }
            population = next;
        }
        return population.get(0);
    }

    //@ requires w != null;
    //@ ensures \result > 0;
    private double fitness(double[] w) {
        double s = 0;
        for (double v : w) s += v * v;
        return 1 / (1 + s);
    }

    //@ requires r != null && n > 0;
    //@ ensures \result.length == n;
    private double[] rand(Random r, int n) {
        double[] v = new double[n];
        for (int i = 0; i < n; i++) v[i] = r.nextDouble() * 2 - 1;
        return v;
    }

    //@ requires a != null && b != null && a.length == b.length;
    //@ ensures \result.length == a.length;
    private double[] crossover(double[] a, double[] b, Random r) {
        int cut = r.nextInt(a.length);
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) c[i] = (i < cut) ? a[i] : b[i];
        return c;
    }

    //@ requires w != null && rate >= 0 && rate <= 1;
    private void mutate(double[] w, Random r, double rate) {
        for (int i = 0; i < w.length; i++)
            if (r.nextDouble() < rate)
                w[i] += r.nextGaussian() * 0.3;
    }

}
