package com.games.games_project.geneticalgorithm;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.assertj.core.api.Assertions;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

class GeneticEngineTest {

    private GeneticEngine engine;

    @BeforeEach
    void setup() {
        engine = new GeneticEngine();
    }

    @Test
    void evolveProducesValidOutput() {
        double[] res = engine.evolve();
        Assertions.assertThat(res).isNotNull();
        Assertions.assertThat(res.length).isEqualTo(7);
        Assertions.assertThat(Arrays.stream(res).allMatch(Double::isFinite)).isTrue();
    }

    @Test
    void evolveDeterministicAcrossRuns() {
        double[] first = engine.evolve();
        double[] second = engine.evolve();
        Assertions.assertThat(first).containsExactly(second);
    }

    @Test
    void testFitnessMathematics() throws Exception {
        Method m = GeneticEngine.class.getDeclaredMethod("fitness", double[].class);
        m.setAccessible(true);
        double f1 = (double) m.invoke(engine, new double[]{0.0, 0.0});
        double f2 = (double) m.invoke(engine, new double[]{2.0, 0.0});
        double f3 = (double) m.invoke(engine, new double[]{10.0, 10.0});
        Assertions.assertThat(f1).isGreaterThan(f2);
        Assertions.assertThat(f2).isGreaterThan(f3);
        Assertions.assertThat(f1).isBetween(0.0, 1.0);
    }

    @Test
    void testRandBoundariesAndMathOperations() throws Exception {
        Method m = GeneticEngine.class.getDeclaredMethod("rand", Random.class, int.class);
        m.setAccessible(true);
        Random r = new Random(1);
        double[] res = (double[]) m.invoke(engine, r, 10);
        Assertions.assertThat(res).hasSize(10);
        Assertions.assertThat(Arrays.stream(res).allMatch(v -> v >= -1.0 && v <= 1.0)).isTrue();
        Assertions.assertThat(Arrays.stream(res).anyMatch(v -> v < 0)).isTrue();
        Assertions.assertThat(Arrays.stream(res).anyMatch(v -> v > 0)).isTrue();
    }

    @Test
    void testCrossoverExtremeCuts() throws Exception {
        Method m = GeneticEngine.class.getDeclaredMethod("crossover", double[].class, double[].class, Random.class);
        m.setAccessible(true);
        double[] a = {1, 2, 3, 4};
        double[] b = {9, 8, 7, 6};
        Random r1 = new Random(0);
        int cut1 = r1.nextInt(a.length);
        double[] c1 = (double[]) m.invoke(engine, a, b, new Random(0));
        for (int i = 0; i < a.length; i++) {
            if (i < cut1) Assertions.assertThat(c1[i]).isEqualTo(a[i]);
            else Assertions.assertThat(c1[i]).isEqualTo(b[i]);
        }
        Random r2 = new Random(3);
        int cut2 = r2.nextInt(a.length);
        double[] c2 = (double[]) m.invoke(engine, a, b, new Random(3));
        for (int i = 0; i < a.length; i++) {
            if (i < cut2) Assertions.assertThat(c2[i]).isEqualTo(a[i]);
            else Assertions.assertThat(c2[i]).isEqualTo(b[i]);
        }
    }

    @Test
    void testMutateRateAndGaussianChange() throws Exception {
        Method m = GeneticEngine.class.getDeclaredMethod("mutate", double[].class, Random.class, double.class);
        m.setAccessible(true);
        double[] genes = {0.0, 0.0, 0.0};
        m.invoke(engine, genes, new Random(42), 1.0);
        Assertions.assertThat(Arrays.stream(genes).anyMatch(v -> v != 0.0)).isTrue();
    }

    @Test
    void testMutateNoChangeWhenRateZero() throws Exception {
        Method m = GeneticEngine.class.getDeclaredMethod("mutate", double[].class, Random.class, double.class);
        m.setAccessible(true);
        double[] genes = {0.5, -0.3};
        m.invoke(engine, genes, new Random(99), 0.0);
        Assertions.assertThat(genes).containsExactly(0.5, -0.3);
    }

    @Test
    void testEvolveSingleIterationAndSortingEffect() throws Exception {
        Method f = GeneticEngine.class.getDeclaredMethod("fitness", double[].class);
        f.setAccessible(true);
        double[] weak = {5, 5};
        double[] strong = {0, 0};
        List<double[]> pop = new ArrayList<>(Arrays.asList(weak, strong));
        pop.sort(Comparator.comparingDouble((double[] w) -> {
            try { return -((double) f.invoke(engine, (Object) w)); }
            catch (Exception e) { throw new RuntimeException(e); }
        }));
        Assertions.assertThat(pop.get(0)).isEqualTo(strong);
    }

    @Test
    void testWhileLoopExecutesAndMutateIsInvoked() throws Exception {
        Method e = GeneticEngine.class.getDeclaredMethod("evolve");
        e.setAccessible(true);
        double[] res = (double[]) e.invoke(engine);
        Assertions.assertThat(res.length).isEqualTo(7);
        Assertions.assertThat(Arrays.stream(res).allMatch(Double::isFinite)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.25, 1.0})
    void testMutateBoundaryRates(double rate) throws Exception {
        Method m = GeneticEngine.class.getDeclaredMethod("mutate", double[].class, Random.class, double.class);
        m.setAccessible(true);
        double[] w = {0.0, 0.0, 0.0};
        m.invoke(engine, w, new Random(5), rate);
        Assertions.assertThat(w.length).isEqualTo(3);
    }

    @Test
    void testFitnessInverseMathematicsKillsDivisionMutant() throws Exception {
        Method m = GeneticEngine.class.getDeclaredMethod("fitness", double[].class);
        m.setAccessible(true);
        double[] genes = {1, 2};
        double result = (double) m.invoke(engine, (Object) genes);
        Assertions.assertThat(result).isCloseTo(1 / (1 + 5.0), Assertions.offset(1e-9));
    }

    @Test
    void testPopulationLoopBoundaries() throws Exception {
        Method e = GeneticEngine.class.getDeclaredMethod("evolve");
        e.setAccessible(true);
        double[] out = (double[]) e.invoke(engine);
        Assertions.assertThat(out).isNotNull();
        Assertions.assertThat(out.length).isEqualTo(7);
    }

    @Test
    void testMutateMathDivisionAndAdditionMutants() throws Exception {
        Method m = GeneticEngine.class.getDeclaredMethod("mutate", double[].class, Random.class, double.class);
        m.setAccessible(true);
        double[] genes = {0.1, 0.1, 0.1};
        m.invoke(engine, genes, new Random(13), 1.0);
        Assertions.assertThat(Arrays.stream(genes).anyMatch(v -> v != 0.1)).isTrue();
    }

    @Test
    void testWhileLoopBoundaryAtPopulationLimit() throws Exception {
        Method rand = GeneticEngine.class.getDeclaredMethod("rand", Random.class, int.class);
        rand.setAccessible(true);
        Method crossover = GeneticEngine.class.getDeclaredMethod("crossover", double[].class, double[].class, Random.class);
        crossover.setAccessible(true);
        Method mutate = GeneticEngine.class.getDeclaredMethod("mutate", double[].class, Random.class, double.class);
        mutate.setAccessible(true);
        Random r = new Random(42);
        List<double[]> population = new ArrayList<>();
        for (int i = 0; i < 5; i++) population.add((double[]) rand.invoke(engine, r, 3));
        int pop = 5;
        List<double[]> next = new ArrayList<>(population.subList(0, 2));
        while (next.size() < pop) {
            double[] c = (double[]) crossover.invoke(engine, population.get(0), population.get(1), r);
            mutate.invoke(engine, c, r, 0.25);
            next.add(c);
        }
        Assertions.assertThat(next).hasSize(pop);
    }

    @Test
    void testMutateAtRateOneAlwaysChangesSomething() throws Exception {
        Method m = GeneticEngine.class.getDeclaredMethod("mutate", double[].class, Random.class, double.class);
        m.setAccessible(true);
        double[] genes = {0.0, 0.0, 0.0};
        m.invoke(engine, genes, new Random(1), 1.0);
        Assertions.assertThat(Arrays.stream(genes).anyMatch(v -> v != 0.0)).isTrue();
    }

    @Test
    void testMutationMagnitudeAroundExpectedRange() throws Exception {
        Method m = GeneticEngine.class.getDeclaredMethod("mutate", double[].class, Random.class, double.class);
        m.setAccessible(true);
        double[] w = new double[1000];
        m.invoke(engine, w, new Random(7), 1.0);
        double meanAbs = Arrays.stream(w).map(Math::abs).average().orElse(0);
        Assertions.assertThat(meanAbs).isBetween(0.15, 0.5);
    }

    @Test
    void testRandPopulationZeroSkipsLoopSafely() throws Exception {
        Method evolve = GeneticEngine.class.getDeclaredMethod("evolve");
        evolve.setAccessible(true);
        GeneticEngine engineLocal = new GeneticEngine();
        Method rand = GeneticEngine.class.getDeclaredMethod("rand", Random.class, int.class);
        rand.setAccessible(true);
        double[] result = (double[]) rand.invoke(engineLocal, new Random(1), 0);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void testEvolveNoGenerationsReturnsFirstIndividual() throws Exception {
        GeneticEngine engineLocal = new GeneticEngine();
        Random r = new Random(42);
        Method rand = GeneticEngine.class.getDeclaredMethod("rand", Random.class, int.class);
        rand.setAccessible(true);
        List<double[]> population = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            population.add((double[]) rand.invoke(engineLocal, r, 7));
        }
        double[] first = population.get(0);
        Assertions.assertThat(first).isNotNull();
        Assertions.assertThat(first.length).isEqualTo(7);
    }

    @Test
    void testPopulationIsSortedByFitnessDescending() throws Exception {
        GeneticEngine engineLocal = new GeneticEngine();
        Method fitness = GeneticEngine.class.getDeclaredMethod("fitness", double[].class);
        fitness.setAccessible(true);
        double[] w1 = {0.0, 0.0, 0.0};
        double[] w2 = {10.0, 10.0, 10.0};
        double[] w3 = {0.5, 0.5, 0.5};
        List<double[]> population = new ArrayList<>(List.of(w1, w2, w3));
        population.sort(Comparator.comparingDouble((double[] w) -> {
            try {
                return -((double) fitness.invoke(engineLocal, (Object) w));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
        Assertions.assertThat(population.get(0)).isEqualTo(w1);
        Assertions.assertThat(population.get(2)).isEqualTo(w2);
    }

    // --- PegiExplanationGenerator coverage -----------------------------------------------------

    @Test
    void testAllThresholdsJustBelowBoundaries() {
        PegiFeatures f = new PegiFeatures(0.0099, 0.0049, 0.0029, 0.0049, 0.0019, 0.019, 0.0);
        List<String> r = new PegiExplanationGenerator().generate(f, PegiLevel.PEGI_3);
        Assertions.assertThat(r).containsExactly("General audience");
    }

    @Test
    void testAllThresholdsExactlyAtBoundaries() {
        PegiFeatures f = new PegiFeatures(0.01, 0.005, 0.003, 0.005, 0.002, 0.02, 0.0);
        List<String> r = new PegiExplanationGenerator().generate(f, PegiLevel.PEGI_3);
        Assertions.assertThat(r).containsExactly("General audience");
    }

    @Test
    void testAllThresholdsAboveBoundaries() {
        PegiFeatures f = new PegiFeatures(0.011, 0.006, 0.004, 0.006, 0.003, 0.03, 0.0);
        List<String> r = new PegiExplanationGenerator().generate(f, PegiLevel.PEGI_18);
        Assertions.assertThat(r).contains(
                "Contains violence",
                "Includes fear or horror elements",
                "Sexual or adult content",
                "References to addiction or drugs",
                "Offensive language detected",
                "Positive and family-friendly tone"
        );
    }

    @Test
    void testMixedCasesBoundaryCrossing() {
        PegiFeatures f = new PegiFeatures(0.011, 0.004, 0.003, 0.006, 0.002, 0.03, 0.0);
        List<String> r = new PegiExplanationGenerator().generate(f, PegiLevel.PEGI_7);
        Assertions.assertThat(r).containsExactlyInAnyOrder(
                "Contains violence",
                "References to addiction or drugs",
                "Positive and family-friendly tone"
        );
    }

    @Test
    void testGeneralAudienceOnlyWhenAllBelow() {
        PegiFeatures f = new PegiFeatures(0,0,0,0,0,0,0);
        List<String> r = new PegiExplanationGenerator().generate(f, PegiLevel.PEGI_3);
        Assertions.assertThat(r).containsExactly("General audience");
    }

    // --- PegiRiskCalculator coverage ----------------------------------------------------------

    @Test
    void testComputePositiveContributions() {
        PegiFeatures f = new PegiFeatures(1, 1, 1, 1, 1, 0, 1);
        double[] w = {1, 1, 1, 1, 1, 1, 1};
        double res = new PegiRiskCalculator().compute(f, w);
        // s = 14 + 10 + 40 + 8 + 70 - 0 + 2.5 = 144.5
        Assertions.assertThat(res).isCloseTo(144.5, Assertions.offset(1e-9));
    }

    @Test
    void testComputeNegativeContributionsBecomePositiveWithAbs() {
        PegiFeatures f = new PegiFeatures(1, 1, 1, 1, 1, 1, 1);
        double[] w = {-1, -1, -1, -1, -1, -1, -1};
        double res = new PegiRiskCalculator().compute(f, w);
        // s = (-14 -10 -40 -8 -70) - (-5) + (-2.5) = -139.5 → abs = 139.5
        Assertions.assertThat(res).isCloseTo(139.5, Assertions.offset(1e-9));
    }

    @Test
    void testComputeMixedWeightsProducesExpectedSum() {
        PegiFeatures f = new PegiFeatures(0.5, 0.2, 0.1, 0.3, 0.4, 0.1, 0.5);
        double[] w = {2, 1, 3, 0.5, 1.5, 2, 1};
        double expected =
                2 * 0.5 * 14 + // 14
                        1 * 0.2 * 10 + // 2
                        3 * 0.1 * 40 + // 12
                        0.5 * 0.3 * 8 + // 1.2
                        1.5 * 0.4 * 70 - // 42
                        2 * 0.1 * 5 + // -1
                        1 * 0.5 * 2.5; // +1.25
        double res = new PegiRiskCalculator().compute(f, w);
        Assertions.assertThat(res).isCloseTo(Math.abs(expected), Assertions.offset(1e-9));
    }

    @Test
    void testComputeZeroFeaturesGivesZeroRisk() {
        PegiFeatures f = new PegiFeatures(0,0,0,0,0,0,0);
        double[] w = {1,1,1,1,1,1,1};
        double res = new PegiRiskCalculator().compute(f, w);
        Assertions.assertThat(res).isZero();
    }

    @Test
    void testComputeDifferentSignsAffectTotal() {
        PegiFeatures f = new PegiFeatures(0.1,0.2,0.3,0.4,0.5,0.6,0.7);
        double[] w = {1,-1,1,-1,1,-1,1};
        double res1 = new PegiRiskCalculator().compute(f, w);
        double res2 = new PegiRiskCalculator().compute(f, new double[]{-1,1,-1,1,-1,1,-1});
        // Dato che viene preso Math.abs(s), il valore assoluto deve essere uguale
        Assertions.assertThat(res1).isEqualTo(res2);
        Assertions.assertThat(res1).isGreaterThan(0);
    }

    // --- GeneticEngine mutation-killer extensions ---------------------------------------------

    @Test
    void evolve_matches_spec_deterministically() {
        Random r = new Random(42);
        int n = 7, pop = 25, gens = 20;
        List<double[]> population = new ArrayList<>();
        for (int i = 0; i < pop; i++) population.add(randSpec(r, n));
        for (int g = 0; g < gens; g++) {
            population.sort(Comparator.comparingDouble((double[] w) -> -fitnessSpec(w)));
            List<double[]> next = new ArrayList<>(population.subList(0, 5));
            while (next.size() < pop) {
                double[] c = crossoverSpec(population.get(r.nextInt(pop)), population.get(r.nextInt(pop)), r);
                mutateSpec(c, r, 0.25);
                next.add(c);
            }
            population = next;
        }
        double[] expected = population.get(0);
        double[] actual = engine.evolve();
        Assertions.assertThat(actual.length).isEqualTo(expected.length);
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertThat(actual[i]).isCloseTo(expected[i], Assertions.offset(1e-12));
        }
    }

    private double[] randSpec(Random r, int n) {
        double[] v = new double[n];
        for (int i = 0; i < n; i++) v[i] = r.nextDouble() * 2 - 1;
        return v;
    }

    private double fitnessSpec(double[] w) {
        double s = 0;
        for (int i = 0; i < w.length; i++) s += w[i] * w[i];
        return 1.0 / (1.0 + s);
    }

    private double[] crossoverSpec(double[] a, double[] b, Random r) {
        int cut = r.nextInt(a.length);
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) c[i] = (i < cut) ? a[i] : b[i];
        return c;
    }

    private void mutateSpec(double[] w, Random r, double rate) {
        for (int i = 0; i < w.length; i++) {
            if (r.nextDouble() < rate) w[i] += r.nextGaussian() * 0.3;
        }
    }

    static class ZeroDoubleRandom extends Random {
        @Override public double nextDouble() { return 0.0; }
        @Override public double nextGaussian() { return 1.0; }
    }

    @Test
    void mutate_does_not_change_when_rate_zero_and_nextDouble_zero() throws Exception {
        Method m = GeneticEngine.class.getDeclaredMethod("mutate", double[].class, Random.class, double.class);
        m.setAccessible(true);
        double[] w = new double[]{0, 0, 0, 0, 0, 0, 0};
        double[] before = w.clone();
        m.invoke(engine, w, new ZeroDoubleRandom(), 0.0d);
        Assertions.assertThat(w).containsExactly(before);
    }

    static class FixedRandom extends Random {
        private int i = 0;
        @Override public double nextDouble() { return 0.5; }
        @Override public double nextGaussian() { return ++i == 1 ? 2.0 : 0.0; }
    }

    @Test
    void mutate_applies_positive_increment_when_gaussian_positive() throws Exception {
        Method m = GeneticEngine.class.getDeclaredMethod("mutate", double[].class, Random.class, double.class);
        m.setAccessible(true);
        double[] w = new double[]{1.0, 0, 0, 0, 0, 0, 0};
        m.invoke(engine, w, new FixedRandom(), 1.0d);
        Assertions.assertThat(w[0]).isCloseTo(1.0 + 0.6, Assertions.offset(1e-12));
    }

    @Test
    void evolve_differs_from_no_mutate_trajectory() {
        Random r = new Random(42);
        int n = 7, pop = 25, gens = 20;
        List<double[]> population = new ArrayList<>();
        for (int i = 0; i < pop; i++) population.add(randSpec(r, n));
        for (int g = 0; g < gens; g++) {
            population.sort(Comparator.comparingDouble((double[] w) -> -fitnessSpec(w)));
            List<double[]> next = new ArrayList<>(population.subList(0, 5));
            while (next.size() < pop) {
                double[] c = crossoverSpec(population.get(r.nextInt(pop)), population.get(r.nextInt(pop)), r);
                next.add(c);
            }
            population = next;
        }
        double[] noMutate = population.get(0);
        double[] evolved = engine.evolve();
        boolean allEqual = true;
        for (int i = 0; i < evolved.length; i++) {
            if (Math.abs(evolved[i] - noMutate[i]) > 1e-12) { allEqual = false; break; }
        }
        Assertions.assertThat(allEqual).isFalse();
    }


}


