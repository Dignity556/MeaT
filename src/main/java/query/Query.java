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
    boolean singleTransactionQuery(String transactionId);

    /**
     * 单节点查询
     * @param nodeId 节点Id
     * @return 该节点的全部交易数据
     */
    boolean singleNodeQuery(String nodeId);

    /**
     * 多属性范围查询
     * @param queries 查询键值。键为查询的属性类型，值为具体的属性值或范围（范围使用逗号,隔开）
     * @return 交易实例
     */
    boolean propertyRangeQuery(Map<String, String> queries);

    boolean mixQuery();
}
