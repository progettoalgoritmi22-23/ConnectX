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
    public static int evaluate(Node node, boolean isFirstPlayer) {
        // Ottengo la board dal nodo
        CXBoard board = node.getBoard().copy();

        /*
         * Controllo se la partita è finita e restituisco il valore della valutazione
         * corrispondente.
         * Se è il primo giocatore (devo massimizzare), restituisco il valore massimo,
         * altrimenti il valore minimo (devo minimizzare)
         */

        // Analizzo vittorie
        if (board.gameState() == CXGameState.WINP1) {
            if (isFirstPlayer) {
                return Integer.MIN_VALUE;
            } else
                return Integer.MAX_VALUE;
        } else if (board.gameState() == CXGameState.WINP2) {
            if (isFirstPlayer) {
                return Integer.MAX_VALUE;
            } else
                return Integer.MIN_VALUE;
        }

        // Calcolo la valutazione in base alla strategia scelta
        int evaluation = 0;

        return evaluation;
    }
}
