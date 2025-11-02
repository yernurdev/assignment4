package org.example.graph;

import java.util.HashMap;
import java.util.Map;

public class SimpleMetrics implements Metrics {
    private final Map<String, Long> m = new HashMap<>();
    @Override public void inc(String key, long d){ m.put(key, m.getOrDefault(key,0L)+d); }
    @Override public long get(String key){ return m.getOrDefault(key,0L); }
    @Override public long timeNow(){ return System.nanoTime(); }
}
