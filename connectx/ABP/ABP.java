//Enrico Ferraiolo 0001020354

package connectx.ABP;

import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;
import connectx.CXCellState;

import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * Giocatore ABP, esso implementa l'algoritmo Alpha-Beta Pruning con iterative
 * deepening e limite sulla profondità per cercare la mossa migliore da giocare.
 */
public class ABP implements CXPlayer {
    private Random rand;
    private CXGameState myWin;
    private CXGameState yourWin;
    private int TIMEOUT;
    private long START;

    private boolean first; // ci dice anche se siamo il player massimizzante o minimizzante

    private final int WINVALUE = 10000000;
    private final int DRAWVALUE = 100;

    private boolean ranOutOfTime_move = false;// true se sono finito fuori tempo durante iterativeDeepening

    private int playerNumber;// 0 se sono il primo a muovere, 1 se sono il secondo
    private int opponentNumber;// 1 se sono il primo a muovere, 0 se sono il secondo

    /* Constructor */
    public ABP() {
    }

    // O(1)
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

    /**
     * Seleziona la miglior colonna da giocare eseguendo iterative deepening con
     * alpha-beta pruning e limite sulla profondità
     * O(𝐿𝑑 * 𝑚^𝑑)
     */
    public int selectColumn(CXBoard B) {
        START = System.currentTimeMillis(); // Save starting time

        Integer[] L = B.getAvailableColumns();

        int bestcol = L[rand.nextInt(L.length)]; // scelgo la prima colonna in modo casuale
        int bestEval = first ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int eval = 0;

        CXBoard myBoard = B.copy();// copio la board per non modificare quella originale

        for (int col : L) {
            final int depth = 10;

            myBoard.markColumn(col);

            try {
                // !first perché dopoo aver fatto la mossa il turno passa all'avversario
                eval = iterativeDeepening(myBoard, !first, depth, L.length);
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

    /*
     * Iterative deepening con limite sulla profondità
     * O(m^d)*d = O(d*m^d), m = numero di mosse possibili, d = profondità
     */
    private int iterativeDeepening(CXBoard Board, boolean isMaxPlayer, int depth, int maxMoves)
            throws TimeoutException {
        checktime();

        int bestEval = 0;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        // il tempo massimo per mossa
        long maxTimePerMove = (TIMEOUT - 1) / maxMoves;
        // tempo limite
        long endTime = System.currentTimeMillis() + maxTimePerMove;

        for (int d = 0; d < depth; d++) {
            if (timeIsRunningOut(maxTimePerMove, endTime)) {
                break;
            }

            int eval = alphaBeta(Board, isMaxPlayer, alpha, beta, d);

            // mentre sono nel limite di tempo
            bestEval = eval;

            // se trovo una mossa che mi fa vincere la ritorno subito
            if (eval >= WINVALUE || eval <= -WINVALUE) {
                return eval;
            }

        }

        ranOutOfTime_move = false; // resetto

        return bestEval;
    }

    /*
     * Ritorna true se il tempo sta per scadere (o è già scaduto)
     * O(1)
     */
    private boolean timeIsRunningOut(long moveTime, long endTime) {
        if (System.currentTimeMillis() >= endTime + moveTime) {
            ranOutOfTime_move = true;
            return true;
        }
        return false;
    }

    /*
     * Alpha-Beta Pruning con limite sulla profondità
     * O(m^d) + O(MNK) = O(m^d), m = numero di mosse possibili, d = profondità
     */
    private int alphaBeta(CXBoard Board, boolean isMaxPlayer, int alpha, int beta, int depth) {
        int eval = evaluateBoard(Board, isMaxPlayer);

        if (depth == 0 || Board.gameState() != CXGameState.OPEN || eval >= WINVALUE || eval <= -WINVALUE) {
            return eval;
        }

        // Massimizzante
        if (isMaxPlayer) {
            for (int col : Board.getAvailableColumns()) {
                eval = Integer.MIN_VALUE;
                Board.markColumn(col);
                eval = Math.max(eval, alphaBeta(Board, false, alpha, beta, depth - 1));
                alpha = Math.max(alpha, eval);
                Board.unmarkColumn();

                if (beta <= alpha) {
                    break;
                }
            }

            return alpha;
        }
        // Minimizzante
        else {
            for (int col : Board.getAvailableColumns()) {
                eval = Integer.MAX_VALUE;
                Board.markColumn(col);
                eval = Math.min(beta, alphaBeta(Board, true, alpha, beta, depth - 1));
                beta = Math.min(beta, eval);
                Board.unmarkColumn();

                if (beta <= alpha) {
                    break;
                }
            }

            return beta;
        }
    }

    /*
     * Valuta la board in base al numero di celle allineate per ogni giocatore,
     * inoltre valuta la board quando si trova in uno stato di vittoria o sconfitta
     * o patta.
     * O(1) + 2*O(MNK) = O(MNK)
     */
    private int evaluateBoard(CXBoard Board, boolean myTurn) {
        // check win
        if (Board.gameState() != CXGameState.OPEN) {
            if (Board.gameState() == CXGameState.WINP1) {
                return WINVALUE;
            } else if (Board.gameState() == CXGameState.WINP2) {
                return -WINVALUE;
            } else if (Board.gameState() == CXGameState.DRAW) {
                if (myTurn) {
                    if (first)
                        return DRAWVALUE; // Massimizzo
                    else
                        return -DRAWVALUE;
                } else {
                    if (first)
                        return -DRAWVALUE; // Minimizzo
                    else
                        return DRAWVALUE;
                }

            }
        }

        int myPlayerCount = 0;
        int opponentCount = 0;

        myPlayerCount = countAlignedCells(Board, playerNumber);
        opponentCount = countAlignedCells(Board, opponentNumber);

        return myPlayerCount - opponentCount;
    }

    /*
     * Conta le celle di un giocatore dato, ritorna il massimo numero di celle
     * allineate: prende in considerazione le celle allineate in verticale,
     * orizzontale, diagonale e anti-diagonale.
     * O(MNK), K=min(M,N)
     */
    private int countAlignedCells(CXBoard B, int player) {
        CXCellState[] playerCell = { CXCellState.P1, CXCellState.P2 }; // array di CXCellState per accedere al giocatore
                                                                       // corrente
        int count = 0;

        int maxVerticalCells = 0;
        int maxHorizontalCells = 0;
        int maxDiagonalCells = 0;
        int maxAntiDiagonalCells = 0;

        // Verticale, O(NM)
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

        // Orizzontale O(MN)
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

        // Diagonale O(MN*min(M,N)) = O(MNK), K=min(M,N)
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

        // Anti-Diagonale O(MN*min(M,N)) = O(MNK), K=min(M,N)
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

    /*
     * O(1)
     */
    private void checktime() throws TimeoutException {
        if ((System.currentTimeMillis() - START) / 1000.0 >= TIMEOUT * (99.0 / 100.0))
            throw new TimeoutException();
    }

    public String playerName() {
        return "ABP";
    }
}
