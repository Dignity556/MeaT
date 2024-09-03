package meat;

import blockchain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PSTBranchNode {
    private String branchId;
    private PSTExtensionNode previous;
    private Map<String,PSTBranchNodeItem> items = new HashMap<>(); //到该层时还剩的交易数量及对应的分类

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public PSTExtensionNode getPrevious() {
        return previous;
    }

    public void setPrevious(PSTExtensionNode previous) {
        this.previous = previous;
    }

    public Map<String, PSTBranchNodeItem> getItems() {
        return items;
    }

    public void setItems(Map<String, PSTBranchNodeItem> items) {
        this.items = items;
    }

    //按照属性值范围分类，假如属性的值是数值类型，用这个分类，默认0，100
    public Map<String, PSTBranchNodeItem> categoryByNumerical(List<Transaction> transactions, int amount, String property) throws IllegalAccessException, NoSuchFieldException {
        Map<String, PSTBranchNodeItem> items = new LinkedHashMap<>();
        double max=100000000;
        double min=0;
        double rangeSize = (max - min) / amount;
        for (int i = 0; i < amount; i++) {
            String rangeKey = String.format("%.2f-%.2f", min + i * rangeSize, min + (i + 1) * rangeSize);
            items.put(rangeKey, new PSTBranchNodeItem());
        }
        for (Transaction transaction : transactions) {
            Field field= transaction.getClass().getDeclaredField(property);
            field.setAccessible(true);
//            System.out.println(property);
//            System.out.println(transaction.id);
//            System.out.println(transaction.game_id);
            double value = Double.parseDouble((String) field.get(transaction)) ;
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
                items.get(str).setPrebranch(this);
                finalItems.put(str, items.get(str));
            }
        }
        return finalItems;
    }

    //按照属性值分类，假如多个属性都是以属性值的形式存在，调用这个
    public Map<String, PSTBranchNodeItem> categoryByValue(List<Transaction> transactions, String property) throws NoSuchFieldException, IllegalAccessException {
        Map<String, PSTBranchNodeItem> itemMap = new LinkedHashMap<>();

        for(Transaction tx: transactions) {
            Field field= tx.getClass().getDeclaredField(property);
            field.setAccessible(true);
            String type = (String) field.get(tx);
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
