package com.games.games_project.geneticalgorithm;

import java.util.ArrayList;
import java.util.List;

//@ non_null_by_default
public class PegiExplanationGenerator {

    //@ requires f != null && lvl != null;
    //@ ensures \result != null;
    //@ assignable \nothing;
    public List<String> generate(PegiFeatures f, PegiLevel lvl) {
        List<String> r = new ArrayList<>();
        if (f.violence() > 0.01) r.add("Contains violence");
        if (f.fear() > 0.005) r.add("Includes fear or horror elements");
        if (f.sexual() > 0.003) r.add("Sexual or adult content");
        if (f.addiction() > 0.005) r.add("References to addiction or drugs");
        if (f.language() > 0.002) r.add("Offensive language detected");
        if (f.positivity() > 0.02) r.add("Positive and family-friendly tone");
        if (r.isEmpty()) r.add("General audience");
        return r;
    }
}
