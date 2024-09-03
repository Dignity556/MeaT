package mst;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import merkle.Leaf;
import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Field;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MSTTree {
    private TrieNode root = new TrieNode();
    private double[][] skylineMatrix;
    public void createMST(Context context) {
        List<Block> blocks = context.getBlocks();
        Map<Block, Set<String>> index = new HashMap<>();
        // 先构建正排索引
        for (Block block : blocks) {
            Set<String> types = new TreeSet<>();
            List<Transaction> txs = block.getTransactions();
            for (Transaction tx : txs) {
                String type = tx.getHome();
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
            if (cur.children.containsKey(word)) {
                return cur.children.get(word);
            } else {
                cur = cur.children.get(word);
            }
        }
        return null;
    }

    public boolean singleTransactionQuery(String txId, String nodeId) {
        Transaction tx = dfs(root, txId);
        if (tx != null && tx.getStartNode().getNodeId().equals(nodeId)) {
            return true;
        }
        return false;
    }

    private Transaction dfs(TrieNode node, String txId) {
        if (node.haveBlock) {
            Block block = node.block;
            List<Transaction> txs = block.getTransactions();
            for (Transaction tx : txs) {
                if (tx.getId().equals(txId)) return tx;
            }
        }
        for (TrieNode child : node.children.values()) {
            return dfs(child, txId);
        }
        return null;
    }

    public List<Transaction> singleNodeQuery(String nodeId, String blockId) {
        List<Transaction> txs = new ArrayList<>();
        nodeQueryIter(root, nodeId, txs);
        List<Transaction> res = new ArrayList<>();
        for (Transaction tx : txs) {
            if (tx.getBeLongBlock().getId().equals(blockId)) {
                res.add(tx);
            }
        }
        return res;
    }

    public List<Transaction> singleNodeQueryAllBlock(String nodeId) {
        List<Transaction> txs = new ArrayList<>();
        nodeQueryIter(root, nodeId, txs);
        return txs;
    }

    private void nodeQueryIter(TrieNode node, String nodeId, List<Transaction> txs) {
        if (node.haveBlock) {
            Block block = node.block;
            List<Transaction> transactions = block.getTransactions();
            for (Transaction transaction : transactions) {
                if (transaction.getStartNode().getNodeId().equals(nodeId)) {
                    txs.add(transaction);
                }
            }
        }
        for (TrieNode child : node.children.values()) {
            nodeQueryIter(child, nodeId, txs);
        }
    }

    public List<Transaction> propertyQuerySingleBlock(Map<String, String> queries, String blockId) throws NoSuchFieldException, IllegalAccessException {
        if (queries.get("home") != null) {
            List<String> keys = new ArrayList<>();
            String key = queries.get("home");
            keys.add(key);
            TrieNode search = search(keys);
            if (search != null && search.block.getId().equals(blockId)) {
                List<Transaction> txs = search.block.getTransactions();
                List<Transaction> filter=new ArrayList<>();
                for (String query:queries.keySet())
                {
                    filter=satisfy(txs,query,queries.get(query));
                }
                return filter;
            }
        } else {
            List<Transaction> res = new ArrayList<>();
            propertyQuerySingleBlockIter(queries, res, root, blockId);
            return res;
        }
        return null;
    }

    public List<Transaction> satisfy(List<Transaction> txs, String property, String target) throws NoSuchFieldException, IllegalAccessException {
        List<Transaction> all=new ArrayList<>();
        for (Transaction tx:txs)
        {
            Field field= tx.getClass().getDeclaredField(property);
            field.setAccessible(true);
            try {
                double value=Double.parseDouble((String) field.get(tx));
                String[] ranges=target.split(",");
                double min=Double.valueOf(ranges[0]);
                double max=Double.valueOf(ranges[1]);
                if ((value <= 0 && min==0) || (value <= max && value >= min) || (max==20000 && value>=20000)) {
                    all.add(tx);
                }
            } catch (NumberFormatException e) {
                if (target.equals((String) field.get(tx)))
                {
                    all.add(tx);
                }
            }
        }
        return all;
    }


    private void propertyQuerySingleBlockIter(Map<String, String> queries, List<Transaction> res, TrieNode node, String blockId) throws NoSuchFieldException, IllegalAccessException {
        if (node == null) return;
        if (node.haveBlock && node.block.getId().equals(blockId)) {
            res.addAll(satisfy(node.block.getTransactions(), queries));
        }
        for (TrieNode child : node.children.values()) {
            propertyQuerySingleBlockIter(queries, res, child, blockId);
        }
    }

    public List<Transaction> propertyQuery(Map<String, String> queries) throws NoSuchFieldException, IllegalAccessException {
        if (queries.get("home") != null) {
            List<String> keys = new ArrayList<>();
            String key = queries.get("home");
            keys.add(key);
            TrieNode search = search(keys);
            if (search != null) {
                List<Transaction> txs = search.block.getTransactions();
                List<Transaction> filter =new ArrayList<>();
                for (String query:queries.keySet())
                {
                    filter=satisfy(txs,query,queries.get(query));
                }
                return filter;
            } else {
                List<Transaction> res = new ArrayList<>();
                propertyQueryIter(queries, res, root);
                return res;
            }
        } else {
            List<Transaction> res = new ArrayList<>();
            propertyQueryIter(queries, res, root);
            return res;
        }
    }

    private void propertyQueryIter(Map<String, String> queries, List<Transaction> res, TrieNode node) throws NoSuchFieldException, IllegalAccessException {
        if (node == null) return;
        if (node.haveBlock) {
            res.addAll(satisfy(node.block.getTransactions(), queries));
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
        int timeCostMin = Double.valueOf(timeCost[0]).intValue();
        int timeCostMax = Double.valueOf(timeCost[1]).intValue();
        int reputationMin = Double.valueOf(reputation[0]).intValue();
        int reputationMax = Double.valueOf(reputation[1]).intValue();
        List<Transaction> res = new ArrayList<>();
        for (Transaction tx : txs) {
            int timeValue = Double.valueOf(tx.getTimeCostForDouble()).intValue();
            int repuValue = Double.valueOf(tx.getReputationForDouble()).intValue();
            if (((timeValue >= timeCostMin) && (timeValue <= timeCostMax)) ||
                    ((repuValue >= reputationMin) && (repuValue <= reputationMax))) {
                res.add(tx);
            }
        }
        return res;
    }

    //多属性查询
    private void mtquery(Map<String, String> queries, List<Transaction> res, TrieNode node) throws NoSuchFieldException, IllegalAccessException {
        if (node == null) return;
        if (node.haveBlock) {
            res.addAll(satisfy(node.block.getTransactions(), queries));
        }
        for (TrieNode child : node.children.values()) {
            propertyQueryIter(queries, res, child);
        }
    }


    public List<Transaction> satisfy(List<Transaction> txs, Map<String, String> queries) throws NoSuchFieldException, IllegalAccessException {
        List<Transaction> return_txs=new ArrayList<>();
        for (Transaction tx: txs)
        {
            boolean flag=true;
            for (String query:queries.keySet())
            {
                Field field= tx.getClass().getDeclaredField(query);
                field.setAccessible(true);
                try {
                    double value=Double.parseDouble((String) field.get(tx));
                    String[] ranges=queries.get(query).split(",");
                    double min=Double.valueOf(ranges[0]);
                    double max=Double.valueOf(ranges[1]);
                    if ((value <= 0 && min==0) || (value <= max && value >= min) || (max==20000 && value>=20000)) {
                    }else{
                        flag=false;
                        break;
                    }
                } catch (NumberFormatException e) {
                    if (queries.get(query).equals((String) field.get(tx)))
                    {

                    }else{
                        flag=false;
                    }
                }

            }
            if (flag)
            {
                return_txs.add(tx);
            }

        }
        return return_txs;
    }

    public List<Transaction> propertyQueryTopK(String type, int topK) {
        PriorityQueue<Transaction> priorityQueue;
        List<Transaction> res = new ArrayList<>();
        if (type.equals("time_cost")) {
            priorityQueue = new PriorityQueue<>(Transaction.compareByTimeCost);
        } else {
            priorityQueue = new PriorityQueue<>(Transaction.compareByReputation);
        }
        iter(priorityQueue, root);
        for (int i = 0; i < topK; i++) {
            res.add(priorityQueue.poll());
        }
        return res;
    }

    private void iter(PriorityQueue<Transaction> queue, TrieNode node) {
        if (node == null) return;
        if (node.haveBlock) {
            for (Transaction transaction : node.block.getTransactions()) {
                queue.add(transaction);
            }
        }
        for (TrieNode child : node.children.values()) {
            iter(queue, child);
        }
    }
    @Getter
    @Setter
    public class TrieNode {
        boolean haveBlock;
        Map<String, TrieNode> children;

        Block block;

        public TrieNode() {
            haveBlock = false;
            children = new HashMap<>();
        }
    }

}
