package meat;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import graph.Node;
import merkle.Merkle;
import query.Query;
import query.QueryCase;
import query.SkylineGraph;

import java.util.*;

/**
 * 顶级类，只应该存在创建方法与查询方法
 */
public class Meat implements Query {
    private Context context;
    public long createMeat(Context context,String[] filter) throws NoSuchFieldException, IllegalAccessException {
        long time = MerkleGraphTree.createMerkleGraphTree(context,filter);
        this.context = context;
        System.out.println("meat构建完成");
        return time;
    }

    public static long traceability_skyline(Context context, Meat meat) throws NoSuchFieldException, IllegalAccessException {
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
                meat.propertyQueryBySingleBlock(queries, blockId);
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

    // TODO 同样应该从根节点往下遍历，目前直接使用gnl
    @Override
    public boolean singleTransactionQuery(String transactionId, String nodeId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            MerkleGraphTree mgt = block.getMgt();
            GraphNodeLink gnl = mgt.getGnl();
            Map<Node, GraphNodeLinkItem> items = gnl.getItems();
            if (items.containsKey(new Node(nodeId))) {
                GraphNodeLinkItem item = items.get(new Node(nodeId));
                // 下层查询
                GraphLeaf root = item.getRoot();
                boolean res = root.singleTransactionQuery(transactionId);
                if (res) return true;
            }
        }
        return false;
    }

//    @Override
//    public Transaction TransactionQuery(String transactionId, String nodeId) {
//        List<Block> blocks = context.getBlocks();
//        Transaction tx=null;
//        for (Block block : blocks) {
//            MerkleGraphTree mgt = block.getMgt();
//            GraphNodeLink gnl = mgt.getGnl();
//            Map<Node, GraphNodeLinkItem> items = gnl.getItems();
//            if (items.containsKey(new Node(nodeId))) {
//                GraphNodeLinkItem item = items.get(new Node(nodeId));
//                // 下层查询
//                GraphLeaf root = item.getRoot();
//                tx = root.retrunTransactionQuery(transactionId);
//            }
//        }
//        return tx;
//    }

    @Override
    public boolean nodeQueryBySingleBlock(String nodeId, String blockId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            if (block.getId().equals(blockId)) {
                MerkleGraphTree mgt = block.getMgt();
                GraphNodeLink gnl = mgt.getGnl();
                for (Node node : gnl.getItems().keySet()) {
                    if (node.getNodeId().equals(nodeId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // TODO 目前采用直接获取gnl的方法进行查询，标准应该从根节点dfs或bfs
    @Override
    public boolean nodeQueryByAllBlock(String nodeId) {
        List<Block> blocks = context.getBlocks();
        List<Transaction> txs = new ArrayList<>();
        for (Block block : blocks) {
            MerkleGraphTree mgt = block.getMgt();
            GraphNodeLink gnl = mgt.getGnl();
            for (Node node : gnl.getItems().keySet()) {
                if (node.getNodeId().equals(nodeId)) {
                   txs.addAll(gnl.getItems().get(node).getTransactions());
                }
            }
        }
        if (!txs.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean propertyQueryBySingleBlock(Map<String, String> queries, String blockId) throws NoSuchFieldException, IllegalAccessException {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            if (block.getId().equals(blockId)) {
                MerkleGraphTree mgt = block.getMgt();
                List<Transaction> txs = mgt.getRoot().mpquery(queries);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean propertyQueryByAllBlock(Map<String, String> queries) throws NoSuchFieldException, IllegalAccessException {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            MerkleGraphTree mgt = block.getMgt();
            res.addAll(mgt.getRoot().mpquery(queries));
        }
        if (!res.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean propertyRangeQueryBySingleBlock(Map<String, String> queries, String blockId, int topK) throws NoSuchFieldException, IllegalAccessException {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            if (block.getId().equals(blockId)) {
                MerkleGraphTree mgt = block.getMgt();
                // topk为0表示不使用topk，作范围查询
                if (topK == 0) {
                    List<Transaction> txs = mgt.getRoot().mpquery(queries);
                    if (!txs.isEmpty()) return true;
                } else {
                    for (String type : queries.keySet()) {
                        List<Transaction> transactions = mgt.getRoot().propertyQueryTopK(type, topK);
                        if (!transactions.isEmpty()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean propertyRangeQueryByAllBlock(Map<String, String> queries, int topK) throws NoSuchFieldException, IllegalAccessException {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            MerkleGraphTree mgt = block.getMgt();
            if (topK == 0) {
                res.addAll(mgt.getRoot().mpquery(queries));
            } else {
                for (String type : queries.keySet()) {
                    res.addAll(mgt.getRoot().propertyQueryTopK(type, topK));
                }
            }
        }
        if (!res.isEmpty()) {
            return true;
        }
        return false;
    }

    public long graphMatrixBuild() {
        List<Block> blocks = context.getBlocks();
        long time = 0;
        for (Block block : blocks) {
            List<Transaction> transactions = block.getTransactions();long start = System.currentTimeMillis();
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
            long end = System.currentTimeMillis();
            time += (end - start);
        }
        return time;
    }

    @Override
    public long nodeAccessQuery(String sourceId, String targetId) {
        long start = System.nanoTime();
        List<Node> nodeList = context.getNodes();
        Node source = null, target = null;
        for (Node node : nodeList) {
            if (node.getNodeId().equals(sourceId)) {
                source = node;
            }
            if (node.getNodeId().equals(targetId)) {
                target = node;
            }
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
                    break;
                }
                for (Node neighbor : current.getNeighbors()) {
                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
        }
        long end = System.nanoTime();
        return (end - start); // No path found
    }

    public long graphNodeAccess(String sourceId, String targetId) {
        List<Node> nodeList = context.getNodes();
        Node source = null, target = null;
        for (Node node : nodeList) {
            if (node.getNodeId().equals(sourceId)) {
                source = node;
            }
            if (node.getNodeId().equals(targetId)) {
                target = node;
            }
        }
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();

        queue.add(source);
        visited.add(source);

        int distance = -1;

        long start = System.nanoTime();
        while (!queue.isEmpty()) {
            distance++;
            for (int i = queue.size(); i > 0; i--) {
                Node current = queue.poll();
                if (current.equals(target)) {
                    break;
                }
                for (Node neighbor : current.getNeighbors()) {
                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
        }
        long end = System.nanoTime();
        return (end - start); // No path found
    }

    public long graphOptimalNodeAccess(String sourceId, String targetId) {
        long start = System.nanoTime();
        List<Node> nodeList = context.getNodes();
        Node source = null, target = null;
        for (Node node : nodeList) {
            if (node.getNodeId().equals(sourceId)) {
                source = node;
            }
            if (node.getNodeId().equals(targetId)) {
                target = node;
            }
        }
        Set<String> visitedFromSource = new HashSet<>();
        Set<String> visitedFromTarget = new HashSet<>();

        Queue<Node> queueFromSource = new LinkedList<>();
        Queue<Node> queueFromTarget = new LinkedList<>();

        queueFromSource.add(source);
        queueFromTarget.add(target);

        visitedFromSource.add(source.getNodeId());
        visitedFromTarget.add(target.getNodeId());

        while (!queueFromSource.isEmpty() && !queueFromTarget.isEmpty()) {
            if (bfsStep(queueFromSource, visitedFromSource, visitedFromTarget)) {
                break;
            }

            if (bfsStep(queueFromTarget, visitedFromTarget, visitedFromSource)) {
                break;
            }
        }
        long end = System.nanoTime();
        return end - start;
    }

    private boolean bfsStep(Queue<Node> queue, Set<String> visitedFromThisSide, Set<String> visitedFromOtherSide) {
        int currentLevelSize = queue.size();
        for (int i = 0; i < currentLevelSize; i++) {
            Node current = queue.poll();
            if (visitedFromOtherSide.contains(current.getNodeId())) {
                return true;
            }
            for (Node neighbor : current.getNeighbors()) {
                if (visitedFromThisSide.add(neighbor.getNodeId())) {
                    queue.add(neighbor);
                }
            }
        }
        return false;
    }
}
