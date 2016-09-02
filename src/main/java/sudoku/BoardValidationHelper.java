package sudoku;

import java.util.HashMap;

/**
 * Created by sujayt123 on 4/15/16.
 */
public class BoardValidationHelper {

    public static boolean isValid(Integer[][] grid) {
        return isValidAlongRows(grid) && isValidAlongCols(grid) && isValidinSubgrids(grid);
    }

    public static boolean isValidAlongRows(Integer[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            HashMap<Integer, Boolean> isInRow = new HashMap<>();
            for (int j = 0; j < grid[0].length; j++) {
                // If we encounter a key that we've visited previously in the row...
                if (grid[i][j] != 0 && isInRow.containsKey(grid[i][j])) {
                    System.out.println("Not valid in rows");
                    return false;
                }
                isInRow.put(grid[i][j], true);
            }
        }
        return true;
    }

    public static boolean isValidAlongCols(Integer[][] grid) {
        for (int i = 0; i < grid[0].length; i++) {
            HashMap<Integer, Boolean> isInRow = new HashMap<>();
            for (int j = 0; j < grid.length; j++) {
                // If we encounter a key that we've visited previously in the column...
                if (grid[j][i] != 0 && isInRow.containsKey(grid[j][i])) {
                    System.out.println("Not valid in cols");
                    return false;
                }
                isInRow.put(grid[j][i], true);
            }
        }
        return true;
    }

    public static boolean isValidinSubgrids(Integer[][] grid) {
        int nRegions = (int) Math.sqrt(grid.length);
        // A pair of (i, j) determines a unique subgrid of the board.
        for (int i = 0; i < nRegions; i++) {
            for (int j = 0; j < nRegions; j++) {
                int startRow = i * nRegions;
                int endRow = (i + 1) * nRegions - 1;
                int startCol = j * nRegions;
                int endCol = (j + 1) * nRegions - 1;
                HashMap<Integer, Boolean> isInSubgrid = new HashMap<>();
                for (int k = startRow; k <= endRow; k++) {
                    for (int l = startCol; l <= endCol; l++) {
                        if (grid[k][l] != 0 && isInSubgrid.containsKey(grid[k][l])) {
                            System.out.println("Not valid in subgrids");
                            return false;
                        }
                        isInSubgrid.put(grid[k][l], true);
                    }
                }
            }
        }
        return true;
    }
}
