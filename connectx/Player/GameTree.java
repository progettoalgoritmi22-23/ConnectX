package connectx.Player;

import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;
import connectx.CXCell;
import java.util.TreeSet;
import java.util.Random;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;
import connectx.Player.Evaluation;
import java.util.List;

public class GameTree {
    private Node root; // Nodo radice
    private int idCounter = 0; // Contatore per gli id dei nodi
    private HashMap<String, Node> nodesMap = new HashMap<String, Node>(); // Tabella hash per la ricerca dei nodi, così
                                                                          // gli stati di gioco non vengono duplicati.
                                                                          // La chiave è la stringa dello stato di
                                                                          // gioco (board.toString()), il valore è il
                                                                          // nodo corrispondente
    private Hashing hashing; // Zobrist hashing

    private int generateUniqueId() {
        idCounter += 1;
        return idCounter;
    }

    // Costruttore
    public GameTree(CXBoard board, boolean first) {
        hashing = new Hashing(board);
        this.root = new Node(board, generateUniqueId(), first, hashing.hashBoard(board));
        nodesMap.put(board.toString(), this.root);
        hashing = new Hashing(board);
    }

    // Aggiunge un nodo alla tabella hash
    public void addNodeToNodesMap(Node node) {
        nodesMap.put(node.getBoard().toString(), node);
    }

    // Restituisce il nodo corrispondente allo stato di gioco
    public Node getNodeFromNodesMap(CXBoard board) {
        return nodesMap.get(board.toString());
    }

    // Elimina un nodo dalla tabella hash
    public void removeNodeFromNodesMap(Node node) {
        nodesMap.remove(node.getBoard().toString());
    }

    public boolean isFirstPlayer() {
        return MyPlayer.isFirstPlayer();
    }

    // Costruisce l'albero radicato fino a che tutti i nodi al livello più profondo
    // sono terminali (WINP1 || WINP2 || DRAW)
    public void buildWholeTree() {
        buildWholeSubTree(this.root);
    }

    // Costruisce un sottoalbero a partire da un nodo fino a che tutti i nodi al
    // livello più profondo sono terminali
    private void buildWholeSubTree(Node node) {
        // Verifico se lo stato di gioco è già presente nella tabella hash
        if (nodesMap.containsKey(node.getBoard().toString())) {
            return; // Se è già presente, non lo aggiungo, e non continuo a costruire il sottoalbero
                    // relativo a quel nodo
        } else {
            addNodeToNodesMap(node);
        }

        // Controllo se ho raggiunto il limite di profondità, o se il nodo è terminale
        // (ovvero che la partite è finita, non che è una foglia)
        if (node.getBoard().gameState() == CXGameState.WINP1
                || node.getBoard().gameState() == CXGameState.WINP2 || node.getBoard().gameState() == CXGameState.DRAW)
            return;

        // Il massimo di figli per nodo è pari a il numero di colonne disponibili
        Integer[] moves = node.getBoard().getAvailableColumns(); // Mosse possibili per il nodo
        for (int move : moves) {
            CXBoard myBoard = node.getBoard().copy();
            myBoard.markColumn(move); // Aggiorno la board con la nuova mossa

            Node child = new Node(myBoard, node, generateUniqueId(), move, this.isFirstPlayer(),
                    hashing.hashBoard(myBoard)); // Creo il nodo
            // figlio
            node.addChild(child); // Aggiungo il nodo figlio al nodo padre
            buildWholeSubTree(child); // Richiamo la funzione ricorsivamente, decrementando la profondità
        }
    }

    // Costruisce un sottoalbero a partire da un nodo fino a che tutti i nodi al
    // livello più profondo sono terminali iterativamente
    public void buildWholeTreeIterative() {
        Stack<Node> stack = new Stack<Node>();

        stack.push(this.root);

        while (!stack.isEmpty()) {
            Node node = stack.pop();

            // Verifico se lo stato di gioco è già presente nella tabella hash
            if (nodesMap.containsKey(node.getBoard().toString())) {
                continue;// Se è già presente, non lo aggiungo, e non continuo a costruire il sottoalbero
                         // relativo a quel nodo
            } else {
                addNodeToNodesMap(node);
            }

            // Controllo se il nodo è terminale (ovvero che la partite è finita, non che
            // è una foglia)
            if (node.getBoard().gameState() == CXGameState.WINP1
                    || node.getBoard().gameState() == CXGameState.WINP2
                    || node.getBoard().gameState() == CXGameState.DRAW)
                continue;

            Integer[] moves = node.getBoard().getAvailableColumns(); // Mosse possibili per il nodo
            // Il massimo di figli per nodo è pari a il numero di colonne disponibili
            for (int move : moves) {
                CXBoard myBoard = node.getBoard().copy();
                myBoard.markColumn(move); // Aggiorno la board con la nuova mossa

                Node child = new Node(myBoard, node, generateUniqueId(), move, this.isFirstPlayer(),
                        hashing.hashBoard(myBoard)); // Creo il nodo
                node.addChild(child); // Aggiungo il nodo figlio al nodo padre
                stack.push(child); // Aggiungo il nodo figlio allo stack
            }
        }
    }

    // Costruisce l'albero radicato fino a una profondità data
    public void buildTree(int depth) {
        buildSubTree(this.root, depth);
    }

    // Costruisce un sottoalbero a partire da un nodo fino a una profondità data
    // iterativamente
    public void buildTreeIterative(int depth) {
        Stack<Node> stack = new Stack<>();
        HashSet<Long> visitedNodes = new HashSet<Long>();// Insieme dei nodi visitati

        stack.push(root);
        visitedNodes.add(root.getZobristHash());

        while (!stack.isEmpty()) {
            Node node = stack.pop();

            /*
             * System.out.println("Nodo: " + node.getBoard().toString() + " Profondità: " +
             * node.getDepth() + " Label: "
             * + node.getLabel());
             */

            if (node.getDepth() == depth || node.getBoard().gameState() == CXGameState.WINP1 ||
                    node.getBoard().gameState() == CXGameState.WINP2 ||
                    node.getBoard().gameState() == CXGameState.DRAW) {
                continue;
            }

            Integer[] moves = node.getBoard().getAvailableColumns();
            for (int move : moves) {
                CXBoard myBoard = node.getBoard().copy();

                System.out.println("Pre mark: " + hashing.hashBoard(myBoard));

                myBoard.markColumn(move);

                System.out.println("Post mark: " + hashing.hashBoard(myBoard));

                // Verifico se lo stato di gioco è già presente nella tabella hash dei nodi
                // visitati
                if (visitedNodes.contains(hashing.hashBoard(myBoard))) {
                    continue; // Se è già presente, non lo aggiungo, e non continuo a costruire il sottoalbero
                              // relativo a quel nodo
                } else {
                    System.out.println(
                            "Aggiungo il nodo " + hashing.hashBoard(myBoard) + " alla tabella hash dei nodi vistati");
                    visitedNodes.add(hashing.hashBoard(myBoard));
                }

                /*
                 * // Verifico se il nodo è già stato visitato
                 * if (visitedNodes.contains(myBoard.toString())) {
                 * continue;
                 * } else {
                 * visitedNodes.add(myBoard.toString());
                 * }
                 */

                Node child = new Node(myBoard, node, generateUniqueId(), move, this.isFirstPlayer(),
                        hashing.hashBoard(myBoard));
                node.addChild(child);

                stack.push(child);
            }
        }
    }

    // Costruisce un sottoalbero a partire da un nodo
    public void buildSubTree(Node node, int depth) {
        // Verifico se lo stato di gioco è già presente nella tabella hash
        if (nodesMap.containsKey(node.getBoard().hashCode())) {
            return; // Se è già presente, non lo aggiungo, e non continuo a costruire il sottoalbero
                    // relativo a quel nodo
        } else {
            addNodeToNodesMap(node);
        }

        // Controllo se ho raggiunto il limite di profondità, o se il nodo è terminale
        // (ovvero che la partite è finita, non che è una foglia)
        if (depth == 0 || node.getBoard().gameState() == CXGameState.WINP1
                || node.getBoard().gameState() == CXGameState.WINP2 || node.getBoard().gameState() == CXGameState.DRAW)
            return;

        Integer[] moves = node.getBoard().getAvailableColumns(); // Mosse possibili per il nodo
        // Il massimo di figli per nodo è pari a il numero di colonne disponibili
        for (int move : moves) {
            CXBoard myBoard = node.getBoard().copy();
            myBoard.markColumn(move); // Aggiorno la board con la nuova mossa

            Node child = new Node(myBoard, node, generateUniqueId(), move, this.isFirstPlayer(),
                    hashing.hashBoard(myBoard)); // Creo il nodo
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

    // Restituisce il numero di nodi totali
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
