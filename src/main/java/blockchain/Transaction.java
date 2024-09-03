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

    // 时间戳
    public String timestamp;
    public String timeCost;
    public String reputation; //相当于权重
    public String type;

    public Block beLongBlock;
    public Edge edge;
    private int matrixId;

    public String id;
    public Node startNode; //team
    public String game_id; //Game
    public String date;//比赛日期
    public String home;//主场or客场
    public Node endNode; // opponent
    public String win_or_lose;//输赢情况

    public String t_point;//得分
    public String o_point;//对方得分
    public String t_fieldgoal;//命中率
    public String t_x3point;//三分命中率
    public String t_freegoal;//罚球命中率
    public String t_offrebound;//进攻篮板
    public String t_totalrebound;//总篮板
    public String t_assist;//助攻
    public String t_steal;//抢断
    public String t_block;//盖帽
    public String t_turnover;//失误
    public String t_fouls;//犯规

    public String o_fieldgoal;//命中率
    public String o_x3point;//三分命中率
    public String o_freegoal;//罚球命中率
    public String o_offrebound;//进攻篮板
    public String o_totalrebound;//总篮板
    public String o_assist;//助攻
    public String o_steal;//抢断
    public String o_block;//盖帽
    public String o_turnover;//失误
    public String o_fouls;//犯规

    public double getTimeCostForDouble() {
        return Double.parseDouble(timeCost);
    }

    public double getReputationForDouble() {
        return Double.parseDouble(reputation);
    }
    public double getGameidfordouble()
    {
        return Double.parseDouble(game_id);
    }
    public double getTPointForDouble(){
        return Double.parseDouble(t_point);
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
