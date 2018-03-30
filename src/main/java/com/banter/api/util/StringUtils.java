package com.banter.api.util;

import org.apache.commons.text.similarity.LevenshteinDistance;

public class StringUtils {

    public static double getLevenshteinDistancePercent(String left, String right) {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        int distance = levenshteinDistance.apply(left, right);
        String larger = ( left.length() > right.length()) ? left : right;
        return (larger.length() - distance) / (double)larger.length(); //cast to double to do float division instead of integer
    }
}
