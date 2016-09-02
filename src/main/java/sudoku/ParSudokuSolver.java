package sudoku;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static sudoku.SudokuHelper.*;

/**
 * Created by sujayt123 on 4/13/16.
 */

public class ParSudokuSolver extends RecursiveAction {

    // Thread-safe implementations of storage for a "solved" boolean and a "finalGrid" Integer[][].
    private static AtomicBoolean solved = new AtomicBoolean(false);
    private static AtomicReference<Integer[][]> finalGrid = new AtomicReference<>(null);

    // The current grid state.
    private final int[][] grid;

    // Keeps track of the current depth of the recursive backtracking.
    private final int depth;

    // After a certain cutoff depth, default to a sequential computation.
    private final int cutoff;

    // Each row and column is divided into nRegions subgrids.
    private final int nRegions;

    public ParSudokuSolver(int[][] grid, int depth, int cutoff) throws Exception {
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
        solved = new AtomicBoolean(false);
        finalGrid = new AtomicReference<>(null);
    }

    /**
     * Computes the solution to the sudoku grid by using a parallelized recursive backtracking strategy.
     */
    @Override
    public void compute() {
        // Check if the board is solved. If it is, return immediately and halt all further computation.
        if (solved.get()) {
            return;
        }

        // First, find an empty cell.
        Optional<Pair<Integer, Integer>> oCoords = findUnassignedCell(grid);

        // Termination condition: we have filled up the board and successfully solved the puzzle.
        if (!oCoords.isPresent() && solved.compareAndSet(false, true)) {
            // Construct a new grid of Integers and store them in the atomic reference.
            finalGrid.compareAndSet(null, toTwoDIntegerArray(grid));
            return;
        }

        // We have a valid cell to target. Do we do our computation sequentially?
        if (depth > cutoff) {
            try {
                SeqSudokuSolver seqSolver = new SeqSudokuSolver(grid);
                if (seqSolver.successfulSolve() && solved.compareAndSet(false, true)) {
                    finalGrid.compareAndSet(null, toTwoDIntegerArray(seqSolver.getGrid()));
                }
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
                    action = new ParSudokuSolver(tempGrid, depth + 1, cutoff);
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

    public static Integer[][] solvePuzzle(int[][] grid) throws Exception {
        int[][] g = deepCopyIntMatrix(grid);
        final ParSudokuSolver task = new ParSudokuSolver(g, 0, 6);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(task);
        task.join();
        if(solved.get()) {
            Integer[][] retVal = finalGrid.get();
            resetAtomicVariables();
            return retVal;
        }
        resetAtomicVariables();
        return null;
    }

    public static void main(String[] args) throws Exception {
        int[][] puzzle = {{3, 0, 6, 5, 0, 8, 4, 0, 0},
                {5, 2, 0, 0, 0, 0, 0, 0, 0},
                {0, 8, 7, 0, 0, 0, 0, 3, 1},
                {0, 0, 3, 0, 1, 0, 0, 8, 0},
                {9, 0, 0, 8, 6, 3, 0, 0, 5},
                {0, 5, 0, 0, 9, 0, 6, 0, 0},
                {1, 3, 0, 0, 0, 0, 2, 5, 0},
                {0, 0, 0, 0, 0, 0, 0, 7, 4},
                {0, 0, 5, 2, 0, 6, 3, 0, 0}};
        final ParSudokuSolver task = new ParSudokuSolver(puzzle, 0, 10);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(task);
        if (solved.get()) {
            printGrid(finalGrid.get());
        }
        else {
            System.out.println("No solution exists.");
        }
    }

}
