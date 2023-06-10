package connectx.Player;

import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;
import connectx.CXCell;
import java.util.TreeSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import connectx.Player.Evaluation;
import java.util.List;

public class GameTree {
    private Node root; // Nodo radice
    private int idCounter = 0; // Contatore per gli id dei nodi

    private int generateUniqueId() {
        idCounter += 1;
        return idCounter;
    }

    // Costruttore
    public GameTree(CXBoard board) {
        this.root = new Node(board, generateUniqueId());
    }

    // Costruisce l'albero radicato fino a una profondità dataF
    public void buildTree(int depth) {
        buildSubTree(this.root, depth);
    }

    // Costruisce un sottoalbero a partire da un nodo
    public void buildSubTree(Node node, int depth) {
        // Controllo se ho raggiunto il limite di profondità, o se il nodo è terminale (ovvero che la partite è finita, non che è una foglia)
        if (depth == 0 || node.getBoard().gameState() == CXGameState.WINP1
                || node.getBoard().gameState() == CXGameState.WINP2 || node.getBoard().gameState() == CXGameState.DRAW)
            return;

        Integer[] moves = node.getBoard().getAvailableColumns(); // Mosse possibili per il nodo
        // Il massimo di figli per nodo è pari a il numero di colonne disponibili
        for (int move : moves) {
            CXBoard myBoard = node.getBoard().copy();
            myBoard.markColumn(move); // Aggiorno la board con la nuova mossa

            Node child = new Node(myBoard, node, generateUniqueId(), move); // Creo il nodo
                                                                            // figlio
            node.addChild(child); // Aggiungo il nodo figlio al nodo padre

            buildSubTree(child, depth - 1); // Richiamo la funzione ricorsivamente, decrementando la profondità
        }
    }

    // Restituisce la radice
    public Node getRoot() {
        return this.root;
    }

    // Restituisce il numero di nodi totali
    public int getNodesCount() {
        return countNodes(this.root);
    }

    private int countNodes(Node node) {
        if (node == null)
            return 0;
        if (node.isLeaf())
            return 1;

        int count = 1; // Conto il nodo corrente
        for (Node child : node.getChildren()) {
            count += countNodes(child);
        }
        return count;
    }
}
