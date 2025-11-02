package org.example.scc;

import org.example.graph.Graph;
import org.example.graph.Metrics;
import java.util.*;

public class TarjanSCC {
    private final Graph g; private final Metrics metrics;
    private int time=0, compCount=0;
    private int[] disc, low, compOf; private boolean[] onSt;
    private Deque<Integer> st = new ArrayDeque<>();

    public TarjanSCC(Graph g, Metrics metrics){ this.g=g; this.metrics=metrics; }

    public static class Result {
        public final int compCount; public final int[] compOf; public final List<List<Integer>> components;
        public Result(int c, int[] map, List<List<Integer>> comps){ this.compCount=c; this.compOf=map; this.components=comps; }
    }

    public Result run(){
        int n=g.n;
        disc=new int[n]; Arrays.fill(disc,-1);
        low=new int[n]; compOf=new int[n]; Arrays.fill(compOf,-1);
        onSt=new boolean[n];

        for(int v=0; v<n; v++) if(disc[v]==-1) dfs(v);

        List<List<Integer>> comps=new ArrayList<>();
        for(int i=0;i<compCount;i++) comps.add(new ArrayList<>());
        for(int v=0; v<n; v++) comps.get(compOf[v]).add(v);
        return new Result(compCount, compOf, comps);
    }

    private void dfs(int u){
        disc[u]=low[u]=time++; st.push(u); onSt[u]=true; metrics.inc("scc_dfs_visits",1);
        for (Graph.Edge e : g.adj().get(u)){
            metrics.inc("scc_edges",1);
            int v=e.to;
            if(disc[v]==-1){ dfs(v); low[u]=Math.min(low[u], low[v]); }
            else if (onSt[v]) low[u]=Math.min(low[u], disc[v]);
        }
        if(low[u]==disc[u]){
            while(true){
                int v=st.pop(); onSt[v]=false; compOf[v]=compCount;
                if(v==u) break;
            }
            compCount++;
        }
    }
}
