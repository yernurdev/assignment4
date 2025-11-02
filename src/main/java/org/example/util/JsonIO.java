package org.example.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.example.graph.Graph;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class JsonIO {

    public static class Loaded {
        public final Graph g;
        public final int source;
        public final String weightModel;
        public Loaded(Graph g, int source, String weightModel){
            this.g = g; this.source = source; this.weightModel = weightModel;
        }
    }

    public static Loaded loadGraph(String path) throws Exception {
        String text = FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
        JSONObject root = new JSONObject(text);

        boolean directed = root.optBoolean("directed", true);
        int n = root.getInt("n");
        Graph g = new Graph(n, directed);

        JSONArray edges = root.getJSONArray("edges");
        for (int i = 0; i < edges.length(); i++) {
            JSONObject e = edges.getJSONObject(i);
            int u = e.getInt("u");
            int v = e.getInt("v");
            int w = e.optInt("w", 1);
            g.addEdge(u, v, w);
        }

        int source = root.optInt("source", 0);
        String wm = root.optString("weight_model", "edge");
        return new Loaded(g, source, wm);
    }

    public static void writePrettyJson(Path out, Object pojo) throws Exception {
        out.toFile().getParentFile().mkdirs();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(pojo);
        FileUtils.writeStringToFile(out.toFile(), json, StandardCharsets.UTF_8);
    }
}
