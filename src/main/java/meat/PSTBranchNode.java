package meat;

import blockchain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PSTBranchNode {
    private String branchId;
    private PSTExtensionNode previous;
    private Map<String,PSTBranchNodeItem> items = new HashMap<>(); //到该层时还剩的交易数量及对应的分类

    public Map<String, PSTBranchNodeItem> categoryByTimeCost(List<Transaction> transactions, int amount) {
        Map<String, PSTBranchNodeItem> items = new HashMap<>();
        double max = Double.parseDouble(transactions.stream()
                .max(Comparator.comparingDouble(Transaction::getTimeCostForDouble))
                .get()
                .getTimeCost());
        double min = Double.parseDouble(transactions.stream()
                .min(Comparator.comparingDouble(Transaction::getTimeCostForDouble))
                .get()
                .getTimeCost());
        for (int i = 0; i < amount; i++) {
            PSTBranchNodeItem item = new PSTBranchNodeItem();
            String range = String.valueOf((min + (i - 1) * (max - min)) / (amount - 1)) +
                    "," + String.valueOf((min + i * (max - min)) / (amount - 1));
            items.put(range, item);
        }
        for (Transaction ts : transactions) {
            int mole = (int)Math.floor(((amount - 1) * (Double.valueOf(ts.getTimeCost()) - min)) / (max - min));
            String key = String.valueOf((min + (mole - 1) * (max - min)) / (amount - 1)) + "," +
                    String.valueOf((min + mole * (max - min)) / (amount - 1));
            items.get(key).getPreTransactions().add(ts);
        }
        Map<String, PSTBranchNodeItem> finalItems = new HashMap<>();
        for (String str : items.keySet()) {
            if (items.get(str).getPreTransactions().size() != 0) {
                finalItems.put(str, items.get(str));
            }
        }
        return finalItems;
    }

    public Map<String, PSTBranchNodeItem> categoryByReputation(List<Transaction> transactions, int amount) {
        Map<String, PSTBranchNodeItem> items = new HashMap<>();
        double max = Double.parseDouble(transactions.stream()
                .max(Comparator.comparingDouble(Transaction::getReputationForDouble))
                .get()
                .getReputation());
        double min = Double.parseDouble(transactions.stream()
                .min(Comparator.comparingDouble(Transaction::getReputationForDouble))
                .get()
                .getReputation());
        for (int i = 0; i < amount; i++) {
            PSTBranchNodeItem item = new PSTBranchNodeItem();
            String range = String.valueOf((min + (i - 1) * (max - min)) / (amount - 1)) +
                    "," + String.valueOf((min + i * (max - min)) / (amount - 1));
            // 记录具体的范围与对应的交易
            items.put(range, item);
        }
        for (Transaction ts : transactions) {
            int mole = (int)Math.floor(((amount - 1) * (Double.valueOf(ts.getReputation()) - min)) / (max - min));
            String key = String.valueOf((min + (mole - 1) * (max - min)) / (amount - 1)) + "," +
                    String.valueOf((min + mole * (max - min)) / (amount - 1));
            items.get(key).getPreTransactions().add(ts);
        }
        Map<String, PSTBranchNodeItem> finalItems = new HashMap<>();
        for (String str : items.keySet()) {
            if (items.get(str).getPreTransactions().size() != 0) {
                finalItems.put(str, items.get(str));
            }
        }
        return finalItems;
    }

    public Map<String, PSTBranchNodeItem> categoryByReputation2(List<Transaction> transactions, int amount) {
        Map<String, PSTBranchNodeItem> items = new LinkedHashMap<>();
        double max = Double.parseDouble(transactions.stream()
                .max(Comparator.comparingDouble(Transaction::getReputationForDouble))
                .get()
                .getReputation());
        double min = Double.parseDouble(transactions.stream()
                .min(Comparator.comparingDouble(Transaction::getReputationForDouble))
                .get()
                .getReputation());
        double rangeSize = (max - min) / amount;
        for (int i = 0; i < amount; i++) {
            String rangeKey = String.format("%.2f-%.2f", min + i * rangeSize, min + (i + 1) * rangeSize);
            items.put(rangeKey, new PSTBranchNodeItem());
        }
        for (Transaction transaction : transactions) {
            double value = transaction.getReputationForDouble();
            int rangeIndex = (int) ((value - min) / rangeSize);
            if (rangeIndex == amount) {
                rangeIndex--; // Handle the case where value is equal to maxValue
            }
            String rangeKey = String.format("%.2f-%.2f", min + rangeIndex * rangeSize, min + (rangeIndex + 1) * rangeSize);
            items.get(rangeKey).getPreTransactions().add(transaction);
        }
        Map<String, PSTBranchNodeItem> finalItems = new HashMap<>();
        for (String str : items.keySet()) {
            if (items.get(str).getPreTransactions().size() != 0) {
                finalItems.put(str, items.get(str));
            }
        }
        return finalItems;
    }

    public Map<String, PSTBranchNodeItem> categoryByTimeCost2(List<Transaction> transactions, int amount) {
        Map<String, PSTBranchNodeItem> items = new LinkedHashMap<>();
        double max = Double.parseDouble(transactions.stream()
                .max(Comparator.comparingDouble(Transaction::getTimeCostForDouble))
                .get()
                .getTimeCost());
        double min = Double.parseDouble(transactions.stream()
                .min(Comparator.comparingDouble(Transaction::getTimeCostForDouble))
                .get()
                .getTimeCost());
        double rangeSize = (max - min) / amount;
        for (int i = 0; i < amount; i++) {
            String rangeKey = String.format("%.2f-%.2f", min + i * rangeSize, min + (i + 1) * rangeSize);
            items.put(rangeKey, new PSTBranchNodeItem());
        }
        for (Transaction transaction : transactions) {
            double value = transaction.getTimeCostForDouble();
            int rangeIndex = (int) ((value - min) / rangeSize);
            if (rangeIndex == amount) {
                rangeIndex--; // Handle the case where value is equal to maxValue
            }
            String rangeKey = String.format("%.2f-%.2f", min + rangeIndex * rangeSize, min + (rangeIndex + 1) * rangeSize);
            items.get(rangeKey).getPreTransactions().add(transaction);
        }
        Map<String, PSTBranchNodeItem> finalItems = new HashMap<>();
        for (String str : items.keySet()) {
            if (items.get(str).getPreTransactions().size() != 0) {
                finalItems.put(str, items.get(str));
            }
        }
        return finalItems;
    }

    /**
     * 根据具体类型分交易
     * @param transactions
     * @return
     */
    public Map<String, PSTBranchNodeItem> categoryByType(List<Transaction> transactions) {
        Map<String, PSTBranchNodeItem> itemMap = new LinkedHashMap<>();
        for(Transaction tx: transactions) {
            String type = tx.getType();
            if (!itemMap.containsKey(type)) {
                PSTBranchNodeItem item = new PSTBranchNodeItem();
                item.getPreTransactions().add(tx);
                itemMap.put(type, item);
            } else {
                itemMap.get(type).getPreTransactions().add(tx);
            }
        }
        return itemMap;
    }

    public Map<String, PSTBranchNodeItem> leafOrBranch(Map<String,PSTBranchNodeItem> items, PSTBranchNode branchNode) {
        Map<String, PSTBranchNodeItem> returnItems = new HashMap<>();
        for (String key : items.keySet()) {
            if (items.get(key).getPreTransactions().size() == 0) {
//                System.out.println("A zero item is created");
            } else if (items.get(key).getPreTransactions().size() == 1) { // 设置branch下边的叶子节点，叶子节点存储实际的交易
                PSTLeafNode leafNode = new PSTLeafNode();
                leafNode.setTransaction(items.get(key).getPreTransactions().get(0));
                leafNode.setPreBranch(items.get(key));
                leafNode.setId("prebranchitem_" + items.get(key).getId() + "_leafnode");
                items.get(key).setNextLeaf(leafNode);
                items.get(key).setId("branchitem_nextleaf_" + items.get(key).getNextLeaf().getId());
                returnItems.put(key, items.get(key));
            } else {
                PSTExtensionNode extensionNode = new PSTExtensionNode();
                extensionNode.setPreItem(items.get(key));
                extensionNode.setId("prebranchitem:" + items.get(key).toString());
                items.get(key).setNextExtension(extensionNode);
                returnItems.put(key, items.get(key));
                items.get(key).setId("branchitem_nextextension_" +
                        items.get(key).getNextExtension().getId());
            }
        }
        return returnItems;
    }
}
