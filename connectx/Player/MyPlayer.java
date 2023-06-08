package connectx.Player;

import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;
import connectx.CXCell;
import java.util.TreeSet;
import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/**
 * Software player only a bit smarter than random.
 * <p>
 * It can detect a single-move win or loss. In all the other cases behaves
 * randomly.
 * </p>
 */

public class MyPlayer implements CXPlayer {
    private Random rand;
    private CXGameState myWin;
    private CXGameState yourWin;
    private int TIMEOUT;
    private long START;

    // Tree class
    private class Tree {

    }

    /* Default empty constructor */
    public MyPlayer() {
    }

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        // New random seed for each game
        rand = new Random(System.currentTimeMillis());
        myWin = first ? CXGameState.WINP1 : CXGameState.WINP2;
        yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
        TIMEOUT = timeout_in_secs;
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
       
        return 0;
    }

    // Evaluation function to assign a score to a board state
    private int evaluateBoard(CXBoard board) {
        // Assign scores based on the current state of the board
        // For example, count the number of player's pieces in winning positions
        // and subtract the number of opponent's pieces in winning positions

        // Return a positive score if it's a favorable state for the player
        // and a negative score if it's an unfavorable state
        // You can experiment with different evaluation heuristics

        return 0;
    }


    private void checktime() throws TimeoutException {
        if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (99.0 / 100.0))
            throw new TimeoutException();
    }

    public String playerName() {
        return "MyPlayer";
    }
}
