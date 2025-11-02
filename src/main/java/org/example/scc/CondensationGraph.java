package org.example.scc;

import org.example.graph.Graph;

public class CondensationGraph {
    public final Graph dag;
    public final int compCount;
    public final int[] compOf;
    public final int[] compSize;

    public CondensationGraph(Graph dag, int compCount, int[] compOf, int[] compSize){
        this.dag=dag; this.compCount=compCount; this.compOf=compOf; this.compSize=compSize;
    }

    public static CondensationGraph build(Graph g, TarjanSCC.Result s){
        int k=s.compCount;
        Graph dag=new Graph(k, true);
        int[] size=new int[k];
        for(int i=0;i<k;i++) size[i]=s.components.get(i).size();

        boolean[][] seen=new boolean[k][k];
        for(int u=0; u<g.n; u++){
            int cu=s.compOf[u];
            for (Graph.Edge e : g.adj().get(u)){
                int cv=s.compOf[e.to];
                if(cu!=cv && !seen[cu][cv]){ dag.addEdge(cu, cv, e.w); seen[cu][cv]=true; }
            }
        }
        return new CondensationGraph(dag, k, s.compOf, size);
    }
}
