package com.games.games_project.geneticalgorithm;

public enum PegiLevel {

    PEGI_3(3),
    PEGI_7(7),
    PEGI_12(12),
    PEGI_16(16),
    PEGI_18(18);

    private /*@ spec_public @*/ final int value;

    /*@
      requires v == 3 || v == 7 || v == 12 || v == 16 || v == 18;
      ensures this.value == v;
    @*/
    PegiLevel(int v) {
        this.value = v;
    }

    /*@
      ensures \result == this.value;
      pure
    @*/
    public int getValue() {
        return value;
    }

    /*@
      ensures \result != null;
      pure
    @*/
    public static PegiLevel fromValue(int v) {
        for (PegiLevel p : PegiLevel.values()) {
            if (p.value == v) return p;
        }
        throw new IllegalArgumentException("Invalid PEGI level: " + v);
    }
}
