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
    static class Priority {
        private boolean isFirstPlayer;// Ci serve perché il valore della valutazione cambia a seconda del giocatore
                                      // (se è il primo giocatore, devo massimizzare, altrimenti minimizzare)
        final public int P1 = 1000;// La mossa è fondamentale per non perdere o per pareggiare
        final public int P2 = 100;
        final public int P3 = 65;
        final public int P4 = 35;
        final public int P5 = 10;

        public Priority(boolean isFirstPlayer) {
            this.isFirstPlayer = isFirstPlayer;
        }

        // Restituisce la priorità della vittoria, winnerPlayer = 0 se ha vinto il
        // primo, 1 se ha vinto il secondo
        public int getWinPriority(int winnerPlayer) {
            if (winnerPlayer == 0) {
                if (isFirstPlayer) {
                    return Integer.MAX_VALUE;
                } else {
                    return Integer.MIN_VALUE;
                }
            } else {
                if (isFirstPlayer) {
                    return Integer.MIN_VALUE;
                } else {
                    return Integer.MAX_VALUE;
                }
            }
        }

        // Restituisce la priorità della mossa, è fondamentale
        public int getP1() {
            if (isFirstPlayer) {
                return P1;
            } else {
                return P1 * -1;
            }
        }

        public int getP2() {
            if (isFirstPlayer) {
                return P2;
            } else {
                return P2 * -1;
            }
        }

        public int getP3() {
            if (isFirstPlayer) {
                return P3;
            } else {
                return P3 * -1;
            }
        }

        public int getP4() {
            if (isFirstPlayer) {
                return P4;
            } else {
                return P4 * -1;
            }
        }

        public int getP5() {
            if (isFirstPlayer) {
                return P5;
            } else {
                return P5 * -1;
            }
        }

        // Restituisce la priorità n-esima, priorità 1 è la più importante, 4 la meno
        public int getPriority(int priorityLevel) {
            switch (priorityLevel) {
                case 1:
                    return getP1();
                case 2:
                    return getP2();
                case 3:
                    return getP3();
                case 4:
                    return getP4();
                case 5:
                    return getP5();
                default:
                    return 0;
            }
        }

        // Restituisce la priorità data in input in base al giocatore
        public int customPriority(int priority) {
            if (isFirstPlayer) {
                return priority;
            } else {
                return priority * -1;
            }
        }
    }

    public static int evaluate(Node node, boolean isFirstPlayer) {
        // Ottengo la board dal nodo
        CXBoard board = node.getBoard().copy();

        // Inizializzo le priorità
        Priority priority = new Priority(isFirstPlayer);

        /*
         * Controllo se la partita è finita e restituisco il valore della valutazione
         * corrispondente.
         * Se è il primo giocatore (devo massimizzare), restituisco il valore massimo,
         * altrimenti il valore minimo (devo minimizzare)
         */

         //FIXME: fai printtre e vedi che non da valori negativi
        // Analizzo vittorie
        if (board.gameState() == CXGameState.WINP1) {
            return priority.getWinPriority(0);
        } else if (board.gameState() == CXGameState.WINP2) {
            return priority.getWinPriority(1);
        } else if (board.gameState() == CXGameState.DRAW) {
            return priority.getPriority(1);
        }

        int evaluation = 0;

        // Linee aperte
        evaluation += evaluateLines(board, isFirstPlayer);

        // Controllo del centro
        evaluation += evaluateCenter(board);

        // Blocchi avversari
        evaluation += evaluateBlockOpponent(board, isFirstPlayer, priority);

        // Connessioni parziali
        evaluation += evaluatePartialConnections(board, isFirstPlayer);

        return evaluation;
    }

    private static int evaluateLines(CXBoard board, boolean isFirstPlayer) {
        return 0;
    }

    private static int evaluateCenter(CXBoard board) {
        return 0;
    }

    private static int evaluateBlockOpponent(CXBoard board, boolean isFirstPlayer, Priority priority) {
        int evaluation = 0;
        CXCellState opponentCellState = isFirstPlayer ? CXCellState.P2 : CXCellState.P1; // Stato del giocatore
                                                                                         // avversario
        int numRows = board.M; // Numero di righe della board
        int numCols = board.N; // Numero di colonne della board
        int X = board.X; // Numero di pedine da mettere in linea per vincere

        // Itera su tutte le colonne della board
        for (int col = 0; col < numCols; col++) {
            int emptyCount = 0; // Contatore delle caselle vuote
            int opponentCount = 0; // Contatore delle pedine avversarie

            // Analizza ogni riga all'interno della colonna
            for (int row = 0; row < numRows; row++) {
                CXCellState cellState = board.cellState(row, col);

                // Verifica se la cella contiene una pedina avversaria
                if (cellState == opponentCellState) {
                    opponentCount++;
                }
                // Verifica se la cella è vuota
                else if (cellState == CXCellState.FREE) {
                    emptyCount++;
                }
                // Se la cella contiene una pedina del giocatore corrente, interrompi l'analisi
                // della colonna
                else {
                    break;
                }
            }

            // Calcola l'aggiunta o la sottrazione del punteggio in base alle condizioni
            if (opponentCount > 0 && emptyCount > 0) {
                // Se ci sono pedine avversarie e caselle vuote, valuta la situazione
                if (opponentCount + emptyCount >= X) {
                    // Se il numero di pedine avversarie e caselle vuote può portare alla vittoria,
                    // assegna un punteggio positivo o negativo in base al giocatore corrente
                    evaluation += priority.customPriority(priority.P2 + opponentCount + emptyCount);
                } else if (opponentCount + emptyCount >= X - 1) {
                    // Se il numero di pedine avversarie e caselle vuote è vicino alla vincita,
                    // assegna un punteggio minore in base al giocatore corrente
                    evaluation += priority.customPriority(priority.P3 + opponentCount + emptyCount);
                } else {
                    // Altrimenti, assegna un punteggio ancora minore in base al giocatore corrente
                    evaluation += priority.customPriority(priority.P4 + opponentCount + emptyCount);
                }
            }

            // Controllo se ci sono solo caselle vuote
            if (opponentCount == 0 && emptyCount > 0) {
                if (emptyCount >= X) {
                    // Se il numero di caselle vuote può portare alla vittoria,
                    // assegna un punteggio positivo o negativo in base al giocatore corrente
                    evaluation += priority.customPriority(priority.P4);
                } else if (emptyCount >= X - 1) {
                    // Se il numero di caselle vuote è vicino alla vincita,
                    // assegna un punteggio minore in base al giocatore corrente
                    evaluation += priority.customPriority(priority.P5 + emptyCount);
                } else {
                    // Altrimenti, assegna un punteggio ancora minore in base al giocatore corrente
                    evaluation += priority.customPriority(priority.P5);
                }
            }
        }

        return evaluation;
    }

    private static int evaluatePartialConnections(CXBoard board, boolean isFirstPlayer) {
        return 0;
    }
}
