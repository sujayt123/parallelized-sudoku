package sudoku;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sudoku.SudokuHelper.*;


/**
 * Created by sujayt123 on 4/14/16.
 */
public class SeqSudokuGenerator{

    // The current grid state.
    private int[][] grid;

    // Each row and column is divided into nRegions subgrids.
    private int nRegions;

    public SeqSudokuGenerator(int[][] grid) throws Exception {
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
     * Creates a filled valid sudoku board of the specified size.
     * @param nRegions the square root of the number of rows
     * @return a completed, valid sudoku board of row and col size nRegions^2
     * @throws Exception
     */
    public static int[][] generateValidCompleteBoard(int nRegions) throws Exception {
        SeqSudokuGenerator s = new SeqSudokuGenerator(new int[nRegions * nRegions][nRegions * nRegions]);
        if (s.generateHelper()) {
            // We have generated a random valid (filled) sudoku board.
            return s.getGrid();
        }
        return null;
    }

    /**
     * Helps create a valid completed sudoku board.
     * @return true if able to generate a valid board, false otherwise.
     */
    public boolean generateHelper() {
        // First find an empty cell.
        Optional<Pair<Integer, Integer>> oCoords = findUnassignedCell(grid);

        // Termination condition: we have filled up the board and successfully solved the puzzle.
        if (!oCoords.isPresent()) {
            return true;
        }

        int r = oCoords.get().getKey();
        int c = oCoords.get().getValue();

        List<Integer> testValues = IntStream.range(1, nRegions * nRegions + 1).boxed().collect(Collectors.toList());
        Collections.shuffle(testValues);
        for (Integer testVal : testValues) {
            if (safeToPlace(grid, r, c, testVal)) {
                grid[r][c] = testVal;
                if(generateHelper()) {
                    return true;
                }
            }
        }
        // If it fails to place a value correctly, make sure the value is "cleaned".
        grid[r][c] = 0;

        return false;
    }


    /**
     * Prune some of the completed values of the board to create a challenging puzzle for the user.
     * @param grid A completed sudoku board.
     * @return A completable sudoku board.
     * @throws Exception
     */
    public static int[][] pruneCompleteBoard(int[][] grid, Difficulty diff) throws Exception {
        // Create a temp variable to hold the previous grid state.
        int[][] oldGrid = null;

        // Create a list that represents all coordinates in the board.
        List<Pair<Integer, Integer>> listCoords = new ArrayList<>();

        // Determine the sequence of cells to prune according to difficulty.
        pruneHelper(grid, listCoords, diff);

        for (int i = 0; i < listCoords.size(); i++) {
            oldGrid = grid;
            // Pick a point at random.
            Pair<Integer, Integer> coord = listCoords.get(i);
            int[][] g = deepCopyIntMatrix(grid);
            // Set its value to 0 in a temporary grid.
            g[coord.getKey()][coord.getValue()] = 0;
            // Check to see if it has mutiple solutions.
            grid = g;
            if (SeqHelper.runHelper(g) > 1) {
                grid = oldGrid;
                // terminate early if the board size is large because otherwise it takes forever
                //if (grid.length >= 16) {
                //    return grid;
                //}
            }
        }
        return grid;
    }

    public static void main(String[] args) throws Exception {
        int[][] completeBoard = generateValidCompleteBoard(3);
        System.out.println("Created a complete board.");
        int[][] prunedBoard = pruneCompleteBoard(completeBoard, Difficulty.EVIL);
        System.out.println("Generated the following sudoku puzzle:");
        printGrid(toTwoDIntegerArray(prunedBoard));
        // Ensure that the board is valid - aka that it's actually solvable.
        assert(SeqSudokuSolver.solvePuzzle(prunedBoard) != null);
    }




    public static class SeqHelper {

        // The current grid state.
        private int[][] grid;

        // Each row and column is divided into nRegions subgrids.
        private int nRegions;

        public SeqHelper(int[][] grid) throws Exception {
            if (grid.length != grid[0].length ||
                    Math.pow(Math.sqrt(grid.length), 2) != grid.length) {
                throw new Exception("The input must be a square matrix that can be easily divided into subregions!");
            }
            this.grid = grid;
            nRegions = (int)Math.sqrt(grid.length);
        }

        /**
         * Checks whether the sudoku puzzle has at least two solutions.
         * @return a number greater than 1 if the puzzle has two unique solutions, 1 if it has one solution, 0 otherwise
         */
        public int countSolns() {
            // First find an empty cell.
            Optional<Pair<Integer, Integer>> oCoords = findUnassignedCell(grid);

            // Termination condition: we have filled up the board and successfully solved the puzzle.
            if (!oCoords.isPresent()) {
                return 1;
            }

            int r = oCoords.get().getKey();
            int c = oCoords.get().getValue();
            int valid_solutions = 0;

            for (int testVal = 1 ; testVal <= nRegions * nRegions; testVal++) {
                if (safeToPlace(grid, r, c, testVal)) {
                    grid[r][c] = testVal;
                    valid_solutions += countSolns();
                }
                if (valid_solutions > 1) {
                    return valid_solutions;
                }
            }
            // If it fails to place a value correctly, make sure the value is "cleaned".
            grid[r][c] = 0;

            return valid_solutions;
        }

        public static int runHelper(int[][] grid) throws Exception {
            final SeqHelper newSolver = new SeqHelper(grid);
            return newSolver.countSolns();
        }

    }
}
