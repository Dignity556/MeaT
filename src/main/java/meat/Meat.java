package meat;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import graph.Node;
import query.Query;

import java.util.ArrayList;
import java.util.Iterator;
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
        System.out.println("meat构建完成");
    }

    // TODO 同样应该从根节点往下遍历，目前直接使用gnl
    @Override
    public boolean singleTransactionQuery(String transactionId, String nodeId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            MerkleGraphTree mgt = block.getMgt();
            GraphNodeLink gnl = mgt.getGnl();
            Map<Node, GraphNodeLinkItem> items = gnl.getItems();
            if (items.containsKey(new Node(nodeId))) {
                GraphNodeLinkItem item = items.get(new Node(nodeId));
                // 下层查询
                GraphLeaf root = item.getRoot();
                boolean res = root.singleTransactionQuery(transactionId);
                if (res) return true;
            }
        }
        return false;
    }

    @Override
    public boolean nodeQueryBySingleBlock(String nodeId, String blockId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            if (block.getId().equals(blockId)) {
                MerkleGraphTree mgt = block.getMgt();
                GraphNodeLink gnl = mgt.getGnl();
                for (Node node : gnl.getItems().keySet()) {
                    if (node.getNodeId().equals(nodeId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // TODO 目前采用直接获取gnl的方法进行查询，标准应该从根节点dfs或bfs
    @Override
    public boolean nodeQueryByAllBlock(String nodeId) {
        List<Block> blocks = context.getBlocks();
        List<Transaction> txs = new ArrayList<>();
        for (Block block : blocks) {
            MerkleGraphTree mgt = block.getMgt();
            GraphNodeLink gnl = mgt.getGnl();
            for (Node node : gnl.getItems().keySet()) {
                if (node.getNodeId().equals(nodeId)) {
                   txs.addAll(gnl.getItems().get(node).getTransactions());
                }
            }
        }
        if (!txs.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean propertyQueryBySingleBlock(Map<String, String> queries, String blockId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            if (block.getId().equals(blockId)) {
                MerkleGraphTree mgt = block.getMgt();
                List<Transaction> txs = mgt.getRoot().propertyRangeQuery(queries);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean propertyQueryByAllBlock(Map<String, String> queries) {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            MerkleGraphTree mgt = block.getMgt();
            res.addAll(mgt.getRoot().propertyRangeQuery(queries));
        }
        if (!res.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean propertyRangeQueryBySingleBlock(Map<String, String> queries, String blockId, int topK) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            if (block.getId().equals(blockId)) {
                MerkleGraphTree mgt = block.getMgt();
                // topk为0表示不使用topk，作范围查询
                if (topK == 0) {
                    List<Transaction> txs = mgt.getRoot().propertyRangeQuery(queries);
                    return true;
                } else {
                    for (String type : queries.keySet()) {
                        List<Transaction> transactions = mgt.getRoot().propertyQueryTopK(type, topK);
                        if (!transactions.isEmpty()) {
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }

    @Override
    public boolean propertyRangeQueryByAllBlock(Map<String, String> queries, int topK) {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            MerkleGraphTree mgt = block.getMgt();
            if (topK == 0) {
                res.addAll(mgt.getRoot().propertyRangeQuery(queries));
            } else {
                for (String type : queries.keySet()) {
                    res.addAll(mgt.getRoot().propertyQueryTopK(type, topK));
                }
            }
        }
        if (!res.isEmpty()) {
            return true;
        }
        return false;
    }


}
