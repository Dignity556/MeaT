package meat;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import graph.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MerkleGraphTree implements Serializable {
    private GraphLeaf root; // mgt结构的根节点
    private Block belongBLock; // 该mgt归属区块
    private GraphNodeLink gnl;

    /**
     * 该方法逻辑为直接生成全部区块mgt
     * @param context 上下文对象
     */
    public static void createMerkleGraphTree(Context context) {
        List<Block> blocks = context.getBlocks();
        int itemCount = 0; // item的id
        List<Long> list = new ArrayList<>();

        // 每个区块创建mgt并挂载
        for (Block block : blocks) {
            int leafId = 0;
            List<Transaction> transactions = block.getTransactions();
            Map<Node, List<Transaction>> nodeTxsMap = context.groupByNode(transactions);
            GraphNodeLink gnl = new GraphNodeLink();
            // 构建下层
            for (Node node : nodeTxsMap.keySet()) {
                List<Transaction> txs = nodeTxsMap.get(node);
                List<GraphLeaf> leaves = new ArrayList<>();
                for (Transaction tx : txs) {
                    GraphLeaf leaf = GraphLeaf.transactionToLeaf(tx);
                    leaf.setId(String.valueOf(leafId));
                    leaves.add(leaf);
                    leafId++;
                }
                // 只有叶子节点有实际交易数据
                GraphLeaf root = createLowerMGT(leaves);
                // 生成item并与下层mgt建立连接
                GraphNodeLinkItem item = new GraphNodeLinkItem(String.valueOf(itemCount), node, root);
                gnl.getItems().put(node, item);
                itemCount++;
            }
            // 设置mgt根root
            GraphLeaf upperMGT = createUpperMGT(gnl);

            // 区块与mgt互相建立联系
            MerkleGraphTree mgt = new MerkleGraphTree();
            mgt.setRoot(upperMGT);
            mgt.setBelongBLock(block);
            mgt.setGnl(gnl);
            block.setMgt(mgt);

            // 建立pst并将pst与mgt建立连接
            String[] filter= {"type", "time_cost", "reputation"};
            int amount = 3;

            PropertySemanticTrie.createPST(block, filter, amount);

//            double[][] matrix = new double[transactions.size()][transactions.size()];
//            for (int i = 0; i < transactions.size(); i++) {
//                for (int j = 0; j < transactions.size(); j++) {
//                    if (i == j) {
//                        matrix[i][j] = 1;
//                    } else {
//                        if (transactions.get(i).getReputationForDouble() > transactions.get(j).getReputationForDouble() &&
//                                transactions.get(j).getTimeCostForDouble() > transactions.get(j).getTimeCostForDouble()) {
//                            matrix[i][j] = 1;
//                        } else {
//                            matrix[i][j] = 0;
//                        }
//                    }
//                }
//            }

        }
    }

    private static GraphLeaf createLowerMGT(List<GraphLeaf> leaves) {
        List<GraphLeaf> newLeaves = new ArrayList<>();
        if (leaves.size() == 1) {
            GraphLeaf leaf = leaves.get(0);
            leaf.setFather(null);
            leaf.setId("root");
            return leaf;
        } else {
            for (int i = 0; i < leaves.size() - 1; i += 2) {
                GraphLeaf father = new GraphLeaf();
                father.setLeft(leaves.get(i));
                father.setRight(leaves.get(i + 1));
                // father.setSubTreeNode(leaves.get(i).getSubTreeNode());
                father.setHashId(GraphLeaf.calculateSHA256(leaves.get(i).getHashId().toString()
                        + leaves.get(i + 1).getHashId().toString()));
                father.setId("father");
                leaves.get(i).setFather(father);
                leaves.get(i + 1).setFather(father);
                newLeaves.add(father);
            }
            if ((leaves.size() % 2) == 1) {
                GraphLeaf newLeaf = leaves.get(leaves.size() - 1);
                newLeaf.setId("leaf");
                newLeaves.add(newLeaf);
            }
            return createLowerMGT(newLeaves);
        }
    }

    private static GraphLeaf createUpperMGT(GraphNodeLink gnl) {
        Map<Node, GraphNodeLinkItem> items = gnl.getItems();
        List<GraphLeaf> leaves = new ArrayList<>();
        for (Node node : items.keySet()) {
            GraphLeaf leaf = GraphLeaf.nodeToLeaf(node);
            leaves.add(leaf);
        }
        GraphLeaf root = createUpperMGTIterator(leaves);
        return root;
    }

    private static GraphLeaf createUpperMGTIterator(List<GraphLeaf> leaves) {
        List<GraphLeaf> newLeaves = new ArrayList<>();
        if (leaves.size() == 1) {
            leaves.get(0).setFather(null);
            leaves.get(0).setId("upper-root");
            return leaves.get(0);
        } else {
            for (int i = 0; i < (leaves.size() - 1); i += 2) {
                // TODO leaf的id是否重要？
                GraphLeaf father = new GraphLeaf();
                father.setLeft(leaves.get(i));
                father.setRight(leaves.get(i + 1));
                father.setSubTreeNode(leaves.get(i).getSubTreeNode());
                father.setHashId(GraphLeaf.calculateSHA256(leaves.get(i).getHashId().toString() +
                        leaves.get(i + 1).getHashId().toString()));
                father.setBeLongBlock(leaves.get(i).getBeLongBlock());
                father.setId("upper-father");
                leaves.get(i).setFather(father);
                leaves.get(i + 1).setFather(father);
                newLeaves.add(father);
            }
            if ((leaves.size() % 2) == 1) {
                GraphLeaf newLeaf = leaves.get(leaves.size() - 1);
                String label = newLeaf.getSubTreeNode().getNodeId();
                newLeaf.setId("leaf");
                newLeaves.add(newLeaf);
            }
            return createUpperMGTIterator(newLeaves);
        }
    }
}
