import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class Board {

    private final char[] tiles;
    private final char dimension;
    private char blank = (char) -1;

    // create a board from an n-by-n array of tiles,
    // where tiles[i(row, col)] = tile at (row, col)
    public Board(int[][] tiles) {

        dimension = (char) tiles.length;
        this.tiles = flatCopy(tiles);

        // find blank tile (zero)
        for (int i = 0; i < this.tiles.length; ++i) {
            if (this.tiles[i] == (char) 0) {
                blank = (char) i;
                break;
            }
        }
        if (blank == (char) -1) throw new IllegalArgumentException("no blank tile");
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append((int) dimension).append("\n");
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                s.append(String.format("%2s ", (int) tiles[i(row, col)]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return dimension;
    }

    // number of tiles out of place
    public int hamming() {
        int hamming = 0;
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                if (tiles[i(row, col)] == (char) 0) continue;
                if (isNotInPlace(row, col)) ++hamming;
            }
        }
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int manhattan = 0;
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                if (tiles[i(row, col)] == (char) 0) continue;
                if (isNotInPlace(row, col)) {
                    int desiredRow = getRow(tiles[i(row, col)] - 1, dimension);
                    int desiredColumn = getColumn(tiles[i(row, col)] - 1, dimension);
                    manhattan = manhattan + Math.abs(row - desiredRow) + Math.abs(col - desiredColumn);
                }
            }
        }
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                if (isNotInPlace(row, col)) return false;
            }
        }
        return true;
    }

    // does this board equal other?
    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != getClass()) return false;
        Board that = (Board) other;
        return Arrays.equals(that.tiles, tiles);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Queue<Board> neighbors = new Queue<>();
        int blankRow = getRow(blank, dimension);
        int blankColumn = getColumn(blank, dimension);
        addNeighbor(blankRow - 1, blankColumn, neighbors);
        addNeighbor(blankRow, blankColumn + 1, neighbors);
        addNeighbor(blankRow + 1, blankColumn, neighbors);
        addNeighbor(blankRow, blankColumn - 1, neighbors);
        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] twinTiles = getDoubleArray();
        int row1 = 0;
        int col1 = 0;
        int row2 = 0;
        int col2 = 1;
        if (twinTiles[row1][col1] == 0) {
            row1 = 1;
            col1 = 1;
        }
        if (twinTiles[row2][col2] == 0) {
            row2 = 1;
            col2 = 0;
        }
        int temp = twinTiles[row2][col2];
        twinTiles[row2][col2] = twinTiles[row1][col1];
        twinTiles[row1][col1] = temp;
        return new Board(twinTiles);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        // for each command-line argument
        for (String filename : args) {

            // read in the board specified in the filename
            In in = new In(filename);
            int n = in.readInt();
            int[][] tiles = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    tiles[i][j] = in.readInt();
                }
            }

            // solve the slider puzzle
            Board board = new Board(tiles);
            StdOut.printf("\nboard: %s\n", board);
            StdOut.printf("\ndimension: %s; hamming: %s; manhattan: %s; is goal: %s;\n",
                    board.dimension(), board.hamming(), board.manhattan(), board.isGoal());
            StdOut.printf("\nequals twin: %s%s\n", board.equals(board.twin()), board.twin());
            StdOut.println("\nneigbours:");
            board.neighbors().forEach(StdOut::println);
        }
    }

    private int[][] getDoubleArray() {
        int[][] twinTiles = new int[dimension][dimension];
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                twinTiles[row][col] = tiles[i(row, col)];
            }
        }
        return twinTiles;
    }

    private boolean isNotInPlace(int row, int col) {
        return tiles[i(row, col)] != getExpected(row, col);
    }

    private char getExpected(int row, int col) {
        return (row == dimension-1 && col == dimension-1) ?
                (char) 0 :
                (char) (dimension * row + col + 1);
    }

    private static int getColumn(int i, char dimension) {
        return i % dimension;
    }

    private static int getRow(int i, char dimension) {
        return i / dimension;
    }

    private void addNeighbor(int row, int column, Queue<Board> neighbors) {
        if (row >= 0 && row < dimension && column >= 0 && column < dimension) {
            int[][] neighbourArray = getDoubleArray();
            moveToBlank(neighbourArray, row, column, blank);
            neighbors.enqueue(new Board(neighbourArray));
        }
    }

    private static void moveToBlank(int[][] tiles, int tileRow, int tileCol, char blank) {
        tiles[getRow(blank, (char) tiles.length)][getColumn(blank, (char) tiles.length)] =
                tiles[tileRow][tileCol];
        tiles[tileRow][tileCol] = 0;
    }

    private static char[] flatCopy(int[][] original) {
        int dimension = original.length;
        char[] copy = new char[dimension * dimension];
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                copy[row * dimension + col] = (char) original[row][col];
            }
        }
        return copy;
    }

    private int i(int row, int col) {
        return row * dimension + col;
    }
}
