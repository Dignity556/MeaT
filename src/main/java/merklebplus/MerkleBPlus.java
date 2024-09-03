package merklebplus;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import graph.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import merkle.MerkleTree;
import query.Query;

import java.util.*;

/**
 * 顶级类，应该只包含创建与查询方法
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MerkleBPlus implements Query {
    private Context context;
    public long createMerkleBPlus(Context context) {
        this.context = context;
        long time = MerkleBPlusTree.createMerkleBPlusTree(context);
        System.out.println("merkle B+树构建完成");
        return time;
    }

    @Override
    public boolean singleTransactionQuery(String transactionId, String nodeId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
            Transaction transaction = bPlusTree.getBPlusTree().singleTransactionQuery(transactionId);
            if (transaction != null && transaction.getStartNode().getNodeId().equals(nodeId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean nodeQueryBySingleBlock(String nodeId, String blockId) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            if (block.getId().equals(blockId)) {
                MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
                List<Transaction> transactions = bPlusTree.getBPlusTree().singleNodeQuery(nodeId);
                if (!transactions.isEmpty()) return true;
            }
        }
        return false;
    }

    @Override
    public boolean nodeQueryByAllBlock(String nodeId) {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            BPlusTree<Transaction, Integer> bPlusTree = block.getMerkleBPlusTree().getBPlusTree();
            res.addAll(bPlusTree.singleNodeQuery(nodeId));
        }
        if (!res.isEmpty()) return true;
        return false;
    }

    @Override
    public boolean propertyQueryBySingleBlock(Map<String, String> queries, String blockId) throws NoSuchFieldException, IllegalAccessException {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            if (block.getId().equals(blockId)) {
                MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
                List<Transaction> transactions = bPlusTree.getBPlusTree().mtquery(bPlusTree.getBPlusTree(),queries);
                if (!transactions.isEmpty()) return true;
            }
        }
        return false;
    }

    @Override
    public boolean propertyQueryByAllBlock(Map<String, String> queries) throws NoSuchFieldException, IllegalAccessException {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
            res.addAll(bPlusTree.getBPlusTree().mtquery(bPlusTree.getBPlusTree(),queries));
        }
        if (!res.isEmpty()) return true;
        return false;
    }

    @Override
    public boolean propertyRangeQueryBySingleBlock(Map<String, String> queries, String blockId, int topK) {
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            if (block.getId().equals(blockId)) {
                MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
                if (topK == 0) {
                    List<Transaction> transactions = bPlusTree.getBPlusTree().propertyQuery(queries);
                    if (!transactions.isEmpty()) return true;
                } else {
                    for (String type : queries.keySet()) {
                        List<Transaction> transactions = bPlusTree.getBPlusTree().propertyQueryTopK(type, topK);
                        if (!transactions.isEmpty()) return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean propertyRangeQueryByAllBlock(Map<String, String> queries, int topK) {
        List<Block> blocks = context.getBlocks();
        List<Transaction> res = new ArrayList<>();
        for (Block block : blocks) {
            MerkleBPlusTree bPlusTree = block.getMerkleBPlusTree();
            if (topK == 0) {
                res.addAll(bPlusTree.getBPlusTree().propertyQuery(queries));
            } else {
                for (String type : queries.keySet()) {
                    res.addAll(bPlusTree.getBPlusTree().propertyQueryTopK(type, topK));
                }
            }
        }
        if (!res.isEmpty()) return true;
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
