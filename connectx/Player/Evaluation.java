package connectx.Player;

import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;
import connectx.CXCell;
import connectx.CXCellState;
import java.util.TreeSet;
import java.util.Random;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

public class Evaluation {
    public static int evaluate(Node node) {
        /*
         * Assign scores based on the current state of the board
         * For example, count the number of player's pieces in winning positions
         * and subtract the number of opponent's pieces in winning positions
         * Return a positive score if it's a favorable state for the player
         * and a negative score if it's an unfavorable state
         * You can experiment with different evaluation heuristics
         */
        int score = 0;



        return score;
    }
}
