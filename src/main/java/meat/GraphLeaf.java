package meat;

import blockchain.Block;
import blockchain.Transaction;
import graph.Edge;
import graph.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraphLeaf implements Serializable {
    // id本身无实际意义
    private String id;
    private byte[] hashId;
    private Block beLongBlock;
    private Edge edge;
    private GraphLeaf father;
    private GraphLeaf left;
    private GraphLeaf right;
    private Node subTreeNode; //上层mgt中叶子结点与实际node相连

    // 只有根节点才与pst连接
    private PSTExtensionNode extensionNode;

    public GraphLeaf(Edge edge) throws NoSuchAlgorithmException {
        this.edge = edge;
        this.beLongBlock = edge.getBlock();
        this.hashId = calculateSHA256(edge.getId());
    }

    public static byte[] calculateSHA256(String input) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static GraphLeaf edgeToLeaf(Edge edge) throws NoSuchAlgorithmException {
        GraphLeaf graphLeaf = new GraphLeaf();
        graphLeaf.setBeLongBlock(edge.getBlock());
        graphLeaf.setEdge(edge);
        graphLeaf.setHashId(calculateSHA256(edge.getId()));
        // TODO 设置subtreenode为startnode
        graphLeaf.setSubTreeNode(edge.getStartNode());
        return graphLeaf;
    }

    public static GraphLeaf transactionToLeaf(Transaction transaction) {
        GraphLeaf graphLeaf = new GraphLeaf();
        graphLeaf.setBeLongBlock(transaction.getBeLongBlock());
        graphLeaf.setHashId(calculateSHA256(transaction.getId().toString()));
        // TODO 设置subtreenode为startnode，转化为交易不需要该字段
        // graphLeaf.setSubTreeNode(transaction.getStartNode());
        graphLeaf.setEdge(new Edge(transaction));
        return graphLeaf;
    }

    public static GraphLeaf nodeToLeaf(Node node) {
        GraphLeaf leaf = new GraphLeaf();
        leaf.setHashId(calculateSHA256(node.getNodeId()));
        // 设置关联的node
        leaf.setSubTreeNode(node);
        return leaf;
    }

    // 下层mgt的查询，深度优先搜索
    public boolean singleTransactionQuery(String txId) {
        if (dfs(this, txId) != null) {
            return true;
        } else {
            return false;
        }
    }
    public Transaction retrunTransactionQuery(String txId)
    {
        if (dfs(this, txId) != null) {
            return dfs(this,txId);
        } else {
            return null;
        }
    }

    private Transaction dfs(GraphLeaf leaf, String txId) {
        if (leaf.getLeft() == null && leaf.getEdge() != null &&
                leaf.getEdge().getTransaction().getId().equals(txId)) {
            return leaf.getEdge().getTransaction();
        }
        if (leaf.getLeft() != null) {
            return dfs(leaf.getLeft(), txId);
        }
        if (leaf.getRight() != null) {
            return dfs(leaf.getRight(), txId);
        }
        return null;
    }

    public List<Transaction> propertyRangeQuery(Map<String, String> queries) {
        PSTBranchNode branchNode = extensionNode.getNextItem();
        Map<String, PSTBranchNodeItem> items = branchNode.getItems();
        List<Transaction> txs = new ArrayList<>();
        Queue<PSTExtensionNode> extensionQueue = new LinkedList<>();
        Map<String, String> newQueries = new HashMap<>();
        for (Map.Entry<String, String> entry : queries.entrySet()) {
            newQueries.put(entry.getKey(), entry.getValue());
        }
        if (queries.containsKey("type")) {
            extensionQueue.add(typeExtensionFilter(newQueries.get("type"), items));
            txs.addAll(typeLeafFilter(newQueries.get("type"), items));
            newQueries.remove("type");
        } else {
            for (String key : newQueries.keySet()) {
                if (items.get(key) != null && items.get(key).getNextExtension() != null) {
                    extensionQueue.add(items.get(key).getNextExtension());
                }
                if (items.get(key) != null && items.get(key).getNextLeaf() != null) {
                    txs.addAll(items.get(key).getNextLeaf().getTransactions());
                }
            }
        }
        // 第二层用递归方法
        if (newQueries.size() != 0) {
           txs.addAll(iterateRangeQueryPST(extensionQueue, newQueries));
        }
        return txs;
    }

    public List<Transaction> iterateRangeQueryPST(Queue<PSTExtensionNode> extensionQueue, Map<String,String> queries){
        List<Transaction> txs = new ArrayList<>();
        if (queries.containsKey("time_cost") && !queries.containsKey("reputation")) {
            while (extensionQueue.size() != 0) {
                if (extensionQueue.peek().getProperty().equals("time_cost")) {
                    PSTExtensionNode pre = extensionQueue.peek();
                    Map<String, PSTBranchNodeItem> items = pre.getNextItem().getItems();
                    txs.addAll(valueLeafFilter("time_cost", queries.get("time_cost"), items));
                    extensionQueue.addAll(valueExtensionFilter("time_cost", queries.get("time_cost"), items));
                    extensionQueue.poll();
                } else {
                    PSTExtensionNode pre = extensionQueue.peek();
                    Map<String,PSTBranchNodeItem> items = pre.getNextItem().getItems();
                    txs.addAll(valueLeafFilter("time_cost",queries.get("time_cost"), items));
                    extensionQueue.poll();
                }
            }
        } else if (!queries.containsKey("time_cost") && queries.containsKey("reputation")) {
            for (PSTExtensionNode node : extensionQueue) {
                PSTBranchNode pre=node.getNextItem();
                for (String key:pre.getItems().keySet()) {
                    extensionQueue.add(pre.getItems().get(key).getNextExtension());
                }
            }
            while (extensionQueue.size() != 0) {
                PSTExtensionNode pre = extensionQueue.peek();
                Map<String,PSTBranchNodeItem> items = pre.getNextItem().getItems();
                txs.addAll(valueLeafFilter("time_cost", queries.get("time_cost"), items));
                extensionQueue.poll();
            }
        }else {
            while (extensionQueue.size() != 0) {
                PSTExtensionNode pre = extensionQueue.peek();
                if (pre != null) {
                    Map<String,PSTBranchNodeItem> items = pre.getNextItem().getItems();
                    txs.addAll(valueLeafFilter("time_cost",queries.get("time_cost"), items));
                    extensionQueue.addAll(valueExtensionFilter("time_cost",queries.get("time_cost"), items));
                }
                extensionQueue.poll();
            }
        }
        return txs;
    }

    public List<Transaction> mpquery(Map<String, String> queries) throws NoSuchFieldException, IllegalAccessException {
        PSTExtensionNode pre_ex=extensionNode;
        PSTBranchNode pre_bracnch = extensionNode.getNextItem();
        Map<String, PSTBranchNodeItem> pre_items = pre_bracnch.getItems();
        List<Transaction> txs = new ArrayList<>();
        Queue<PSTExtensionNode> extensionQueue = new LinkedList<>();
        Map<String, String> newQueries = new HashMap<>();
        for (Map.Entry<String, String> entry : queries.entrySet()) {
            newQueries.put(entry.getKey(), entry.getValue());
        }
        extensionQueue.add(pre_ex);
        // 从头开始采用递归方法
        if (iterMTquery(extensionQueue, newQueries)!=null && extensionQueue.size()!=0)
        {
            txs.addAll(iterMTquery(extensionQueue, newQueries));
        }
        return txs;
    }

    public List<Transaction> iterMTquery(Queue<PSTExtensionNode> extensionQueue, Map<String, String> queries) throws NoSuchFieldException, IllegalAccessException {
        List<Transaction> transactions=new ArrayList<>();
        PSTExtensionNode pre_ex=extensionQueue.peek();
        PSTBranchNode pre_branch=pre_ex.getNextItem();
        Map<String, PSTBranchNodeItem> pre_items=pre_branch.getItems();
        int round=0;
        boolean flag=true;
        while(extensionQueue.size()!=0)
        {
            //如果包含这个属性
            if (queries.containsKey(pre_ex.getProperty()))
            {
//                System.out.println("pro");
                if (pre_items.keySet().isEmpty())
                {
                    extensionQueue.poll();
                }
                //遍历branchitems
                for (String range:pre_items.keySet())
                {
                    //如果是数值类型，判断在哪个范围内，更新队列和多个pre
                    if (PropertySemanticTrie.judge(pre_ex.getProperty(), pre_items.get(range).getPreTransactions()).equals("numerical"))
                    {
                        if (isInRange(range, queries.get(pre_ex.getProperty())))
                        {
                            pre_ex=pre_items.get(range).getNextExtension();
                            //判断branchitem还有没有下一个extensionnode
                            if(pre_ex==null)
                            {
                                transactions.addAll(pre_items.get(range).getNextLeaf().getTransactions());
                                extensionQueue.poll();
                                pre_ex=extensionQueue.peek();
                                break;
                            }else{
                                extensionQueue.add(pre_ex);
                                extensionQueue.poll();
                                pre_ex=extensionQueue.peek();
                                pre_branch=pre_ex.getNextItem();
                                pre_items=pre_branch.getItems();
                                break;
                            }
                        }
//                        System.out.println("num");
                    }
                    //如果是属性类型，直接判断是否一致就行，更新队列和多个pre
                    else{
                        if (range.equals(queries.get(pre_ex.getProperty())))
                        {
                            pre_ex=pre_items.get(range).getNextExtension();
                            if(pre_ex==null)
                            {
                                transactions.addAll(pre_items.get(range).getNextLeaf().getTransactions());
                                extensionQueue.poll();
                                pre_ex=extensionQueue.peek();
                                break;
                            }else{
                                extensionQueue.add(pre_ex);
                                extensionQueue.poll();
                                pre_ex=extensionQueue.peek();
                                pre_branch=pre_ex.getNextItem();
                                pre_items=pre_branch.getItems();
                                break;
                            }
                        }else{
                            flag=false;
                        }
//                        System.out.println("value");
                    }
                }
                if (flag==false)
                {
                    return null;
                }
            }
            //如果不包含，则将当前的extensionnode的所有branchitem的所有儿子节点加入到队列中
            else
            {
                for (String range:pre_items.keySet())
                {
                    PSTBranchNodeItem item=pre_items.get(range);
                    pre_ex= item.getNextExtension();
                    //判断branchitem还有没有下一个extensionnode
                    if(pre_ex==null)
                    {
                        transactions.addAll(pre_items.get(range).getNextLeaf().getTransactions());
                    }else{
                        extensionQueue.add(pre_ex);
                    }
                }
                extensionQueue.poll();
                if (extensionQueue.size()!=0)
                {
                    pre_ex=extensionQueue.peek();
                    pre_branch=pre_ex.getNextItem();
                    pre_items=pre_branch.getItems();
                }
            }
            round+=1;
//            System.out.println("round:"+round);
        }

        return transactions;
    }

    public boolean isInRange(String range, String target)
    {
        String[] ranges=range.split("-");
        double min=Double.valueOf(ranges[0]);
        double max=Double.valueOf(ranges[1]);
        String[] targets=target.split(",");
        double value = Double.valueOf(targets[0]);
        if ((value <= 0 && min==0) || (value <= max && value >= min) || (max==20000 && value>=20000)) {
            return true;
        }else{
            return false;
        }
    }


    //确认属性在哪个item中
    public PSTExtensionNode typeExtensionFilter(String type, Map<String, PSTBranchNodeItem> branchNodeItems){
        PSTBranchNodeItem nodeItems = branchNodeItems.get(type);
        if (nodeItems != null) {
            return nodeItems.getNextExtension();
        }
        return null;
    }

    //确认属性在哪个item中
    public List<Transaction> typeLeafFilter(String type, Map<String,PSTBranchNodeItem> branchNodeItems){
        List<Transaction> txs = new ArrayList<>();
        PSTBranchNodeItem nodeItems = branchNodeItems.get(type);
        if (nodeItems != null && nodeItems.getNextLeaf() != null) {
            txs = nodeItems.getNextLeaf().getTransactions();
        }
        return txs;
    }

    //返回所有item的再下一层extension_node
    public LinkedList<PSTExtensionNode> valueExtensionFilter(String costOrRepu, String propertyValue, Map<String,PSTBranchNodeItem> branchNodeItems)
    {
        //先比较值在哪些key中
        LinkedList<PSTExtensionNode> nodes = new LinkedList<>();
        ArrayList<String> keys = new ArrayList<>();
        for (String key: branchNodeItems.keySet()) {
            String[] min_max = key.split(",");
            double min = Double.valueOf(min_max[0]);
            double max = Double.valueOf(min_max[1]);
            if (propertyValue == null) {
                keys.add(key);
            } else {
                double value = Double.valueOf(propertyValue);
                if (value >= max || (value <= max && value >= min)) {
                    keys.add(key);
                }
            }
        }
        //根据对应的key值筛选extensionnode
        for (int i = 0; i < keys.size(); i++) {
            if (branchNodeItems.get(keys.get(i)).getNextExtension() != null) {
                nodes.add(branchNodeItems.get(keys.get(i)).getNextExtension());
            }
        }
        return nodes;
    }

    //返回所有leaf的transactions
    public ArrayList<Transaction> valueLeafFilter(String costOrRepu, String propertyValue, Map<String,PSTBranchNodeItem> branchNodeItems)
    {
        //先比较值在哪些key中
        ArrayList<Transaction> txs = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();
        for (String key: branchNodeItems.keySet()) {
            //cost/repu的值用
            String[] min_max = key.split(",");
            double min=Double.valueOf(min_max[0]);
            double max=Double.valueOf(min_max[1]);
            double value=Double.valueOf(propertyValue);
            if(value>=max || (value<=max && value>=min))
            {
                keys.add(key);
            }
        }
        //根据对应的key值筛选extensionnode
        for (int i=0; i<keys.size();i++) {
            if (branchNodeItems.get(keys.get(i)).getNextLeaf() != null) {
                txs.addAll(branchNodeItems.get(keys.get(i)).getNextLeaf().getTransactions());
            }
        }
        return txs;
    }

    public List<Transaction> propertyQueryTopK(String queryType, int topK) {
        PSTBranchNode branchNode = extensionNode.getNextItem();
        Map<String, PSTBranchNodeItem> items = branchNode.getItems();

        List<Transaction> txs = new ArrayList<>();
        Queue<PSTExtensionNode> extensionQueue = new LinkedList<>();
        if (items.get(queryType) != null && items.get(queryType).getNextExtension() != null) {
            extensionQueue.add(items.get(queryType).getNextExtension());
        }
        if (items.get(queryType) != null && items.get(queryType).getNextLeaf() != null) {
            txs.addAll(items.get(queryType).getNextLeaf().getTransactions());
        }
        PriorityQueue<Transaction> priorityQueue;
        if (queryType.equals("time_cost")) {
            priorityQueue = new PriorityQueue<>(Transaction.compareByTimeCost);
        } else {
            priorityQueue = new PriorityQueue<>(Transaction.compareByReputation);
        }
        // 第二层用递归方法
        iterateTopKRangeQueryPST(extensionQueue, queryType, priorityQueue);
        for (int i = 0; i < topK; i++) {
            txs.add(priorityQueue.poll());
        }
        return txs;
    }

    public void iterateTopKRangeQueryPST(Queue<PSTExtensionNode> extensionQueue, String queryType,
                                                      PriorityQueue<Transaction> priorityQueue){
        if (queryType.equals("time_cost")) {
            while (extensionQueue.size() != 0) {
                if (extensionQueue.peek().getProperty().equals("time_cost")) {
                    PSTExtensionNode pre = extensionQueue.peek();
                    Map<String, PSTBranchNodeItem> items = pre.getNextItem().getItems();
                    for (PSTBranchNodeItem item : items.values()) {
                        ArrayList<Transaction> transactions = item.getNextLeaf().getTransactions();
                        for (Transaction transaction : transactions) {
                            priorityQueue.add(transaction);
                        }
                    }
                    extensionQueue.addAll(valueExtensionFilter("time_cost", null, items));
                    extensionQueue.poll();
                }
            }
        }
        if (queryType.equals("reputation")) {
            while (extensionQueue.size() != 0) {
                if (extensionQueue.peek().getProperty().equals("reputation")) {
                    PSTExtensionNode pre = extensionQueue.peek();
                    Map<String, PSTBranchNodeItem> items = pre.getNextItem().getItems();
                    for (PSTBranchNodeItem item : items.values()) {
                        ArrayList<Transaction> transactions = item.getNextLeaf().getTransactions();
                        for (Transaction transaction : transactions) {
                            priorityQueue.add(transaction);
                        }
                    }
                    extensionQueue.addAll(valueExtensionFilter("reputation", null, items));
                    extensionQueue.poll();
                }
            }
        }
    }
}
