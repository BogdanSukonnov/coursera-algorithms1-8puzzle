import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

public class Solver {

    private Queue<Board> solution = new Queue<>();
    private int moves = 0;
    private boolean isSolvable = false;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("Solver with null board");
        MinPQ<SearchNode> mainPQ = new MinPQ<>();
        mainPQ.insert(new SearchNode(initial, 0, null));
        Set<Board> wereMain = new HashSet<>();
        MinPQ<SearchNode> twinPQ = new MinPQ<>();
        twinPQ.insert(new SearchNode(initial.twin(), 0, null));
        Set<Board> wereTwin = new HashSet<>();
        while (true) {
            SearchNode mainNode = doNextMove(mainPQ, wereMain);
            solution.enqueue(mainNode.getBoard());
            if (mainNode.getBoard().isGoal()) {
                moves = mainNode.getMoves();
                isSolvable = true;
                break;
            }
            if (twinPQ.min().getBoard().isGoal()) {
                solution = new Queue<>();
                break;
            }
            doNextMove(twinPQ, wereTwin);
        }
    }

    private SearchNode doNextMove(MinPQ<SearchNode> minPQ, Set<Board> wereSet) {
        SearchNode searchNode = minPQ.delMin();
        wereSet.add(searchNode.getBoard());
        for (Board neighbor : searchNode.getBoard().neighbors()) {
            if (wereSet.contains(neighbor)) continue;
            minPQ.insert(new SearchNode(neighbor, searchNode.getMoves() + 1, searchNode));
        }
        return searchNode;
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        return solution;
    }

    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

    private static class SearchNode implements Comparable<SearchNode> {
        private Board board;
        private int moves;
        private SearchNode previousNode;
        private int hamming;
        private int manhattan;

        public SearchNode(Board board, int moves, SearchNode previousNode) {
            this.board = board;
            this.moves = moves;
            this.previousNode = previousNode;
            this.hamming = board.hamming();
            this.manhattan = board.manhattan();
        }

        @Override
        public int compareTo(SearchNode otherNode) {
            return Integer.compare(manhattan + moves, otherNode.getManhattan() + otherNode.getMoves());
        }

        public Board getBoard() {
            return board;
        }

        public SearchNode getPreviousNode() {
            return previousNode;
        }

        public int getMoves() {
            return moves;
        }

        public int getHamming() {
            return hamming;
        }

        public int getManhattan() {
            return manhattan;
        }
    }
}
