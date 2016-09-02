package sudoku;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static sudoku.SudokuHelper.*;

/**
 * Created by sujayt123 on 4/14/16.
 */
public class ParSudokuGenerator {

    // The current grid state.
    private int[][] grid;

    // Each row and column is divided into nRegions subgrids.
    private int nRegions;

    public ParSudokuGenerator(int[][] grid) throws Exception {
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
        ParSudokuGenerator s = new ParSudokuGenerator(new int[nRegions * nRegions][nRegions * nRegions]);
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
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                listCoords.add(new Pair<>(r, c));
            }
        }

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
            if (ParHelper.runHelper(g) > 1) {
                grid = oldGrid;
                // if the board size is too large, terminate early because otherwise it takes forever.
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
        printGrid(toTwoDIntegerArray(completeBoard));
        int[][] prunedBoard = pruneCompleteBoard(completeBoard, Difficulty.EVIL);
        System.out.println("Generated the following sudoku puzzle:");
        printGrid(toTwoDIntegerArray(prunedBoard));
        // Ensure that the board is valid - aka that it's actually solvable.
        assert(SeqSudokuSolver.solvePuzzle(prunedBoard) != null);
    }


    public static class ParHelper extends RecursiveAction {

        // A thread-safe variable that stores the number of totalSolns found to the sudoku problem.
        private static AtomicInteger totalSolns = new AtomicInteger(0);

        // The current grid state.
        private final int[][] grid;

        // Keeps track of the current depth of the recursive backtracking.
        private final int depth;

        // After a certain cutoff depth, default to a sequential computation.
        private final int cutoff;

        // Each row and column is divided into nRegions subgrids.
        private final int nRegions;

        public ParHelper(int[][] grid, int depth, int cutoff) throws Exception {
            this.grid = grid;
            this.depth = depth;
            this.cutoff = cutoff;
            if (grid.length != grid[0].length ||
                    Math.pow(Math.sqrt(grid.length), 2) != grid.length) {
                throw new Exception("The input must be a square matrix that can be easily divided into subregions!");
            }

            nRegions = (int)Math.sqrt(grid.length);
        }

        private static void resetAtomicVariables() {
            totalSolns = new AtomicInteger(0);
        }

        /**
         * Computes the solution to the sudoku grid by using a parallelized recursive backtracking strategy.
         */
        @Override
        public void compute() {
            // Check if we've found at least 2 solutions for the board across all threads. If it does, return immediately.
            if (totalSolns.get() > 1) {
                return;
            }

            // First, find an empty cell.
            Optional<Pair<Integer, Integer>> oCoords = findUnassignedCell(grid);

            // We have filled up the board and successfully solved the puzzle. Let other threads continue operations.
            if (!oCoords.isPresent()) {
                totalSolns.incrementAndGet();
                return;
            }

            // We have a valid cell to target. Do we do our computation sequentially?
            if (depth > cutoff) {
                try {
                    // Find the count of solutions that result from the current grid and add to count.
                    totalSolns.addAndGet(SeqSudokuGenerator.SeqHelper.runHelper(grid));
                    return;
                }
                catch(Exception e) {
                    System.exit(100);
                }
            }

            // If not sequentially, we'll do it in parallel.
            int r = oCoords.get().getKey();
            int c = oCoords.get().getValue();
            final List<RecursiveAction> actions = new ArrayList<>();

            for (int testVal = 1 ; testVal <= nRegions * nRegions; testVal++) {
                if (safeToPlace(grid, r, c, testVal)) {
                    // Construct a deep-copy of the grid.
                    final int[][] tempGrid = deepCopyIntMatrix(grid);
                    // Modify it to reflect the testing value.
                    tempGrid[r][c] = testVal;
                    // Create a new task to check if this leads to a valid solution.
                    RecursiveAction action = null;
                    try {
                        action = new ParHelper(tempGrid, depth + 1, cutoff);
                    }
                    catch (Exception e) {
                        System.exit(100);
                    }
                    // Add this task to the "to-do" list.
                    actions.add(action);
                }
            }

            // Invoke all the tasks.
            invokeAll(actions);

            for (RecursiveAction action: actions) {
                action.join();
            }
        }

        public static int runHelper(int[][] grid) throws Exception {
            final ParHelper newSolver = new ParHelper(grid, 0, 10);
            ForkJoinPool pool = new ForkJoinPool();
            pool.invoke(newSolver);
            int retVal = totalSolns.get();
            resetAtomicVariables();
            return retVal;
        }

    }

}
