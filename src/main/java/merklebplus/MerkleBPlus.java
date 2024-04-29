package merklebplus;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import merkle.MerkleTree;
import query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 顶级类，应该只包含创建与查询方法
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MerkleBPlus implements Query {
    private Context context;
    public void createMerkleBPlus(Context context) {
        this.context = context;
        MerkleBPlusTree.createMerkleBPlusTree(context);
        System.out.println("merkle B+树构建完成");
    }

    @Override
    public boolean singleTransactionQuery(String transactionId, String nodeId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
            Transaction transaction = bPlusTree.getBPlusTree().singleTransactionQuery(transactionId);
            if (transaction != null && transaction.getStartNode().getNodeId().equals(nodeId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean nodeQueryBySingleBlock(String nodeId, String blockId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            if (block.getId().equals(blockId)) {
                MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
                List<Transaction> transactions = bPlusTree.getBPlusTree().singleNodeQuery(nodeId);
                if (!transactions.isEmpty()) return true;
            }
        }
        return false;
    }

    @Override
    public boolean nodeQueryByAllBlock(String nodeId) {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            BPlusTree<Transaction, Integer> bPlusTree = block.getMerkleBPlusTree().getBPlusTree();
            res.addAll(bPlusTree.singleNodeQuery(nodeId));
        }
        if (!res.isEmpty()) return true;
        return false;
    }

    @Override
    public boolean propertyQueryBySingleBlock(Map<String, String> queries, String blockId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            if (block.getId().equals(blockId)) {
                MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
                List<Transaction> transactions = bPlusTree.getBPlusTree().propertyQuery(queries);
                if (!transactions.isEmpty()) return true;
            }
        }
        return false;
    }

    @Override
    public boolean propertyQueryByAllBlock(Map<String, String> queries) {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
            res.addAll(bPlusTree.getBPlusTree().propertyQuery(queries));
        }
        if (!res.isEmpty()) return true;
        return false;
    }

    @Override
    public boolean propertyRangeQueryBySingleBlock(Map<String, String> queries, String blockId, int topK) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            if (block.getId().equals(blockId)) {
                MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
                if (topK == 0) {
                    List<Transaction> transactions = bPlusTree.getBPlusTree().propertyQuery(queries);
                    if (!transactions.isEmpty()) return true;
                } else {
                    for (String type : queries.keySet()) {
                        List<Transaction> transactions = bPlusTree.getBPlusTree().propertyQueryTopK(type, topK);
                        if (!transactions.isEmpty()) return true;
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
            MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
            if (topK == 0) {
                res.addAll(bPlusTree.getBPlusTree().propertyQuery(queries));
            } else {
                for (String type : queries.keySet()) {
                    res.addAll(bPlusTree.getBPlusTree().propertyQueryTopK(type, topK));
                }
            }
        }
        if (!res.isEmpty()) return true;
        return false;
    }

    @Override
    public int nodeAccessQuery(String sourceId, String targetId) {
        return 0;
    }
}
