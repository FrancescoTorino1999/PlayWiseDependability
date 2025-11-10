package com.games.games_project.geneticalgorithm;

import org.junit.jupiter.api.*;
import org.assertj.core.api.Assertions;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PegiFeatureExtractorTest {

    private final PegiFeatureExtractor extractor = new PegiFeatureExtractor();

    @Test
    void extract_counts_each_category_exactly() {
        String text = "Blood fight horror sex drug fuck family fun";
        PegiFeatures f = extractor.extract(text);
        double total = text.toLowerCase().split("\\s+").length;
        int v = 2, fear = 1, sexual = 1, addiction = 1, lang = 1, pos = 2;
        double ratio = (double)(v + fear + sexual + addiction + lang + 1) / (pos + 1);
        Assertions.assertThat(f.violence()).isEqualTo(v / total);
        Assertions.assertThat(f.fear()).isEqualTo(fear / total);
        Assertions.assertThat(f.sexual()).isEqualTo(sexual / total);
        Assertions.assertThat(f.addiction()).isEqualTo(addiction / total);
        Assertions.assertThat(f.language()).isEqualTo(lang / total);
        Assertions.assertThat(f.positivity()).isEqualTo(pos / total);
        Assertions.assertThat(f.ratio()).isCloseTo(ratio, Assertions.offset(1e-12));
    }

    @Test
    void extract_zero_positive_increases_ratio() {
        String text = "blood kill drug";
        PegiFeatures f = extractor.extract(text);
        double total = 3.0;
        double expectedRatio = (3 + 0 + 1) / (0 + 1.0);
        Assertions.assertThat(f.ratio()).isCloseTo(expectedRatio, Assertions.offset(1e-12));
    }

    @Test
    void extract_positive_high_lowers_ratio() {
        String text = "family friendly cartoon fun";
        PegiFeatures f = extractor.extract(text);
        double total = 4.0;
        double expectedRatio = (0 + 0 + 0 + 0 + 0 + 1) / (4 + 1.0);
        Assertions.assertThat(f.ratio()).isCloseTo(expectedRatio, Assertions.offset(1e-12));
    }

    @Test
    void extract_handles_mixed_case_and_unrelated_words() {
        String text = "This GAME has Blood and Friendly fights but also EDUCATION";
        PegiFeatures f = extractor.extract(text);
        Assertions.assertThat(f.violence()).isGreaterThan(0);
        Assertions.assertThat(f.positivity()).isGreaterThan(0);
        Assertions.assertThat(f.ratio()).isGreaterThan(0);
    }

    @Test
    void count_returns_expected_values_for_known_terms() throws Exception {
        Method m = PegiFeatureExtractor.class.getDeclaredMethod("count", String.class, List.class);
        m.setAccessible(true);
        String d = "blood fight war peace love cartoon";
        int v = (int) m.invoke(extractor, d, List.of("blood","fight","war"));
        int p = (int) m.invoke(extractor, d, List.of("peace","cartoon"));
        Assertions.assertThat(v).isEqualTo(3);
        Assertions.assertThat(p).isEqualTo(2);
    }

    @Test
    void count_returns_zero_when_no_terms_present() throws Exception {
        Method m = PegiFeatureExtractor.class.getDeclaredMethod("count", String.class, List.class);
        m.setAccessible(true);
        int c = (int) m.invoke(extractor, "no keywords here", List.of("violence","blood"));
        Assertions.assertThat(c).isZero();
    }

    @Test
    void extract_ratio_math_changes_if_arithmetic_mutated() {
        String text = "blood fear sex drug hell"; // una parola per ogni categoria, nessuna positiva
        PegiFeatures f = extractor.extract(text);
        double total = 5.0;
        int v = 1, fe = 1, s = 1, a = 1, l = 1, p = 0;
        double expectedRatio = (double)(v + fe + s + a + l + 1) / (p + 1);
        Assertions.assertThat(f.ratio())
                .isCloseTo(expectedRatio, Assertions.offset(1e-12))
                .isGreaterThan(1.0)
                .isLessThan(10.0);
    }

    @Test
    void testContainsSubstr_edgeCases() throws Exception {
        PegiFeatureExtractor extractor = new PegiFeatureExtractor();

        // Caso 1: sottostringa vuota → deve tornare true
        Method m = PegiFeatureExtractor.class.getDeclaredMethod("containsSubstr", String.class, String.class);
        m.setAccessible(true);
        boolean resultEmpty = (boolean) m.invoke(extractor, "abc", "");
        assertTrue(resultEmpty);

        // Caso 2: sottostringa più lunga del testo → deve tornare false
        boolean resultLong = (boolean) m.invoke(extractor, "hi", "hello");
        assertFalse(resultLong);
    }

}
