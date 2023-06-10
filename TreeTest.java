import connectx.CXBoard;
import connectx.Player.GameTree;
import connectx.Player.Node;

public class TreeTest {
    // Testa la costruzione dell'albero
    public static void main(String[] args) {
        CXBoard board = new CXBoard(6, 7, 4);
        GameTree tree = new GameTree(board);
        tree.buildTree(1);
        System.out.println("Nodes: " + tree.getNodesCount());
        System.out.println("Root max children: " + tree.getRoot().getMaxChildrenCount());
        printTree(tree.getRoot(), 1);
    }

    // Funzione ricorsiva per la stampa dell'albero
    public static void printTree(Node node, int depth) {
        // System.out.println(indent + "Column: " + node.getColumn());
        if (node.isRoot() && node.isLeaf()) {
            System.out.println("D: " + depth + ", Root and Leaf");
            return;
        }

        if (node.isRoot() && !node.isLeaf()) {
            System.out.println("D: " + depth + ", Root");
        }

        if (node.isLeaf() && !node.isRoot()) {
            System.out.println("D: " + depth + ", Leaf");
            return;
        }

        for (Node child : node.getChildren()) {
            printTree(child, depth + 1);
        }
    }
}
