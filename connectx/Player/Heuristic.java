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

public class Heuristic {
    private class Player {
        int winningScore;
        int currentScore = 0;
        boolean isCurrentPlayer;
    }

    private class P1 extends Player {
        int winningScore = 100;
    }

    private class P2 extends Player {
        int winningScore = -100;

    }

    // Evaluation function to assign a score to a board state, P1 wins = +100, P2
    // wins = -100
    public static int evaluateBoard(CXBoard B) {

        /*
         * Assign scores based on the current state of the board
         * For example, count the number of player's pieces in winning positions
         * and subtract the number of opponent's pieces in winning positions
         * Return a positive score if it's a favorable state for the player
         * and a negative score if it's an unfavorable state
         * You can experiment with different evaluation heuristics
         */

        // Create two players
        P1 p1 = new Heuristic().new P1();
        P2 p2 = new Heuristic().new P2();

        int P1_CELL_VALUE = 10;
        int P2_CELL_VALUE = -10;
        int EMPTY_CELL_VALUE = 0;

        // Copy board to a temp one
        CXBoard myBoard = B.copy();

        // Now we must assign a score to each cell
        int boardValues[][] = new int[myBoard.M][myBoard.N]; // Create a 2D array to store the values of each cell

        // If a cell is empty, assign 0, if it is occupied by P1, assign 10, if it is
        // occupied by P2, assign -10
        for (int i = 0; i < myBoard.M; i++) {
            for (int j = 0; j < myBoard.N; j++) {
                if (myBoard.cellState(i, j) == CXCellState.FREE) {
                    boardValues[i][j] = EMPTY_CELL_VALUE;
                } else if (myBoard.cellState(i, j) == CXCellState.P1) {
                    boardValues[i][j] = P1_CELL_VALUE;
                } else if (myBoard.cellState(i, j) == CXCellState.P2) {
                    boardValues[i][j] = P2_CELL_VALUE;
                }
            }
        }

        prettyPrint(boardValues);

        // Check if there is a winning move for Player
        // P1 turn
        if (myBoard.currentPlayer() == 0) {
            // Check if there is a winning column

        }

        return 0;
    }

    // checkWin checks if the player has won, P1 = 0, P2 = 1
    public static boolean checkWin(CXBoard B, int player) {
        // Check if player has won
        if (player == 0) {
            if (B.gameState() == CXGameState.WINP1) {
                return true;
            }
        } else if (player == 1) {
            if (B.gameState() == CXGameState.WINP2) {
                return true;
            }
        }
        return false;
    }

    // Print the values of each cell in ASCII format with borders
    private static void prettyPrint(int[][] boardValues) {
    }

}
