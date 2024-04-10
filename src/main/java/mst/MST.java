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
    }

    @Override
    public boolean singleTransactionQuery(String transactionId) {
        return mstTree.singleTransactionQuery(transactionId);
    }

    @Override
    public boolean singleNodeQuery(String nodeId) {
        List<Transaction> txs = mstTree.singleNodeQuery(nodeId);
        if (!txs.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean propertyRangeQuery(Map<String, String> queries) {
        List<Transaction> txs = mstTree.propertyQuery(queries);
        if (!txs.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mixQuery() {
        return false;
    }
}
