package com.games.games_project.dto;

import java.util.Map;
import java.util.List;

public class PegiResponseDto {

    private int pegiLevel;
    private List<String> reasoning;
    private Map<String, Double> features;

    /*@ public normal_behavior
      @   ensures \result == pegiLevel;
      @*/
    public /*@ pure @*/ int getPegiLevel() {
        return pegiLevel;
    }

    /*@ public normal_behavior
      @   requires pegiLevel == 3 || pegiLevel == 7 || pegiLevel == 12 || pegiLevel == 16 || pegiLevel == 18;
      @   ensures this.pegiLevel == pegiLevel;
      @   assignable this.pegiLevel;
      @*/
    public void setPegiLevel(int pegiLevel) {
        this.pegiLevel = pegiLevel;
    }

    /*@ public normal_behavior
      @   ensures \result == reasoning;
      @*/
    public /*@ pure @*/ List<String> getReasoning() {
        return reasoning;
    }

    /*@ public normal_behavior
      @   requires reasoning != null;
      @   ensures this.reasoning == reasoning;
      @   assignable this.reasoning;
      @*/
    public void setReasoning(List<String> reasoning) {
        this.reasoning = reasoning;
    }

    /*@ public normal_behavior
      @   ensures \result == features;
      @*/
    public /*@ pure @*/ Map<String, Double> getFeatures() {
        return features;
    }

    /*@ public normal_behavior
      @   requires features != null;
      @   ensures this.features == features;
      @   assignable this.features;
      @*/
    public void setFeatures(Map<String, Double> features) {
        this.features = features;
    }
}
