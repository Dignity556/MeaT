package data;

import blockchain.Block;
import blockchain.Transaction;
import com.sun.xml.internal.ws.api.server.SDDocument;
import graph.Node;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class NBADataProcessor implements DataProcessor{
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
                transaction.setId(String.valueOf(count));
                transaction.setBeLongBlock(blocks.get(String.valueOf(blockId)));
                transaction.setGame_id(String.valueOf(record.get(2)));
                transaction.setDate(String.valueOf(record.get(3)));
                transaction.setHome(String.valueOf(record.get(4)));
                transaction.setWin_or_lose(String.valueOf(record.get(6)));
                transaction.setT_point(String.valueOf(record.get(7)));
                transaction.setO_point(String.valueOf(record.get(8)));
                transaction.setT_fieldgoal(String.valueOf(record.get(9)));
                transaction.setT_x3point(String.valueOf(record.get(10)));
                transaction.setT_freegoal(String.valueOf(record.get(11)));
                transaction.setT_offrebound(String.valueOf(record.get(12)));
                transaction.setT_totalrebound(String.valueOf(record.get(13)));
                transaction.setT_assist(String.valueOf(record.get(14)));
                transaction.setT_steal(String.valueOf(record.get(15)));
                transaction.setT_block(String.valueOf(record.get(16)));
                transaction.setT_turnover(String.valueOf(record.get(17)));
                transaction.setT_fouls(String.valueOf(record.get(18)));
                transaction.setO_fieldgoal(String.valueOf(record.get(19)));
                transaction.setO_x3point(String.valueOf(record.get(20)));
                transaction.setO_freegoal(String.valueOf(record.get(21)));
                transaction.setO_offrebound(String.valueOf(record.get(22)));
                transaction.setO_totalrebound(String.valueOf(record.get(23)));
                transaction.setO_assist(String.valueOf(record.get(24)));
                transaction.setO_steal(String.valueOf(record.get(25)));
                transaction.setO_block(String.valueOf(record.get(26)));
                transaction.setO_turnover(String.valueOf(record.get(27)));
                transaction.setO_fouls(String.valueOf(record.get(28)));

                // 记录了起始node和到达node
                if (!nodes.containsKey(record.get(1))) {
                    Node node = new Node(record.get(1));
                    nodes.put(record.get(1), node);
                }
                if (!nodes.containsKey(record.get(5))) {
                    Node node = new Node(record.get(5));
                    nodes.put(record.get(5), node);
                }
                Node startNode = nodes.get(record.get(1));
                Node endNode = nodes.get(record.get(5));
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
        System.out.println("Data is ready");
        return new Context(newBLocks,
                newNodes,
                transactions);
    }
}
