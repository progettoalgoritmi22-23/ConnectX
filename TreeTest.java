import connectx.CXBoard;
import connectx.Player.GameTree;
import connectx.Player.Node;

public class TreeTest {
    // Testa la costruzione dell'albero
    public static void main(String[] args) {
        CXBoard board = new CXBoard(4, 4, 3);
        GameTree tree = new GameTree(board, true);
        //tree.buildWholeTreeIterative();
        //tree.buildWholeTree();
        //tree.buildTree(10);
        tree.buildTreeIterative(10);
        System.out.println("Nodes: " + tree.getNodesCount());
        //System.out.println("Root max children: " + tree.getRoot().getMaxChildrenCount());
        //printTree(tree.getRoot(), 1);
    }

    // Funzione ricorsiva per la stampa dell'albero
    public static void printTree(Node node, int depth) {
        // System.out.println(indent + "Column: " + node.getColumn());

        String indent = "  ";
        System.out.print(indent.repeat(depth));
        System.out.println("D: " + node.getDepth() + " - Label: " + node.getLabel() + " - Eval: " + node.getEval());

        for (Node child : node.getChildren()) {
            printTree(child, depth + 1);
        }
    }
}