## SAP
length(int v, int w) query:
1. BFS(v), 对于所有reachable vertex, 记录到v的距离, 对w做同样操作, O(E + V) time.
2. 在reachable vertex中, 选择到v和w的距离和最小的vertex. O(V)

对于length(Iterable<Integer> v, Iterable<Integer> w): 使用 bfs(Digraph G, Iterable<Integer> sources).
