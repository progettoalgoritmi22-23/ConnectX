package connectx.Player;

import java.util.Random;

import connectx.CXBoard;
import connectx.CXCellState;

public class Hashing {
    private static long[][] zobristHash;

    public Hashing(CXBoard board) {
        Random random = new Random();
        zobristHash = new long[board.M][board.N];
        for (int i = 0; i < board.M; i++) {
            for (int j = 0; j < board.N; j++) {
                zobristHash[i][j] = random.nextLong();
            }
        }
    }

    public long hashBoard(CXBoard board) {
        /*
         * long hash = 0;
         * for (int i = 0; i < board.M; i++) {
         * for (int j = 0; j < board.N; j++) {
         * if (board.cellState(i, j) == CXCellState.P1) {
         * hash ^= zobristHash.table[i][j][0];
         * } else if (board.cellState(i, j) == CXCellState.P2) {
         * hash ^= zobristHash.table[i][j][1];
         * }
         * }
         * }
         * return hash;
         */

        long currentHash = 0;
        for (int i = 0; i < board.M; i++) {
            for (int j = 0; j < board.N; j++) {
                if (board.cellState(i, j) != CXCellState.FREE) {
                    currentHash ^= zobristHash[i][j];
                }
            }
        }

        return currentHash;
    }
}