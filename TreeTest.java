import connectx.CXBoard;
import connectx.Player.GameTree;
import connectx.Player.MyPlayer;
import connectx.Player.Node;
import connectx.Player.Utils;

public class TreeTest {
    // Testa la costruzione dell'albero
    public static void main(String[] args) {
        CXBoard board = new CXBoard(4, 4, 4);// Più colonne ho e più tempo ci mette
        GameTree tree = new GameTree(board, true);
        // tree.buildWholeTreeIterative();
       //tree.buildWholeTree();
         tree.buildTree(16);
        //tree.buildWholeTreeIterative();
        // tree.buildTreeIterative(5);
        System.out.println("Nodes: " + tree.getNodesCount());
        // System.out.println("Root max children: " +
        // tree.getRoot().getMaxChildrenCount());
        // Utils.printTree(tree.getRoot(), 5);
    }

}