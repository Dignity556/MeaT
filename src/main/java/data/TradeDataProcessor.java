package data;

import blockchain.Block;
import blockchain.Transaction;
import graph.Node;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TradeDataProcessor implements DataProcessor{
    @Override
    public Context getDataContext(String path, int blockTxNum) {
        BufferedReader reader = null;
        Map<String, Node> nodes = new HashMap<>(); // 全部节点
        Map<String, Block> blocks = new HashMap<>(); // 全部区块
        List<Transaction> transactions = new ArrayList<>(); // 全部交易

        try {
            reader = Files.newBufferedReader(Paths.get(path));
            // 流式读取
            Iterator<CSVRecord> iterator = CSVFormat.DEFAULT.parse(reader).iterator();
            iterator.next(); // 略过表头
            int count = 0; // 计数，同时也是交易id
            int blockId = 1; // 区块id

            while (iterator.hasNext()) {
                // TODO 自定义每个区块存储交易的数量
                if (count % blockTxNum == 0 && count != 0) {
                    blockId++;
                }
                count++;
                CSVRecord record = iterator.next();
                Transaction transaction = new Transaction();
                // 生成区块
                if (!blocks.containsKey(String.valueOf(blockId))) {
                    Block block = new Block(String.valueOf(blockId));
                    blocks.put(String.valueOf(blockId), block);
//                    System.out.println("Current block:" + blockId);
                }
                transaction.setBeLongBlock(blocks.get(String.valueOf(blockId)));
                transaction.setId(String.valueOf(count));
                transaction.setTimestamp(String.valueOf(blockId)); // 设置时间戳平替
                transaction.setTimeCost(String.valueOf(record.get(3)));
                transaction.setType(String.valueOf(record.get(6))); // 使用某一列数据代表交易类型
                transaction.setReputation(record.get(5));

                // 记录了起始node和到达node
                if (!nodes.containsKey(record.get(0))) {
                    Node node = new Node(record.get(0));
                    nodes.put(record.get(0), node);
                }
                if (!nodes.containsKey(record.get(1))) {
                    Node node = new Node(record.get(1));
                    nodes.put(record.get(1), node);
                }
                Node startNode = nodes.get(record.get(0));
                Node endNode = nodes.get(record.get(1));
                startNode.getNeighbors().add(endNode);
                endNode.getNeighbors().add(startNode);
                transaction.setStartNode(startNode);
                transaction.setEndNode(endNode); // 结束node
                blocks.get(String.valueOf(blockId)).getTransactions().add(transaction);
                transactions.add(transaction);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Block> newBLocks = new ArrayList<>();
        for (Map.Entry<String, Block> entry : blocks.entrySet()) {
            newBLocks.add(entry.getValue());
        }
        List<Node> newNodes = new ArrayList<>();
        for (Map.Entry<String, Node> entry : nodes.entrySet()) {
            newNodes.add(entry.getValue());
        }
        return new Context(newBLocks,
                newNodes,
                transactions);
    }
}
