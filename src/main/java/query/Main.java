package query;

import blockchain.Block;
import data.Context;
import data.DataProcessor;
import data.KaggleDataProcessor;
import data.TradeDataProcessor;
import meat.Meat;
import merkle.Merkle;
import merklebplus.MerkleBPlus;
import mst.MST;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // 加载数据
        String dataPath = "./data/2500.csv";
        DataProcessor dataProcessor = new KaggleDataProcessor();
        // 记录数据
        Record record = new Record();

        // 变量，每个区块的
        int blockTxNum = 25;
        for (; blockTxNum <= 200; blockTxNum *= 2) {
            Context context = dataProcessor.getDataContext(dataPath, blockTxNum);
            record.createNewSheet(blockTxNum);
            // 生成索引
            Meat meat = new Meat();
            long t1 = System.currentTimeMillis();
            meat.createMeat(context);
            long t2 = System.currentTimeMillis();
            System.out.println("meat构建时间：" + (t2 - t1));

            long t3 = System.currentTimeMillis();
            Merkle merkle = new Merkle();
            merkle.createMerkle(context);
            long t4 = System.currentTimeMillis();
            System.out.println("merkle-tree构建时间：" + (t4 - t3));

            long t5 = System.currentTimeMillis();
            MerkleBPlus merkleBPlus = new MerkleBPlus();
            merkleBPlus.createMerkleBPlus(context);
            long t6 = System.currentTimeMillis();
            System.out.println("merkleB+tree构建时间：" + (t6 - t5));

            long t7 = System.currentTimeMillis();
            MST mst = new MST();
            mst.createMST(context);
            long t8 = System.currentTimeMillis();
            System.out.println("mst构建时间：" + (t8 - t7));

            System.out.println("---------------------------------------------------------");

            // 内存占用


            // 查询测试
            QueryCase queryCase = new QueryCase(context);
            // TODO 生成测试用例的数量
            int caseNum = 100;

            /**
             * 单节点，交易查询
             */
            Map<String, String> case1 = queryCase.getSingleTransactionQueryCase(caseNum);
            long time1 = System.nanoTime();
            for (Map.Entry<String, String> entry : case1.entrySet()) {
                String txId = entry.getKey();
                String nodeId = entry.getValue();
                meat.singleTransactionQuery(txId, nodeId);
            }
            long time2 = System.nanoTime();
            System.out.println("meat单节点、单交易查询总时延：" + (time2 - time1));
            System.out.println("meat单节点、单交易查询平均时延：" + (time2 - time1)/ caseNum);
            record.record(Constant.MEAT, Constant.SINGLETX, time2 - time1, (time2 - time1)/ caseNum);

            long time3 = System.nanoTime();
            for (Map.Entry<String, String> entry : case1.entrySet()) {
                String txId = entry.getKey();
                String nodeId = entry.getValue();
                merkle.singleTransactionQuery(txId, nodeId);
            }
            long time4 = System.nanoTime();
            System.out.println("merkle-tree单节点、单交易查询总时延：" + (time4 - time3));
            System.out.println("merkle-tree单节点、单交易查询平均时延：" + (time4 - time3)/ caseNum);
            record.record(Constant.MERKLE, Constant.SINGLETX, time4 - time3, (time4 - time3)/ caseNum);

            long time5 = System.nanoTime();
            for (Map.Entry<String, String> entry : case1.entrySet()) {
                String txId = entry.getKey();
                String nodeId = entry.getValue();
                merkleBPlus.singleTransactionQuery(txId, nodeId);
            }
            long time6 = System.nanoTime();
            System.out.println("merkleB+tree单节点、单交易查询总时延：" + (time6 - time5));
            System.out.println("merkleB+tree单节点、单交易查询平均时延：" + (time6 - time5)/ caseNum);
            record.record(Constant.MERKLEBPLUS, Constant.SINGLETX, time6 - time5, (time6 - time5)/ caseNum);

            long time7 = System.nanoTime();
            for (Map.Entry<String, String> entry : case1.entrySet()) {
                String txId = entry.getKey();
                String nodeId = entry.getValue();
                mst.singleTransactionQuery(txId, nodeId);
            }
            long time8 = System.nanoTime();
            System.out.println("mst单节点、单交易查询总时延：" + (time8 - time7));
            System.out.println("mst单节点、单交易查询平均时延：" + (time8 - time7)/ caseNum);
            record.record(Constant.MST, Constant.SINGLETX, time8 - time7, (time8 - time7)/ caseNum);

            System.out.println("---------------------------------------------------------");

            /**
             * 单节点，单区块查询
             */
            Map<String, String> case2 = queryCase.getNodeQueryBySingBlockCase(caseNum);
            long time9 = System.nanoTime();
            for (Map.Entry<String, String> entry : case2.entrySet()) {
                String nodeId = entry.getKey();
                String blockId = entry.getValue();
                meat.nodeQueryBySingleBlock(nodeId, blockId);
            }
            long time10 = System.nanoTime();
            System.out.println("meat单节点、单区块查询总时延：" + (time10 - time9));
            System.out.println("meat单节点、单区块查询平均时延：" + (time10 - time9)/ caseNum);
            record.record(Constant.MEAT, Constant.NODESINGLEBLOCK, time10 - time9, (time10 - time9)/ caseNum);

            long time11 = System.nanoTime();
            for (Map.Entry<String, String> entry : case2.entrySet()) {
                String nodeId = entry.getKey();
                String blockId = entry.getValue();
                merkle.nodeQueryBySingleBlock(nodeId, blockId);
            }
            long time12 = System.nanoTime();
            System.out.println("merkle-tree单节点、单区块查询总时延：" + (time12 - time11));
            System.out.println("merkle-tree单节点、单区块查询平均时延：" + (time12 - time11)/ caseNum);
            record.record(Constant.MERKLE, Constant.NODESINGLEBLOCK, time12 - time11, (time12 - time11)/ caseNum);

            long time13 = System.nanoTime();
            for (Map.Entry<String, String> entry : case2.entrySet()) {
                String nodeId = entry.getKey();
                String blockId = entry.getValue();
                merkleBPlus.nodeQueryBySingleBlock(nodeId, blockId);
            }
            long time14 = System.nanoTime();
            System.out.println("merkleB+tree单节点、单区块查询总时延：" + (time14 - time13));
            System.out.println("merkleB+tree单节点、单区块查询平均时延：" + (time14 - time13)/ caseNum);
            record.record(Constant.MERKLEBPLUS, Constant.NODESINGLEBLOCK, time14 - time13, (time14 - time13)/ caseNum);

            long time15 = System.nanoTime();
            for (Map.Entry<String, String> entry : case2.entrySet()) {
                String nodeId = entry.getKey();
                String blockId = entry.getValue();
                mst.nodeQueryBySingleBlock(nodeId, blockId);
            }
            long time16 = System.nanoTime();
            System.out.println("mst单节点、单区块查询总时延：" + (time16 - time15));
            System.out.println("mst单节点、单区块查询平均时延：" + (time16 - time15)/ caseNum);
            record.record(Constant.MST, Constant.NODESINGLEBLOCK, time16 - time15, (time16 - time15)/ caseNum);

            System.out.println("---------------------------------------------------------");

            /**
             * 单节点全区块查询
             */
            List<String> case3 = queryCase.getNodeQueryCase(caseNum);
            long time17 = System.nanoTime();
            for (String nodeId : case3) {
                meat.nodeQueryByAllBlock(nodeId);
            }
            long time18 = System.nanoTime();
            System.out.println("meat单节点、全区块查询总时延：" + (time18 - time17));
            System.out.println("meat单节点、全区块查询平均时延：" + (time18 - time17)/ caseNum);
            record.record(Constant.MEAT, Constant.NODEALLEBLOCK, time18 - time17, (time18 - time17)/ caseNum);

            long time19 = System.nanoTime();
            for (String nodeId : case3) {
                merkle.nodeQueryByAllBlock(nodeId);
            }
            long time20 = System.nanoTime();
            System.out.println("merkle-tree单节点、全区块查询总时延：" + (time20 - time19));
            System.out.println("merkle-tree单节点、全区块查询平均时延：" + (time20 - time19)/ caseNum);
            record.record(Constant.MERKLE, Constant.NODEALLEBLOCK, time20 - time19, (time20 - time19)/ caseNum);

            long time21 = System.nanoTime();
            for (String nodeId : case3) {
                merkleBPlus.nodeQueryByAllBlock(nodeId);
            }
            long time22 = System.nanoTime();
            System.out.println("merkleB+tree单节点、全区块查询总时延：" + (time22 - time21));
            System.out.println("merkleB+tree单节点、全区块查询平均时延：" + (time22 - time21)/ caseNum);
            record.record(Constant.MERKLEBPLUS, Constant.NODEALLEBLOCK, time22 - time21, (time22 - time21)/ caseNum);

            long time23 = System.nanoTime();
            for (String nodeId : case3) {
                mst.nodeQueryByAllBlock(nodeId);
            }
            long time24 = System.nanoTime();
            System.out.println("mst单节点、全区块查询总时延：" + (time24 - time23));
            System.out.println("mst单节点、全区块查询平均时延：" + (time24 - time23)/ caseNum);
            record.record(Constant.MST, Constant.NODEALLEBLOCK, time24 - time23, (time24 - time23)/ caseNum);

            System.out.println("---------------------------------------------------------");

            /**
             * 单区块属性查询
             */
            Map<String, Map<String, String>> case4 = queryCase.getPropertyQuerySingleBlockCase(caseNum);
            long time25 = System.nanoTime();
            for (Map.Entry<String, Map<String, String>> entry : case4.entrySet()) {
                String blockId = entry.getKey();
                Map<String, String> queries = entry.getValue();
                meat.propertyQueryBySingleBlock(queries, blockId);
            }
            long time26 = System.nanoTime();
            System.out.println("meat属性值、单区块查询总时延：" + (time26 - time25));
            System.out.println("meat属性值、单区块查询平均时延：" + (time26 - time25)/ caseNum);
            record.record(Constant.MEAT, Constant.PROVALUESINGLEBLOCK, time26 - time25, (time26 - time25)/ caseNum);

            long time27 = System.nanoTime();
            for (Map.Entry<String, Map<String, String>> entry : case4.entrySet()) {
                String blockId = entry.getKey();
                Map<String, String> queries = entry.getValue();
                merkle.propertyQueryBySingleBlock(queries, blockId);
            }
            long time28 = System.nanoTime();
            System.out.println("merkle-tree属性值、单区块查询总时延：" + (time28 - time27));
            System.out.println("merkle-tree属性值、单区块查询平均时延：" + (time28 - time27)/ caseNum);
            record.record(Constant.MERKLE, Constant.PROVALUESINGLEBLOCK, time28 - time27, (time28 - time27)/ caseNum);

            long time29 = System.nanoTime();
            for (Map.Entry<String, Map<String, String>> entry : case4.entrySet()) {
                String blockId = entry.getKey();
                Map<String, String> queries = entry.getValue();
                merkleBPlus.propertyQueryBySingleBlock(queries, blockId);
            }
            long time30 = System.nanoTime();
            System.out.println("merkleB+tree属性值、单区块查询总时延：" + (time30 - time29));
            System.out.println("merkleB+tree属性值、单区块查询平均时延：" + (time30 - time29)/ caseNum);
            record.record(Constant.MERKLEBPLUS, Constant.PROVALUESINGLEBLOCK, time30 - time29, (time30 - time29)/ caseNum);

            long time31 = System.nanoTime();
            for (Map.Entry<String, Map<String, String>> entry : case4.entrySet()) {
                String blockId = entry.getKey();
                Map<String, String> queries = entry.getValue();
                queries.put("type", "1");
                mst.propertyQueryBySingleBlock(queries, blockId);
            }
            long time32 = System.nanoTime();
            System.out.println("mst属性值、单区块查询总时延：" + (time32 - time31));
            System.out.println("mst属性值、单区块查询平均时延：" + (time32 - time31)/ caseNum);
            record.record(Constant.MST, Constant.PROVALUESINGLEBLOCK, time32 - time31, (time32 - time31)/ caseNum);

            System.out.println("---------------------------------------------------------");

            /**
             * 全区块属性值查询
             */
            List<Map<String, String>> case5 = queryCase.getPropertyQueryCase(caseNum);
            long time33 = System.nanoTime();
            for (Map<String, String> queries : case5) {
                meat.propertyQueryByAllBlock(queries);
            }
            long time34 = System.nanoTime();
            System.out.println("meat属性值、多区块查询总时延：" + (time34 - time33));
            System.out.println("meat属性值、多区块查询平均时延：" + (time34 - time33)/ caseNum);
            record.record(Constant.MEAT, Constant.PROVALUEALLBLOCK, time34 - time33, (time34 - time33)/ caseNum);

            long time35 = System.nanoTime();
            for (Map<String, String> queries : case5) {
                merkle.propertyQueryByAllBlock(queries);
            }
            long time36 = System.nanoTime();
            System.out.println("merkle-tree属性值、多区块查询总时延：" + (time36 - time35));
            System.out.println("merkle-tree属性值、多区块查询平均时延：" + (time36 - time35)/ caseNum);
            record.record(Constant.MERKLE, Constant.PROVALUEALLBLOCK, time36 - time35, (time36 - time35)/ caseNum);

            long time37 = System.nanoTime();
            for (Map<String, String> queries : case5) {
                merkleBPlus.propertyQueryByAllBlock(queries);
            }
            long time38 = System.nanoTime();
            System.out.println("merkleB+tree属性值、多区块查询总时延：" + (time38 - time37));
            System.out.println("merkleB+tree属性值、多区块查询平均时延：" + (time38 - time37)/ caseNum);
            record.record(Constant.MERKLEBPLUS, Constant.PROVALUEALLBLOCK, time38 - time37, (time38 - time37)/ caseNum);

            long time39 = System.nanoTime();
            for (Map<String, String> queries : case5) {
                queries.put("type", "1");
                mst.propertyQueryByAllBlock(queries);
            }
            long time40 = System.nanoTime();
            System.out.println("mst属性值、多区块查询总时延：" + (time40 - time39));
            System.out.println("mst属性值、多区块查询平均时延：" + (time40 - time39)/ caseNum);
            record.record(Constant.MST, Constant.PROVALUEALLBLOCK, time40 - time39, (time40 - time39)/ caseNum);

            System.out.println("---------------------------------------------------------");

            /**
             * 单区块属性范围查询
             */
            Map<String, Map<String, String>> case6 = queryCase.getPropertyRangeQuerySingleBlockCase(caseNum);
            long time41 = System.nanoTime();
            for (Map.Entry<String, Map<String, String>> entry : case6.entrySet()) {
                String blockId = entry.getKey();
                Map<String, String> queries = entry.getValue();
                meat.propertyRangeQueryBySingleBlock(queries, blockId, 0);
            }
            long time42 = System.nanoTime();
            System.out.println("meat属性范围、单区块查询总时延：" + (time42 - time41));
            System.out.println("meat属性范围、单区块查询平均时延：" + (time42 - time41)/ caseNum);
            record.record(Constant.MEAT, Constant.PRORANGESINGLEBLOCK, time42 - time41, (time42 - time41)/ caseNum);

            long time43 = System.nanoTime();
            for (Map.Entry<String, Map<String, String>> entry : case6.entrySet()) {
                String blockId = entry.getKey();
                Map<String, String> queries = entry.getValue();
                merkle.propertyRangeQueryBySingleBlock(queries, blockId, 0);
            }
            long time44 = System.nanoTime();
            System.out.println("merkle-tree属性范围、单区块查询总时延：" + (time44 - time43));
            System.out.println("merkle-tree属性范围、单区块查询平均时延：" + (time44 - time43)/ caseNum);
            record.record(Constant.MERKLE, Constant.PRORANGESINGLEBLOCK, time44 - time43, (time44 - time43)/ caseNum);

            long time45 = System.nanoTime();
            for (Map.Entry<String, Map<String, String>> entry : case6.entrySet()) {
                String blockId = entry.getKey();
                Map<String, String> queries = entry.getValue();
                merkleBPlus.propertyRangeQueryBySingleBlock(queries, blockId, 0);
            }
            long time46 = System.nanoTime();
            System.out.println("merkleB+tree属性范围、单区块查询总时延：" + (time46 - time45));
            System.out.println("merkleB+tree属性范围、单区块查询平均时延：" + (time46 - time45)/ caseNum);
            record.record(Constant.MERKLEBPLUS, Constant.PRORANGESINGLEBLOCK, time46 - time45, (time46 - time45)/ caseNum);

            long time47 = System.nanoTime();
            for (Map.Entry<String, Map<String, String>> entry : case6.entrySet()) {
                String blockId = entry.getKey();
                Map<String, String> queries = entry.getValue();
                queries.put("type", "1");
                mst.propertyRangeQueryBySingleBlock(queries, blockId, 0);
            }
            long time48 = System.nanoTime();
            System.out.println("mst属性范围、单区块查询总时延：" + (time48 - time47));
            System.out.println("mst属性范围、单区块查询平均时延：" + (time48 - time47)/ caseNum);
            record.record(Constant.MST, Constant.PRORANGESINGLEBLOCK, time48 - time47, (time48 - time47)/ caseNum);

            System.out.println("---------------------------------------------------------");

            /**
             * 属性范围，多区块查询
             */
            List<Map<String, String>> case7 = queryCase.getPropertyRangeQueryCase(caseNum);
            long time49 = System.nanoTime();
            for (Map<String, String> queries : case7) {
                meat.propertyRangeQueryByAllBlock(queries, 0);
            }
            long time50 = System.nanoTime();
            System.out.println("meat属性范围、多区块查询总时延：" + (time50 - time49));
            System.out.println("meat属性范围、多区块查询平均时延：" + (time50 - time49)/ caseNum);
            record.record(Constant.MEAT, Constant.PRORANGEALLBLOCK, time50 - time49, (time50 - time49)/ caseNum);

            long time51 = System.nanoTime();
            for (Map<String, String> queries : case7) {
                merkle.propertyRangeQueryByAllBlock(queries, 0);
            }
            long time52 = System.nanoTime();
            System.out.println("merkle-tree属性范围、多区块查询总时延：" + (time52 - time51));
            System.out.println("merkle-tree属性范围、多区块查询平均时延：" + (time52 - time51)/ caseNum);
            record.record(Constant.MERKLE, Constant.PRORANGEALLBLOCK, time52 - time51, (time52 - time51)/ caseNum);

            long time53 = System.nanoTime();
            for (Map<String, String> queries : case7) {
                merkleBPlus.propertyRangeQueryByAllBlock(queries, 0);
            }
            long time54 = System.nanoTime();
            System.out.println("merkleB+tree属性范围、多区块查询总时延：" + (time54 - time53));
            System.out.println("merkleB+tree属性范围、多区块查询平均时延：" + (time54 - time53)/ caseNum);
            record.record(Constant.MERKLEBPLUS, Constant.PRORANGEALLBLOCK, time54 - time53, (time54 - time53)/ caseNum);

            long time55 = System.nanoTime();
            for (Map<String, String> queries : case7) {
                queries.put("type", "1");
                mst.propertyRangeQueryByAllBlock(queries, 0);
            }
            long time56 = System.nanoTime();
            System.out.println("mst属性范围、多区块查询总时延：" + (time56 - time55));
            System.out.println("mst属性范围、多区块查询平均时延：" + (time56 - time55)/ caseNum);
            record.record(Constant.MST, Constant.PRORANGEALLBLOCK, time56 - time55, (time56 - time55)/ caseNum);

            System.out.println("---------------------------------------------------------");

            /**
             * 属性topk，单区块查询
             */
            Map<String, Map<String, String>> case8 = queryCase.getTopKSingleBlockCase(caseNum);
            long time57 = System.nanoTime();
            for (Map.Entry<String, Map<String, String>> entry : case8.entrySet()) {
                String blockId = entry.getKey();
                Map<String, String> queries = entry.getValue();
                meat.propertyRangeQueryBySingleBlock(queries, blockId, 5);
            }
            long time58 = System.nanoTime();
            System.out.println("meat属性topk、单区块查询总时延：" + (time58 - time57));
            System.out.println("meat属性topk、单区块查询平均时延：" + (time58 - time57)/ caseNum);
            record.record(Constant.MEAT, Constant.PROTOPKSINGLEBLOCK, time58 - time57, (time58 - time47)/ caseNum);

            long time59 = System.nanoTime();
            for (Map.Entry<String, Map<String, String>> entry : case8.entrySet()) {
                String blockId = entry.getKey();
                Map<String, String> queries = entry.getValue();
                merkle.propertyRangeQueryBySingleBlock(queries, blockId, 5);
            }
            long time60 = System.nanoTime();
            System.out.println("merkle-tree属性topk、单区块查询总时延：" + (time60 - time59));
            System.out.println("merkle-tree属性topk、单区块查询平均时延：" + (time60 - time59)/ caseNum);
            record.record(Constant.MERKLE, Constant.PROTOPKSINGLEBLOCK, time60 - time59, (time60 - time59)/ caseNum);

            long time61 = System.nanoTime();
            for (Map.Entry<String, Map<String, String>> entry : case8.entrySet()) {
                String blockId = entry.getKey();
                Map<String, String> queries = entry.getValue();
                merkleBPlus.propertyRangeQueryBySingleBlock(queries, blockId, 5);
            }
            long time62 = System.nanoTime();
            System.out.println("merkleB+tree属性topk、单区块查询总时延：" + (time62 - time61));
            System.out.println("merkleB+tree属性topk、单区块查询平均时延：" + (time62 - time61) / caseNum);
            record.record(Constant.MERKLEBPLUS, Constant.PROTOPKSINGLEBLOCK, time62 - time61, (time62 - time61)/ caseNum);

            long time63 = System.nanoTime();
            for (Map.Entry<String, Map<String, String>> entry : case8.entrySet()) {
                String blockId = entry.getKey();
                Map<String, String> queries = entry.getValue();
                queries.put("type", "1");
                mst.propertyRangeQueryBySingleBlock(queries, blockId, 5);
            }
            long time64 = System.nanoTime();
            System.out.println("mst属性topk、单区块查询总时延：" + (time64 - time63));
            System.out.println("mst属性topk、单区块查询平均时延：" + (time64 - time63)/ caseNum);
            record.record(Constant.MST, Constant.PROTOPKSINGLEBLOCK, time64 - time63, (time64 - time63)/ caseNum);

            System.out.println("---------------------------------------------------------");

            /**
             * 属性topk，多区块查询
             */
            List<Map<String, String>> case9 = queryCase.getTopKQueryCase(caseNum);
            long time65 = System.nanoTime();
            for (Map<String, String> queries : case9) {
                meat.propertyRangeQueryByAllBlock(queries, 5);
            }
            long time66 = System.nanoTime();
            System.out.println("meat属性topk、多区块查询总时延：" + (time66 - time65));
            System.out.println("meat属性topk、多区块查询平均时延：" + (time66 - time65) / caseNum);
            record.record(Constant.MEAT, Constant.PROTOPKALLBLOCK, time66 - time65, (time66 - time65)/ caseNum);

            long time67 = System.nanoTime();
            for (Map<String, String> queries : case9) {
                merkle.propertyRangeQueryByAllBlock(queries, 5);
            }
            long time68 = System.nanoTime();
            System.out.println("merkle-tree属性topk、多区块查询总时延：" + (time68 - time67));
            System.out.println("merkle-tree属性topk、多区块查询平均时延：" + (time68 - time67) / caseNum);
            record.record(Constant.MERKLE, Constant.PROTOPKALLBLOCK, time68 - time67, (time68 - time67)/ caseNum);

            long time69 = System.nanoTime();
            for (Map<String, String> queries : case9) {
                merkleBPlus.propertyRangeQueryByAllBlock(queries, 5);
            }
            long time70 = System.nanoTime();
            System.out.println("merkleB+tree属性topk、多区块查询总时延：" + (time70 - time69));
            System.out.println("merkleB+tree属性topk、多区块查询平均时延：" + (time70 - time69) / caseNum);
            record.record(Constant.MERKLEBPLUS, Constant.PROTOPKALLBLOCK, time70 - time69, (time70 - time69)/ caseNum);

            long time71 = System.nanoTime();
            for (Map<String, String> queries : case9) {
                queries.put("type", "1");
                mst.propertyRangeQueryByAllBlock(queries, 5);
            }
            long time72 = System.nanoTime();
            System.out.println("mst属性topk、多区块查询总时延：" + (time72 - time71));
            System.out.println("mst属性topk、多区块查询平均时延：" + (time72 - time71) / caseNum);
            record.record(Constant.MST, Constant.PROTOPKALLBLOCK, time72 - time71, (time72 - time71)/ caseNum);

        }
        record.save();
    }

}
