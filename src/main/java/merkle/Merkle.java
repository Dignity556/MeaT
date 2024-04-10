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
    }
    @Override
    public boolean singleTransactionQuery(String transactionId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            MerkleTree merkleTree = block.getMerkleTree();
            boolean res = merkleTree.singleTransactionQuery(transactionId);
            if (res) return true;
        }
        return false;
    }

    @Override
    public boolean singleNodeQuery(String nodeId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            MerkleTree merkleTree = block.getMerkleTree();
            List<Transaction> transactions = merkleTree.singleNodeQuery(nodeId);
            if (transactions.size() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean propertyRangeQuery(Map<String, String> queries) {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            MerkleTree merkleTree = block.getMerkleTree();
            res.addAll(merkleTree.propertyQuery(queries));
        }
        if (res.size() != 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mixQuery() {
        return false;
    }
}
