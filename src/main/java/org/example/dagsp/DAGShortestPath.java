package org.example.dagsp;

import org.example.graph.Graph;
import org.example.graph.Metrics;
import java.util.*;

public class DAGShortestPath {
    public static class SSSPResult {
        public final long[] dist; public final int[] parent;
        public SSSPResult(long[] d, int[] p){ dist=d; parent=p; }
    }

    public static SSSPResult sssp(Graph dag, int src, List<Integer> topo, Metrics metrics){
        int n=dag.n; long INF=Long.MAX_VALUE/4;
        long[] dist=new long[n]; int[] parent=new int[n];
        Arrays.fill(dist, INF); Arrays.fill(parent, -1); dist[src]=0;

        for(int u: topo){
            if(dist[u]==INF) continue;
            for (Graph.Edge e: dag.adj().get(u)){
                long cand=dist[u]+e.w; metrics.inc("dag_relax",1);
                if(cand<dist[e.to]){ dist[e.to]=cand; parent[e.to]=u; }
            }
        }
        return new SSSPResult(dist, parent);
    }

    public static List<Integer> reconstruct(int t, int[] parent){
        List<Integer> path=new ArrayList<>();
        for(int v=t; v!=-1; v=parent[v]) path.add(v);
        Collections.reverse(path); return path;
    }
}
