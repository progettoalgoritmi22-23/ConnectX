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
    private int id; // Identificatore del nodo
    private String label; // Etichetta del nodo
    private int currentDepth; // Profondità del nodo
    private int eval = 0; // Valutazione del nodo
    private boolean isMaximizing; // Indica se il nodo è un nodo massimizzante o meno
    private int alpha = Integer.MIN_VALUE; // Valore alpha
    private int beta = Integer.MAX_VALUE; // Valore beta

    // Costruttore per la radice
    public Node(CXBoard board, int id) {
        this.board = board;
        this.parent = null;
        this.children = new ArrayList<>();
        this.column = -1;
        this.label = "root";
        this.id = id;
        this.currentDepth = 0;
        this.eval = 0; // Nessuna mossa è stata fatta, quindi non c'è valutazione
        this.isMaximizing = true; // La radice è sempre un nodo massimizzante
    }

    // Costruttore per i nodi figli
    public Node(CXBoard board, Node parent, int id, int column) {
        this.board = board;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.column = column;
        this.label = generateLabel(parent, column);
        this.id = id;
        this.currentDepth = parent.getDepth() + 1;
        this.eval = Evaluation.evaluate(this);
        this.isMaximizing = !parent.getIsMaximizing();
    }

    // Genera etichetta
    private String generateLabel(Node parent, int move) {
        String label = "";

        if (!parent.isRoot()) {
            label = parent.getLabel() + "-" + String.valueOf(move);
        } else {
            label = String.valueOf(move);
        }

        return label;
    }

    // Restituisce l'identificatore del nodo
    public int getId() {
        return this.id;
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

    public String getLabel() {
        return this.label;
    }

    public int getDepth() {
        return this.currentDepth;
    }

    public int getEval() {
        return this.eval;
    }

    public boolean getIsMaximizing() {
        return this.isMaximizing;
    }

    public int getAlpha() {
        return this.alpha;
    }

    public int getBeta() {
        return this.beta;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public void setBeta(int beta) {
        this.beta = beta;
    }
}
