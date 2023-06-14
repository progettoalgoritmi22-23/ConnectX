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

    /* Default empty constructor */
    public MyPlayer() {
    }

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        // New random seed for each game
        rand = new Random(System.currentTimeMillis());
        myWin = first ? CXGameState.WINP1 : CXGameState.WINP2;
        yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
        TIMEOUT = timeout_in_secs;
        this.first = first;
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
        GameTree tree = new GameTree(B, first);
        System.err.println("\nFIRST: " + first);
        final int MAX_DEPTH = 5;

        tree.buildTreeIterative(MAX_DEPTH);

        int bestScore = 0;
        int bestColumn = rand.nextInt(rand.nextInt(B.getAvailableColumns().length));// Intanto scelgo una colonna a caso

        /*
         * FIXME:
         * java connectx.CXGame 3 3 3 connectx.Player.MyPlayer
         * 
         * FIRST: true
         * Best score: -2147483648
         * Best column: 1
         * 
         * FIRST: true
         * Best score: -2147483648
         * Best column: 0
         * 
         * FIRST: true
         * Best score: -2147483648
         * Best column: 1
         * 
         * FIRST: true
         * Error: MyPlayer interrupted due to exception
         * java.util.concurrent.ExecutionException: java.lang.IllegalArgumentException:
         * bound must be positive
         */

        // Se sono il primo giocatore, devo massimizzare
        if (first) {
            bestScore = Integer.MIN_VALUE;

            for (Node child : tree.getRoot().getChildren()) {
                int score = minimax(child, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, first);
                // System.out.println("Score: " + score);
                if (score > bestScore) {
                    bestScore = score;
                    bestColumn = child.getColumn();
                }
            }
        }
        // Se sono il secondo giocatore, devo minimizzare
        else {
            bestScore = Integer.MAX_VALUE;

            for (Node child : tree.getRoot().getChildren()) {
                int score = minimax(child, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, first);
                // System.out.println("Score: " + score);
                if (score < bestScore) {
                    bestScore = score;
                    bestColumn = child.getColumn();
                }
            }
        }

        System.out.println("Best score: " + bestScore);
        System.out.println("Best column: " + bestColumn);
        // Utils.printTree(tree.getRoot(), MAX_DEPTH);

        return bestColumn;
    }

    // MiniMax con potatura Alpha-Beta
    public int minimax(Node node, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        if (depth == 0 || node.getBoard().gameState() != CXGameState.OPEN) {
            return node.getEval();
        }

        if (isMaximizingPlayer) {
            // System.out.println("+ Maximizing: " + " Depth: " + depth);
            int eval = Integer.MIN_VALUE;
            for (Node child : node.getChildren()) {
                eval = Math.max(eval, minimax(child, depth - 1, alpha, beta, false));
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break; // Potatura Alpha-Beta
                }
            }
            return eval;
        } else {
            // System.out.println("- Minimizing" + " Depth: " + depth);
            int eval = Integer.MAX_VALUE;
            for (Node child : node.getChildren()) {
                eval = Math.min(eval, minimax(child, depth - 1, alpha, beta, true));
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break; // Potatura Alpha-Beta
                }
            }
            return eval;
        }
    }

    public static void checktime() throws TimeoutException {
        if ((System.currentTimeMillis() - getStartTime()) / 1000.0 >= getTimeout() * (99.0 / 100.0))
            throw new TimeoutException();
    }

    public String playerName() {
        return "MyPlayer";
    }
}
