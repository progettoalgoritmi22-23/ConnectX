package connectx.Player;

public class Utils {
    // Funzione ricorsiva per la stampa dell'albero
    public static void printTree(Node node, int depth) {
        // System.out.println(indent + "Column: " + node.getColumn());

        String indent = "  ";
        System.out.print(indent.repeat(depth));
        System.out.println("D: " + node.getDepth() + " - Label: " + node.getLabel() + " - Eval: " + node.getEval()+ " - isMax = " + node.getIsMaximizing());

        for (Node child : node.getChildren()) {
            printTree(child, depth + 1);
        }
    }
}
