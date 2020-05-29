/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description: an immutable data type BaseballElimination that represents
 *  a sports division and determines which teams are mathematically eliminated.
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BaseballElimination {
    private int n; // number of teams
    private String[] teams; // all teams
    private HashMap<String, Integer> teamsID;
    private HashMap<String, ArrayList<String>> subset;
    private Boolean[] isEliminated;
    // private ArrayList<String>[] subset;
    private int[] wins; // the number of wins for each team
    private int[] loses; // the number of losses for each team
    private int[] remains; // the number of remaining games for each team
    private int[][] againsts; // each teams's remaining games against each team

    /**
     * create a baseball division from given filename in format specified below
     *
     * @param filename The input format is the number of teams in the division n
     *                 followed by one line for each team. Each line contains the team name
     *                 (with no internal whitespace characters), the number of wins,
     *                 the number of losses, the number of remaining games,
     *                 and the number of remaining games against each team in the division.
     *                 format as below:
     *                 4
     *                 Atlanta       83 71  8  0 1 6 1
     *                 Philadelphia  80 79  3  1 0 0 2
     *                 New_York      78 78  6  6 0 0 0
     *                 Montreal      77 82  3  1 2 0 0
     */
    public BaseballElimination(String filename) {
        In in;
        in = new In(filename);
        while (!in.isEmpty()) {
            n = in.readInt();
            StdOut.println(n);
            teams = new String[n];
            teamsID = new HashMap<String, Integer>();
            isEliminated = new Boolean[n]; // by default null
            subset = new HashMap<String, ArrayList<String>>();
            wins = new int[n];
            loses = new int[n];
            remains = new int[n];
            againsts = new int[n][n];
            for (int i = 0; i < n; i++) {
                teams[i] = in.readString();
                teamsID.put(teams[i], i);
                wins[i] = in.readInt();
                loses[i] = in.readInt();
                remains[i] = in.readInt();
                // StdOut.printf("%s   %d %d  %d ", teams[i], wins[i], loses[i], remains[i]);
                for (int j = 0; j < n; j++) {
                    againsts[i][j] = in.readInt();
                    // StdOut.printf(" %d ", againsts[i][j]);
                }
            }
        }
        checkTeams();
    }


    public int numberOfTeams() {
        // number of teams
        return n;
    }

    public Iterable<String> teams() {
        // all teams
        ArrayList<String> allTeams = new ArrayList<String>();
        Collections.addAll(allTeams, teams);
        return allTeams;
    }

    public int wins(String team) {
        // number of wins for given tea
        validateTeam(team);
        return wins[getID(team)];
    }

    public int losses(String team) {
        // number of losses for given team
        validateTeam(team);
        return loses[getID(team)];
    }

    public int remaining(String team) {
        // number of remaining games for given team
        validateTeam(team);
        return remains[getID(team)];
    }

    public int against(String team1, String team2) {
        // number of remaining games between team1 and team2
        validateTeam(team1);
        validateTeam(team2);
        return againsts[teamsID.get(team1)][teamsID.get(team2)];
    }

    public boolean isEliminated(String team) {
        // is given team eliminated?
        validateTeam(team);
        return isEliminated[getID(team)];
    }


    private void checkTeams() {
        for (int i = 0; i < n; i++) {
            String team = teams[i];
            ArrayList<String> r = trivialElimination(team);
            if (isEliminated[getID(team)] == null)
                r = nontrivialElimination(team);
            subset.put(team, r);
        }
    }

    private ArrayList<String> trivialElimination(String team) {
        // If the maximum number of games team x can win is less than
        // the number of wins of some other team i,
        // then team x is trivially eliminated.
        // That is, if w[x] + r[x] < w[i], then team x is mathematically eliminated.
        ArrayList<String> rSet = new ArrayList<>();
        int t = getID(team);
        for (int i = 0; i < n; i++) {
            if (i != t && wins[t] + remains[t] < wins[i]) {
                rSet.add(teams[i]);
                isEliminated[getID(team)] = Boolean.TRUE;
                return rSet;
            }

        }
        return rSet;
    }

    private ArrayList<String> nontrivialElimination(String team) {
        // create a flow network and solve a maxflow problem in it.
        // Team not eliminated iff all edges pointing from s are full in maxflow.
        // In the network, feasible integral flows correspond to outcomes of the remaining schedule.
        // There are vertices corresponding to teams (other than team x)
        // and to remaining divisional games (not involving team x).
        // Intuitively, each unit of flow in the network corresponds to a remaining game.
        // As it flows through the network from s to t, it passes from a game vertex,
        // say between teams i and j, then through one of the team vertices i or j,
        // classifying this game as being won by that team.
        // by default, the first n index is for teams, the last 2 is for s and t.
        int v = n + n * n + 2;
        // int g = (int) Math.round((Math.pow(n - 1, 2) - (n - 1)) / 2);
        // int v = 2 + n - 1 + g;
        int s = v - 2;
        int t = v - 1;
        FlowNetwork flow = buildFlowNetwork(team, v, s, t);
        // StdOut.printf("v %d, s %d, t %d \n", v, s, t);
        // StdOut.printf("%s : %d", team, teamsID.get(team));
        // StdOut.printf("%s", flow.toString());
        FordFulkerson ff = new FordFulkerson(flow, s, t);
        int capacity = 0;
        for (FlowEdge e : flow.adj(s)) {
            capacity += e.capacity();
        }

        ArrayList<String> rSet = null;
        if (ff.value() < capacity) {
            isEliminated[getID(team)] = Boolean.TRUE;
            rSet = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (ff.inCut(i)) rSet.add(teams[i]);
            }
        } // else means no teams in st-cut's s side
        else isEliminated[getID(team)] = Boolean.FALSE;

        return rSet;
    }


    private FlowNetwork buildFlowNetwork(String team, int v, int s, int t) {
        // connect an artificial source vertex s to each game vertex i-j and set its capacity to g[i][j].
        // If a flow uses all g[i][j] units of capacity on this edge,
        // then we interpret this as playing all of these games,
        // with the wins distributed between the team vertices i and j.
        // int thisTeam = getID(team);
        FlowNetwork flow = new FlowNetwork(v);
        int game = n; // begining of the games vertex
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (i != getID(team) && j != getID(team) && againsts[i][j] > 0) {
                    // edge from s to game in against[i][j]
                    flow.addEdge(new FlowEdge(s, game, againsts[i][j]));
                    // add edge from game to teams
                    flow.addEdge(new FlowEdge(game, i, Double.POSITIVE_INFINITY));
                    flow.addEdge(new FlowEdge(game, j, Double.POSITIVE_INFINITY));
                    game++;
                }
            }
        }
        if (game >= s) throw new RuntimeException(game + " is overlapped with s or t");

        int best = wins[getID(team)] + remains[getID(team)];
        for (int i = 0; i < n; i++) {
            if (i != getID(team))
                // add edge from game to t
                flow.addEdge(new FlowEdge(i, t, best - wins[i]));
        }
        return flow;
    }


    private int getID(String team) {
        return teamsID.get(team);
    }


    public Iterable<String> certificateOfElimination(String team) {
        // ubset R of teams that eliminates given team; null if not eliminated
        // if there is more than one certificate of elimination? Return any such subset.
        validateTeam(team);
        return subset.get(team);
    }


    private void validateTeam(String team) {
        if (!teamsID.containsKey(team)) {
            throw new IllegalArgumentException(team + " not a valid team name");
        }
    }

    public static void main(String[] args) {
        // BaseballElimination division = new BaseballElimination(args[0]);
        // for (String team : division.teams()) {
        //     StdOut.printf("%s : %d \n", team, division.getID(team));
        //     division.isEliminated(team);
        //     // division.nontrivialElimination(team);
        // }
        // for (String team : division.teams()) {
        //     if (division.isEliminated(team)) {
        //         StdOut.print(team + " is eliminated by the subset R = { ");
        //         for (String t : division.certificateOfElimination(team)) {
        //             StdOut.print(t + " ");
        //         }
        //         StdOut.println("}");
        //     }
        //     else {
        //         StdOut.println(team + " is not eliminated");
        //     }
        // }
    }
}
