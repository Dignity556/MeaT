package meat;

import blockchain.Transaction;
import graph.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraphNodeLinkItem {
    private Node node; // 关联的具体node
    private String id;

    // 连接的下层mgt
    private GraphLeaf root;

    public GraphNodeLinkItem(String id, Node node, GraphLeaf leaf) {
        this.id = id;
        this.node = node;
        this.root = leaf;
    }

    public List<Transaction> getTransactions() {
        GraphLeaf cur = root;
        List<Transaction> txs = new ArrayList<>();
        dfs(root, txs);
        return txs;
    }
    private void dfs(GraphLeaf node, List<Transaction> txs) {
        if (node != null && node.getEdge() != null) {
            txs.add(node.getEdge().getTransaction());
            return;
        }
        if (node.getLeft() != null) {
            dfs(node.getLeft(), txs);
        }
        if (node.getRight() != null) {
            dfs(node.getRight(), txs);
        }
    }
}
