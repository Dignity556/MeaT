package mst;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MSTTree {
    private TrieNode root;
    public void createMST(Context context) {
        List<Block> blocks = context.getBlocks();
        Map<Block, Set<String>> index = new HashMap<>();
        // 先构建正排索引
        for (Block block : blocks) {
            Set<String> types = new TreeSet<>();
            List<Transaction> txs = block.getTransactions();
            for (Transaction tx : txs) {
                String type = tx.getType();
                types.add(type);
            }
            index.put(block, types);
        }
        for (Block block : index.keySet()) {
            Set<String> keys = index.get(block);
            insert(block, keys);
        }
    }

    private void insert(Block block, Set<String> words) {
        TrieNode cur = root;

        for (String word : words) {
            cur.children.computeIfAbsent(word, key -> new TrieNode());
            cur.children.get(word).haveBlock = true;
            cur.children.get(word).block = block;
        }

        for (String word : words) {
            cur = cur.children.computeIfAbsent(word, key -> new TrieNode());
        }
        cur.haveBlock = true;
        cur.block = block;
    }

    private TrieNode search(List<String> words){
        TrieNode cur = root;
        for (String word : words) {
            if (!cur.children.containsKey(word)) {
                return cur.children.get(word);
            } else {
                cur = cur.children.get(word);
            }
        }
        return null;
    }

    public boolean singleTransactionQuery(String txId) {
        TrieNode res = dfs(root, txId);
        if (res != null) {
            return true;
        }
        return false;
    }

    private TrieNode dfs(TrieNode node, String txId) {
        if (!node.haveBlock) {
            return null;
        } else {
            Block block = node.block;
            List<Transaction> txs = block.getTransactions();
            for (Transaction tx : txs) {
                if (tx.getId().equals(txId)) {
                    return node;
                }
            }
        }
        for (TrieNode child : node.children.values()) {
            dfs(child, txId);
        }
        return null;
    }



    public List<Transaction> singleNodeQuery(String nodeId) {
        List<Transaction> txs = new ArrayList<>();
        nodeQueryIter(root, nodeId, txs);
        return txs;
    }

    private void nodeQueryIter(TrieNode node, String nodeId, List<Transaction> txs) {
        if (!node.haveBlock) return;
        Block block = node.block;
        List<Transaction> transactions = block.getTransactions();
        for (Transaction transaction : transactions) {
            if (transaction.getStartNode().getNodeId().equals(nodeId)) {
                txs.add(transaction);
            }
        }
        for (TrieNode child : node.children.values()) {
            nodeQueryIter(child, nodeId, txs);
        }
    }

    public List<Transaction> propertyQuery(Map<String, String> queries) {
        if (queries.get("type") != null) {
            List<String> keys = new ArrayList<>();
            String key = queries.get("type");
            keys.add(key);
            TrieNode search = search(keys);
            List<Transaction> txs = search.block.getTransactions();
            List<Transaction> filter = filter(txs, queries);
            return filter;
        } else {
            List<Transaction> res = new ArrayList<>();
            propertyQueryIter(queries, res, root);
            return res;
        }
    }

    private void propertyQueryIter(Map<String, String> queries, List<Transaction> res, TrieNode node) {
        if (node == null) return;
        if (node.haveBlock) {
            res.addAll(filter(node.block.getTransactions(), queries));
        }
        for (TrieNode child : node.children.values()) {
            propertyQueryIter(queries, res, child);
        }
    }

    private List<Transaction> filter(List<Transaction> txs, Map<String, String> queries) {
        String[] timeCost = new String[2];
        String[] reputation = new String[2];
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
        double timeCostMin = Double.valueOf(timeCost[0]);
        double timeCostMax = Double.valueOf(timeCost[1]);
        double reputationMin = Double.valueOf(reputation[0]);
        double reputationMax = Double.valueOf(reputation[1]);
        List<Transaction> res = new ArrayList<>();
        for (Transaction tx : txs) {
            double timeValue = tx.getTimeCostForDouble();
            double repuValue = tx.getReputationForDouble();
            if ((timeValue > timeCostMin) && (timeValue < timeCostMax) &&
                    (repuValue > reputationMin) && (repuValue < reputationMax)) {
                res.add(tx);
            }
        }
        return res;
    }
    class TrieNode {
        boolean haveBlock;
        Map<String, TrieNode> children;

        Block block;

        public TrieNode() {
            haveBlock = false;
            children = new HashMap<>();
        }
    }

}
