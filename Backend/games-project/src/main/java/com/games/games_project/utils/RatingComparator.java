package com.games.games_project.utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class RatingComparator implements Comparator<String> {
    private static final Map<String, Integer> ratingPriority = new HashMap<>();

    static {
        ratingPriority.put("AO", 1);    // Adults Only
        ratingPriority.put("M", 2);     // Mature
        ratingPriority.put("T", 3);     // Teen
        ratingPriority.put("E10+", 4);  // Everyone 10+
        ratingPriority.put("E", 5);     // Everyone
        ratingPriority.put("K-A", 6);   // Kids-To-Adults
    }

    @Override
    public int compare(String rating1, String rating2) {
        Integer priority1 = ratingPriority.getOrDefault(rating1, Integer.MAX_VALUE);
        Integer priority2 = ratingPriority.getOrDefault(rating2, Integer.MAX_VALUE);

        return Integer.compare(priority1, priority2);
    }
}
