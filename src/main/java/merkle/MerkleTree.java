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
import java.util.Map;

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

    public boolean singleTransactionQuery(String txId) {
        if (dfs(root, txId) != null) {
            return true;
        }
        return false;
    }

    private Transaction dfs(Leaf leaf, String txId) {
        if (leaf.getLeft() != null && leaf.getTransaction().getId().equals(txId)) {
            return leaf.getTransaction();
        }
        if (leaf.getLeft() != null) {
            return dfs(leaf.getLeft(), txId);
        }
        if (leaf.getRight() != null) {
            return dfs(leaf.getRight(), txId);
        }
        return null;
    }

    public List<Transaction> singleNodeQuery(String nodeId) {
        List<Transaction> res = new ArrayList<>();
        return singleNodeQueryIter(root, nodeId, res);
    }

    private List<Transaction> singleNodeQueryIter(Leaf leaf, String nodeId, List<Transaction> txs) {
        if (leaf.getLeft() == null && leaf.getTransaction().getStartNode().getNodeId().equals(nodeId)) {
            txs.add(leaf.getTransaction());
        }

        // 如果当前节点为叶子节点但哈希值不匹配，或者当前节点为非叶子节点，则继续遍历子节点
        if (leaf.getLeft() != null) {
            // 递归遍历左子节点
            txs = singleNodeQueryIter(leaf.getLeft(), nodeId, txs);
        }
        // 递归遍历右子节点（如果存在）
        if (leaf.getRight() != null) {
            txs = singleNodeQueryIter(leaf.getRight(), nodeId, txs);
        }
        // 如果在当前节点及其子节点中都未找到目标交易，则返回 false
        return txs;
    }

    public List<Transaction> propertyQuery(Map<String, String> queries) {
        String type = null;
        String[] timeCost = new String[2];
        String[] reputation = new String[2];
        if (queries.get("type") != null) {
            type = queries.get("type");
        }
        if (queries.get("time_cost") != null) {
            String[] split = queries.get("time_cost").split(",");
            timeCost[0] = split[0];
            timeCost[1] = split[1];
        }
        if (queries.get("reputation") != null) {
            String[] split = queries.get("reputation").split(",");
            reputation[0] = split[0];
            reputation[1] = split[1];
        }
        List<Transaction> txs = new ArrayList<>();
        return propertyQueryIter(root, type, timeCost, reputation, txs);
    }

    private List<Transaction> propertyQueryIter(Leaf leaf, String type, String[] timeCost,
                                                String[] reputation, List<Transaction> txs) {
        double timeCostMin = Double.valueOf(timeCost[0]);
        double timeCostMax = Double.valueOf(timeCost[1]);
        double reputationMin = Double.valueOf(reputation[0]);
        double reputationMax = Double.valueOf(reputation[1]);

        Transaction tx = leaf.getTransaction();
        double timeValue = Double.valueOf(tx.getTimeCost());
        double repuValue = Double.valueOf(tx.getReputation());
        if (leaf.getLeft() == null && (tx.getType().equals(type) &&
                (timeValue > timeCostMin) && (timeValue < timeCostMax) &&
                (repuValue > reputationMin) && (repuValue < reputationMax))) {
            txs.add(tx);
        }
        if (leaf.getLeft() != null) {
            propertyQueryIter(leaf.getLeft(), type, timeCost, reputation, txs);
        }
        if (leaf.getRight() != null) {
            propertyQueryIter(leaf.getRight(), type, timeCost, reputation, txs);
        }
        return txs;
    }
}
