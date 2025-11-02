package org.example;

import org.example.graph.*;
import org.example.scc.*;
import org.example.topo.TopologicalSort;
import org.example.dagsp.*;
import org.example.util.JsonIO;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Main {

    private static String baseName(Path p) {
        String fn = p.getFileName().toString();
        int i = fn.lastIndexOf('.');
        return (i >= 0) ? fn.substring(0, i) : fn;
    }

    private static List<Path> listAllDataJson() throws IOException {
        Path dir = Paths.get("data");
        if (!Files.isDirectory(dir)) return List.of();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, "*.json")) {
            List<Path> out = new ArrayList<>();
            for (Path p : ds) out.add(p);
            out.sort(Comparator.naturalOrder());
            return out;
        }
    }

    private static String groupOf(String basename) {
        String b = basename.toLowerCase(Locale.ROOT);
        if (b.startsWith("small")) return "small";
        if (b.startsWith("medium")) return "medium";
        if (b.startsWith("large")) return "large";
        return null;
    }

    public static void main(String[] args) throws Exception {
        List<Path> inputs = (args.length == 0)
                ? listAllDataJson()
                : List.of(resolveArg(args[0]));
        if (inputs.isEmpty()) return;

        for (Path input : inputs) {
            if (!Files.exists(input)) continue;
            JsonIO.Loaded L;
            try { L = JsonIO.loadGraph(input.toString()); }
            catch (Exception e) { continue; }

            Graph g = L.g;
            int src = L.source;
            Metrics metrics = new SimpleMetrics();

            TarjanSCC.Result sccRes = new TarjanSCC(g, metrics).run();
            CondensationGraph cg = CondensationGraph.build(g, sccRes);
            List<Integer> topo = TopologicalSort.kahn(cg.dag, metrics);
            int srcComp = sccRes.compOf[src];
            DAGShortestPath.SSSPResult sssp = DAGShortestPath.sssp(cg.dag, srcComp, topo, metrics);
            DAGLongestPath.LongestResult lres = DAGLongestPath.longest(cg.dag, srcComp, topo);
            List<Integer> critical = DAGShortestPath.reconstruct(lres.argmax, lres.parent);

            List<Integer> topoTasks = new ArrayList<>();
            for (int c : topo) topoTasks.addAll(sccRes.components.get(c));

            Map<String,Object> out = new LinkedHashMap<>();
            List<Map<String,Object>> comps = new ArrayList<>();
            for (List<Integer> comp : sccRes.components) {
                Map<String,Object> obj = new LinkedHashMap<>();
                obj.put("vertices", comp);
                obj.put("size", comp.size());
                comps.add(obj);
            }
            out.put("scc_components", comps);
            out.put("scc_count", sccRes.compCount);

            out.put("topo_order_components", topo);
            out.put("topo_order_tasks", topoTasks);

            Map<String,Object> ssspNode = new LinkedHashMap<>();
            ssspNode.put("source_component", srcComp);
            long INF = Long.MAX_VALUE/4;
            List<Object> dist = new ArrayList<>();
            for (long d : sssp.dist) dist.add(d >= INF ? "INF" : d);
            ssspNode.put("dist", dist);
            int targetComp = topo.get(topo.size()-1);
            ssspNode.put("path_to_last_component", DAGShortestPath.reconstruct(targetComp, sssp.parent));
            out.put("dag_shortest_paths", ssspNode);

            Map<String,Object> lpNode = new LinkedHashMap<>();
            lpNode.put("start_component", srcComp);
            lpNode.put("end_component", lres.argmax);
            lpNode.put("length", lres.best[lres.argmax]);
            lpNode.put("critical_path_components", critical);
            out.put("dag_longest_path", lpNode);

            String base = baseName(input);
            Path outPath = Paths.get("output", "result_" + base + ".json");
            JsonIO.writePrettyJson(outPath, out);

            String group = groupOf(base);
            if (group != null) {
                Path alias = Paths.get("output", "result_" + group + ".json");
                JsonIO.writePrettyJson(alias, out);
            }
        }
    }

    private static Path resolveArg(String arg) {
        String a = arg.trim().toLowerCase(Locale.ROOT);
        if (a.equals("small") || a.equals("medium") || a.equals("large")) {
            return Paths.get("data", a + ".json");
        }
        return Paths.get(arg);
    }
}
