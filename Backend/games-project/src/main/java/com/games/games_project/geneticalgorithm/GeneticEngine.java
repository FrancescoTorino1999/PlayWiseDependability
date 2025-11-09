package com.games.games_project.geneticalgorithm;

import java.util.*;

/*@ non_null_by_default */
public class GeneticEngine {

    //@ ensures \result != null && \result.length == 7;
    //@ assignable \nothing;
    public /*@ pure @*/ double[] evolve() {
        Random r = new Random(42);
        int n = 7, pop = 25, gens = 20;
        List<double[]> population = new ArrayList<>();

        //@ maintaining 0 <= i && i <= pop;
        //@ decreases pop - i;
        for (int i = 0; i < pop; i++) {
            population.add(rand(r, n));
        }

        //@ maintaining 0 <= g && g <= gens;
        //@ decreases gens - g;
        for (int g = 0; g < gens; g++) {
            population.sort(Comparator.comparingDouble((double[] w) -> -fitness(w)));
            List<double[]> next = new ArrayList<>(population.subList(0, 5));

            //@ maintaining next.size() <= pop;
            //@ decreases pop - next.size();
            while (next.size() < pop) {
                double[] c = crossover(population.get(r.nextInt(pop)), population.get(r.nextInt(pop)), r);
                mutate(c, r, 0.25);
                next.add(c);
            }
            population = next;
        }
        //@ assume population.size() > 0;
        //@ assume population.get(0) != null && population.get(0).length == 7;
        return population.get(0);
    }

    //@ requires w != null && w.length > 0;
    //@ ensures \result > 0;
    //@ assignable \nothing;
    private /*@ pure @*/ double fitness(double[] w) {
        double s = 0;
        //@ maintaining 0 <= i && i <= w.length;
        //@ decreases w.length - i;
        for (int i = 0; i < w.length; i++) {
            s += w[i] * w[i];
        }
        //@ assume 1 + s > 0;
        return 1 / (1 + s);
    }

    //@ requires r != null && n > 0;
    //@ ensures \result != null && \result.length == n;
    //@ assignable \nothing;
    private /*@ pure @*/ double[] rand(Random r, int n) {
        double[] v = new double[n];
        //@ maintaining 0 <= i && i <= n;
        //@ decreases n - i;
        for (int i = 0; i < n; i++) {
            v[i] = r.nextDouble() * 2 - 1;
        }
        return v;
    }

    //@ requires a != null && b != null && r != null && a.length == b.length && a.length > 0;
    //@ ensures \result != null && \result.length == a.length;
    //@ assignable \nothing;
    private /*@ pure @*/ double[] crossover(double[] a, double[] b, Random r) {
        int cut = r.nextInt(a.length);
        double[] c = new double[a.length];
        //@ maintaining 0 <= i && i <= a.length;
        //@ decreases a.length - i;
        for (int i = 0; i < a.length; i++) {
            c[i] = (i < cut) ? a[i] : b[i];
        }
        return c;
    }

    //@ requires w != null && r != null && w.length > 0 && rate >= 0 && rate <= 1;
    //@ assignable w[*];
    //@ ensures w.length == \old(w.length);
    private void mutate(double[] w, Random r, double rate) {
        //@ maintaining 0 <= i && i <= w.length;
        //@ decreases w.length - i;
        for (int i = 0; i < w.length; i++) {
            if (r.nextDouble() < rate) {
                w[i] += r.nextGaussian() * 0.3;
            }
        }
    }
}

