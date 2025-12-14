package org.example.filter;

public class Statistics {
    long count = 0;

    Double min = null;
    Double max = null;
    double sum = 0;

    Integer minLength = null;
    Integer maxLength = null;

    void addNumber(double value) {
        count++;
        sum += value;
        min = min == null ? value : Math.min(min, value);
        max = max == null ? value : Math.max(max, value);
    }

    void addString(String s) {
        count++;
        int len = s.length();
        minLength = minLength == null ? len : Math.min(minLength, len);
        maxLength = maxLength == null ? len : Math.max(maxLength, len);
    }
}
