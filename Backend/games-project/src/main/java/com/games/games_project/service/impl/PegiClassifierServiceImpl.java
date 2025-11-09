//@ non_null_by_default
package com.games.games_project.service.impl;

import com.games.games_project.dto.PegiResponseDto;
import com.games.games_project.service.PegiClassifierService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PegiClassifierServiceImpl implements PegiClassifierService {

    private enum PegiLevel {
        PEGI_3(3), PEGI_7(7), PEGI_12(12), PEGI_16(16), PEGI_18(18);
        private final int value;
        PegiLevel(int v){this.value=v;}
        //@ ensures \result == value;
        //@ assignable \nothing;
        public int getValue(){return value;}
    }

    private static final List<String> VIOLENCE = List.of("blood","kill","violence","fight","weapon","war","explosion","gore","shoot","battle");
    private static final List<String> FEAR = List.of("horror","fear","dark","scary","nightmare","haunted");
    private static final List<String> SEXUAL = List.of("sex","nude","adult","seduce","love");
    private static final List<String> ADDICTION = List.of("gambling","drug","alcohol","casino");
    private static final List<String> LANGUAGE = List.of("fuck","shit","damn","bastard","hell");
    private static final List<String> POSITIVE = List.of("family","friendly","colorful","education","learning","puzzle","fun","cute","cartoon");

    private record Features(double violence,double fear,double sexual,double addiction,double language,double positivity,double ratio){}

    //@ requires text != null;
    //@ ensures \result != null;
    //@ assignable \nothing;
    public PegiResponseDto estimatePegiDetailed(String text) {
        Features f = extractFeatures(text);
        double[] weights = evolveWeights();
        double risk = computeRisk(f, weights);
        PegiLevel level = toPegi(risk, text);
        List<String> reasons = explain(f, level);
        Map<String, Double> map = Map.of(
                "violence", f.violence(),
                "fear", f.fear(),
                "sexual", f.sexual(),
                "addiction", f.addiction(),
                "language", f.language(),
                "positivity", f.positivity(),
                "ratio", f.ratio()
        );

        PegiResponseDto dto = new PegiResponseDto();
        dto.setPegiLevel(level.getValue());
        dto.setReasoning(reasons);
        dto.setFeatures(map);
        return dto;
    }

    //@ requires text != null;
    //@ ensures \result != null;
    //@ assignable \nothing;
    private Features extractFeatures(String text) {
        String d = text.toLowerCase(Locale.ROOT);
        int total = Math.max(1, d.split("\\s+").length);
        int v = count(d, VIOLENCE);
        int f = count(d, FEAR);
        int s = count(d, SEXUAL);
        int a = count(d, ADDICTION);
        int l = count(d, LANGUAGE);
        int p = count(d, POSITIVE);
        //@ assume v >= 0 && f >= 0 && s >= 0 && a >= 0 && l >= 0 && p >= 0;
        //@ assume p + 1 > 0;
        double ratio = (double)(v+f+s+a+l+1)/(p+1);
        //@ assume ratio > 0;
        return new Features((double)v/total,(double)f/total,(double)s/total,
                (double)a/total,(double)l,(double)p/total,ratio);
    }

    //@ ensures \result != null && \result.length == 7;
    //@ assignable \nothing;
    private double[] evolveWeights() {
        Random r = new Random(42);
        int n = 7, pop = 25, gens = 20;
        List<double[]> population = new ArrayList<>();
        //@ maintaining i >= 0 && i <= pop;
        //@ decreases pop - population.size();
        for(int i=0;i<pop;i++) population.add(rand(r,n));
        //@ maintaining g >= 0 && g <= gens;
        //@ decreases gens - population.size();
        for(int g=0;g<gens;g++){
            population.sort(Comparator.comparingDouble((double[] w)->-fitness(w)));
            List<double[]> next = new ArrayList<>(population.subList(0,5));
            while(next.size()<pop){
                double[] c = crossover(population.get(r.nextInt(pop)),population.get(r.nextInt(pop)),r);
                mutate(c,r,0.25);
                next.add(c);
            }
            population = next;
        }
        //@ assume population.size() > 0 && population.get(0).length == 7;
        return population.get(0);
    }

    //@ requires w != null;
    //@ ensures \result > 0;
    //@ assignable \nothing;
    private double fitness(double[] w){
        double s=0;
        //@ maintaining 0 <= i && i <= w.length;
        //@ decreases w.length - i;
        for(int i=0;i<w.length;i++) s+=w[i]*w[i];
        return 1/(1+s);
    }

    //@ requires r != null && n > 0;
    //@ ensures \result != null && \result.length == n;
    //@ assignable \nothing;
    private double[] rand(Random r,int n){
        double[] v=new double[n];
        //@ maintaining 0 <= i && i <= n;
        //@ decreases n - i;
        for(int i=0;i<n;i++) v[i]=r.nextDouble()*2-1;
        return v;
    }

    //@ requires a != null && b != null && r != null && a.length == b.length && a.length > 0;
    //@ ensures \result.length == a.length;
    //@ assignable \nothing;
    private double[] crossover(double[] a,double[] b,Random r){
        int cut=r.nextInt(a.length);
        double[] c=new double[a.length];
        //@ maintaining 0 <= i && i <= a.length;
        //@ decreases a.length - i;
        for(int i=0;i<a.length;i++) c[i]=(i<cut)?a[i]:b[i];
        return c;
    }

    //@ requires w != null && r != null && w.length > 0 && rate >= 0 && rate <= 1;
    //@ assignable w[*];
    private void mutate(double[] w,Random r,double rate){
        //@ maintaining 0 <= i && i <= w.length;
        //@ decreases w.length - i;
        for(int i=0;i<w.length;i++)
            if(r.nextDouble()<rate)
                w[i]+=r.nextGaussian()*0.3;
    }

    //@ requires f != null && w != null && w.length >= 7;
    //@ ensures \result >= 0;
    //@ assignable \nothing;
    private double computeRisk(Features f,double[] w){
        double s = 0;
        s += w[0]*f.violence()*14;
        s += w[1]*f.fear()*10;
        s += w[2]*f.sexual()*40;
        s += w[3]*f.addiction()*8;
        s += w[4]*f.language()*70;
        s -= w[5]*f.positivity()*5;
        s += w[6]*f.ratio()*2.5;
        return Math.abs(s);
    }

    //@ requires text != null;
    //@ ensures \result != null;
    //@ assignable \nothing;
    private PegiLevel toPegi(double score, String text){
        String lower = text.toLowerCase(Locale.ROOT);
        for(String bad : LANGUAGE){
            if(lower.contains(bad))
                return PegiLevel.PEGI_16;
        }
        if(score<0.7) return PegiLevel.PEGI_3;
        if(score<1.3) return PegiLevel.PEGI_7;
        if(score<2.4) return PegiLevel.PEGI_12;
        if(score<2.8) return PegiLevel.PEGI_16;
        return PegiLevel.PEGI_18;
    }

    //@ requires f != null && lvl != null;
    //@ ensures \result != null;
    //@ assignable \nothing;
    private List<String> explain(Features f,PegiLevel lvl){
        List<String> r = new ArrayList<>();
        if(f.violence()>0.01) r.add("Contains violence");
        if(f.fear()>0.005) r.add("Includes fear or horror elements");
        if(f.sexual()>0.003) r.add("Sexual or adult content");
        if(f.addiction()>0.005) r.add("References to addiction or drugs");
        if(f.language()>0.002) r.add("Offensive language detected");
        if(f.positivity()>0.02) r.add("Positive and family-friendly tone");
        if(r.isEmpty()) r.add("General audience");
        return r;
    }

    //@ requires d != null && terms != null;
    //@ ensures \result >= 0;
    //@ assignable \nothing;
    private int count(String d,List<String> terms){
        int c=0;
        //@ maintaining 0 <= i && i <= terms.size();
        //@ decreases terms.size() - i;
        for(String t:terms) if(d.contains(t)) c++;
        return c;
    }
}
