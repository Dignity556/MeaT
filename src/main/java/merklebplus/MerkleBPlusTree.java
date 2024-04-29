package merklebplus;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MerkleBPlusTree implements Serializable {
    private BPlusTree<Transaction, Integer> bPlusTree;
    public static void createMerkleBPlusTree(Context context) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            List<Transaction> transactions = block.getTransactions();

            // 挂载
            BPlusTree<Transaction, Integer> tree = new BPlusTree<>();
            MerkleBPlusTree merkleBPlusTree = new MerkleBPlusTree();
            merkleBPlusTree.setBPlusTree(tree);
            block.setMerkleBPlusTree(merkleBPlusTree);

            // 插入交易至b+树
            transactions.stream().forEach(t -> tree.insert(t, t.getIdForInt()));
            // 生成每个b+树叶子的merkle树
            tree.createMerkle();
            double[][] matrix = new double[transactions.size()][transactions.size()];
            for (int i = 0; i < transactions.size(); i++) {
                for (int j = 0; j < transactions.size(); j++) {
                    if (i == j) {
                        matrix[i][j] = 1;
                    } else {
                        if (transactions.get(i).getReputationForDouble() > transactions.get(j).getReputationForDouble() &&
                                transactions.get(j).getTimeCostForDouble() > transactions.get(j).getTimeCostForDouble()) {
                            matrix[i][j] = 1;
                        } else {
                            matrix[i][j] = 0;
                        }
                    }
                }
            }
        }
    }
}
