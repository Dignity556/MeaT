package merklebplus;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MerkleBPlusTree {
    private BPlusTree<Transaction, Integer> bPlusTree;
    public static void createMerkleBPlusTree(Context context) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            List<Transaction> txs = block.getTransactions();

            // 挂载
            BPlusTree<Transaction, Integer> tree = new BPlusTree<>();
            MerkleBPlusTree merkleBPlusTree = new MerkleBPlusTree();
            merkleBPlusTree.setBPlusTree(tree);
            block.setMerkleBPlusTree(merkleBPlusTree);

            // 插入交易至b+树
            txs.stream().forEach(t -> tree.insert(t, t.getIdForInt()));
            // 生成每个b+树叶子的merkle树
            tree.createMerkle();

        }
    }
}
