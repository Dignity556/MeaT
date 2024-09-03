package query;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import meat.*;
import merkle.Leaf;
import merklebplus.BPlusTree;
import mst.MST;
import mst.MSTTree;

import java.util.*;

public class MemoryCalculator {
    private Context context;

    public MemoryCalculator(Context context) {
        this.context = context;
    }

    public long getMGTSize() {
        long size = 0;

        List<GraphLeaf> graphLeafList = new ArrayList<>();
        List<PSTExtensionNode> extensionNodeList = new ArrayList<>();
        List<PSTBranchNode> branchNodeList = new ArrayList<>();
        List<PSTBranchNodeItem> itemList = new ArrayList<>();
        List<PSTLeafNode> leafNodeList = new ArrayList<>();

        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            GraphLeaf root = block.getMgt().getRoot();
            GraphNodeLink gnl = block.getMgt().getGnl();
            // 遍历上层mgt
            mgtdfs(graphLeafList, root);

            // 遍历pst
            PSTExtensionNode extensionRoot = root.getExtensionNode();
            Queue<PSTExtensionNode> queue = new LinkedList<>();
            queue.offer(extensionRoot);
            extensionNodeList.add(extensionRoot);
            while (!queue.isEmpty()) {
                PSTExtensionNode extensionNode = queue.poll();
                PSTBranchNode branch = extensionNode.getNextItem();
                if (branch != null) {
                    branchNodeList.add(branch);
                    Map<String, PSTBranchNodeItem> items = branch.getItems();
                    if (!items.isEmpty()) {
                        for (PSTBranchNodeItem item : items.values()) {
                            if (item != null) {
                                itemList.add(item);
                                PSTLeafNode nextLeaf = item.getNextLeaf();
                                if (nextLeaf != null) {
                                    leafNodeList.add(nextLeaf);
                                }
                                PSTExtensionNode nextExtension = item.getNextExtension();
                                if (nextExtension != null) {
                                    queue.offer(nextExtension);
                                    extensionNodeList.add(nextExtension);
                                }
                            }
                        }
                    }
                }
            }
            // 遍历gnl和下层mgt
            for (GraphNodeLinkItem item : gnl.getItems().values()) {
                size += MyObjectSizeCalculator.getObjectSize(item);
                GraphLeaf lowerRoot = item.getRoot();
                mgtdfs(graphLeafList, lowerRoot);
            }
        }
        // 计算内存
        for (PSTExtensionNode extensionNode : extensionNodeList) {
            size += MyObjectSizeCalculator.getObjectSize(extensionNode);
        }
        for (PSTBranchNode branchNode : branchNodeList) {
            size += MyObjectSizeCalculator.getObjectSize(branchNode);
        }
        for (PSTBranchNodeItem item : itemList) {
            size += MyObjectSizeCalculator.getObjectSize(item);
        }
        for (PSTLeafNode pstLeafNode : leafNodeList) {
            size += MyObjectSizeCalculator.getObjectSize(pstLeafNode);
        }
        for (GraphLeaf leaf : graphLeafList) {
            size += MyObjectSizeCalculator.getObjectSize(leaf);
        }
        return size;
    }

    private void mgtdfs(List<GraphLeaf> list, GraphLeaf node) {
        if (node == null) return;
        list.add(node);
        mgtdfs(list, node.getLeft());
        mgtdfs(list, node.getRight());
    }

    public long getMerkleSize() {
        long size = 0;
        List<Leaf> leafList = new ArrayList<>();
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            Leaf root = block.getMerkleTree().root;
            Queue<Leaf> queue = new LinkedList<>();
            queue.offer(root);
            while (!queue.isEmpty()) {
                int length = queue.size();
                while (length > 0) {
                    Leaf poll = queue.poll();
                    leafList.add(poll);
                    if (poll.getLeft() != null) queue.offer(poll.getLeft());
                    if (poll.getRight() != null) queue.offer(poll.getRight());
                    length--;
                }
            }
        }
        for (Leaf leaf : leafList) {
            size += MyObjectSizeCalculator.getObjectSize(leaf);
        }
        return size;
    }

    public long getMerkleBPlusSize() {
        long size = 0;
        List<Block> blocks = context.getBlocks();
        List<BPlusTree.Node> list = new ArrayList<>();
        List<merklebplus.Leaf> leafList = new ArrayList<>();
        for (Block block : blocks) {
            BPlusTree<Transaction, Integer> bPlusTree = block.getMerkleBPlusTree().getBPlusTree();
            BPlusTree<Transaction, Integer>.Node<Transaction, Integer> root = bPlusTree.getRoot();
            dfs(list, root);
            BPlusTree<Transaction, Integer>.LeafNode<Transaction, Integer> left = bPlusTree.getLeft();
            while (left != null) {
                if (left.getMerkleRoot() != null) {
                    merklebplus.Leaf merkleRoot = left.getMerkleRoot();
                    bdfs(leafList, merkleRoot);
                }
                left = left.getLeft();
            }
        }
        for (BPlusTree.Node node : list) {
            size += MyObjectSizeCalculator.getObjectSize(node);
        }
        for (merklebplus.Leaf leaf : leafList) {
            size += MyObjectSizeCalculator.getObjectSize(leaf);
        }
        return size;
    }
    public long getMSTSize(MST mst) {
        long size = 0;
        MSTTree mstTree = mst.getMstTree();
        List<MSTTree.TrieNode> list = new ArrayList<>();
        mstdfs(list, mstTree.getRoot());
        for (MSTTree.TrieNode trieNode : list) {
            size += MyObjectSizeCalculator.getObjectSize(trieNode);
        }
        List<Block> blocks = context.getBlocks();
        for (Block block : blocks) {
            size += MyObjectSizeCalculator.getObjectSize(block);
        }
        return size;
    }
    private void mstdfs(List<MSTTree.TrieNode> list, MSTTree.TrieNode node) {
        if (node == null) return;
        list.add(node);
        Map<String, MSTTree.TrieNode> children = node.getChildren();
        if (children != null) {
            for (MSTTree.TrieNode trieNode : children.values()) {
                mstdfs(list, trieNode);
            }
        }
    }
    private void dfs(List<BPlusTree.Node> list, BPlusTree.Node node) {
        if (node == null) return;
        list.add(node);
        for (BPlusTree.Node child : node.getChilds()) {
            dfs(list, child);
        }
    }
    private void bdfs(List<merklebplus.Leaf> list, merklebplus.Leaf node) {
        if (node == null) return;
        list.add(node);
        bdfs(list, node.getLeft());
        bdfs(list, node.getRight());
    }
}
