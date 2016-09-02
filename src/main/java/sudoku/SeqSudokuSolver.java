package sudoku;

import javafx.util.Pair;
import java.util.Optional;

import static sudoku.SudokuHelper.*;

/**
 * Created by sujayt123 on 4/13/16.
 */

public class SeqSudokuSolver {

    // The current grid state.
    private int[][] grid;

    // Each row and column is divided into nRegions subgrids.
    private int nRegions;

    public SeqSudokuSolver(int[][] grid) throws Exception {
        if (grid.length != grid[0].length ||
                Math.pow(Math.sqrt(grid.length), 2) != grid.length) {
            throw new Exception("The input must be a square matrix that can be easily divided into subregions!");
        }
        this.grid = grid;
        nRegions = (int)Math.sqrt(grid.length);
    }

    /**
     * Getter method for the sudoku grid.
     * @return the sudoku grid
     */
    public int[][] getGrid() {
        return grid;
    }

    /**
     * Attempts to solve the sudoku puzzle defined by "grid" by using a sequential recursive backtracking strategy.
     * @return true if the puzzle was successfully solved, false otherwise.
     */
    public boolean successfulSolve() {
        // First find an empty cell.
        Optional<Pair<Integer, Integer>> oCoords = findUnassignedCell(grid);

        // Termination condition: we have filled up the board and successfully solved the puzzle.
        if (!oCoords.isPresent()) {
            return true;
        }

        int r = oCoords.get().getKey();
        int c = oCoords.get().getValue();

        for (int testVal = 1 ; testVal <= nRegions * nRegions; testVal++) {
            if (safeToPlace(grid, r, c, testVal)) {
                grid[r][c] = testVal;
                if(successfulSolve()) {
                    return true;
                }
            }
        }
        // If it fails to place a value correctly, make sure the value is "cleaned".
        grid[r][c] = 0;

        return false;
    }

    public static Integer[][] solvePuzzle(int[][] grid) throws Exception {
        int[][] g = deepCopyIntMatrix(grid);
        final SeqSudokuSolver newSolver = new SeqSudokuSolver(g);
        if (newSolver.successfulSolve()) {
            return toTwoDIntegerArray(newSolver.getGrid());
        }

        return null;
    }

    public static void main(String[] args) throws Exception{
        int[][] puzzle = {{3, 0, 6, 5, 0, 8, 4, 0, 0},
                {5, 2, 0, 0, 0, 0, 0, 0, 0},
                {0, 8, 7, 0, 0, 0, 0, 3, 1},
                {0, 0, 3, 0, 1, 0, 0, 8, 0},
                {9, 0, 0, 8, 6, 3, 0, 0, 5},
                {0, 5, 0, 0, 9, 0, 6, 0, 0},
                {1, 3, 0, 0, 0, 0, 2, 5, 0},
                {0, 0, 0, 0, 0, 0, 0, 7, 4},
                {0, 0, 5, 2, 0, 6, 3, 0, 0}};
        SeqSudokuSolver newSolver = new SeqSudokuSolver(puzzle);
        if (newSolver.successfulSolve()) {
            printGrid(toTwoDIntegerArray(newSolver.getGrid()));
        }
        else {
            System.out.println("No solution found.");
        }
    }

}
