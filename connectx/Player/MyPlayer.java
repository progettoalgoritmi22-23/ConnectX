package connectx.Player;

import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;
import connectx.CXCell;
import java.util.TreeSet;
import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import connectx.Player.Evaluation;

/**
 * Software player only a bit smarter than random.
 * <p>
 * It can detect a single-move win or loss. In all the other cases behaves
 * randomly.
 * </p>
 */

public class MyPlayer implements CXPlayer {
    private Random rand;
    private static CXGameState myWin;
    private static CXGameState yourWin;
    private static int TIMEOUT;
    private static long START;
    private static boolean first;
    private GameTree tree;

    /* Default empty constructor */
    public MyPlayer() {
    }

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        // New random seed for each game
        rand = new Random(System.currentTimeMillis());
        myWin = first ? CXGameState.WINP1 : CXGameState.WINP2;
        yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
        TIMEOUT = timeout_in_secs;
        MyPlayer.first = first;
        tree = null;
    }

    public static CXGameState getMyWin() {
        return myWin;
    }

    public static CXGameState getYourWin() {
        return yourWin;
    }

    public static boolean isFirstPlayer() {
        return first;
    }

    // DEBUG
    public static long getStartTime() {
        return START;
    }

    // DEBUG
    public static int getTimeout() {
        return TIMEOUT;
    }

    /**
     * Selects a free colum on game board.
     * <p>
     * Selects a winning column (if any), otherwise selects a column (if any)
     * that prevents the adversary to win with his next move. If both previous
     * cases do not apply, selects a random column.
     * </p>
     */
    public int selectColumn(CXBoard B) {
        START = System.currentTimeMillis(); // Save starting time
        // Evaluation.evaluateBoard(B);
        CXBoard tmpBoard = B.copy();
        // GameTree tree = new GameTree(myBoard, first);
        System.err.println("\nFIRST: " + first);
        final int MAX_DEPTH = 5;
        int bestColumn = 0;
        // tree.buildTreeIterative(MAX_DEPTH);
        // tree.buildWholeTreeIterative();

        /*
         * //Print markedcells
         * for(CXCell cell : tmpBoard.getMarkedCells()){
         * System.err.println("Marked cell: " + cell.i + " " + cell.j);
         * }
         */

        // controllo se non è ancora stata fatta la prima mossa, quindi se non ho ancora
        // costruito l'albero
        if (tree == null) {
            // Controllo chi gioca per primo
            if (first) {
                // Piazzo la prima mossa al centro, è sempre la migliore per partire
                bestColumn = B.N / 2;
                tmpBoard.markColumn(bestColumn);
                tree = new GameTree(tmpBoard, first);
                tree.buildWholeTreeIterative();
            } else {
                tree = new GameTree(tmpBoard, first);
                tree.buildWholeTreeIterative();
                bestColumn = tree.nextMove();
            }
        } else {
            // sono nel caso in cui non è la prima mossa
            tree.updateMove(tmpBoard.getMarkedCells()[tmpBoard.getMarkedCells().length - 1].j);
            bestColumn = tree.nextMove();

        }
        // System.out.println("ultima col: " + tree.getRoot().getColumn());

        // int bestColumn = tree.nextMove();

        // System.out.println("Best score: " + bestScore);
        System.out.println("Best column: " + bestColumn);
        // Utils.printTree(tree.getRoot(), MAX_DEPTH);

        return bestColumn;
    }

    public static void checktime() throws TimeoutException {
        if ((System.currentTimeMillis() - getStartTime()) / 1000.0 >= getTimeout() * (99.0 / 100.0))
            throw new TimeoutException();
    }

    public String playerName() {
        return "MyPlayer";
    }
}
