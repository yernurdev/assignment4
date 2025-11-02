import org.example.util.JsonIO;
import org.example.graph.SimpleMetrics;
import org.example.scc.TarjanSCC;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TarjanSCTest {
    @Test
    public void counts() throws Exception {
        var L = JsonIO.loadGraph("data/small3.json");
        var r = new TarjanSCC(L.g, new SimpleMetrics()).run();
        assertTrue(r.compCount >= 3);
        for (int v = 0; v < L.g.n; v++) assertTrue(r.compOf[v] >= 0);
    }
}
