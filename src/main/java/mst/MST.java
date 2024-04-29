package mst;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import query.Query;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * 顶级类，包含创建和查询方法
 * mst在整个链上创建索引，因此具体实现与其他结构有所不同
 */
public class MST implements Query {
    private Context context;

    private MSTTree mstTree;
    public void createMST(Context context) {
        this.context = context;
        MSTTree mstTree = new MSTTree();
        mstTree.createMST(context);
        this.mstTree = mstTree;
        for (Block block : context.getBlocks()) {
            List<Transaction> transactions = block.getTransactions();
            double[][] matrix = new double[transactions.size()][transactions.size()];
            for (int i = 0; i < transactions.size(); i++) {
                for (int j = 0; j < transactions.size(); j++) {
                    if (i == j) {
                        matrix[i][j] = 1;
                    } else {
                        if (transactions.get(i).getReputationForDouble() > transactions.get(j).getReputationForDouble() &&
                                transactions.get(j).getTimeCostForDouble() > transactions.get(j).getTimeCostForDouble()) {
                            matrix[i][j] = 1;
                        } else {
                            matrix[i][j] = 0;
                        }
                    }
                }
            }
        }
        System.out.println("mst构建完成");
    }

    @Override
    public boolean singleTransactionQuery(String transactionId, String nodeId) {
        return mstTree.singleTransactionQuery(transactionId, nodeId);
    }

    @Override
    public boolean nodeQueryBySingleBlock(String nodeId, String blockId) {
        List<Transaction> transactions = mstTree.singleNodeQuery(nodeId, blockId);
        if (!transactions.isEmpty()) return true;
        return false;
    }

    @Override
    public boolean nodeQueryByAllBlock(String nodeId) {
        List<Transaction> transactions = mstTree.singleNodeQueryAllBlock(nodeId);
        if (!transactions.isEmpty()) return true;
        return false;
    }

    @Override
    public boolean propertyQueryBySingleBlock(Map<String, String> queries, String blockId) {
        List<Transaction> transactions = mstTree.propertyQuerySingleBlock(queries, blockId);
        if (transactions != null && !transactions.isEmpty()) return true;
        return false;
    }

    @Override
    public boolean propertyQueryByAllBlock(Map<String, String> queries) {
        List<Transaction> transactions = mstTree.propertyQuery(queries);
        if (!transactions.isEmpty()) return true;
        return false;
    }

    @Override
    public boolean propertyRangeQueryBySingleBlock(Map<String, String> queries, String blockId, int topK) {
        if (topK == 0) {
            List<Transaction> transactions = mstTree.propertyQuerySingleBlock(queries, blockId);
            if (transactions != null && !transactions.isEmpty()) return true;
        } else {
            for (String type : queries.keySet()) {
                List<Transaction> transactions = mstTree.propertyQueryTopK(type, topK);
                if (!transactions.isEmpty()) return true;
            }
        }
        return false;
    }

    @Override
    public boolean propertyRangeQueryByAllBlock(Map<String, String> queries, int topK) {
        if (topK == 0) {
            List<Transaction> transactions = mstTree.propertyQuery(queries);
            if (!transactions.isEmpty()) return true;
        } else {
            for (String type : queries.keySet()) {
                List<Transaction> transactions = mstTree.propertyQueryTopK(type, topK);
                if (!transactions.isEmpty()) return true;
            }
        }
        return false;
    }

    @Override
    public int nodeAccessQuery(String sourceId, String targetId) {
        return 0;
    }


}
