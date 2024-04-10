package meat;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import graph.Node;
import query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 顶级类，只应该存在创建方法与查询方法
 */
public class Meat implements Query {
    private Context context;
    public void createMeat(Context context) {
        MerkleGraphTree.createMerkleGraphTree(context);
        this.context = context;
    }

    @Override
    public boolean singleTransactionQuery(String transactionId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            MerkleGraphTree mgt = block.getMgt();
            GraphNodeLink gnl = mgt.getGnl();
            for (GraphNodeLinkItem item : gnl.getItems().values()) {
                // 下层查询
                GraphLeaf root = item.getRoot();
                boolean res = root.singleTransactionQuery(transactionId);
                if (res) return true;
            }
        }
        return false;
    }

    // TODO 目前采用直接获取gnl的方法进行查询，标准应该从根节点dfs或bfs
    @Override
    public boolean singleNodeQuery(String nodeId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            MerkleGraphTree mgt = block.getMgt();
            GraphNodeLink gnl = mgt.getGnl();
            for (Node node : gnl.getItems().keySet()) {
                if (node.getNodeId().equals(nodeId)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean propertyRangeQuery(Map<String, String> queries) {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            MerkleGraphTree mgt = block.getMgt();
            List<Transaction> txs = mgt.getRoot().propertyQuery(queries);
            res.addAll(txs);
        }
        if (res.size() != 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mixQuery() {
        return true;
    }
}
