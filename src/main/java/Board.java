import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

    private final int[][] tiles;
    private final int dimension;
    private int blankRow = -1;
    private int blankCol = -1;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {

        dimension = tiles.length;
        this.tiles = new int[dimension][dimension];

        for (int row = 0; row < dimension; ++row) {
            System.arraycopy(tiles[row], 0, this.tiles[row], 0, dimension);
        }

        // find blank tile (zero)
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                if (tiles[row][col] == 0) {
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
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return true;
        if (y.getClass() != getClass()) return false;
        Board that = (Board) y;
        if (that.dimension() != dimension() || that.tiles.length != tiles.length) return false;
        for (int row = 0; row < dimension; ++row) {
            for (int col = 0; col < dimension; ++col) {
                if (that.tiles[row][col] != this.tiles[row][col]) return false;
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        List<Board> neighbors = new ArrayList<>(4);
        addNeighbor(blankRow - 1, blankCol, neighbors);
        addNeighbor(blankRow, blankCol + 1, neighbors);
        addNeighbor(blankRow + 1, blankCol, neighbors);
        addNeighbor(blankRow, blankCol + 1, neighbors);
        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {}

    // unit testing (not graded)
    public static void main(String[] args) {}

    private boolean isInPlace(int row, int col) {
        return tiles[row][col] == dimension * row + col + 1;
    }


    private int getColumn(int tile) {
        return tile % dimension;
    }

    private int getRow(int tile) {
        return tile / dimension;
    }

    private void addNeighbor(int row, int column, List<Board> neighbors) {
        if (row > 0 && row < dimension && column > 0 && column < dimension) {
            Board neighbor = new Board(tiles);
            moveIn(neighbor, row, column);
            neighbors.add(neighbor);
        }
    }

    private static void moveIn(Board board, int tileRow, int tileCol) {
        board.tiles[board.blankRow][board.blankCol] = board.tiles[tileRow][tileCol];
        board.tiles[tileRow][tileCol] = 0;
        board.blankRow = tileRow;
        board.blankCol = tileCol;
    }
}
