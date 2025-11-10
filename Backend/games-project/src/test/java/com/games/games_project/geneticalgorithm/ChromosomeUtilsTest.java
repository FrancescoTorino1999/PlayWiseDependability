package com.games.games_project.geneticalgorithm;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.assertj.core.api.Assertions;

import java.lang.reflect.Constructor;
import java.util.Random;
import java.util.stream.Stream;

class ChromosomeUtilsTest {

    @ParameterizedTest
    @MethodSource("diversityCases")
    void testComputeDiversityProducesExpectedResult(double[] genes, double expected, boolean allowZero) {
        double diversity = ChromosomeUtils.computeDiversity(genes);

        // Caso limite: array vuoto → restituisce NaN o Infinity ma non deve crashare
        if (genes.length == 0) {
            Assertions.assertThat(Double.isFinite(diversity) || Double.isNaN(diversity)).isTrue();
            return;
        }

        // Non deve essere NaN o negativo
        Assertions.assertThat(diversity).isNotNaN();
        Assertions.assertThat(diversity).isGreaterThanOrEqualTo(0);

        if (!allowZero)
            Assertions.assertThat(diversity).isGreaterThan(0);

        Assertions.assertThat(diversity).isCloseTo(expected, Assertions.offset(1e-9));
    }

    // 🔹 Deve essere static per essere visibile al MethodSource
    static Stream<Arguments> diversityCases() {
        return Stream.of(
                Arguments.of(new double[]{1, 1, 1}, 0.0, true),
                Arguments.of(new double[]{1, 2, 3}, Math.sqrt(2.0 / 3.0), false),
                Arguments.of(new double[]{-1, 1}, 1.0, false),
                Arguments.of(new double[]{}, 0.0, true)
        );
    }

    @Test
    void testComputeDiversityNumericAccuracy() {
        double[] genes = {1.0, 3.0, 5.0};
        double expectedMean = (1 + 3 + 5) / 3.0;
        double expectedVar = ((1 - expectedMean) * (1 - expectedMean)
                + (3 - expectedMean) * (3 - expectedMean)
                + (5 - expectedMean) * (5 - expectedMean)) / 3.0;
        double expected = Math.sqrt(expectedVar);
        double result = ChromosomeUtils.computeDiversity(genes);
        Assertions.assertThat(result).isEqualTo(expected);
    }

    @Test
    void testCloneChromosomeProducesExactCopy() {
        double[] genes = {0.1, 0.2, 0.3};
        double[] copy = ChromosomeUtils.cloneChromosome(genes);
        Assertions.assertThat(copy).isNotSameAs(genes);
        Assertions.assertThat(copy).containsExactly(genes);
        copy[0] = 99;
        Assertions.assertThat(copy[0]).isNotEqualTo(genes[0]);
    }

    @Test
    void testCloneChromosomeEmptyArray() {
        double[] genes = {};
        double[] copy = ChromosomeUtils.cloneChromosome(genes);
        Assertions.assertThat(copy).isEmpty();
    }

    @Test
    void testRandomIndexWithinBoundsMultipleRuns() {
        Random r = new Random(42);
        double[] genes = new double[5];
        boolean hasNonZero = false;
        for (int i = 0; i < 100; i++) {
            int idx = ChromosomeUtils.randomIndex(r, genes);
            Assertions.assertThat(idx).isBetween(0, genes.length - 1);
            if (idx != 0) hasNonZero = true;
        }
        Assertions.assertThat(hasNonZero).isTrue(); // uccide "return 0" mutant
    }

    @Test
    void testRandomIndexWithSingleGeneAlwaysZero() {
        Random r = new Random(10);
        double[] genes = new double[1];
        int idx = ChromosomeUtils.randomIndex(r, genes);
        Assertions.assertThat(idx).isZero();
    }

    @Test
    void testPrivateConstructorAccessible() throws Exception {
        Constructor<ChromosomeUtils> c = ChromosomeUtils.class.getDeclaredConstructor();
        c.setAccessible(true);
        ChromosomeUtils instance = c.newInstance();
        Assertions.assertThat(instance).isNotNull();
    }

    @Test
    void testComputeDiversityHandlesNegativeValues() {
        double[] genes = {-5, -3, -1};
        double result = ChromosomeUtils.computeDiversity(genes);
        Assertions.assertThat(result).isEqualTo(Math.sqrt(8.0 / 3.0));
    }

    @Test
    void testVarianceFormulaSensitiveToSign() {
        // Caso asimmetrico: un valore molto grande e uno piccolo
        double[] genes = {10.0, 1.0};
        double diversity = ChromosomeUtils.computeDiversity(genes);

        // Se si usasse (g + mean) anziché (g - mean), il risultato crescerebbe enormemente
        // quindi deve restare nell'ordine di grandezza atteso (~4.5)
        Assertions.assertThat(diversity).isLessThan(10.0);
        Assertions.assertThat(diversity).isCloseTo(Math.sqrt(20.25), Assertions.offset(1e-9));
    }

}
