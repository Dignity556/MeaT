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
