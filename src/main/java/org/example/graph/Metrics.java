package org.example.graph;

public interface Metrics {
    void inc(String key, long delta);
    long get(String key);
    long timeNow();
}
