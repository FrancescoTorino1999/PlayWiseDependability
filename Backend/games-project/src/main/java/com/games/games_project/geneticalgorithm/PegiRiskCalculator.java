package com.games.games_project.geneticalgorithm;

//@ pure
public class PegiRiskCalculator {

    //@ requires f != null && w != null && w.length >= 7;
    //@ ensures \result >= 0;
    //@ assignable \nothing;
    public double compute(PegiFeatures f, double[] w) {
        double s = 0;
        s += w[0] * f.violence() * 14;
        s += w[1] * f.fear() * 10;
        s += w[2] * f.sexual() * 40;
        s += w[3] * f.addiction() * 8;
        s += w[4] * f.language() * 70;
        s -= w[5] * f.positivity() * 5;
        s += w[6] * f.ratio() * 2.5;
        return Math.abs(s);
    }
}
