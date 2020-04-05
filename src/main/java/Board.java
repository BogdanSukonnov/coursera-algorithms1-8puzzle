import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class Board {

    private final int[][] tiles;
    private final int dimension;
    private int blankRow = -1;
    private int blankCol = -1;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {

        this.tiles = deepCopy(tiles);
        dimension = this.tiles.length;

        // find blank tile (zero)
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                if (this.tiles[row][col] == 0) {
                    blankRow = row;
                    blankCol = col;
                    break;
                }
            }
        }
        if (blankRow == -1) throw new IllegalArgumentException("no blank tile");
    }

    // string representation of this board
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(dimension);
        for (int row = 0; row < dimension; ++row) {
            stringBuilder.append("\n");
            for (int col = 0; col < dimension; ++col) {
                stringBuilder.append(String.format("%s%s", col == 0 ? "" : " ", tiles[row][col]));
            }
        }
        return stringBuilder.toString();
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
                if (tiles[row][col] == 0) continue;
                if (!isInPlace(row, col)) ++hamming;
            }
        }
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int manhattan = 0;
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                if (tiles[row][col] == 0) continue;
                if (!isInPlace(row, col)) {
                    int desiredRow = getRow(tiles[row][col] - 1);
                    int desiredColumn = getColumn(tiles[row][col] - 1);
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
                if (!isInPlace(row, col)) return false;
            }
        }
        return true;
    }

    // does this board equal y?
    @Override
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return true;
        if (y.getClass() != getClass()) return false;
        Board that = (Board) y;
        return Arrays.deepEquals(that.tiles, tiles);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(tiles);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Queue<Board> neighbors = new Queue<>();
        addNeighbor(blankRow - 1, blankCol, neighbors);
        addNeighbor(blankRow, blankCol + 1, neighbors);
        addNeighbor(blankRow + 1, blankCol, neighbors);
        addNeighbor(blankRow, blankCol - 1, neighbors);
        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] twinTiles = deepCopy(tiles);
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

    private boolean isInPlace(int row, int col) {
        return tiles[row][col] == getExpected(row, col);
    }

    private int getExpected(int row, int col) {
        return (row == dimension-1 && col == dimension-1) ? 0 : dimension*row+col+1;
    }

    private int getColumn(int tile) {
        return tile % dimension;
    }

    private int getRow(int tile) {
        return tile / dimension;
    }

    private void addNeighbor(int row, int column, Queue<Board> neighbors) {
        if (row >= 0 && row < dimension && column >= 0 && column < dimension) {
            Board neighbor = new Board(tiles);
            moveToBlank(neighbor, row, column);
            neighbors.enqueue(neighbor);
        }
    }

    private static void moveToBlank(Board board, int tileRow, int tileCol) {
        board.tiles[board.blankRow][board.blankCol] = board.tiles[tileRow][tileCol];
        board.tiles[tileRow][tileCol] = 0;
        board.blankRow = tileRow;
        board.blankCol = tileCol;
    }

    private int[][] deepCopy(int[][] original) {
        int [][] copy = new int[original.length][original.length];
        for (int row = 0; row < original.length; ++row) {
            copy[row] = Arrays.copyOf(original[row], original.length);
        }
        return copy;
    }
}
