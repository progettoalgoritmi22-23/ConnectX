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

    private boolean first;

    private int alpha = Integer.MIN_VALUE;
    private int beta = Integer.MAX_VALUE;

    private final int WINVALUE = 1000000;

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

        int bestcol = L[0]; // scelgo la prima colonn a caso
        int eval;

        int maxScore = first ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        CXBoard myBoard = B.copy();

        for (int col : L) {
            myBoard.markColumn(col);
            int depth = 10;
            try {
                checktime();
                eval = IterativeDeepening(myBoard, depth);
            } catch (TimeoutException e) {
                return bestcol;
            }

            myBoard.unmarkColumn();

            if (eval == WINVALUE) {
                return col;
            }
            if (eval > maxScore) {
                maxScore = eval;
                bestcol = col;
            } else {
                if (eval <= -WINVALUE)
                    return col;
                if (eval < maxScore) {
                    maxScore = eval;
                    bestcol = col;
                }
            }
        }

        return bestcol;

        /*
         * try {
         * int col = singleMoveWin(B, L);
         * if (col != -1)
         * return col;
         * else
         * return singleMoveBlock(B, L);
         * } catch (TimeoutException e) {
         * System.err.println("Timeout!!! Random column selected");
         * return save;
         * }
         */
    }

    private int IterativeDeepening(CXBoard Board, int depth) {
        int eval = 0;
        for (int d = 1; d < depth; d++) {
            try {
                checktime();
                eval = alphaBeta(Board, first, Integer.MIN_VALUE, Integer.MAX_VALUE, d);
            } catch (TimeoutException e) {
                return eval;
            }
        }
        return eval;
    }

    private int alphaBeta(CXBoard Board, boolean player, int alpha, int beta, int depth) {
        Integer L[] = Board.getAvailableColumns(); // colonne disponibili

        int eval = evaluate(Board);

        if (depth == 0 || Board.gameState() != CXGameState.OPEN) {
            return eval;
        }

        if (player) {
            for (int col : L) {
                Board.markColumn(col);
                eval = Math.max(eval, alphaBeta(Board, !player, alpha, beta, depth - 1));
                Board.unmarkColumn();
                alpha = Math.max(eval, alpha);
                if (beta <= alpha)
                    break;
            }
        } else {
            for (int col : L) {
                Board.markColumn(col);
                eval = alphaBeta(Board, !player, alpha, beta, depth - 1);
                Board.unmarkColumn();
                beta = Math.min(beta, eval);
                if (beta <= alpha)
                    break;
            }
        }

        return eval;
    }

    private int evaluate(CXBoard Board) {
        if (Board.gameState() != CXGameState.OPEN) {
            if (Board.gameState() == myWin)
                return WINVALUE;
            else if (Board.gameState() == yourWin)
                return -WINVALUE;
        }
        return 0;
    }

    private void checktime() throws TimeoutException {
        if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (99.0 / 100.0))
            throw new TimeoutException();
    }

    /**
     * Check if we can win in a single move
     *
     * Returns the winning column if there is one, otherwise -1
     */
    private int singleMoveWin(CXBoard B, Integer[] L) throws TimeoutException {
        for (int i : L) {
            checktime(); // Check timeout at every iteration
            CXGameState state = B.markColumn(i);
            if (state == myWin)
                return i; // Winning column found: return immediately
            B.unmarkColumn();
        }
        return -1;
    }

    /**
     * Check if we can block adversary's victory
     *
     * Returns a blocking column if there is one, otherwise a random one
     */
    private int singleMoveBlock(CXBoard B, Integer[] L) throws TimeoutException {
        TreeSet<Integer> T = new TreeSet<Integer>(); // We collect here safe column indexes

        for (int i : L) {
            checktime();
            T.add(i); // We consider column i as a possible move
            B.markColumn(i);

            int j;
            boolean stop;

            for (j = 0, stop = false; j < L.length && !stop; j++) {
                // try {Thread.sleep((int)(0.2*1000*TIMEOUT));} catch (Exception e) {} //
                // Uncomment to test timeout
                checktime();
                if (!B.fullColumn(L[j])) {
                    CXGameState state = B.markColumn(L[j]);
                    if (state == yourWin) {
                        T.remove(i); // We ignore the i-th column as a possible move
                        stop = true; // We don't need to check more
                    }
                    B.unmarkColumn(); //
                }
            }
            B.unmarkColumn();
        }

        if (T.size() > 0) {
            Integer[] X = T.toArray(new Integer[T.size()]);
            return X[rand.nextInt(X.length)];
        } else {
            return L[rand.nextInt(L.length)];
        }
    }

    public String playerName() {
        return "L1";
    }
}
