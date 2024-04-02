package meat;

import blockchain.Transaction;
import data.Context;
import query.Query;

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
    public Transaction singleTransactionQuery(String transactionId) {
        return null;
    }

    @Override
    public List<Transaction> singleNodeQuery(String nodeId) {
        return null;
    }

    @Override
    public List<Transaction> propertyQuery(String property) {
        return null;
    }

    @Override
    public List<Transaction> propertyRangeQuery(Map<String, String> queries) {
        return null;
    }

    @Override
    public List<Transaction> mixQuery() {
        return null;
    }
}
