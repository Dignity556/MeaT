package meat;

import blockchain.Block;
import blockchain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.formula.functions.T;
import query.SkylineGraph;

import java.lang.reflect.Field;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class PropertySemanticTrie {
    private static boolean repuFlag = true;
    private static boolean timeFlag = true;
    public static double[][] matrix;
    public static long matrixTime;


    @Override
    public String toString() {
        return super.toString();
    }

    public static long createPST(Block block, String[] filterOrder, int amount) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<Transaction> transactions = (ArrayList<Transaction>) block.getTransactions();
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
        matrix = new double[transactions.size()][transactions.size()];

        // 键为对应的属性类，值为该属性对应的item，实际存储的为交易
        Map<String, PSTBranchNodeItem> itemMap = new LinkedHashMap<>();
        ;
        if (judge(filterOrder[0],transactions).equals("numerical"))
        {
            itemMap=branchNode.categoryByNumerical(transactions,20000,filterOrder[0]);
        }else
        {
            itemMap=branchNode.categoryByValue(transactions,filterOrder[0]);
        }
//        if (filterOrder[0].equals("type")) {
//            itemMap = branchNode.categoryByType(transactions);
//        } else if (filterOrder[0].equals("reputation")) {
//            itemMap = branchNode.categoryByReputation2(transactions, amount);
//        } else {
//            itemMap = branchNode.categoryByTimeCost2(transactions, amount);
//        }
        branchNode.setItems(itemMap);
        branchNode.setPrevious(rootExtension);
        // 如果item中只有一个交易，生成叶子节点，不是生成extension node
        Map<String, PSTBranchNodeItem> newItem = branchNode.leafOrBranch(itemMap, branchNode);
        block.getMgt().setSkylineMatrix(matrix);

        timeFlag = true;
        repuFlag = true;
        // 递归构建
        iterateFilter(newItem, 1, filterOrder, amount, branchNode, matrix, newItem,transactions);
        return matrixTime;
    }

    public static void iterateFilter(Map<String, PSTBranchNodeItem> branchItems, int pre_type, String[] types,
                                     int amount, PSTBranchNode branchNode, double[][] matrix, Map<String, PSTBranchNodeItem> source, ArrayList<Transaction> transactions) throws NoSuchFieldException, IllegalAccessException {
        for (String key: branchItems.keySet()) {
            //判断是否达到叶子节点 是叶子节点就变成叶子节点
            if (branchItems.get(key).getNextLeaf() != null || pre_type == types.length) {
                PSTBranchNodeItem item = branchItems.get(key);
                branchItems.get(key).setNextExtension(null);
                PSTLeafNode leafNode = new PSTLeafNode();
                for (Transaction tx : branchItems.get(key).getPreTransactions()) {
                    leafNode.addTransaction(tx);
                }
                branchItems.get(key).setNextLeaf(leafNode);
                leafNode.setPreBranch(branchItems.get(key));
                leafNode.setId("prebranch" + leafNode.getPreBranch().getId() + "leafnode");
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
                    if(judge(types[pre_type],transactions).equals("numerical")) {
                       branchitems1 = branch1.categoryByNumerical(branchItems.get(key).getPreTransactions(), amount, types[pre_type]);
                    } else {
                        branchitems1 = branch1.categoryByValue(branchItems.get(key).getPreTransactions(), types[pre_type]);
                    }
                    Map<String,PSTBranchNodeItem> new_branch_items = branch1.leafOrBranch(branchitems1, branch1);
                    if (types[pre_type].equals("game_id") && timeFlag) {
                        long t = updateMatrix(source, matrix, types[pre_type]);
                        matrixTime += t;
                        timeFlag = false;
                    }
                    if (types[pre_type].equals("t_point") && repuFlag) {
                        long t = updateMatrix(source, matrix, types[pre_type]);
                        matrixTime += t;
                        repuFlag = false;
                    }
                    iterateFilter(new_branch_items, pre_type + 1, types, amount, branch1, matrix, source, transactions);
                }
            }
        }
    }

    private static long updateMatrix(Map<String, PSTBranchNodeItem> items, double[][] matrix, String type) {
        long start = System.nanoTime();
        List<Transaction> transactions = new ArrayList<>();
        for (Map.Entry<String, PSTBranchNodeItem> entry : items.entrySet()) {
            transactions.addAll(entry.getValue().getPreTransactions());
        }
        if (type.equals("game_id")) {
            transactions.sort(Comparator.comparingDouble(Transaction::getGameidfordouble));
        } else {
            transactions.sort(Comparator.comparingDouble(Transaction::getTPointForDouble));
        }
        for (int i = 0; i < transactions.size(); i++) {
            Transaction tx = transactions.get(i);
            if (tx.getMatrixId()==0)
            {
                tx.setMatrixId(i);
            }
            for (int j = 0; j <= i ; j++) {
                Transaction target = transactions.get(j);
                if (target.getMatrixId()==0)
                {
                    target.setMatrixId(j);
                }
                matrix[tx.getMatrixId()][target.getMatrixId()] += 0.5;
            }
        }
//        for (double[] row : matrix) {
//            for (double element : row) {
//                System.out.print(element + " ");
//            }
//            System.out.println();
//        }
        long end = System.nanoTime();
        return (end - start);
    }

    public static String judge(String filterorder,ArrayList<Transaction> transactions) throws IllegalAccessException, NoSuchFieldException {
        Transaction transaction=transactions.get(0);
        Field field= transaction.getClass().getDeclaredField(filterorder);
        field.setAccessible(true);
        try {
            Double.parseDouble((String) field.get(transaction));
//            System.out.println(field.get(transaction));
            return "numerical"; // 转换成功
        } catch (NumberFormatException e) {
            return "value"; // 转换失败
        }
    }



    public static long matrix_row_merge()
    {
        long start=System.nanoTime();
        for (int row=0; row<matrix.length; row++)
        {
            int sum=0;
        }
        long end=System.nanoTime();
        return (end-start);
    }

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        PropertySemanticTrie pst=new PropertySemanticTrie();
        Transaction tx=new Transaction();
        tx.setType("A");
        tx.setTimestamp("123");
        ArrayList<Transaction> transactions=new ArrayList<>();
        transactions.add(tx);
        String s=pst.judge("timestamp",transactions);
        System.out.println(s);

    }
}
