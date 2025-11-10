package com.games.games_project.geneticalgorithm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PegiLevelTest {

    @Test
    void testValidValues() {
        assertEquals(3, PegiLevel.PEGI_3.getValue());
        assertEquals(7, PegiLevel.PEGI_7.getValue());
        assertEquals(12, PegiLevel.PEGI_12.getValue());
        assertEquals(16, PegiLevel.PEGI_16.getValue());
        assertEquals(18, PegiLevel.PEGI_18.getValue());
    }

    @Test
    void testFromValueValid() {
        assertEquals(PegiLevel.PEGI_3, PegiLevel.fromValue(3));
        assertEquals(PegiLevel.PEGI_7, PegiLevel.fromValue(7));
        assertEquals(PegiLevel.PEGI_12, PegiLevel.fromValue(12));
        assertEquals(PegiLevel.PEGI_16, PegiLevel.fromValue(16));
        assertEquals(PegiLevel.PEGI_18, PegiLevel.fromValue(18));
    }

    @Test
    void testFromValueInvalidThrows() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> PegiLevel.fromValue(10));
        assertTrue(e.getMessage().contains("Invalid PEGI level"));
    }
}
