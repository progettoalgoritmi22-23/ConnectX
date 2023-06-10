import connectx.CXBoard;
import connectx.Player.GameTree;
import connectx.Player.Node;

public class TreeTest {
    // Testa la costruzione dell'albero
    public static void main(String[] args) {
        CXBoard board = new CXBoard(1, 3, 4);
        GameTree tree = new GameTree(board);
        tree.buildTree(3);
        System.out.println("Nodes: " + tree.getNodesCount());
        System.out.println("Root max children: " + tree.getRoot().getMaxChildrenCount());
        printTree(tree.getRoot(), 1);
    }

    // Funzione ricorsiva per la stampa dell'albero
    public static void printTree(Node node, int depth) {
        // System.out.println(indent + "Column: " + node.getColumn());

        String indent = "  ";
        System.out.print(indent.repeat(depth));
        System.out.println("D: " + node.getDepth() + " - Label: " + node.getLabel());

        for (Node child : node.getChildren()) {
            printTree(child, depth + 1);
        }
    }
}
