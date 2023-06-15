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
    private HashMap<Long, Node> nodesMap = new HashMap<Long, Node>(); // Tabella hash per la ricerca dei nodi, così
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
        addNodeToNodesMap(root);
    }

    // Restituisce la hash table
    public HashMap<Long, Node> getHashTable() {
        return nodesMap;
    }

    // Aggiunge un nodo alla tabella hash
    public void addNodeToNodesMap(Node node) {
        nodesMap.put(hashing.hashBoard(node.getBoard()), node);
    }

    // Restituisce il nodo corrispondente allo stato di gioco
    public Node getNodeFromNodesMap(CXBoard board) {
        return nodesMap.get(hashing.hashBoard(board));
    }

    // Elimina un nodo dalla tabella hash
    public void removeNodeFromNodesMap(Node node) {
        nodesMap.remove(hashing.hashBoard(node.getBoard()));
    }

    public boolean isFirstPlayer() {
        return MyPlayer.isFirstPlayer();
    }

    // Costruisce l'albero radicato fino a che tutti i nodi al livello più profondo
    // sono terminali (WINP1 || WINP2 || DRAW), è ricorsivo.
    public void buildWholeTree() {
        buildWholeSubTree(this.root);
    }

    // Costruisce un sottoalbero a partire da un nodo fino a che tutti i nodi al
    // livello più profondo sono terminali, è ricorsivo.
    private void buildWholeSubTree(Node node) {
        addNodeToNodesMap(node);

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
                    hashing.hashBoard(myBoard)); // Creo il nodo figlio

            // Verifico se lo stato di gioco è già presente nella tabella hash
            if (nodesMap.containsKey(hashing.hashBoard(myBoard))) {
                continue; // Se è già presente, non lo aggiungo, e non continuo a costruire il sottoalbero
                          // relativo a quel nodo
            } else {
                node.addChild(child); // Aggiungo il nodo figlio al nodo padre
                buildWholeSubTree(child); // Richiamo la funzione ricorsivamente
                addNodeToNodesMap(child);
            }
        }
    }

    // Costruisce un sottoalbero a partire da un nodo fino a che tutti i nodi al
    // livello più profondo sono terminali iterativamente
    public void buildWholeTreeIterative() {
        Stack<Node> stack = new Stack<>();

        stack.push(root);
        addNodeToNodesMap(root);

        while (!stack.isEmpty()) {
            Node node = stack.pop();

            if (node.getBoard().gameState() == CXGameState.WINP1 ||
                    node.getBoard().gameState() == CXGameState.WINP2 ||
                    node.getBoard().gameState() == CXGameState.DRAW) {
                continue;
            }

            Integer[] moves = node.getBoard().getAvailableColumns();
            for (int move : moves) {
                CXBoard myBoard = node.getBoard().copy();

                myBoard.markColumn(move);

                Node child = new Node(myBoard, node, generateUniqueId(), move, this.isFirstPlayer(),
                        hashing.hashBoard(myBoard));// Creo il nodo figlio

                // Verifico se lo stato di gioco è già presente nella tabella hash dei nodi
                // visitati
                if (nodesMap.containsKey(hashing.hashBoard(myBoard))) {
                    continue; // Se è già presente, non lo aggiungo, e non continuo a costruire il sottoalbero
                              // relativo a quel nodo
                } else {
                    // System.out.println("Aggiungo il nodo " + hashing.hashBoard(myBoard) + " alla
                    // tabella hash dei nodi vistati");
                    addNodeToNodesMap(child);
                    node.addChild(child);
                    stack.push(child);
                }

            }
        }
    }

    // Costruisce un sottoalbero a partire da un nodo fino a una profondità data
    // iterativamente
    public void buildTreeIterative(int depth) {
        Stack<Node> stack = new Stack<>();

        stack.push(root);
        addNodeToNodesMap(root);

        while (!stack.isEmpty()) {
            Node node = stack.pop();

            if (node.getDepth() == depth || node.getBoard().gameState() == CXGameState.WINP1 ||
                    node.getBoard().gameState() == CXGameState.WINP2 ||
                    node.getBoard().gameState() == CXGameState.DRAW) {
                continue;
            }

            Integer[] moves = node.getBoard().getAvailableColumns();
            for (int move : moves) {
                CXBoard myBoard = node.getBoard().copy();

                myBoard.markColumn(move);

                Node child = new Node(myBoard, node, generateUniqueId(), move, this.isFirstPlayer(),
                        hashing.hashBoard(myBoard));// Creo il nodo figlio

                // Verifico se lo stato di gioco è già presente nella tabella hash dei nodi
                // visitati
                if (nodesMap.containsKey(hashing.hashBoard(myBoard))) {
                    continue; // Se è già presente, non lo aggiungo, e non continuo a costruire il sottoalbero
                              // relativo a quel nodo
                } else {
                    // System.out.println("Aggiungo il nodo " + hashing.hashBoard(myBoard) + " alla
                    // tabella hash dei nodi vistati");
                    addNodeToNodesMap(child);
                    node.addChild(child);
                    stack.push(child);
                }

            }
        }
    }

    // Costruisce l'albero radicato fino a una profondità data, è ricorsivo
    public void buildTree(int depth) {
        buildSubTree(this.root, depth);
    }

    // Costruisce un sottoalbero a partire da un nodo, è ricorsivo
    public void buildSubTree(Node node, int depth) {
        addNodeToNodesMap(node);

        // Controllo se ho raggiunto il limite di profondità, o se il nodo è terminale
        // (ovvero che la partite è finita, non che è una foglia)
        if (depth == 0 || node.getBoard().gameState() == CXGameState.WINP1
                || node.getBoard().gameState() == CXGameState.WINP2 || node.getBoard().gameState() == CXGameState.DRAW)
            return;

        // Il massimo di figli per nodo è pari a il numero di colonne disponibili
        Integer[] moves = node.getBoard().getAvailableColumns(); // Mosse possibili per il nodo
        for (int move : moves) {
            CXBoard myBoard = node.getBoard().copy();
            myBoard.markColumn(move); // Aggiorno la board con la nuova mossa

            Node child = new Node(myBoard, node, generateUniqueId(), move, this.isFirstPlayer(),
                    hashing.hashBoard(myBoard)); // Creo il nodo figlio

            // Verifico se lo stato di gioco è già presente nella tabella hash
            if (nodesMap.containsKey(hashing.hashBoard(myBoard))) {
                continue; // Se è già presente, non lo aggiungo, e non continuo a costruire il sottoalbero
                          // relativo a quel nodo
            } else {
                node.addChild(child); // Aggiungo il nodo figlio al nodo padre
                buildSubTree(child, depth - 1); // Richiamo la funzione ricorsivamente, decrementando la profondità
                addNodeToNodesMap(child);
            }
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
