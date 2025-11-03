package com.games.games_project.utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class RatingComparator implements Comparator<String> {
    private static final Map<String, Integer> ratingPriority = new HashMap<>();

    static {
        ratingPriority.put("AO", 1);
        ratingPriority.put("M", 2);
        ratingPriority.put("T", 3);
        ratingPriority.put("E10+", 4);
        ratingPriority.put("E", 5);
        ratingPriority.put("K-A", 6);
    }

    @Override
    public int compare(String rating1, String rating2) {
        Integer priority1 = ratingPriority.getOrDefault(rating1, Integer.MAX_VALUE);
        Integer priority2 = ratingPriority.getOrDefault(rating2, Integer.MAX_VALUE);

        return Integer.compare(priority1, priority2);
    }
}
