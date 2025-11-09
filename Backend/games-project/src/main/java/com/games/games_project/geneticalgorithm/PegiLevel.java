package com.games.games_project.geneticalgorithm;

//@ immutable
public enum PegiLevel {
    PEGI_3(3), PEGI_7(7), PEGI_12(12), PEGI_16(16), PEGI_18(18);
    private final int value;
    PegiLevel(int v){this.value=v;}
    //@ ensures \result == value;
    //@ assignable \nothing;
    public int getValue(){return value;}
}
