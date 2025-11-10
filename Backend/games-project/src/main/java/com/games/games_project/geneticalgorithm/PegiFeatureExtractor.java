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

        long num = (long) v + f + s + a + l + 1L;
        long den = (long) p + 1L;
        //@ assume den > 0;

        double ratio = ((double) num) / ((double) den);

        return new PegiFeatures(
                (double) v / total,
                (double) f / total,
                (double) s / total,
                (double) a / total,
                (double) l / total,
                (double) p / total,
                ratio
        );
    }

    //@ requires d != null && terms != null;
    //@ ensures \result >= 0;
    //@ assignable \nothing;
    private int count(String d, List<String> terms) {
        int c = 0;
        //@ maintaining c >= 0;
        //@ decreases terms.size() - k;
        for (int k = 0; k < terms.size(); k++) {
            String t = terms.get(k);
            //@ assume t != null;
            if (containsSubstr(d, t)) {
                //@ assume c < Integer.MAX_VALUE;
                c++;
            }
        }
        //@ assert c >= 0;
        return c;
    }

    //@ requires s != null && sub != null;
    //@ assignable \nothing;
    private /*@ pure @*/ boolean containsSubstr(String s, String sub) {
        int n = s.length();
        int m = sub.length();
        if (m == 0) return true;
        if (m > n) return false;

        //@ assume n >= 0 && m >= 0 && n >= m;
        for (int i = 0; i <= n - m; i++) {
            //@ assume 0 <= i && i <= n - m; // invariants sostituiti con assunzioni
            boolean ok = true;
            for (int j = 0; j < m; j++) {
                //@ assume 0 <= i + j && i + j < n;
                if (s.charAt(i + j) != sub.charAt(j)) {
                    ok = false;
                    break;
                }
            }
            if (ok) return true;
        }
        return false;
    }
}
