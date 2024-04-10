package blockchain;

import graph.Edge;
import graph.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String id;
    // 时间戳
    private String timestamp;
    private String timeCost;
    private String reputation; //相当于权重
    private Node startNode; // node可以理解为user
    private Node endNode;
    private String type;
    private Block beLongBlock;
    private Edge edge;

    public double getTimeCostForDouble() {
        return Double.parseDouble(timeCost);
    }

    public double getReputationForDouble() {
        return Double.parseDouble(reputation);
    }

    public int getIdForInt() {
        return Integer.parseInt(id);
    }
}
