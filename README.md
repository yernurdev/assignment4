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

### 1. Graph Tasks

#### 1.1 Strongly Connected Components (SCC)
- **Algorithm:** Tarjan’s SCC algorithm
- **Input:** directed dependency graph `tasks.json`
- **Output:**
    - List of strongly connected components (each as a list of vertices)
    - Their sizes
    - A **Condensation Graph (DAG)** where each SCC becomes one node

#### 1.2 Topological Sort
- **Algorithm:** Kahn’s Algorithm (BFS-based)
- **Input:** Condensation DAG
- **Output:**
    - Valid **topological order** of SCC components
    - Derived order of **original tasks** after SCC compression

#### 1.3 Shortest & Longest Paths in DAG
- **Weight Model Used:** `edge` (weights are stored per edge, not per node)
- **Implemented:**
    - Single-source **Shortest Paths** (using DP over topological order)
    - **Longest Path (Critical Path)** computation (via sign inversion / max-DP)
- **Output:**
    - Shortest distances from a given source
    - One optimal shortest path reconstruction
    - Longest (critical) path and its length

---

##  Dataset Generation

Each dataset models different levels of graph complexity and density.  
All input graphs are stored under the `/data` folder.

| Category | Nodes (n) | Description | Variants |
|-----------|------------|--------------|-----------|
| **Small** | 6–10 | Simple cases, 1–2 cycles or pure DAG | 3 |
| **Medium** | 10–20 | Mixed structures, several SCCs | 3 |
| **Large** | 20–50 | Performance and timing tests | 3 |

Total: **9 datasets per student**

All graphs vary in:
- Density (sparse vs. dense)
- Structure (cyclic vs. acyclic)
- Number and size of SCCs

Example dataset format (`data/small.json`):
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
      small3.json
│  ├─ medium2.json
      medium3.json
│  └─ large2.json
      large3.json
├─ output/
│  ├─ result_small1(2)(3).json
│  ├─ result_medium1(2)(3).json
│  └─ result_large1(2)(3).json
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

##  Run Instructions

Project builds via **Maven**:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="org.example.Main"
```

##  Tests

JUnit 5 tests are provided under src/test/java:
TarjanSCTest — verifies correctness of SCC detection
TopologicalSortTest — validates topological order
DAGSPTest — checks shortest and longest paths in sample DAGs

Run via:
```bash
mvn test
```
## Results & Analysis

Each result JSON includes:
 - SCC list and sizes
 - Condensation DAG structure
 - Topological order (components + tasks)
 - Shortest and longest path results
 - Execution time and operation counters
 - Example result excerpt:
```json
{
  "scc_count": 3,
  "topo_order_components": [0,1,2],
  "dag_longest_path": {
    "start_component": 0,
    "end_component": 2,
    "length": 15
  }
}
```

## Conclusions
- SCC compression is crucial when dealing with cyclic dependencies. 
- Topological ordering enables deterministic task scheduling after condensation. 
- Shortest path DP is efficient for acyclic structures. 
- Longest path (Critical Path) reveals the overall project duration or bottleneck chain.