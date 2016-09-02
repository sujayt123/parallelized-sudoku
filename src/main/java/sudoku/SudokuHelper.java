package sudoku;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by sujayt123 on 4/14/16.
 */
public class SudokuHelper {

    public enum Difficulty {
        EASY, DIFFICULT, EVIL;
    }

    public static void pruneHelper (int[][] grid, List<Pair<Integer, Integer>> listCoords, Difficulty diff) {
        if (diff == Difficulty.EVIL) {
            // (Evil) Left to Right then Top to Bottom
            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < grid[0].length; c++) {
                    listCoords.add(new Pair<>(r, c));
                }
            }
        }

        else if (diff == Difficulty.DIFFICULT) {
            // (Difficult) Wandering along “S”
            int x = 0;
            int y = 0;
            while (x < grid.length) {
                if (x % 2 == 0) {
                    while (y < grid[0].length) {
                        listCoords.add(new Pair<>(x, y));
                        y++;
                    }
                }
                else {
                    while (y > 0) {
                        y--;
                        listCoords.add(new Pair<>(x, y));
                    }
                }
                x++;
            }
        }

        else {
            // (Easy) Randomizing globally
            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < grid[0].length; c++) {
                    listCoords.add(new Pair<>(r, c));
                }
            }
            Collections.shuffle(listCoords);
        }
    }

    /**
     * Converts an array of primitive type 'int' to an array of boxed type 'Integer' using Java8 streams.
     * @param data a 1D array of ints
     * @return a 1D array of Integers
     */
    public static Integer[] toOneDIntegerArray(int[] data) {
        return Arrays.stream(data).boxed().toArray(Integer[]::new);
    }

    /**
     * Converts a matrix of primitive type 'int' to a matrix of boxed type 'Integer' using Java8 streams.
     * @param matrix a 2D array of ints
     * @return a 2D array of Integers
     */
    public static Integer[][] toTwoDIntegerArray(int[][] matrix) {
        return java.util.Arrays.stream(matrix).map(SudokuHelper::toOneDIntegerArray).toArray(Integer[][]::new);
    }

    /**
     * Creates a deep copy of an int[][] matrix.
     * @param grid a 2D array of ints
     * @return a deep copy of matrix
     */
    public static int[][] deepCopyIntMatrix(int[][] grid) {
        return java.util.Arrays.stream(grid).map(el -> el.clone()).toArray($ -> grid.clone());
    }

    /**
     * Looks through the grid to find the first unassigned cell.
     * @return an Optional<pair of integers> that specifies the coordinate of an unassigned cell, if any.
     */
    public static Optional<Pair<Integer, Integer>> findUnassignedCell(int[][] grid) {
        int r, c;
        for (r = 0; r < grid.length; r++) {
            for (c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == 0) {
                    return Optional.of(new Pair<>(r, c));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Checks to see if "value" can be placed in "grid" at the location (r, c) without violating a Sudoku rule.
     * @param grid the Sudoku grid
     * @param r the row number
     * @param c the col number
     * @param value the value to be placed
     * @return true if the value is safe to place, false otherwise
     */
    public static boolean safeToPlace(int[][] grid, int r, int c, int value) {
        return safeInRow(grid, r, value) &&
                safeInCol(grid, c, value) &&
                safeInSubgrid(grid, r, c, value);
    }

    /**
     * Checks if placing "value" in row "r" will violate a Sudoku rule.
     * @param grid the Sudoku grid
     * @param r the row number
     * @param value the value to be placed
     * @return true if the value is safe to place, false otherwise
     */
    public static boolean safeInRow(int[][] grid, int r, int value) {
        for (int c = 0; c < grid[0].length; c++) {
            if (grid[r][c] == value) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if placing "value" in column "c" will violate a Sudoku rule.
     * @param grid the Sudoku grid
     * @param c the col number
     * @param value the value to be placed
     * @return true if the value is safe to place, false otherwise
     */
    public static boolean safeInCol(int[][] grid, int c, int value) {
        for (int r = 0; r < grid.length; r++) {
            if (grid[r][c] == value) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if placing "value" in (r, c) will violate a subgrid-based Sudoku rule.
     * @param grid the Sudoku grid
     * @param r the row number
     * @param c the col number
     * @param value the value to be placed
     * @return true if the value is safe to place, false otherwise
     */
    public static boolean safeInSubgrid(int[][] grid, int r, int c, int value) {
        int nRegions = (int) Math.sqrt(grid.length);
        int sRow = r - r % nRegions;
        int sCol = c - c % nRegions;

        for (int i = sRow; i < sRow + nRegions; i++) {
            for (int j = sCol; j < sCol + nRegions; j++) {
                if (grid[i][j] == value) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Prints a Sudoku grid.
     * @param g an Integer[][] grid representation of a sudoku puzzle
     */
    public static void printGrid(Integer[][] g) {
        for (int r = 0 ; r < g.length; r++) {
            for (int c = 0 ; c < g[0].length; c++) {
                System.out.print(g[r][c] + " ");
            }
            System.out.print("\n");
        }
    }
}
