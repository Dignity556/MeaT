package meat;

import blockchain.Block;
import blockchain.Transaction;
import graph.Edge;
import graph.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraphLeaf {
    // id本身无实际意义
    private String id;
    private byte[] hashId;
    private Block beLongBlock;
    private Edge edge;
    private GraphLeaf father;
    private GraphLeaf left;
    private GraphLeaf right;
    private Node subTreeNode; //上层mgt中叶子结点与实际node相连

    // 只有根节点才与pst连接
    private PSTExtensionNode extensionNode;

    public GraphLeaf(Edge edge) throws NoSuchAlgorithmException {
        this.edge = edge;
        this.beLongBlock = edge.getBlock();
        this.hashId = calculateSHA256(edge.getId());
    }

    public static byte[] calculateSHA256(String input) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static GraphLeaf edgeToLeaf(Edge edge) throws NoSuchAlgorithmException {
        GraphLeaf graphLeaf = new GraphLeaf();
        graphLeaf.setBeLongBlock(edge.getBlock());
        graphLeaf.setEdge(edge);
        graphLeaf.setHashId(calculateSHA256(edge.getId()));
        // TODO 设置subtreenode为startnode
        graphLeaf.setSubTreeNode(edge.getStartNode());
        return graphLeaf;
    }

    public static GraphLeaf transactionToLeaf(Transaction transaction) {
        GraphLeaf graphLeaf = new GraphLeaf();
        graphLeaf.setBeLongBlock(transaction.getBeLongBlock());
        graphLeaf.setHashId(calculateSHA256(transaction.getId().toString()));
        // TODO 设置subtreenode为startnode，转化为交易不需要该字段
        // graphLeaf.setSubTreeNode(transaction.getStartNode());
        return graphLeaf;
    }

    public static GraphLeaf nodeToLeaf(Node node) {
        GraphLeaf leaf = new GraphLeaf();
        leaf.setHashId(calculateSHA256(node.getNodeId()));
        // 设置关联的node
        leaf.setSubTreeNode(node);
        return leaf;
    }

    // 下层mgt的查询
    public boolean singleTransactionQuery(String txId) {
        if (dfs(this, txId) != null) {
            return true;
        } else {
            return false;
        }
    }
    private Transaction dfs(GraphLeaf leaf, String txId) {
        if (leaf.getLeft() != null && leaf.getEdge().getTransaction().getId().equals(txId)) {
            return leaf.getEdge().getTransaction();
        }
        if (leaf.getLeft() != null) {
            return dfs(leaf.getLeft(), txId);
        }
        if (leaf.getRight() != null) {
            return dfs(leaf.getRight(), txId);
        }
        return null;
    }
}
