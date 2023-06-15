import connectx.CXBoard;
import connectx.Player.GameTree;
import connectx.Player.MyPlayer;
import connectx.Player.Node;
import connectx.Player.Utils;

public class TreeTest {
    // Testa la costruzione dell'albero
    public static void main(String[] args) {
        CXBoard board = new CXBoard(3, 3, 2);// Più colonne ho e più tempo ci mette
        GameTree tree = new GameTree(board, true);
        // tree.buildWholeTreeIterative();
        tree.buildWholeTree();
        // tree.buildTree(16);
        //tree.buildWholeTreeIterative();
        // tree.buildTreeIterative(5);
        System.out.println("Nodes: " + tree.getNodesCount());
        // System.out.println("Root max children: " +
        // tree.getRoot().getMaxChildrenCount());
        Utils.printTree(tree.getRoot(), 5);
        while (true) {
            System.out.println("Label pre next move: " + tree.getRoot().getLabel());
            System.out.println("Next move: " + tree.nextMove());
            System.out.println("Label post next move: " + tree.getRoot().getLabel() + "\n");
        }
        // System.out.println("Next move: " + nextCol);
    }

}

/*
 * FIXME:
 * - ALBERO NON HA PIù EVAL NEI NODI SOPRA
 * - ERRORE DOPO UN PO' DI MOSSE PERCHé NON ESISTE NEXTCHILD
 */