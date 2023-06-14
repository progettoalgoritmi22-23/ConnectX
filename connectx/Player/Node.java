package connectx.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

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
    public Node(CXBoard board, int id, boolean isMaximizing) {
        this.board = board;
        this.parent = null;
        this.children = new ArrayList<>();
        this.column = -1; // La radice non ha una colonna associata
        this.label = "root";
        this.id = id;
        this.currentDepth = 0;
        this.eval = 0; // Nessuna mossa è stata fatta, quindi non c'è valutazione
        this.isMaximizing = isMaximizing; // La radice è sempre un nodo massimizzante
    }

    // Costruttore per i nodi figli
    public Node(CXBoard board, Node parent, int id, int column, boolean isFirstPlayer) {
        this.board = board;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.column = column; // Ultima colonna giocata per arrivare al nodo corrente
        this.label = generateLabel(parent, column);
        this.id = id;
        this.currentDepth = parent.getDepth() + 1;
        try {
            this.eval = Evaluation.evaluate(this, isFirstPlayer);
        } catch (TimeoutException e) {
            // TODO riempire
            eval = 0; // non ho fatto in tempo a valutare il nodo
        }
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
            if (child != null) {
                childrenCount++;
            }
        }

        // Se tutti i nodi figli sono vuoti, il nodo è una foglia
        return childrenCount == 0;
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

    // Restituisce la colonna giocata per arrivare al nodo corrente
    public int getColumn() {
        return this.column;
    }

    // Restituisce, tra i figli, la colonna il cui nodo ha eval massimo
    public int bestNextColumn() {
        // Controllo se NON è un nodo foglia
        if (isLeaf() == false) {
            Random rand = new Random(System.currentTimeMillis());
            int bestColumn = rand.nextInt(rand.nextInt(getBoard().getAvailableColumns().length)); // Scelgo una colonna
                                                                                                  // a caso
            int bestEval = Integer.MIN_VALUE;
            for (Node child : getChildren()) {
                if (child.getEval() > bestEval) {
                    bestEval = child.getEval();
                    bestColumn = child.getColumn();
                }
            }
            return bestColumn;
        } else {
            return -1;
        }
    }
}
