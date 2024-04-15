package meat;

import blockchain.Block;
import blockchain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PropertySemanticTrie {
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

        Map<String, PSTBranchNodeItem> itemMap = new HashMap<>();
        if (filterOrder[0].equals("type")) {
            itemMap = branchNode.categoryByType(transactions);
        } else if (filterOrder[0].equals("reputation")) {
            itemMap = branchNode.categoryByReputation(transactions, amount);
        } else {
            itemMap = branchNode.categoryByTimeCost(transactions, amount);
        }
        branchNode.setItems(itemMap);
        branchNode.setPrevious(rootExtension);
        Map<String, PSTBranchNodeItem> newItem = branchNode.leafOrBranch(itemMap, branchNode);

        // 递归构建
        iterateFilter(newItem, 0, filterOrder, amount, branchNode);
    }

    public static void iterateFilter(Map<String, PSTBranchNodeItem> branchItems, int pre_type, String[] types, int amount, PSTBranchNode branchNode) {
        for (String key: branchItems.keySet()) {
            //判断是否达到叶子节点 是叶子节点就变成叶子节点
            if (branchItems.get(key).getNextLeaf() != null || pre_type == 3) {
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
                if(extensionNode != null) {
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
                        branchitems1 = branch1.categoryByTimeCost(branchItems.get(key).getPreTransactions(), amount);
//                        System.out.println("This layer: time");
                    } else {
                        branchitems1 = branch1.categoryByReputation(branchItems.get(key).getPreTransactions(), amount);
//                        System.out.println("This layer: repu");
                    }
                    Map<String,PSTBranchNodeItem> new_branch_items = branch1.leafOrBranch(branchitems1, branch1);
                    iterateFilter(new_branch_items, pre_type + 1, types, amount, branch1);
                }
            }
        }
    }
}
