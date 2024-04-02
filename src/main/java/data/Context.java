package data;

import blockchain.Block;
import blockchain.Transaction;
import graph.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Context {
    private List<Block> blocks; // 数据集全部区块
    private List<Node> nodes; // 数据集全部节点
    private List<Transaction> transactions; // 全部交易

    public Map<Node, List<Transaction>> groupByNode(List<Transaction> transactions) {
        Map<Node, List<Transaction>> collect = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getStartNode));
        return collect;
    }
}
