package mst;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import graph.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import query.Constant;
import query.Query;
import query.QueryCase;
import query.SkylineGraph;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * 顶级类，包含创建和查询方法
 * mst在整个链上创建索引，因此具体实现与其他结构有所不同
 */
public class MST implements Query {
    private Context context;

    private MSTTree mstTree;
    public long createMST(Context context) {
        this.context = context;
        MSTTree mstTree = new MSTTree();
        mstTree.createMST(context);
        this.mstTree = mstTree;
        long start = System.currentTimeMillis();
//        for (Block block : context.getBlocks()) {
//            List<Transaction> transactions = block.getTransactions();
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
//            SkylineGraph skylineGraph=new SkylineGraph();
//            HashMap<String,ArrayList<String>> lattices=new HashMap<>();
//            HashMap<String,ArrayList<Integer>> final_lattice=skylineGraph.concept_lattice(matrix,lattices);
//            ArrayList<HashSet<Integer>> skyline_layers=skylineGraph.skyline_layer(final_lattice);
//        }
        long end = System.currentTimeMillis();
//        System.out.println("mst构建完成");
        return (end - start);
    }

    public long traceability_skyline(Context context, MST mst) throws NoSuchFieldException, IllegalAccessException {
        this.context = context;
        ArrayList<Transaction> transactions=new ArrayList<>();
        long start = System.currentTimeMillis();
        for (Block block : context.getBlocks()) {
            transactions.addAll(block.getTransactions());
            int caseNum=block.getTransactions().size();
            QueryCase queryCase = new QueryCase(context);
            Map<String, Map<String, String>> cases = queryCase.getPropertyQuerySingleBlockCase(caseNum, 2);
                List<Map<String,String>> case_all=queryCase.getPropertyQueryCase(caseNum,2);
                for (Map.Entry<String, Map<String, String>> entry : cases.entrySet()) {
                    String blockId = entry.getKey();
                    Map<String, String> queries = entry.getValue();
                    mst.propertyQueryBySingleBlock(queries, blockId);
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
//        SkylineGraph skylineGraph=new SkylineGraph();
//        HashMap<String,ArrayList<String>> lattices=new HashMap<>();
//        HashMap<String,ArrayList<Integer>> final_lattice=skylineGraph.concept_lattice(matrix,lattices);
//        ArrayList<HashSet<Integer>> skyline_layers=skylineGraph.skyline_layer(final_lattice);
        long end = System.currentTimeMillis();
        return (end - start);
    }

    @Override
    public boolean singleTransactionQuery(String transactionId, String nodeId) {
        return mstTree.singleTransactionQuery(transactionId, nodeId);
    }

    @Override
    public boolean nodeQueryBySingleBlock(String nodeId, String blockId) {
        List<Transaction> transactions = mstTree.singleNodeQuery(nodeId, blockId);
        if (!transactions.isEmpty()) return true;
        return false;
    }

    @Override
    public boolean nodeQueryByAllBlock(String nodeId) {
        List<Transaction> transactions = mstTree.singleNodeQueryAllBlock(nodeId);
        if (!transactions.isEmpty()) return true;
        return false;
    }

    @Override
    public boolean propertyQueryBySingleBlock(Map<String, String> queries, String blockId) throws NoSuchFieldException, IllegalAccessException {
        List<Transaction> transactions = mstTree.propertyQuerySingleBlock(queries, blockId);
        if (transactions != null && !transactions.isEmpty()) return true;
        return false;
    }

    @Override
    public boolean propertyQueryByAllBlock(Map<String, String> queries) throws NoSuchFieldException, IllegalAccessException {
        List<Transaction> transactions = mstTree.propertyQuery(queries);
        if (!transactions.isEmpty()) return true;
        return false;
    }

    @Override
    public boolean propertyRangeQueryBySingleBlock(Map<String, String> queries, String blockId, int topK) throws NoSuchFieldException, IllegalAccessException {
        if (topK == 0) {
            List<Transaction> transactions = mstTree.propertyQuerySingleBlock(queries, blockId);
            if (transactions != null && !transactions.isEmpty()) return true;
        } else {
            for (String type : queries.keySet()) {
                List<Transaction> transactions = mstTree.propertyQueryTopK(type, topK);
                if (!transactions.isEmpty()) return true;
            }
        }
        return false;
    }

    @Override
    public boolean propertyRangeQueryByAllBlock(Map<String, String> queries, int topK) throws NoSuchFieldException, IllegalAccessException {
        if (topK == 0) {
            List<Transaction> transactions = mstTree.propertyQuery(queries);
            if (!transactions.isEmpty()) return true;
        } else {
            for (String type : queries.keySet()) {
                List<Transaction> transactions = mstTree.propertyQueryTopK(type, topK);
                if (!transactions.isEmpty()) return true;
            }
        }
        return false;
    }

    @Override
    public long nodeAccessQuery(String sourceId, String targetId) {
        List<Node> nodeList = context.getNodes();
        List<Transaction> transactions = context.getTransactions();
        Node source = null, target = null;
        for (Node node : nodeList) {
            node.setNeighbors(new ArrayList<>());
            if (node.getNodeId().equals(sourceId)) {
                source = node;
            }
            if (node.getNodeId().equals(targetId)) {
                target = node;
            }
        }
        for (Transaction transaction : transactions) {
            Node startNode = transaction.getStartNode();
            Node endNode = transaction.getEndNode();
            startNode.getNeighbors().add(endNode);
            endNode.getNeighbors().add(startNode);
        }

        if (sourceId.equals(targetId)) {
            return 0;
        }

        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();

        queue.add(source);
        visited.add(source);

        int distance = -1;

        while (!queue.isEmpty()) {
            distance++;
            for (int i = queue.size(); i > 0; i--) {
                Node current = queue.poll();
                if (current.equals(target)) {
                    return distance;
                }
                for (Node neighbor : current.getNeighbors()) {
                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
        }
        return -1; // No path found
    }


}
