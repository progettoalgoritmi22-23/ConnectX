import connectx.CXBoard;
import connectx.Player.GameTree;
import connectx.Player.MyPlayer;
import connectx.Player.Node;
import connectx.Player.Utils;

public class TreeTest {
    // Testa la costruzione dell'albero
    public static void main(String[] args) {
        CXBoard board = new CXBoard(3, 3, 2);
        GameTree tree = new GameTree(board, true);
        //tree.buildWholeTreeIterative();
        //tree.buildWholeTree();
        //tree.buildTree(10);
        tree.buildTreeIterative(5);
        System.out.println("Nodes: " + tree.getNodesCount());
        //System.out.println("Root max children: " + tree.getRoot().getMaxChildrenCount());
        Utils.printTree(tree.getRoot(), 5);
    }

    
}