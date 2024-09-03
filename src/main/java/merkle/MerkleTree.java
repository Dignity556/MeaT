package merkle;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import query.Constant;
import query.QueryCase;
import query.SkylineGraph;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MerkleTree implements Serializable {
    public static Leaf root;
    private double[][] skylineMatrix;
    public static long createMerkleTree(Context context) {
        SkylineGraph skylineGraph=new SkylineGraph();
        List<Block> blocks = context.getBlocks();
        long time = 0L;
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
                merkleTree.root=root;
                block.setMerkleTree(merkleTree);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            long start = System.currentTimeMillis();
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
//            HashMap<String,ArrayList<String>> lattices=new HashMap<>();
//            HashMap<String,ArrayList<Integer>> final_lattice=skylineGraph.concept_lattice(matrix,lattices);
//            ArrayList<HashSet<Integer>> skyline_layers=skylineGraph.skyline_layer(final_lattice);
            long end = System.currentTimeMillis();
            time += (end - start);
//            block.getMerkleTree().setSkylineMatrix(matrix);
        }
        return time;
    }

    public static long traceability_skyline(Context context,Merkle merkle) throws NoSuchFieldException, IllegalAccessException {
        SkylineGraph skylineGraph=new SkylineGraph();
        List<Block> blocks = context.getBlocks();
        ArrayList<Transaction> transactions=new ArrayList<>();
        long start = System.currentTimeMillis();
        long time = 0L;
        for (Block block : blocks) {
            transactions.addAll(block.getTransactions());
            int caseNum=block.getTransactions().size();
            QueryCase queryCase = new QueryCase(context);
            Map<String, Map<String, String>> cases = queryCase.getPropertyQuerySingleBlockCase(caseNum,2);
                List<Map<String,String>> case_all=queryCase.getPropertyQueryCase(caseNum,2);
                for (Map.Entry<String, Map<String, String>> entry : cases.entrySet()) {
                    String blockId = entry.getKey();
                    Map<String, String> queries = entry.getValue();
                    merkle.propertyQueryBySingleBlock(queries, blockId);
                }
        }
        double[][] matrix = new double[transactions.size()][transactions.size()];
        for (int i = 0; i < transactions.size(); i++) {
            for (int j = 0; j < transactions.size(); j++) {
                if (i == j) {
                    matrix[i][j] = 1;
                } else {
                    if (Double.parseDouble(transactions.get(i).getGame_id()) > Double.parseDouble(transactions.get(j).getGame_id()) &&
                            Double.parseDouble(transactions.get(i).getT_point()) > Double.parseDouble(transactions.get(i).getT_point())) {
                        matrix[i][j] = 1;
                    } else {
                        matrix[i][j] = 0;
                    }
                }
            }
        }
//        HashMap<String,ArrayList<String>> lattices=new HashMap<>();
//        HashMap<String,ArrayList<Integer>> final_lattice=skylineGraph.concept_lattice(matrix,lattices);
//        ArrayList<HashSet<Integer>> skyline_layers=skylineGraph.skyline_layer(final_lattice);
        long end = System.currentTimeMillis();
        time += (end - start);
        return time;
    }





    private static Leaf createMerkleIterator(List<Leaf> leaves) throws NoSuchAlgorithmException {
        List<Leaf> newLeaves = new ArrayList<>();
        int count = 0;//记录树中节点的总个数

        if (leaves.size() == 1) {
            leaves.get(0).setFather(null);
            leaves.get(0).setId("root" + leaves.get(0).getBlock().getId());
            return leaves.get(0);
        } else {
            for (int i = 0; i < leaves.size() - 1; i += 2) {
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
            return createMerkleIterator(newLeaves);
        }
    }

    public boolean singleTransactionQuery(String txId, String nodeId) {
        if (dfs(root, txId, nodeId) != null) {
            return true;
        }
        return false;
    }

    private Transaction dfs(Leaf leaf, String txId, String nodeId) {
        if (leaf.getLeft() == null && leaf.getTransaction() != null &&
                leaf.getTransaction().getId().equals(txId) &&
                leaf.getTransaction().getStartNode().getNodeId().equals(nodeId)) {
            return leaf.getTransaction();
        }
        if (leaf.getLeft() != null) {
            return dfs(leaf.getLeft(), txId, nodeId);
        }
        if (leaf.getRight() != null) {
            return dfs(leaf.getRight(), txId, nodeId);
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
        int timeCostMin = Double.valueOf(timeCost[0]).intValue();
        int timeCostMax = Double.valueOf(timeCost[1]).intValue();
        int reputationMin = Double.valueOf(reputation[0]).intValue();
        int reputationMax = Double.valueOf(reputation[1]).intValue();


        if (leaf.getLeft() == null) {
            Transaction tx = leaf.getTransaction();
            int timeValue = Double.valueOf(tx.getTimeCost()).intValue();
            int repuValue = Double.valueOf(tx.getReputation()).intValue();
            if ((tx.getType().equals(type) ||
                    ((timeValue >= timeCostMin) && (timeValue <= timeCostMax)) ||
                    ((repuValue >= reputationMin) && (repuValue <= reputationMax)))) {
                txs.add(tx);
            }
        }
        if (leaf.getLeft() != null) {
            propertyQueryIter(leaf.getLeft(), type, timeCost, reputation, txs);
        }
        if (leaf.getRight() != null) {
            propertyQueryIter(leaf.getRight(), type, timeCost, reputation, txs);
        }
        return txs;
    }

    public List<Transaction> propertyQueryTopK(String type, int topK) {
        PriorityQueue<Transaction> priorityQueue;
        List<Transaction> res = new ArrayList<>();
        if (type.equals("time_cost")) {
            priorityQueue = new PriorityQueue<>(Transaction.compareByTimeCost);
        } else {
            priorityQueue = new PriorityQueue<>(Transaction.compareByReputation);
        }
        propertyQueryTopKIter(root, priorityQueue);
        for (int i = 0; i < topK; i++) {
            res.add(priorityQueue.poll());
        }
        return res;
    }



    private void propertyQueryTopKIter(Leaf leaf, PriorityQueue<Transaction> priorityQueue) {
        if (leaf.getLeft() == null) {
            priorityQueue.add(leaf.getTransaction());
        }
        if (leaf.getLeft() != null) {
            propertyQueryTopKIter(leaf.getLeft(), priorityQueue);
        }
        if (leaf.getRight() != null) {
            propertyQueryTopKIter(leaf.getRight(), priorityQueue);
        }
    }

    //多属性查询，map的size即是属性的个数
    public Queue<Transaction> iterMerkle(Leaf leaf, Queue<Transaction> priorityQueue){
        if (leaf.getLeft() == null) {
            priorityQueue.add(leaf.getTransaction());
        }
        if (leaf.getLeft() != null) {
            iterMerkle(leaf.getLeft(), priorityQueue);
        }
        if (leaf.getRight() != null) {
            iterMerkle(leaf.getRight(), priorityQueue);
        }
        return priorityQueue;
    }

    public List<Transaction> mtquery(Leaf leaf,Map<String,String> queries) throws NoSuchFieldException, IllegalAccessException {
        List<Transaction> txs=new ArrayList<>();
        Queue<Transaction> all_transactions=new LinkedList<>();
        all_transactions=iterMerkle(leaf,all_transactions);
        for (Transaction tx: all_transactions)
        {
            for (String query:queries.keySet())
            {
                if (satisfy(tx,query,queries.get(query)))
                {
                    txs.add(tx);
                }
            }
        }
        return txs;
    }

    public boolean satisfy(Transaction tx, String property, String target) throws NoSuchFieldException, IllegalAccessException {
        Field field= tx.getClass().getDeclaredField(property);
        field.setAccessible(true);
        try {
            double value=Double.parseDouble((String) field.get(tx));
            String[] ranges=target.split(",");
            double min=Double.valueOf(ranges[0]);
            double max=Double.valueOf(ranges[1]);
            if ((value <= 0 && min==0) || (value <= max && value >= min) || (max==20000 && value>=20000)) {
                return true;
            }else{
                return false;
            }
        } catch (NumberFormatException e) {
            if (target.equals((String) field.get(tx)))
            {
                return true;
            }else{
                return false;
            }
        }
    }

}
