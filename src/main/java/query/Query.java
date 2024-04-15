package query;

import blockchain.Transaction;

import java.util.List;
import java.util.Map;

public interface Query {
    /**
     * 单交易查询
     * 已知交易id，节点id，查询在哪个区块；
     * @param transactionId 交易id
     * @param nodeId 节点id
     * @return 交易实例
     */
    boolean singleTransactionQuery(String transactionId, String nodeId);

    /**
     * 查询某个节点在某个区块内的所有交易；
     * @param nodeId 节点id
     * @param blockId 区块id
     * @return
     */
    boolean nodeQueryBySingleBlock(String nodeId, String blockId);

    /**
     * 查询某个节点在所有区块内的所有交易；
     * @param nodeId 节点id
     * @return
     */
    boolean nodeQueryByAllBlock(String nodeId);

    /**
     * 查询某个区块中；一个、两个、三个属性具体的值已知下的所有交易；
     * @param queries 查询指令
     * @param blockId 区块id
     * @return
     */
    boolean propertyQueryBySingleBlock(Map<String, String> queries, String blockId);

    /**
     * 查询所有区块中，xxxxxxxxxxx
     * @param queries 查询指令
     * @return
     */
    boolean propertyQueryByAllBlock(Map<String, String> queries);

    /**
     * 查询某个区块中，一个、两个、三个属性特定范围下的满足条件的所有交易；
     * @param queries 查询键值。键为查询的属性类型，值为具体的属性值或范围（范围使用逗号,隔开）
     * @return 交易实例
     */
    boolean propertyRangeQueryBySingleBlock(Map<String, String> queries, String blockId, int topK);

    /**
     * 查询所有区块中，xxxxxxxxxxx
     * @param queries
     * @return
     */
    boolean propertyRangeQueryByAllBlock(Map<String, String> queries, int topK);
}
