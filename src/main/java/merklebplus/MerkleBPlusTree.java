package merklebplus;

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
            // 挂载
            BPlusTree<Transaction, Integer> tree = new BPlusTree<>();
            MerkleBPlusTree merkleBPlusTree = new MerkleBPlusTree();
            merkleBPlusTree.setBPlusTree(tree);
            block.setMerkleBPlusTree(merkleBPlusTree);

            // 插入交易至b+树
            transactions.stream().forEach(t -> tree.insert(t, t.getIdForInt()));
            // 生成每个b+树叶子的merkle树
            tree.createMerkle();
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
//            SkylineGraph skylineGraph=new SkylineGraph();HashMap<String,ArrayList<String>> lattices=new HashMap<>();
//            HashMap<String, ArrayList<Integer>> final_lattice=skylineGraph.concept_lattice(matrix,lattices);
//            ArrayList<HashSet<Integer>> skyline_layers=skylineGraph.skyline_layer(final_lattice);
            long end = System.currentTimeMillis();
            time += (end - start);
//            block.getMerkleBPlusTree().setSkylineMatrix(matrix);
        }
        return time;
    }

    public static long traceability_skyline(Context context, MerkleBPlus mbp) throws NoSuchFieldException, IllegalAccessException {
        List<Block> blocks = context.getBlocks();
        ArrayList<Transaction> transactions=new ArrayList<>();
        long time = 0L;
        long start=System.currentTimeMillis();
        for (Block block : blocks) {
            transactions.addAll(block.getTransactions());
            int caseNum=block.getTransactions().size();
            QueryCase queryCase = new QueryCase(context);
            Map<String, Map<String, String>> cases = queryCase.getPropertyQuerySingleBlockCase(caseNum, 2);
                List<Map<String,String>> case_all=queryCase.getPropertyQueryCase(caseNum,2);
                long time29 = System.nanoTime();
                for (Map.Entry<String, Map<String, String>> entry : cases.entrySet()) {
                    String blockId = entry.getKey();
                    Map<String, String> queries = entry.getValue();
                    mbp.propertyQueryBySingleBlock(queries, blockId);
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
//        SkylineGraph skylineGraph=new SkylineGraph();HashMap<String,ArrayList<String>> lattices=new HashMap<>();
//        HashMap<String, ArrayList<Integer>> final_lattice=skylineGraph.concept_lattice(matrix,lattices);
//        ArrayList<HashSet<Integer>> skyline_layers=skylineGraph.skyline_layer(final_lattice);
        long end = System.currentTimeMillis();
        long cost=end-start;
        return cost;
    }


}
