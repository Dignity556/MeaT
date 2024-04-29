package blockchain;

import graph.Edge;
import graph.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;

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
    private int matrixId;

    public double getTimeCostForDouble() {
        return Double.parseDouble(timeCost);
    }

    public double getReputationForDouble() {
        return Double.parseDouble(reputation);
    }

    public int getIdForInt() {
        return Integer.parseInt(id);
    }

    // 自定义比较器
    public static Comparator<Transaction> compareByTimeCost = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return Double.compare(o1.getTimeCostForDouble(), o2.getTimeCostForDouble());
        }
    };

    public static Comparator<Transaction> compareByReputation = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return Double.compare(o1.getReputationForDouble(), o2.getReputationForDouble());
        }
    };
}
