/**
 * Created by sujayt123 on 4/14/16.
 */

import junit.framework.TestCase;
import sudoku.ParSudokuSolver;
import sudoku.SeqSudokuGenerator;
import sudoku.SeqSudokuSolver;
import sudoku.SudokuHelper;

public class SudokuSolverPerformanceTest extends TestCase {

    public void testSudoku_9_9_EASY() throws Exception {
        double totalSeqTime = 0;
        double totalParTime = 0;
        long startTime, endTime;
        int[][] grid;
        for (int i = 0 ; i < 50; i++) {

            grid = SeqSudokuGenerator.pruneCompleteBoard(SeqSudokuGenerator.generateValidCompleteBoard(3), SudokuHelper.Difficulty.EASY);

            startTime = System.nanoTime();
            SeqSudokuSolver.solvePuzzle(grid);
            endTime = System.nanoTime();
            totalSeqTime += (endTime - startTime)/10000000.0;

            startTime = System.nanoTime();
            ParSudokuSolver.solvePuzzle(grid);
            endTime = System.nanoTime();
            totalParTime += (endTime - startTime)/10000000.0;
        }

        System.out.println("Duration seq (board solution) for 50 iterations for 9x9 E: "  + totalSeqTime);
        System.out.println("Duration par (board solution) for 50 iterations for 9x9 E: " + totalParTime);
        System.out.println("SpeedupE: TPar/TSeq = " + totalParTime/totalSeqTime);

    }

    public void testSudoku_9_9_DIFFICULT() throws Exception {
        double totalSeqTime = 0;
        double totalParTime = 0;
        long startTime, endTime;
        int[][] grid;
        for (int i = 0 ; i < 50; i++) {

            grid = SeqSudokuGenerator.pruneCompleteBoard(SeqSudokuGenerator.generateValidCompleteBoard(3), SudokuHelper.Difficulty.DIFFICULT);

            startTime = System.nanoTime();
            SeqSudokuSolver.solvePuzzle(grid);
            endTime = System.nanoTime();
            totalSeqTime += (endTime - startTime)/10000000.0;

            startTime = System.nanoTime();
            ParSudokuSolver.solvePuzzle(grid);
            endTime = System.nanoTime();
            totalParTime += (endTime - startTime)/10000000.0;
        }

        System.out.println("Duration seq (board solution) for 50 iterations for 9x9 D: "  + totalSeqTime + " ms");
        System.out.println("Duration par (board solution) for 50 iterations for 9x9 D: " + totalParTime + " ms");
        System.out.println("SpeedupD: TPar/TSeq = " + totalParTime/totalSeqTime);

    }

    public void testSudoku_9_9_EVIL() throws Exception {
        double totalSeqTime = 0;
        double totalParTime = 0;
        long startTime, endTime;
        int[][] grid;
        for (int i = 0 ; i < 50; i++) {

            grid = SeqSudokuGenerator.pruneCompleteBoard(SeqSudokuGenerator.generateValidCompleteBoard(3), SudokuHelper.Difficulty.DIFFICULT);

            startTime = System.nanoTime();
            SeqSudokuSolver.solvePuzzle(grid);
            endTime = System.nanoTime();
            totalSeqTime += (endTime - startTime)/10000000.0;

            startTime = System.nanoTime();
            ParSudokuSolver.solvePuzzle(grid);
            endTime = System.nanoTime();
            totalParTime += (endTime - startTime)/10000000.0;
        }

        System.out.println("Duration seq (board solution) for 10 iterations for 9x9 V: "  + totalSeqTime);
        System.out.println("Duration par (board solution) for 10 iterations for 9x9 V: " + totalParTime);
        System.out.println("SpeedupV: TPar/TSeq = " + totalParTime/totalSeqTime);


    }

}
