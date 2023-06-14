package connectx.Player;

public class Utils {
    // Funzione ricorsiva per la stampa dell'albero
    public static void printTree(Node node, int depth) {
        // System.out.println(indent + "Column: " + node.getColumn());

        String indent = "  ";
        System.out.print(indent.repeat(depth));
        System.out.print("D: " + node.getDepth());
        System.out.print(" - Label: " + node.getLabel());
        System.out.print(" - Eval: " + node.getEval());
        System.out.print(" - isMax: " + node.getIsMaximizing());
        //System.out.print(" - Alpha: " + node.getAlpha());
        //System.out.print(" - Beta: " + node.getBeta());
        //System.out.print(" - LEAF: " + node.isLeaf());
        System.out.println();
        for (Node child : node.getChildren()) {
            printTree(child, depth + 1);
        }
    }
}
