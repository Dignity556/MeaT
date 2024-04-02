package graph;

import blockchain.Block;
import blockchain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Edge {
    private Node startNode;
    private Node endNode;
    // 个人理解Edge的id应该与交易相同，因为本质都是代表一条交易
    private String id;
    private String timestamp;
    private String timeCost;
    private String type;
    private String reputation; //相当于权重
    private Block block;
    private Transaction transaction;
}
