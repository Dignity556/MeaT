package merklebplus;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MerkleBPlusTree implements Serializable {
    private BPlusTree<Transaction, Integer> bPlusTree;
    private double[][] skylineMatrix;
    public static long createMerkleBPlusTree(Context context) {
        List<Block> blocks = context.getBlocks();
        long time = 0L;
        for (Block block : blocks) {
            List<Transaction> transactions = block.getTransactions();
            BPlusTree<Transaction, Integer> tree = new BPlusTree<>();
            MerkleBPlusTree merkleBPlusTree = new MerkleBPlusTree();
            merkleBPlusTree.setBPlusTree(tree);
            block.setMerkleBPlusTree(merkleBPlusTree);
            transactions.stream().forEach(t -> tree.insert(t, t.getIdForInt()));
            tree.createMerkle();
            long start = System.currentTimeMillis();
            long end = System.currentTimeMillis();
            time += (end - start);
        }
        return time;
    }



}
