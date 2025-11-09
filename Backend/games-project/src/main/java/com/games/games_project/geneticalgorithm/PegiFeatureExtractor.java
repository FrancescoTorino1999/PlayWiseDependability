package com.games.games_project.geneticalgorithm;

import java.util.*;

//@ pure
public class PegiFeatureExtractor {

    private static final List<String> VIOLENCE = List.of("blood","kill","violence","fight","weapon","war","explosion","gore","shoot","battle");
    private static final List<String> FEAR = List.of("horror","fear","dark","scary","nightmare","haunted");
    private static final List<String> SEXUAL = List.of("sex","nude","adult","seduce","love");
    private static final List<String> ADDICTION = List.of("gambling","drug","alcohol","casino");
    private static final List<String> LANGUAGE = List.of("fuck","shit","damn","bastard","hell");
    private static final List<String> POSITIVE = List.of("family","friendly","colorful","education","learning","puzzle","fun","cute","cartoon");

    //@ requires text != null;
    //@ ensures \result != null;
    //@ assignable \nothing;
    public PegiFeatures extract(String text) {
        String d = text.toLowerCase(Locale.ROOT);
        int total = Math.max(1, d.split("\\s+").length);
        int v = count(d, VIOLENCE);
        int f = count(d, FEAR);
        int s = count(d, SEXUAL);
        int a = count(d, ADDICTION);
        int l = count(d, LANGUAGE);
        int p = count(d, POSITIVE);
        double ratio = (double)(v + f + s + a + l + 1) / (p + 1);
        return new PegiFeatures((double)v / total, (double)f / total, (double)s / total,
                (double)a / total, (double)l / total, (double)p / total, ratio);
    }

    //@ requires d != null && terms != null;
    //@ ensures \result >= 0;
    //@ assignable \nothing;
    private int count(String d, List<String> terms) {
        int c = 0;
        for (String t : terms) if (d.contains(t)) c++;
        return c;
    }
}
