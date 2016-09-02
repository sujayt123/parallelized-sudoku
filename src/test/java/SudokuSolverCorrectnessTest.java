/**
 * Created by sujayt123 on 4/14/16.
 */

import junit.framework.TestCase;
import sudoku.ParSudokuSolver;
import sudoku.SeqSudokuSolver;
import sudoku.SeqSudokuGenerator;
import sudoku.SudokuHelper;


import static sudoku.BoardValidationHelper.*;

public class SudokuSolverCorrectnessTest extends TestCase {

    public void testSudoku_9_9() throws Exception {
        int[][] grid;
        for (int i = 0; i < 3; i++) {
            grid = SeqSudokuGenerator.pruneCompleteBoard(SeqSudokuGenerator.generateValidCompleteBoard(3), SudokuHelper.Difficulty.EVIL);
            assertTrue(isValid(SeqSudokuSolver.solvePuzzle(grid)));
            assertTrue(isValid(ParSudokuSolver.solvePuzzle(grid)));
        }
    }
}
