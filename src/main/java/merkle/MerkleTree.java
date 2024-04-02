package merkle;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MerkleTree {
    private Leaf root;
    public static void createMerkleTree(Context context) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            List<Transaction> transactions = block.getTransactions();
            List<Leaf> leaves = new ArrayList<>();
            for (Transaction transaction : transactions) {
                leaves.add(new Leaf(transaction));
            }
            try {
                // 生成根节点
                Leaf root = createMerkleIterator(leaves);

                // 挂载至区块
                MerkleTree merkleTree = new MerkleTree();
                merkleTree.setRoot(root);
                block.setMerkleTree(merkleTree);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Leaf createMerkleIterator(List<Leaf> leaves) throws NoSuchAlgorithmException {
        List<Leaf> newLeaves = new ArrayList<>();
        int count = 0;//记录树中节点的总个数

        if(leaves.size() == 1) {
            leaves.get(0).setFather(null);
            leaves.get(0).setId("root" + leaves.get(0).getBlock().getId());
        }else{
            for(int i = 0;i < leaves.size() - 1;i += 2) {
                Leaf father = new Leaf();
                father.setLeft(leaves.get(i));
                father.setRight(leaves.get(i + 1));
                father.setHashId(Leaf.calculateSHA256(leaves.get(i).getHashId().toString()+leaves.get(i+1).getHashId().toString()));
                father.setBlock(leaves.get(i).getBlock());
                father.setId("father" + "_"+leaves.get(0).getBlock().getId());
                leaves.get(i).setFather(father);
                leaves.get(i+1).setFather(father);
                newLeaves.add(father);
                count += 1;
//                System.out.println("Now is creating the MGT, this layer has "+count+" nodes");
            }
            if (leaves.size() % 2 == 1) {
                Leaf newLeaf = leaves.get(leaves.size() - 1);
                newLeaf.setId("leaf");
                newLeaves.add(newLeaf);
                count += 1;
//                System.out.println("Ok, we lost one, "+count+" nodes in total");
            }
        }
        return createMerkleIterator(leaves);
    }
}
