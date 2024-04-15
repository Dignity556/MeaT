package query;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import graph.Node;

import java.util.*;

public class QueryCase {
    private Context context;
    private Random random = new Random();

    private double timeMax;
    private double timeMin;
    private double repuMax;
    private double repuMin;

    public QueryCase(Context context) {
        this.context = context;
        getRange();
    }
    public Map<String, String> getSingleTransactionQueryCase(int caseNum) {
        Map<String, String> txBlockMap = new HashMap<>();
        List<Transaction> transactions = context.getTransactions();
        List<Node> nodes = context.getNodes();
        for (int i = 0; i < caseNum; i++) {
            Node node = nodes.get(random.nextInt(nodes.size()));
            Transaction transaction = transactions.get(random.nextInt(transactions.size()));
//            Node startNode = transaction.getStartNode();
            txBlockMap.put(transaction.getId(), node.getNodeId());
        }
        return txBlockMap;
    }

    public Map<String, String> getNodeQueryBySingBlockCase(int caseNum) {
        Map<String, String> nodeBlockMap = new HashMap<>();
        List<Block> blocks = context.getBlocks();
        List<Node> nodes = context.getNodes();
        for (int i = 0; i < caseNum; i++) {
            Node node = nodes.get(random.nextInt(nodes.size()));
            Block block = blocks.get(random.nextInt(blocks.size()));
            nodeBlockMap.put(node.getNodeId(), block.getId());
        }
        return nodeBlockMap;
    }

    public List<String> getNodeQueryCase(int caseNum) {
        List<Node> nodes = context.getNodes();
        List<String> nodeIds = new ArrayList<>();
        for (int i = 0; i < caseNum; i++) {
            Node node = nodes.get(random.nextInt(nodes.size()));
            nodeIds.add(node.getNodeId());
        }
        return nodeIds;
    }

    public Map<String, Map<String, String>> getPropertyQuerySingleBlockCase(int caseNum) {
        List<Block> blocks = context.getBlocks();
        Map<String, Map<String, String>> blockProMap = new HashMap<>();
        for (int i = 0; i < caseNum; i++) {
            Block block = blocks.get(random.nextInt(blocks.size()));
            Map<String, String> map = new HashMap<>();
            map.put("type", "1");
            String rangeTimeCost = getRangeTimeCost();
            String rangeReputation = getRangeReputation();
            map.put("time_cost", rangeTimeCost + "," + rangeTimeCost);
            map.put("reputation", rangeReputation + "," + rangeReputation);
            blockProMap.put(block.getId(), map);
        }
        return blockProMap;
    }

    public List<Map<String, String>> getPropertyQueryCase(int caseNum) {
        List<Map<String, String>> queries = new ArrayList<>();
        for (int i = 0; i < caseNum; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("type", "1");
            String rangeTimeCost = getRangeTimeCost();
            String rangeReputation = getRangeReputation();
            map.put("time_cost", rangeTimeCost + "," + rangeTimeCost);
            map.put("reputation", rangeReputation + "," + rangeReputation);
            queries.add(map);
        }
        return queries;
    }

    public Map<String, Map<String, String>> getPropertyRangeQuerySingleBlockCase(int caseNum) {
        List<Block> blocks = context.getBlocks();
        Map<String, Map<String, String>> blockProMap = new HashMap<>();
        for (int i = 0; i < caseNum; i++) {
            Block block = blocks.get(random.nextInt(blocks.size()));
            Map<String, String> map = new HashMap<>();
            map.put("type", "1");
            String rangeTimeCost1 = getRangeTimeCost();
            String rangeTimeCost2 = getRangeTimeCost();
            if (Double.valueOf(rangeTimeCost1) > Double.valueOf(rangeTimeCost2)) {
                String temp = rangeTimeCost1;
                rangeTimeCost1 = rangeTimeCost2;
                rangeTimeCost2 = temp;
            }
            String rangeReputation1 = getRangeReputation();
            String rangeReputation2 = getRangeReputation();
            if (Double.valueOf(rangeReputation1) > Double.valueOf(rangeReputation2)) {
                String temp = rangeReputation1;
                rangeReputation1 = rangeReputation2;
                rangeReputation2 = temp;
            }
            map.put("time_cost", rangeTimeCost1 + "," + rangeTimeCost2);
            map.put("reputation", rangeReputation1 + "," + rangeReputation2);
            blockProMap.put(block.getId(), map);
        }
        return blockProMap;
    }

    public List<Map<String, String>> getPropertyRangeQueryCase(int caseNum) {
        List<Map<String, String>> queries = new ArrayList<>();
        for (int i = 0; i < caseNum; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("type", "1");
            String rangeTimeCost1 = getRangeTimeCost();
            String rangeTimeCost2 = getRangeTimeCost();
            if (Double.valueOf(rangeTimeCost1) > Double.valueOf(rangeTimeCost2)) {
                String temp = rangeTimeCost1;
                rangeTimeCost1 = rangeTimeCost2;
                rangeTimeCost2 = temp;
            }
            String rangeReputation1 = getRangeReputation();
            String rangeReputation2 = getRangeReputation();
            if (Double.valueOf(rangeReputation1) > Double.valueOf(rangeReputation2)) {
                String temp = rangeReputation1;
                rangeReputation1 = rangeReputation2;
                rangeReputation2 = temp;
            }
            map.put("time_cost", rangeTimeCost1 + "," + rangeTimeCost2);
            map.put("reputation", rangeReputation1 + "," + rangeReputation2);
            queries.add(map);
        }
        return queries;
    }

    public Map<String, Map<String, String>> getTopKSingleBlockCase(int caseNum) {
        List<Block> blocks = context.getBlocks();
        Map<String, Map<String, String>> blockProMap = new HashMap<>();
        for (int i = 0; i < caseNum; i++) {
            Block block = blocks.get(random.nextInt(blocks.size()));
            Map<String, String> map = new HashMap<>();
            if (i % 2 == 0) {
                map.put("reputation", null);
            } else {
                map.put("time_cost", null);
            }
            blockProMap.put(block.getId(), map);
        }
        return blockProMap;
    }

    public List<Map<String, String>> getTopKQueryCase(int caseNum) {
        List<Map<String, String>> queries = new ArrayList<>();
        for (int i = 0; i < caseNum; i++) {
            Map<String, String> map = new HashMap<>();
            if (i % 2 == 0) {
                map.put("reputation", null);
            } else {
                map.put("time_cost", null);
            }
            queries.add(map);
        }
        return queries;
    }

    private void getRange() {
        List<Transaction> transactions = context.getTransactions();
        double timeMax = 0, timeMin = 0;
        double repuMax = 0, repuMin = 0;
        for (Transaction transaction : transactions) {
            Double repu = Double.valueOf(transaction.getReputation());
            Double time = Double.valueOf(transaction.getTimeCost());
            if (time > timeMax) timeMax = time;
            if (time < timeMin) timeMin = time;
            if (repu > repuMax) repuMax = repu;
            if (repu < repuMin) repuMin = repu;
        }
        this.repuMax = repuMax;
        this.repuMin = repuMin;
        this.timeMax = timeMax;
        this.timeMin = timeMin;
    }

    private String getRangeTimeCost() {
        int time = random.nextInt((int) timeMax - (int) timeMin + 1) + (int) timeMin;
        return String.valueOf(time);
    }

    private String getRangeReputation() {
        int repu = random.nextInt((int) repuMax - (int) repuMin + 1) + (int) repuMin;
        return String.valueOf(repu);
    }

}
