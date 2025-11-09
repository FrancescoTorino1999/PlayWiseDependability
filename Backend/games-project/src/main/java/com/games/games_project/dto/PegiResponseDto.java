package com.games.games_project.dto;

import java.util.Map;
import java.util.List;

public class PegiResponseDto {

    private int pegiLevel;
    private List<String> reasoning;
    private Map<String, Double> features;

    //@ ensures \result == pegiLevel;
    public int getPegiLevel() { return pegiLevel; }
    public void setPegiLevel(int pegiLevel) { this.pegiLevel = pegiLevel; }

    //@ ensures \result != null;
    public List<String> getReasoning() { return reasoning; }
    public void setReasoning(List<String> reasoning) { this.reasoning = reasoning; }

    //@ ensures \result != null;
    public Map<String, Double> getFeatures() { return features; }
    public void setFeatures(Map<String, Double> features) { this.features = features; }
}
