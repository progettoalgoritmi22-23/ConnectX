package connectx.Player;

import java.util.ArrayList;
import java.util.List;
import connectx.CXBoard;

public class Node {

    // Classe nodo
    private CXBoard board; // Board
    private Node parent; // Nodo padre, null se root
    private List<Node> children; // Nodi figli
    private int column; // Ultima colonna giocata per arrivare al nodo corrente, -1 se root

    // Costruttore per la radice
    public Node(CXBoard board, Node parent) {
        this.board = board;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.column = -1;
    }

    // Costruttore per i nodi figli
    public Node(CXBoard board, Node parent, int column) {
        this.board = board;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.column = column;
    }

    public CXBoard getBoard() {
        return this.board;
    }

    public Node getParent() {
        return this.parent;
    }

    public List<Node> getChildren() {
        return this.children;
    }

    public int getChildrenCount() {
        return this.children.size();
    }

    public boolean isLeaf() {
        // Controllo se tutti i nodi figli sono vuoti
        int childrenCount = 0;
        for (Node child : getChildren()) {
            if (child == null) {
                childrenCount++;
            }
        }

        // Se tutti i nodi figli sono vuoti, il nodo è una foglia
        return childrenCount == getChildrenCount();
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public void removeChild(Node child) {
        this.children.remove(child);
    }

    // Restituisce il numero massimo di figli (mosse) per nodo
    public int getMaxChildrenCount() {
        return this.board.getAvailableColumns().length;
    }

    public int getColumnplayed() {
        return this.column;
    }
}
