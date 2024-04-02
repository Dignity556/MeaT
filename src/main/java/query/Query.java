package query;

import blockchain.Transaction;

import java.util.List;
import java.util.Map;

public interface Query {
    /**
     * 单交易查询
     * @param transactionId 交易id
     * @return 交易实例
     */
    Transaction singleTransactionQuery(String transactionId);

    /**
     * 单节点查询
     * @param nodeId 节点Id
     * @return 该节点的全部交易数据
     */
    List<Transaction> singleNodeQuery(String nodeId);

    /**
     * 单属性查询
     * @param property 属性
     * @return 交易实例
     */
    List<Transaction> propertyQuery(String property);

    /**
     * 多属性范围查询
     * @param queries 查询键值
     * @return 交易实例
     */
    List<Transaction> propertyRangeQuery(Map<String, String> queries);

    List<Transaction> mixQuery();
}
