/*
 *  Copyright (C) 2022 Lamberto Colazzo
 *  
 *  This file is part of the ConnectX software developed for the
 *  Intern ship of the course "Information technology", University of Bologna
 *  A.Y. 2021-2022.
 *
 *  ConnectX is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This  is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details; see <https://www.gnu.org/licenses/>.
 */

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

/**
 * Software player only a bit smarter than random.
 * <p>
 * It can detect a single-move win or loss. In all the other cases behaves
 * randomly.
 * </p>
 */
public class MyPlayer2 implements CXPlayer {
    private Random rand;
    private CXGameState myWin;
    private CXGameState yourWin;
    private int TIMEOUT;
    private long START;

    private boolean first; // ci dice anche se siamo il player massimizzante o minimizzante

    private final int WINVALUE = 10000000;

    private boolean time_cutoff = false;

    private int playerNumber;// 0 se sono il primo a muovere, 1 se sono il secondo
    private int opponentNumber;// 1 se sono il primo a muovere, 0 se sono il secondo

    /* Default empty constructor */
    public MyPlayer2() {
    }

    public void initPlayer(int M, int N, int K, boolean first, int timeout_in_secs) {
        // New random seed for each game
        rand = new Random(System.currentTimeMillis());
        myWin = first ? CXGameState.WINP1 : CXGameState.WINP2;
        yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
        TIMEOUT = timeout_in_secs;
        this.first = first;
        playerNumber = first ? 0 : 1;
        opponentNumber = first ? 1 : 0;
    }

    /*
     * Mescola l'array di colonne in modo casuale così ogni colonna è equiprobabile
     * O(n)
     */
    private void fisherYates(Integer[] L) {
        for (int i = L.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = L[i];
            L[i] = L[j];
            L[j] = temp;
        }
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

        Integer[] L = B.getAvailableColumns();
        fisherYates(L); // mescola colonne in modo casuale

        int bestcol = L[0]; // scelgo la prima colonna in modo casuale
        int bestEval = first ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int eval = 0;

        CXBoard myBoard = B.copy();// copio la board per non modificare quella originale

        for (int col : L) {
            long maxMoveTime = (TIMEOUT - 1) / L.length;
            myBoard.markColumn(col);

            try {
                eval = iterativeDeepening(myBoard, maxMoveTime);
            } catch (TimeoutException e) {
                return bestcol;
            }

            myBoard.unmarkColumn();

            if (first) {
                if (eval >= WINVALUE) {
                    return col;
                }

                if (eval > bestEval) {
                    bestEval = eval;
                    bestcol = col;
                }
            } else {
                if (eval <= -WINVALUE) {
                    return col;
                }

                if (eval < bestEval) {
                    bestEval = eval;
                    bestcol = col;
                }
            }
        }

        return bestcol;
    }

    // Iterative deepening senza depth limit, o meglio: esso è dato da un tempo che decido
    // massimo
    private int iterativeDeepening(CXBoard Board, long maxTime) throws TimeoutException {
        checktime();

        int depth = 1;
        int bestEval = 0;
        time_cutoff = false;

        long endTime = System.currentTimeMillis() + maxTime;
        boolean flag = true;

        while (flag) {
            long startTime = System.currentTimeMillis();

            if (startTime >= endTime) {
                flag = false;
            }

            int eval = alphaBeta(Board, Integer.MIN_VALUE, Integer.MAX_VALUE, depth, startTime,
                    endTime - startTime);

            if (eval >= WINVALUE) {
                return eval;
            }

            if (time_cutoff == false) {
                bestEval = eval;
            }

            depth++;
        }

        time_cutoff = false;

        return bestEval;
    }

    private int alphaBeta(CXBoard Board, int alpha, int beta, int depth, long startTime,
            long maxTime) throws TimeoutException {

        boolean isMaxPlayer = Board.currentPlayer() == 0;

        int eval = evaluateBoard(Board, isMaxPlayer);

        // controllo se ho superato il tempo massimo
        if (System.currentTimeMillis() - startTime >= maxTime) {
            time_cutoff = true;
            return eval;
        }

        if (depth == 0 || Board.gameState() != CXGameState.OPEN || eval >= WINVALUE || eval <= -WINVALUE) {
            return eval;
        }

        if (isMaxPlayer) {
            for (int col : Board.getAvailableColumns()) {
                eval = Integer.MIN_VALUE;
                Board.markColumn(col);
                eval = Math.max(eval, alphaBeta(Board, alpha, beta, depth - 1, startTime, maxTime));
                alpha = Math.max(alpha, eval);
                Board.unmarkColumn();

                if (beta <= alpha) {
                    break;
                }
            }

            return alpha;
        } else {
            for (int col : Board.getAvailableColumns()) {
                eval = Integer.MAX_VALUE;
                Board.markColumn(col);
                eval = Math.min(beta, alphaBeta(Board, alpha, beta, depth - 1, startTime, maxTime));
                beta = Math.min(beta, eval);
                Board.unmarkColumn();

                if (beta <= alpha) {
                    break;
                }
            }

            return beta;
        }

    }

    private void printNode(CXBoard Board, int eval) {

    }

    // valuta la board e restituisce un valore in base al giocatore corrente
    private int evaluateBoard(CXBoard Board, boolean myTurn) {
        // check win
        if (Board.gameState() != CXGameState.OPEN) {
            if (Board.gameState() == CXGameState.WINP1) {
                // System.out.println("WINP1: (" + Board.getLastMove().i + ", " +
                // Board.getLastMove().j + ")");
                return WINVALUE;
            } else if (Board.gameState() == CXGameState.WINP2) {
                // System.out.println("WINP2: (" + Board.getLastMove().i + ", " +
                // Board.getLastMove().j + ")");
                return -WINVALUE;
            }
        }

        int myPlayerCount = 0;
        int opponentCount = 0;

        myPlayerCount = countAlignedCells(Board, playerNumber);
        opponentCount = countAlignedCells(Board, opponentNumber);

        // System.out.println("Mie pedine allineate: " + myPlayerCount);
        // System.out.println("Avversario pedine allineate: " + opponentCount);

        return myPlayerCount - opponentCount;
    }

    // conta le celle di un giocatore dato, ritorna il massimo numero di celle
    // allineate
    private int countAlignedCells(CXBoard B, int player) {
        CXCellState[] playerCell = { CXCellState.P1, CXCellState.P2 }; // array di CXCellState per accedere al giocatore
                                                                       // corrente
        int count = 0;

        int maxVerticalCells = 0;
        int maxHorizontalCells = 0;
        int maxDiagonalCells = 0;
        int maxAntiDiagonalCells = 0;

        // Verticale
        for (int i = 0; i < B.N; i++) {
            int foundCells = 0;

            for (int j = 0; j < B.M; j++) {
                if (B.cellState(j, i) == playerCell[player]) {
                    foundCells++;
                    if (foundCells > maxVerticalCells) {
                        maxVerticalCells = foundCells;
                    }
                } else {
                    foundCells = 0;
                }
            }
        }

        count = maxVerticalCells;

        // Orizzontale
        for (int i = 0; i < B.M; i++) {
            int foundCells = 0;

            for (int j = 0; j < B.N; j++) {
                if (B.cellState(i, j) == playerCell[player]) {
                    foundCells++;
                    if (foundCells > maxHorizontalCells) {
                        maxHorizontalCells = foundCells;
                    }
                } else {
                    foundCells = 0;
                }
            }
        }

        count = Math.max(count, maxHorizontalCells);

        // Diagonale
        for (int i = 0; i < B.M; i++) {
            for (int j = 0; j < B.N; j++) {
                int foundCells = 0;
                int k = 0;

                while (i + k < B.M && j + k < B.N) {
                    if (B.cellState(i + k, j + k) == playerCell[player]) {
                        foundCells++;
                        if (foundCells > maxDiagonalCells) {
                            maxDiagonalCells = foundCells;
                        }
                    } else {
                        foundCells = 0;
                    }
                    k++;
                }
            }
        }

        count = Math.max(count, maxDiagonalCells);

        // Anti-Diagonale
        for (int i = 0; i < B.M; i++) {
            for (int j = 0; j < B.N; j++) {
                int foundCells = 0;
                int k = 0;

                while (i + k < B.M && j - k >= 0) {
                    if (B.cellState(i + k, j - k) == playerCell[player]) {
                        foundCells++;
                        if (foundCells > maxAntiDiagonalCells) {
                            maxAntiDiagonalCells = foundCells;
                        }
                    } else {
                        foundCells = 0;
                    }
                    k++;
                }
            }
        }

        count = Math.max(count, maxAntiDiagonalCells);

        return count;
    }

    private void checktime() throws TimeoutException {
        if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (99.0 / 100.0))
            throw new TimeoutException();
    }

    public String playerName() {
        return "notL1";
    }
}
