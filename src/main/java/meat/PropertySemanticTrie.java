package meat;

import blockchain.Block;
import blockchain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class PropertySemanticTrie {
    private static boolean repuFlag = true;
    private static boolean timeFlag = true;

    @Override
    public String toString() {
        return super.toString();
    }

    public static void createPST(Block block, String[] filterOrder, int amount) {
        List<Transaction> transactions = block.getTransactions();
        PSTExtensionNode rootExtension = new PSTExtensionNode();
        // pst根节点与mgt互相建立联系
        rootExtension.setRootMGT(block.getMgt());
        block.getMgt().getRoot().setExtensionNode(rootExtension);

        rootExtension.setProperty(filterOrder[0]);
        rootExtension.setPreItem(null);
        rootExtension.setId("root" + filterOrder[0]);

        // 建立下一层branch
        PSTBranchNode branchNode = new PSTBranchNode();
        rootExtension.setNextBranch(branchNode);
        branchNode.setPrevious(rootExtension);
        branchNode.setBranchId("branch:" + rootExtension.getProperty() + ",pre_extension:" + rootExtension.getId());

        // TODO 矩阵生成
        double[][] matrix = new double[transactions.size()][transactions.size()];

        // 键为对应的属性类，值为该属性对应的item，实际存储的为交易
        Map<String, PSTBranchNodeItem> itemMap = new LinkedHashMap<>();
        if (filterOrder[0].equals("type")) {
            itemMap = branchNode.categoryByType(transactions);
        } else if (filterOrder[0].equals("reputation")) {
            itemMap = branchNode.categoryByReputation2(transactions, amount);
        } else {
            itemMap = branchNode.categoryByTimeCost2(transactions, amount);
        }
        branchNode.setItems(itemMap);
        branchNode.setPrevious(rootExtension);
        // 如果item中只有一个交易，生成叶子节点，不是生成extension node
        Map<String, PSTBranchNodeItem> newItem = branchNode.leafOrBranch(itemMap, branchNode);

        // 递归构建
        iterateFilter(newItem, 1, filterOrder, amount, branchNode, matrix, newItem);
    }

    public static void iterateFilter(Map<String, PSTBranchNodeItem> branchItems, int pre_type, String[] types,
                                     int amount, PSTBranchNode branchNode, double[][] matrix, Map<String, PSTBranchNodeItem> source) {
        for (String key: branchItems.keySet()) {
            //判断是否达到叶子节点 是叶子节点就变成叶子节点
            if (branchItems.get(key).getNextLeaf() != null || pre_type == 3) {
                PSTBranchNodeItem item = branchItems.get(key);
                branchItems.get(key).setNextExtension(null);
                PSTLeafNode leafNode = new PSTLeafNode();
                for (Transaction tx : branchItems.get(key).getPreTransactions()) {
                    leafNode.addTransaction(tx);
                }
                branchItems.get(key).setNextLeaf(leafNode);
                leafNode.setPreBranch(branchItems.get(key));
                leafNode.setId("prebranch" + leafNode.getPreBranch().getId() + "leafnode");
//                System.out.println("Leaf");
            } else {
                PSTExtensionNode extensionNode = branchItems.get(key).getNextExtension();
                if (extensionNode != null) {
                    extensionNode.setProperty(types[pre_type]);
                    PSTBranchNode branch1 = new PSTBranchNode();
                    extensionNode.setNextBranch(branch1);
                    branch1.setBranchId("branch:" + extensionNode.getProperty() + ",pre_extension:" + extensionNode.getId());
                    extensionNode.setId("preitem_" + branchItems.get(key).getId() + "_nextbranch_" + branch1);
                    branch1.setPrevious(extensionNode);
                    Map<String,PSTBranchNodeItem> branchitems1 = new HashMap<>();
                    if(types[pre_type].equals("type")) {
                        branchitems1 = branch1.categoryByType(branchItems.get(key).getPreTransactions());
//                        System.out.println("This layer: type");
                    } else if(types[pre_type].equals("time_cost")) {
                        branchitems1 = branch1.categoryByTimeCost2(branchItems.get(key).getPreTransactions(), amount);
                        int a = 0;
//                        System.out.println("This layer: time");
                    } else {
                        branchitems1 = branch1.categoryByReputation2(branchItems.get(key).getPreTransactions(), amount);
//                        System.out.println("This layer: repu");
                    }
                    Map<String,PSTBranchNodeItem> new_branch_items = branch1.leafOrBranch(branchitems1, branch1);
                    if (types[pre_type].equals("time_cost") && timeFlag) {
                        updateMatrix(source, matrix, types[pre_type]);
                        timeFlag = false;
                    }
                    if (types[pre_type].equals("reputation") && repuFlag) {
                        updateMatrix(source, matrix, types[pre_type]);
                        repuFlag = false;
                    }
                    iterateFilter(new_branch_items, pre_type + 1, types, amount, branch1, matrix, source);
                }
            }
        }
    }

    private static void updateMatrix(Map<String, PSTBranchNodeItem> items, double[][] matrix, String type) {
        List<Transaction> transactions = new ArrayList<>();
        for (Map.Entry<String, PSTBranchNodeItem> entry : items.entrySet()) {
            transactions.addAll(entry.getValue().getPreTransactions());
        }
        if (type.equals("reputation")) {
            transactions.sort(Comparator.comparingDouble(Transaction::getReputationForDouble));
        } else {
            transactions.sort(Comparator.comparingDouble(Transaction::getTimeCostForDouble));
        }
        for (int i = 0; i < transactions.size(); i++) {
            Transaction tx = transactions.get(i);
            for (int j = 0; j <= i ; j++) {
                Transaction target = transactions.get(j);
                matrix[tx.getMatrixId()][target.getMatrixId()] += 0.5;
            }
        }
    }
}
