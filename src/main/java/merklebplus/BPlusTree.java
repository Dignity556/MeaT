package merklebplus;

import blockchain.Transaction;
import lombok.Getter;
import lombok.Setter;

import java.security.NoSuchAlgorithmException;
import java.util.*;

@Getter
@Setter
public class BPlusTree<T, V extends Comparable<V>> {
    //B+树的阶
    private Integer bTreeOrder;
    //B+树的非叶子节点最小拥有的子节点数量（同时也是键的最小数量）
    //B+树的非叶子节点最大拥有的节点数量（同时也是键的最大数量）
    private Integer maxNumber;

    private Node<T, V> root;

    private LeafNode<T, V> left;

    //无参构造方法，默认阶为3
    public BPlusTree(){
        this(3);
    }

    //有参构造方法，可以设定B+树的阶
    public BPlusTree(Integer bTreeOrder){
        this.bTreeOrder = bTreeOrder;
        //this.minNUmber = (int) Math.ceil(1.0 * bTreeOrder / 2.0);
        //因为插入节点过程中可能出现超过上限的情况,所以这里要加1
        this.maxNumber = bTreeOrder + 1;
        this.root = new LeafNode<T, V>();
        this.left = null;
    }

    //查询
    public T find(V key){
        T t = this.root.find(key);
        if (t == null){
//            System.out.println("不存在");
        }
        return t;
    }

    //插入
    public void insert(T value, V key){
        if(key == null)
            return;
        Node<T, V> t = this.root.insert(value, key);
        if(t != null)
            this.root = t;
        this.left = (LeafNode<T, V>)this.root.refreshLeft();

//        System.out.println("插入完成,当前根节点为:");
//        for(int j = 0; j < this.root.number; j++) {
//            System.out.print((V) this.root.keys[j] + " ");
//        }
//        System.out.println();
    }

    public void createMerkle() {
        LeafNode<T, V> cur = this.left;
        while (cur != null) {

            Transaction[] txs = new Transaction[cur.values.length];
            for (int i = 0; i < cur.values.length; i++) {
                txs[i] = (Transaction)cur.values[i];
            }
            List<Leaf> leaves = new ArrayList<>();
            for (Transaction tx : txs) {
                if (tx == null) {
//                    System.out.println("交易为空");
                } else {
                    leaves.add(new Leaf(tx));
                }
            }
            try {
                Leaf root = createMerkleIterator(leaves);
                cur.merkleRoot = root;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            cur = cur.getLeft();
        }
    }

    private Leaf createMerkleIterator(List<Leaf> leaves) throws NoSuchAlgorithmException {
        List<Leaf> newLeaves = new ArrayList<>();
        int count = 0;//记录树中节点的总个数

        if(leaves.size() == 1) {
            leaves.get(0).setFather(null);
            leaves.get(0).setId("root" + leaves.get(0).getBlock().getId());
            return leaves.get(0);
        }else{
            for(int i = 0;i < leaves.size() - 1;i += 2) {
                Leaf father = new Leaf();
                father.setLeft(leaves.get(i));
                father.setRight(leaves.get(i + 1));
                father.setHashId(Leaf.calculateSHA256(leaves.get(i).getHashId().toString()+leaves.get(i+1).getHashId().toString()));
                father.setBlock(leaves.get(i).getBlock());
                father.setId("father" + "_"+leaves.get(0).getBlock().getId());
                leaves.get(i).setFather(father);
                leaves.get(i+1).setFather(father);
                newLeaves.add(father);
                count += 1;
//                System.out.println("Now is creating the MGT, this layer has "+count+" nodes");
            }
            if (leaves.size() % 2 == 1) {
                Leaf newLeaf = leaves.get(leaves.size() - 1);
                newLeaf.setId("leaf");
                newLeaves.add(newLeaf);
                count += 1;
//                System.out.println("Ok, we lost one, "+count+" nodes in total");
            }
            return createMerkleIterator(newLeaves);
        }
    }

    public Transaction singleTransactionQuery(String txId) {
        LeafNode cur = left;
        while (cur != null) {
            Transaction[] transactions = new Transaction[cur.values.length];
            for (int i = 0; i < cur.values.length; i++) {
                transactions[i] = (Transaction)cur.values[i];
                if (transactions[i] != null && transactions[i].getId().equals(txId)) {
                    return transactions[i];
                }
            }
            cur = cur.getLeft();
        }
        return null;
    }

    public List<Transaction> singleNodeQuery(String nodeId) {
        List<Transaction> txs = new ArrayList<>();
        LeafNode cur = left;
        // TODO 暂时使用直接遍历方式，后续采用merkle遍历
        while (cur != null) {
            Transaction[] transactions = new Transaction[cur.values.length];
            for (int i = 0; i < cur.values.length; i++) {
                transactions[i] = (Transaction)cur.values[i];
            }
            for (Transaction transaction : transactions) {
                if (transaction != null && transaction.getStartNode().getNodeId().equals(nodeId)) {
                    txs.add(transaction);
                }
            }
            cur = cur.getRight();
        }
        return txs;
    }

    public List<Transaction> propertyQuery(Map<String, String> queries) {
        List<Transaction> res = new ArrayList<>();
        String type = null;
        String[] timeCost = new String[2];
        String[] reputation = new String[2];
        if (queries.get("type") != null) {
            type = queries.get("type");
        }
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

        LeafNode cur = getLeftLeaf();
        while (cur != null) {
            Transaction[] transactions = new Transaction[cur.values.length];
            for (int i = 0; i < cur.values.length; i++) {
                transactions[i] = (Transaction)cur.values[i];
            }
            for (Transaction tx : transactions) {
                if (tx != null) {
                    int timeValue = Double.valueOf(tx.getTimeCost()).intValue();
                    int repuValue = Double.valueOf(tx.getReputation()).intValue();
                    if ((tx.getType().equals(type) ||
                            ((timeValue >= timeCostMin) && (timeValue <= timeCostMax)) ||
                            ((repuValue >= reputationMin) && (repuValue <= reputationMax)))) {
                        res.add(tx);
                    }
                }
            }
            cur = cur.getLeft();
        }
        return res;
    }

    private LeafNode getLeftLeaf() {
        Queue<Node> queue = new LinkedList<>();
        List<List<Node>> res = new ArrayList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            List<Node> itemList = new ArrayList<>();
            int len = queue.size();
            while (len > 0) {
                Node tmpNode = queue.poll();
                itemList.add(tmpNode);
                for (Node child : tmpNode.childs) {
                    if (child != null) {
                        queue.offer(child);
                    }
                }
                len--;
            }
            res.add(itemList);
        }
        return (LeafNode) res.get(res.size() - 1).get(0);
    }

    public List<Transaction> propertyQueryTopK(String type, int topK) {
        PriorityQueue<Transaction> priorityQueue;
        List<Transaction> res = new ArrayList<>();
        if (type.equals("time_cost")) {
            priorityQueue = new PriorityQueue<>(Transaction.compareByTimeCost);
        } else {
            priorityQueue = new PriorityQueue<>(Transaction.compareByReputation);
        }
        LeafNode cur = getLeftLeaf();
        while (cur != null) {
            Transaction[] transactions = new Transaction[cur.values.length];
            for (int i = 0; i < cur.values.length; i++) {
                transactions[i] = (Transaction)cur.values[i];
            }
            for (Transaction tx : transactions) {
                if (tx != null) {
                    priorityQueue.add(tx);
                }
            }
            cur = cur.getLeft();
        }
        for (int i = 0; i < topK; i++) {
            res.add(priorityQueue.poll());
        }
        return res;
    }

    abstract class Node<T, V extends Comparable<V>> {
        protected Node<T, V> parent;
        protected Node<T, V>[] childs;
        // 子节点数量
        protected Integer number;
        // 键
        protected Object[] keys;

        public Node() {
            this.parent = null;
            this.number = 0;
            this.childs = new Node[maxNumber];
            this.keys = new Object[maxNumber];
        }

        //查找
        abstract T find(V key);

        //插入
        abstract Node<T, V> insert(T value, V key);

        abstract LeafNode<T, V> refreshLeft();

    }

    class BPlusNode<T, V extends Comparable<V>> extends Node<T, V> {
        public BPlusNode() {
            super();
        }

        @Override
        T find(V key) {
            int i = 0;
            while (i < this.number){
                // 在当前节点寻找
                if (key.compareTo((V)this.keys[i]) <= 0) {
                    break;
                } else {
                    i++;
                }
            }
            // 不存在
            if (this.number == i) {
                return null;
            }
            // 递归查找
            return this.childs[i].find(key);
        }

        @Override
        Node<T, V> insert(T value, V key) {
            int i = 0;
            while (i < this.number){
                if (key.compareTo((V) this.keys[i]) < 0) {
                    break;
                } else {
                    i++;
                }
            }
            if (key.compareTo((V) this.keys[this.number - 1]) >= 0) {
                i--;
            }

            return this.childs[i].insert(value, key);
        }

        @Override
        LeafNode<T, V> refreshLeft() {
            return this.childs[0].refreshLeft();
        }

        /**
         * 当叶子节点插入成功完成分解时,递归地向父节点插入新的节点以保持平衡
         * @param node1
         * @param node2
         * @param key
         */
        Node<T, V> insertNode(Node<T, V> node1, Node<T, V> node2, V key){

            V oldKey = null;
            if (this.number > 0)
                oldKey = (V) this.keys[this.number - 1];
            //如果原有key为null,说明这个非节点是空的,直接放入两个节点即可
            if (key == null || this.number <= 0){
//                System.out.println("非叶子节点,插入key: " + node1.keys[node1.number - 1] + " " + node2.keys[node2.number - 1] + "直接插入");
                this.keys[0] = node1.keys[node1.number - 1];
                this.keys[1] = node2.keys[node2.number - 1];
                this.childs[0] = node1;
                this.childs[1] = node2;
                this.number += 2;
                return this;
            }
            //原有节点不为空,则应该先寻找原有节点的位置,然后将新的节点插入到原有节点中
            int i = 0;
            while (key.compareTo((V)this.keys[i]) != 0){
                i++;
            }
            //左边节点的最大值可以直接插入,右边的要挪一挪再进行插入
            this.keys[i] = node1.keys[node1.number - 1];
            this.childs[i] = node1;

            Object tempKeys[] = new Object[maxNumber];
            Object tempChilds[] = new Node[maxNumber];

            System.arraycopy(this.keys, 0, tempKeys, 0, i + 1);
            System.arraycopy(this.childs, 0, tempChilds, 0, i + 1);
            System.arraycopy(this.keys, i + 1, tempKeys, i + 2, this.number - i - 1);
            System.arraycopy(this.childs, i + 1, tempChilds, i + 2, this.number - i - 1);
            tempKeys[i + 1] = node2.keys[node2.number - 1];
            tempChilds[i + 1] = node2;

            this.number++;

            //判断是否需要拆分
            //如果不需要拆分,把数组复制回去,直接返回
            if (this.number <= bTreeOrder){
                System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
                System.arraycopy(tempChilds, 0, this.childs, 0, this.number);

//                System.out.println("非叶子节点,插入key: " + node1.keys[node1.number - 1] + " " + node2.keys[node2.number - 1] + ", 不需要拆分");

                return null;
            }

//            System.out.println("非叶子节点,插入key: " + node1.keys[node1.number - 1] + " " + node2.keys[node2.number - 1] + ",需要拆分");

            //如果需要拆分,和拆叶子节点时类似,从中间拆开
            Integer middle = this.number / 2;

            //新建非叶子节点,作为拆分的右半部分
            BPlusNode<T, V> tempNode = new BPlusNode<T, V>();
            //非叶节点拆分后应该将其子节点的父节点指针更新为正确的指针
            tempNode.number = this.number - middle;
            tempNode.parent = this.parent;
            //如果父节点为空,则新建一个非叶子节点作为父节点,并且让拆分成功的两个非叶子节点的指针指向父节点
            if (this.parent == null) {

//                System.out.println("非叶子节点,插入key: " + node1.keys[node1.number - 1] + " " + node2.keys[node2.number - 1] + ",新建父节点");

                BPlusNode<T, V> tempBPlusNode = new BPlusNode<>();
                tempNode.parent = tempBPlusNode;
                this.parent = tempBPlusNode;
                oldKey = null;
            }
            System.arraycopy(tempKeys, middle, tempNode.keys, 0, tempNode.number);
            System.arraycopy(tempChilds, middle, tempNode.childs, 0, tempNode.number);
            for(int j = 0; j < tempNode.number; j++){
                tempNode.childs[j].parent = tempNode;
            }
            //让原有非叶子节点作为左边节点
            this.number = middle;
            this.keys = new Object[maxNumber];
            this.childs = new Node[maxNumber];
            System.arraycopy(tempKeys, 0, this.keys, 0, middle);
            System.arraycopy(tempChilds, 0, this.childs, 0, middle);

            //叶子节点拆分成功后,需要把新生成的节点插入父节点
            BPlusNode<T, V> parentNode = (BPlusNode<T, V>)this.parent;
            return parentNode.insertNode(this, tempNode, oldKey);
        }
    }

    /**
     * 叶节点类
     * @param <T>
     * @param <V>
     */
    @Getter
    @Setter
    class LeafNode <T, V extends Comparable<V>> extends Node<T, V> {

        protected Object values[];
        protected LeafNode left;
        protected LeafNode right;
        protected Leaf merkleRoot;

        public LeafNode(){
            super();
            this.values = new Object[maxNumber];
            this.left = null;
            this.right = null;
        }

        public

        /**
         * 进行查找,经典二分查找,不多加注释
         * TODO 应该使用merkle进行递归查找
         * @param key
         * @return
         */
        @Override
        T find(V key) {
            if(this.number <= 0)
                return null;

            Integer left = 0;
            Integer right = this.number;

            Integer middle = (left + right) / 2;

            while(left < right){
                V middleKey = (V) this.keys[middle];
                if(key.compareTo(middleKey) == 0)
                    return (T) this.values[middle];
                else if(key.compareTo(middleKey) < 0)
                    right = middle;
                else
                    left = middle;
                middle = (left + right) / 2;
            }
            return null;
        }

        /**
         *
         * @param value
         * @param key
         */
        @Override
        Node<T, V> insert(T value, V key) {

//            System.out.println("叶子节点,插入key: " + key);

            //保存原始存在父节点的key值
            V oldKey = null;
            if(this.number > 0)
                oldKey = (V) this.keys[this.number - 1];
            //先插入数据
            int i = 0;
            while(i < this.number){
                if(key.compareTo((V) this.keys[i]) < 0)
                    break;
                i++;
            }

            //复制数组,完成添加
            Object tempKeys[] = new Object[maxNumber];
            Object tempValues[] = new Object[maxNumber];
            System.arraycopy(this.keys, 0, tempKeys, 0, i);
            System.arraycopy(this.values, 0, tempValues, 0, i);
            System.arraycopy(this.keys, i, tempKeys, i + 1, this.number - i);
            System.arraycopy(this.values, i, tempValues, i + 1, this.number - i);
            tempKeys[i] = key;
            tempValues[i] = value;

            this.number++;

//            System.out.println("插入完成,当前节点key为:");
//            for(int j = 0; j < this.number; j++)
//                System.out.print(tempKeys[j] + " ");
//            System.out.println();

            //判断是否需要拆分
            //如果不需要拆分完成复制后直接返回
            if(this.number <= bTreeOrder){
                System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
                System.arraycopy(tempValues, 0, this.values, 0, this.number);

                //有可能虽然没有节点分裂，但是实际上插入的值大于了原来的最大值，所以所有父节点的边界值都要进行更新
                Node node = this;
                while (node.parent != null){
                    V tempkey = (V)node.keys[node.number - 1];
                    if(tempkey.compareTo((V)node.parent.keys[node.parent.number - 1]) > 0){
                        node.parent.keys[node.parent.number - 1] = tempkey;
                        node = node.parent;
                    }
                    else {
                        break;
                    }
                }
//                System.out.println("叶子节点,插入key: " + key + ",不需要拆分");

                return null;
            }

//            System.out.println("叶子节点,插入key: " + key + ",需要拆分");

            //如果需要拆分,则从中间把节点拆分差不多的两部分
            Integer middle = this.number / 2;

            //新建叶子节点,作为拆分的右半部分
            LeafNode<T, V> tempNode = new LeafNode<T, V>();
            tempNode.number = this.number - middle;
            tempNode.parent = this.parent;
            //如果父节点为空,则新建一个非叶子节点作为父节点,并且让拆分成功的两个叶子节点的指针指向父节点
            if(this.parent == null) {

//                System.out.println("叶子节点,插入key: " + key + ",父节点为空 新建父节点");

                BPlusNode<T, V> tempBPlusNode = new BPlusNode<>();
                tempNode.parent = tempBPlusNode;
                this.parent = tempBPlusNode;
                oldKey = null;
            }
            System.arraycopy(tempKeys, middle, tempNode.keys, 0, tempNode.number);
            System.arraycopy(tempValues, middle, tempNode.values, 0, tempNode.number);

            //让原有叶子节点作为拆分的左半部分
            this.number = middle;
            this.keys = new Object[maxNumber];
            this.values = new Object[maxNumber];
            System.arraycopy(tempKeys, 0, this.keys, 0, middle);
            System.arraycopy(tempValues, 0, this.values, 0, middle);

            this.right = tempNode;
            tempNode.left = this;

            //叶子节点拆分成功后,需要把新生成的节点插入父节点
            BPlusNode<T, V> parentNode = (BPlusNode<T, V>)this.parent;
            return parentNode.insertNode(this, tempNode, oldKey);
        }

        @Override
        LeafNode<T, V> refreshLeft() {
            if(this.number <= 0)
                return null;
            return this;
        }
    }

}

