import junit.framework.TestCase;
import sudoku.ParSudokuGenerator;
import sudoku.SeqSudokuGenerator;
import sudoku.SudokuHelper;

/**
 * Created by sujayt123 on 4/15/16.
 */
public class SudokuGeneratorPerformanceTest extends TestCase {

    public void testSudoku_9_9_EASY() throws Exception {
        double totalSeqTime = 0;
        double totalParTime = 0;
        long startTime, endTime;
        for (int i = 0 ; i < 50; i++) {

            startTime = System.nanoTime();
            SeqSudokuGenerator.pruneCompleteBoard(SeqSudokuGenerator.generateValidCompleteBoard(3), SudokuHelper.Difficulty.EASY);
            endTime = System.nanoTime();
            totalSeqTime += (endTime - startTime)/10000000.0;

            startTime = System.nanoTime();
            ParSudokuGenerator.pruneCompleteBoard(ParSudokuGenerator.generateValidCompleteBoard(3), SudokuHelper.Difficulty.EASY);
            endTime = System.nanoTime();
            totalParTime += (endTime - startTime)/10000000.0;
        }

        System.out.println("Duration seq (board generation) for 50 iterations for 9x9: "  + totalSeqTime + " ms.");
        System.out.println("Duration par (board generation) for 50 iterations for 9x9: " + totalParTime + " ms.");
        System.out.println("Speedup: TPar/TSeq = " + totalParTime/totalSeqTime);
    }

    public void testSudoku_9_9_DIFFICULT() throws Exception {
        double totalSeqTime = 0;
        double totalParTime = 0;
        long startTime, endTime;
        for (int i = 0 ; i < 50; i++) {

            startTime = System.nanoTime();
            SeqSudokuGenerator.pruneCompleteBoard(SeqSudokuGenerator.generateValidCompleteBoard(3), SudokuHelper.Difficulty.DIFFICULT);
            endTime = System.nanoTime();
            totalSeqTime += (endTime - startTime)/10000000.0;

            startTime = System.nanoTime();
            ParSudokuGenerator.pruneCompleteBoard(ParSudokuGenerator.generateValidCompleteBoard(3), SudokuHelper.Difficulty.DIFFICULT);
            endTime = System.nanoTime();
            totalParTime += (endTime - startTime)/10000000.0;
        }

        System.out.println("Duration seq (board generation) for 50 iterations for 9x9: "  + totalSeqTime + " ms.");
        System.out.println("Duration par (board generation) for 50 iterations for 9x9: " + totalParTime + " ms.");
        System.out.println("Speedup: TPar/TSeq = " + totalParTime/totalSeqTime);
    }


    public void testSudoku_9_9_EVIL() throws Exception {
        double totalSeqTime = 0;
        double totalParTime = 0;
        long startTime, endTime;
        for (int i = 0 ; i < 50; i++) {

            startTime = System.nanoTime();
            SeqSudokuGenerator.pruneCompleteBoard(SeqSudokuGenerator.generateValidCompleteBoard(3), SudokuHelper.Difficulty.EVIL);
            endTime = System.nanoTime();
            totalSeqTime += (endTime - startTime)/10000000.0;

            startTime = System.nanoTime();
            ParSudokuGenerator.pruneCompleteBoard(ParSudokuGenerator.generateValidCompleteBoard(3), SudokuHelper.Difficulty.EVIL);
            endTime = System.nanoTime();
            totalParTime += (endTime - startTime)/10000000.0;
        }

        System.out.println("Duration seq (board generation) for 50 iterations for 9x9: "  + totalSeqTime + " ms.");
        System.out.println("Duration par (board generation) for 50 iterations for 9x9: " + totalParTime + " ms.");
        System.out.println("Speedup: TPar/TSeq = " + totalParTime/totalSeqTime);
    }
}
