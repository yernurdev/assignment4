package org.example.dagsp;

import org.example.graph.Graph;
import java.util.*;

public class DAGLongestPath {
    public static class LongestResult {
        public final long[] best; public final int[] parent; public final int argmax;
        public LongestResult(long[] b, int[] p, int a){ best=b; parent=p; argmax=a; }
    }

    public static LongestResult longest(Graph dag, int src, List<Integer> topo){
        int n=dag.n; long NEG=Long.MIN_VALUE/4;
        long[] best=new long[n]; int[] parent=new int[n];
        Arrays.fill(best, NEG); Arrays.fill(parent, -1); best[src]=0;

        for(int u: topo){
            if(best[u]==NEG) continue;
            for (Graph.Edge e: dag.adj().get(u)){
                long cand=best[u]+e.w;
                if(cand>best[e.to]){ best[e.to]=cand; parent[e.to]=u; }
            }
        }
        int arg=src; for(int i=0;i<n;i++) if(best[i]>best[arg]) arg=i;
        return new LongestResult(best, parent, arg);
    }
}
