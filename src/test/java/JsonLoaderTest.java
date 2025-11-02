import org.example.util.JsonIO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JsonLoaderTest {
    @Test
    public void loadSmall2() throws Exception {
        var L = JsonIO.loadGraph("data/small2.json");
        assertTrue(L.g.directed);
        assertEquals(7, L.g.n);
        assertEquals("edge", L.weightModel);
    }
}
