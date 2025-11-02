package org.example.topo;

import org.example.graph.Graph;
import org.example.graph.Metrics;
import java.util.*;

public class TopologicalSort {
    public static List<Integer> kahn(Graph dag, Metrics metrics){
        int n=dag.n;
        int[] indeg=new int[n];
        for(int u=0;u<n;u++) for (Graph.Edge e: dag.adj().get(u)) indeg[e.to]++;

        Deque<Integer> q=new ArrayDeque<>();
        for(int i=0;i<n;i++) if(indeg[i]==0) q.add(i);

        List<Integer> order=new ArrayList<>();
        while(!q.isEmpty()){
            int u=q.removeFirst(); metrics.inc("kahn_pops",1);
            order.add(u);
            for (Graph.Edge e: dag.adj().get(u)){
                indeg[e.to]--; metrics.inc("kahn_relax",1);
                if(indeg[e.to]==0){ q.addLast(e.to); metrics.inc("kahn_pushes",1); }
            }
        }
        if(order.size()!=n) throw new IllegalStateException("Condensation graph is not a DAG.");
        return order;
    }
}
