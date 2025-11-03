# Assignment 4 — Smart City / Smart Campus Scheduling

## Goal

Consolidate two course topics in one practical case **“Smart City / Smart Campus Scheduling”**:

1. **Strongly Connected Components (SCC)** & **Topological Ordering**
2. **Shortest Paths in Directed Acyclic Graphs (DAGs)**

---

## Scenario

You receive datasets for city-service tasks (street cleaning, repairs, camera/sensor maintenance) and internal analytics subtasks.  
Some dependencies are **cyclic** (must be detected and compressed into SCCs), while others are **acyclic** (can be planned optimally).  
Separate subtasks use standard **dynamic programming (DP)** patterns.

---

## Implemented Algorithms

### 1) Graph Tasks

#### 1.1 Strongly Connected Components (SCC)
- **Algorithm:** Tarjan’s SCC algorithm
- **Input:** directed dependency graph `tasks.json`
- **Output:**
  - List of strongly connected components (each as a list of vertices)
  - Their sizes
  - A **condensation graph (DAG)** where each SCC becomes one node

#### 1.2 Topological Sort
- **Algorithm:** Kahn’s Algorithm (BFS-based)
- **Input:** condensation DAG
- **Output:**
  - Valid **topological order** of SCC components
  - Derived order of **original tasks** after SCC compression

#### 1.3 Shortest & Longest Paths in DAG
- **Weight model:** **`edge`** (weights are stored per edge)
- **Implemented:**
  - Single-source **Shortest Paths** (DP over topological order)
  - **Longest Path (Critical Path)** (max-DP over topological order)
- **Output:**
  - Shortest distances from the given source
  - One optimal shortest path reconstruction
  - Longest (critical) path and its length

---

## Dataset Generation

Each dataset models different levels of graph complexity and density.  
All input graphs are stored under the `/data` folder.

| Category  | Nodes (n) | Description                               | Variants |
|-----------|-----------:|-------------------------------------------|---------:|
| **Small** | 6–10       | Simple cases, 1–2 cycles or pure DAG      |        3 |
| **Medium**| 10–20      | Mixed structures, several SCCs            |        3 |
| **Large** | 20–50      | Performance and timing tests              |        3 |

Total: **9 datasets** per student

All graphs vary in:
- Density (sparse vs. dense)
- Structure (cyclic vs. acyclic)
- Number and size of SCCs

**Example dataset format (`data/small.json`):**
```json
{
  "directed": true,
  "n": 8,
  "edges": [
    {"u": 0, "v": 1, "w": 3},
    {"u": 1, "v": 2, "w": 2},
    {"u": 2, "v": 3, "w": 4},
    {"u": 3, "v": 1, "w": 1}
  ],
  "source": 0,
  "weight_model": "edge"
}

```

## Project Structure
```bash
DAA_4/
├─ data/
│  ├─ small.json
│  ├─ medium.json
│  ├─ large.json
│  ├─ small2.json
│  ├─ small3.json
│  ├─ medium2.json
│  ├─ medium3.json
│  ├─ large2.json
│  └─ large3.json
├─ output/
│  ├─ result_small.json / result_small2.json / result_small3.json
│  ├─ result_medium.json / result_medium2.json / result_medium3.json
│  └─ result_large.json / result_large2.json / result_large3.json
├─ src/
│  ├─ main/java/org/example/
│  │  ├─ Main.java
│  │  ├─ graph/
│  │  │  ├─ Graph.java
│  │  │  ├─ Metrics.java
│  │  │  └─ SimpleMetrics.java
│  │  ├─ scc/
│  │  │  ├─ TarjanSCC.java
│  │  │  └─ CondensationGraph.java
│  │  ├─ topo/
│  │  │  └─ TopologicalSort.java
│  │  ├─ dagsp/
│  │  │  ├─ DAGShortestPath.java
│  │  │  └─ DAGLongestPath.java
│  │  └─ util/
│  │     └─ JsonIO.java
│  └─ test/java/
│     ├─ JsonLoaderTest.java
│     ├─ TarjanSCTest.java
│     └─ DAGSPTest.java
├─ pom.xml
└─ README.md

```

# ▶ Run Instructions

##  Project Build

The project builds via **Maven**:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="org.example.Main"
```
If launched from an IDE (e.g., IntelliJ IDEA), simply run Main.java —
it will automatically process all JSON datasets inside /data and produce results in /output.

## Tests

- JUnit 5 tests are provided under src/test/java:
- TarjanSCTest — verifies correctness of SCC detection
- TopologicalSortTest — validates topological order
- DAGSPTest — checks shortest and longest paths in sample DAGs

Run via:
```
mvn test
```
##  Results & Analysis (from generated `output/result_*.json`)

Below is a concise summary computed from the JSON outputs you provided.

---

###  Legend

| Symbol | Meaning |
|:--------|:---------|
| **n** | Number of original tasks (`topo_order_tasks.length`) |
| **#SCC** | Number of strongly connected components (`scc_count`) |
| **largest SCC** | Maximum SCC size |
| **\|topo\|** | Number of DAG nodes (equals `#SCC`) |
| **critical length** | Length of `dag_longest_path` |
| **Reachability** | Fraction of non-`"INF"` entries in `dag_shortest_paths.dist` |

---

###  Summary Table

| Dataset label* | n  | #SCC | largest SCC | \|topo\| | critical length | Reachability |
|----------------|---:|------:|-------------:|---------:|----------------:|--------------:|
| **L1** (32 nodes; chain-like) | 32 | 27 | 2 | 27 | **31** | **1.00** |
| **L2** (30 nodes; mostly singletons) | 30 | 30 | 1 | 30 | **8** | ~**0.33** |
| **M1** (15 nodes; two 3-cycles + tails) | 15 | 10 | 3 | 10 | **0** | **0.10** |
| **M2** (12 nodes; all singletons) | 12 | 12 | 1 | 12 | **9** | **0.75** |
| **S1** (9 nodes; two 3-cycles + singles) | 9 | 5 | 3 | 5 | **8** | **1.00** |
| **S2** (7 nodes; 3-cycle + singles) | 7 | 5 | 3 | 5 | **5** | **0.80** |

\*Labels (**L1/L2/M1/M2/S1/S2**) are for readability here;  
in your repo they correspond to the actual `result_*.json` files.

---

###  Key Observations

####  SCC → DAG
- Cycles are compressed into SCCs; the condensation graph is always **acyclic (DAG)**.
- The number of SCCs directly affects the number of nodes in the DAG.

####  Topological Ordering
- The size of `topo_order_components` equals the number of SCCs (`#SCC`).
- In **linear structures** like **L1**, the topological order is long and completely connected.
- Smaller datasets (e.g., **S1**, **S2**) maintain clear hierarchical ordering with moderate depth.

####  Shortest Paths (DAG SSSP)
- Reachability differs across datasets:
  - **L1** and **S1** → full coverage (`Reachability = 1.00`)
  - **L2** and **M1** → limited connectivity due to fragmented subgraphs
- The structure of the DAG heavily influences how many nodes are reachable from the source component.

####  Critical Path Analysis
- **L1:** Longest path = **31**, nearly linear — **main scheduling bottleneck**.
- **M2**, **S1**, **S2:** Shorter critical paths (**5–9**), indicating moderate dependency chains.
- **M1:** Critical path length = **0**, meaning the source component is isolated.

---

###  Practical Recommendations

1. **Always perform SCC compression** before planning — cyclic dependencies must be treated as atomic blocks.
2. **Use topological ordering** to determine safe execution order after SCC condensation.
3. **If reachability is low**, review the chosen source or graph connectivity to ensure the analysis covers all components.
4. **To reduce total project duration (critical path):**
  - Remove redundant dependencies
  - Increase task parallelism
  - Adjust or optimize edge weights
5. **For deeper performance analysis**, include timing metrics and counters:
  - SCC → DFS visits / edges
  - Kahn → pushes / pops / relaxations
  - DAG-SP → number of relaxations per edge  
    *(Append to the `out` map in `Main.java` before writing results for richer profiling.)*

---

###  Example Result Excerpt

```json
{
  "scc_count": 3,
  "topo_order_components": [0, 1, 2],
  "dag_longest_path": {
    "start_component": 0,
    "end_component": 2,
    "length": 15
  }
}
```

## Conclusions
- SCC compression ensures that cyclic dependencies are grouped logically and efficiently.
- Topological ordering creates deterministic scheduling and dependency resolution.
- Shortest Path DP provides efficient planning within acyclic task structures.
- Longest Path (Critical Path) reveals total project duration and identifies bottlenecks.
- These methods are essential in Smart City / Smart Campus Scheduling systems for:
- Efficient maintenance routing
- Task dependency optimization
- Predictive scheduling of services