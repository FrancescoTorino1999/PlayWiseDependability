package com.games.games_project.geneticalgorithm;

import java.util.*;

/*@ non_null_by_default */
public class GeneticEngine {

    //@ ensures \result != null && \result.length == 7;
    //@ assignable \everything;
    public double[] evolve() {
        Random r = new Random(42);
        int n = 7, pop = 25, gens = 20;
        List<double[]> population = new ArrayList<>();

        for (int i = 0; i < pop; i++) {
            population.add(rand(r, n));
        }

        for (int g = 0; g < gens; g++) {
            //@ assume population != null && !population.isEmpty();
            population.sort(Comparator.comparingDouble((double[] w) -> -fitness(w)));
            List<double[]> next = new ArrayList<>(population.subList(0, 5));

            //@ maintaining next.size() <= pop;
            while (next.size() < pop) {
                double[] pa = population.get(r.nextInt(pop));
                double[] pb = population.get(r.nextInt(pop));
                //@ assume pa != null && pb != null && pa.length == pb.length && pa.length > 0;
                double[] c = crossover(pa, pb, r);
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
    private double fitness(double[] w) {
        double s = 0;
        for (int i = 0; i < w.length; i++) {
            //@ assume 0 <= i && i < w.length;
            s += w[i] * w[i];
        }
        //@ assume s >= 0 && 1 + s > 0;
        return 1 / (1 + s);
    }

    //@ requires r != null && n > 0;
    //@ ensures \result != null && \result.length == n;
    //@ assignable \everything;
    private double[] rand(Random r, int n) {
        double[] v = new double[n];
        for (int i = 0; i < n; i++) {
            //@ assume 0 <= i && i < n;
            v[i] = r.nextDouble() * 2 - 1;
        }
        return v;
    }

    //@ requires a != null && b != null && r != null && a.length == b.length && a.length > 0;
    //@ ensures \result != null && \result.length == a.length;
    //@ assignable \everything;
    private double[] crossover(double[] a, double[] b, Random r) {
        int cut = r.nextInt(a.length);
        //@ assume 0 <= cut && cut < a.length;
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            //@ assume 0 <= i && i < a.length;
            c[i] = (i < cut) ? a[i] : b[i];
        }
        return c;
    }

    //@ requires w != null && r != null && w.length > 0 && rate >= 0 && rate <= 1;
    //@ assignable \everything;
    //@ ensures w.length == \old(w.length);
    private void mutate(double[] w, Random r, double rate) {
        for (int i = 0; i < w.length; i++) {
            //@ assume 0 <= i && i < w.length;
            if (r.nextDouble() < rate) {
                w[i] += r.nextGaussian() * 0.3;
            }
        }
    }
}
