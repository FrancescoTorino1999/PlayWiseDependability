package com.games.games_project.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RatingComparatorTest {

    private RatingComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new RatingComparator();
    }

    @Test
    @DisplayName("Confronto tra rating con priorità diverse (AO < M < T < E10+ < E < K-A)")
    void testDifferentRatingsOrder() {
        assertTrue(comparator.compare("AO", "M") < 0);
        assertTrue(comparator.compare("M", "T") < 0);
        assertTrue(comparator.compare("E", "K-A") < 0);
    }

    @Test
    @DisplayName("Confronto tra rating uguali → restituisce 0")
    void testSameRating() {
        assertEquals(0, comparator.compare("T", "T"));
    }

    @Test
    @DisplayName("Confronto con rating sconosciuto → usa Integer.MAX_VALUE")
    void testUnknownRatings() {
        assertEquals(0, comparator.compare("XYZ", "ABC"));
        assertTrue(comparator.compare("M", "XYZ") < 0);
        assertTrue(comparator.compare("XYZ", "E") > 0);
    }

    @Test
    @DisplayName("Confronto con valori nulli → trattati come sconosciuti")
    void testNullRatings() {
        assertEquals(0, comparator.compare(null, null));
        assertTrue(comparator.compare("M", null) < 0);
        assertTrue(comparator.compare(null, "E") > 0);
    }
}
