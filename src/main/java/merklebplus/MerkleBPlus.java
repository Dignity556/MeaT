package merklebplus;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    }

    @Override
    public boolean singleTransactionQuery(String transactionId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
            Transaction transaction = bPlusTree.getBPlusTree().find(Integer.valueOf(transactionId));
            if (transaction != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean singleNodeQuery(String nodeId) {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
            List<Transaction> transactions = bPlusTree.getBPlusTree().singleNodeQuery(nodeId);
            res.addAll(transactions);
        }
        if (!res.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean propertyRangeQuery(Map<String, String> queries) {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
            List<Transaction> txs = bPlusTree.getBPlusTree().propertyQuery(queries);
            res.addAll(txs);
        }
        if (!res.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mixQuery() {
        return false;
    }
}
