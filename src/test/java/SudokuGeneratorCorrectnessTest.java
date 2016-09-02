/**
 * Created by sujayt123 on 4/15/16.
 */

import junit.framework.TestCase;
import sudoku.ParSudokuGenerator;
import sudoku.SeqSudokuGenerator;

import static sudoku.SudokuHelper.*;
import static sudoku.BoardValidationHelper.*;

public class SudokuGeneratorCorrectnessTest extends TestCase {

    public void testSudoku_4_4() throws Exception {
        int[][] grid;
        for (int i = 0 ; i < 5; i++) {
            grid = SeqSudokuGenerator.pruneCompleteBoard(SeqSudokuGenerator.generateValidCompleteBoard(2), Difficulty.EVIL);
            assertTrue(isValid(toTwoDIntegerArray(grid)));
            grid = ParSudokuGenerator.pruneCompleteBoard(ParSudokuGenerator.generateValidCompleteBoard(2), Difficulty.EVIL);
            assertTrue(isValid(toTwoDIntegerArray(grid)));
        }
    }

    public void testSudoku_9_9() throws Exception {
        int[][] grid;
        for (int i = 0 ; i < 5; i++) {
            grid = SeqSudokuGenerator.pruneCompleteBoard(SeqSudokuGenerator.generateValidCompleteBoard(3), Difficulty.EVIL);
            assertTrue(isValid(toTwoDIntegerArray(grid)));
            grid = ParSudokuGenerator.pruneCompleteBoard(ParSudokuGenerator.generateValidCompleteBoard(3), Difficulty.EVIL);
            assertTrue(isValid(toTwoDIntegerArray(grid)));
        }
    }

}
