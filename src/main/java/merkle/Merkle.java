package merkle;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 顶级类，应该只包含创建与查询方法
 */
public class Merkle implements Query {
    private Context context;
    public void createMerkle(Context context) {
        MerkleTree.createMerkleTree(context);
        this.context = context;
        System.out.println("merkle树构建完成");
    }
    @Override
    public boolean singleTransactionQuery(String transactionId, String nodeId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            long l1 = System.nanoTime();
            MerkleTree merkleTree = block.getMerkleTree();
            boolean res = merkleTree.singleTransactionQuery(transactionId, nodeId);
            long l2 = System.nanoTime();
            if (res) return true;
        }
        return false;
    }

    @Override
    public boolean nodeQueryBySingleBlock(String nodeId, String blockId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            if (block.getId().equals(blockId)) {
                MerkleTree merkleTree = block.getMerkleTree();
                List<Transaction> transactions = merkleTree.singleNodeQuery(nodeId);
                if (transactions.size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean nodeQueryByAllBlock(String nodeId) {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            MerkleTree merkleTree = block.getMerkleTree();
            res.addAll(merkleTree.singleNodeQuery(nodeId));
        }
        if (!res.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean propertyQueryBySingleBlock(Map<String, String> queries, String blockId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            if (block.getId().equals(blockId)) {
                MerkleTree merkleTree = block.getMerkleTree();
                List<Transaction> transactions = merkleTree.propertyQuery(queries);
                if (!transactions.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean propertyQueryByAllBlock(Map<String, String> queries) {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            MerkleTree merkleTree = block.getMerkleTree();
            res.addAll(merkleTree.propertyQuery(queries));
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
                MerkleTree merkleTree = block.getMerkleTree();
                if (topK == 0) {
                    List<Transaction> transactions = merkleTree.propertyQuery(queries);
                    if (!transactions.isEmpty()) return true;
                } else {
                    for (String type : queries.keySet()) {
                        List<Transaction> transactions = merkleTree.propertyQueryTopK(type, topK);
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
            MerkleTree merkleTree = block.getMerkleTree();
            if (topK == 0) {
                res.addAll(merkleTree.propertyQuery(queries));
            } else {
                for (String type : queries.keySet()) {
                    res.addAll(merkleTree.propertyQueryTopK(type, topK));
                }
            }
        }
        if (!res.isEmpty()) return true;
        return false;
    }


}
