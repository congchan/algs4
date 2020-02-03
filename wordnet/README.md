## SAP
length(int v, int w) query:
1. BFS(v), we got distance to every reachable vertices, so as w, O(E + V) time.
2. There are one or more vertice that has the smallest total distance to v and w, pick one as ancestor, the total distance as length, O(V) time.

For length(Iterable<Integer> v, Iterable<Integer> w): use bfs(Digraph G, Iterable<Integer> sources).

## WordNet
Since a noun could appear in different synsets, and all the methods' parameters are noun, hence we use a map {key: bag} to represent the data(the noun as key, the bag of ids it belong to as values).
