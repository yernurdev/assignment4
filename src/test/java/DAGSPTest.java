import org.example.util.JsonIO;
import org.example.graph.SimpleMetrics;
import org.example.scc.*;
import org.example.topo.TopologicalSort;
import org.example.dagsp.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DAGSPTest {
    @Test
    public void ssspWorks() throws Exception {
        var L = JsonIO.loadGraph("data/small2.json"); // pure DAG
        var r = new TarjanSCC(L.g, new SimpleMetrics()).run();
        var cg = CondensationGraph.build(L.g, r);
        var topo = TopologicalSort.kahn(cg.dag, new SimpleMetrics());
        int src = r.compOf[L.source];

        var sssp = DAGShortestPath.sssp(cg.dag, src, topo, new SimpleMetrics());
        assertEquals(0L, sssp.dist[src]);
        boolean hasReachable=false;
        for (long d: sssp.dist) if (d < Long.MAX_VALUE/4) { hasReachable=true; break; }
        assertTrue(hasReachable);
    }
}
