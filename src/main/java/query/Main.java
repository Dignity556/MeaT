package query;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import data.DataProcessor;
import data.NBADataProcessor;
import data.TradeDataProcessor;
import meat.Meat;
import query.Constant;
import query.MemoryCalculator;
import query.QueryCase;
import query.Record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        System.setProperty("java.vm.name", "Java HotSpot(TM) ");
        // 加载数据
        String dataPath = "./data/nba_mini.csv";
        DataProcessor nbaProcessor = new NBADataProcessor();
        // 变量，每个区块的
        ArrayList<Transaction> temporary_txs = new ArrayList<>();
        int blockTxNum = 100;
        for (; blockTxNum <= 4000; blockTxNum *= 2) {
            Context context = nbaProcessor.getDataContext(dataPath, blockTxNum);
            //生成meat
            Meat meat = new Meat();
            long t1 = System.currentTimeMillis();
            String[] nba_filter={"game_id", "home", "win_or_lose", "t_point", "t_fieldgoal", "t_x3point", "t_freegoal", "t_offrebound", "t_totalrebound", "t_assist", "t_steal", "t_block", "t_turnover", "t_fouls", "o_point", "o_fieldgoal", "o_x3point", "o_freegoal", "o_offrebound", "o_totalrebound", "o_assist", "o_steal", "o_block", "o_turnover", "o_fouls"};
            long meatMatrixTime=meat.createMeat(context,nba_filter);
            long t2 = System.currentTimeMillis();
            System.out.println("meat构建时间：" + (t2 - t1));
            System.out.println("meat skyline矩阵构建时间：" + (t2 - t1 - meatMatrixTime / 1000000));
            //测试用例
            QueryCase queryCase = new QueryCase(context);
            int caseNum = 10;
            for (int i=2; i<16; i+=2)
            {
                Map<String, Map<String, String>> cases = queryCase.getPropertyQuerySingleBlockCase(caseNum,i);
                long time25 = System.nanoTime();
                for (Map.Entry<String, Map<String, String>> entry : cases.entrySet()) {
                    String blockId = entry.getKey();
                    Map<String, String> queries = entry.getValue();
                    meat.propertyQueryBySingleBlock(queries, blockId);
                }
                long time26 = System.nanoTime();
                System.out.println("meat" + i + "属性查询时间为"+ (time26 - time25));
                System.out.println("meat" + i + "属性平均查询时间为" + (time26 - time25) / caseNum);
            }
//            long t3 = System.currentTimeMillis();
//            Merkle merkle = new Merkle();
//            long merkleMatrixTime = merkle.createMerkle(context);
//            long t4 = System.currentTimeMillis();
//            long merkleSkyline=MerkleTree.traceability_skyline(context,merkle);
//            System.out.println("Merkle树Skyline查询："+merkleSkyline);
////            System.out.println("merkle-tree构建时间：" + (t4 - t3));
////            System.out.println("merkle-tree skyline矩阵构建时间：" + merkleMatrixTime);
////            record_node.record_node(Constant.MERKLE, merkleMatrixTime);
////            long merkleSize = memoryCalculator.getMerkleSize();
////            System.out.println("merkle内存：" + merkleSize);
////            record_construct.record_construct(Constant.MERKLE, (t4 - t3 - merkleMatrixTime), merkleMatrixTime, merkleSize);
//
//            long t5 = System.currentTimeMillis();
//            MerkleBPlus merkleBPlus = new MerkleBPlus();
//            long merkleBPlusMatrixTime = merkleBPlus.createMerkleBPlus(context);
//            long t6 = System.currentTimeMillis();
//            long mbpSkyline= MerkleBPlusTree.traceability_skyline(context,merkleBPlus);
//            System.out.println("MBT树Skyline查询："+mbpSkyline);
////            System.out.println("merkleB+tree构建时间：" + (t6 - t5));
////            System.out.println("merkleB+tree skyline矩阵构建时间：" + merkleBPlusMatrixTime);
////            record_node.record_node(Constant.MERKLEBPLUS, merkleBPlusMatrixTime);
////            long merkleBPlusSize = memoryCalculator.getMerkleBPlusSize();
////            System.out.println("bplus内存：" + merkleBPlusSize);
////            record_construct.record_construct(Constant.MERKLEBPLUS, (t6 - t5 - merkleBPlusMatrixTime), merkleBPlusMatrixTime, merkleBPlusSize);
//
//
//            long t7 = System.currentTimeMillis();
//            MST mst = new MST();
//            long mstMatrixTime = mst.createMST(context);
//            long t8 = System.currentTimeMillis();
//            long mstSkyline=mst.traceability_skyline(context,mst);
//            System.out.println("MST树Skyline查询："+mstSkyline);
////            System.out.println("mst构建时间：" + (t8 - t7));
////            System.out.println("mst skyline矩阵构建时间：" + mstMatrixTime);
////            record_node.record_node(Constant.MST, mstMatrixTime);
////            long mstSize = memoryCalculator.getMSTSize(mst);
////            System.out.println("mst内存：" + mstSize);
////            record_construct.record_construct(Constant.MST, (t8 - t7 - mstMatrixTime), mstMatrixTime, mstSize);
//
//
//            System.out.println("---------------------------------------------------------");
            System.gc();
//            record.save();
//            record_node.save_node();
//            record_path.save_path();
//            record_construct.save_construct();
//            record_details.save_details();
        }
    }
}
// 实验2.1
//            //MeaT的skyline node query时间
//            Meat meat = new Meat();
//            long t1 = System.currentTimeMillis();
//            long meatMatrixTime = meat.createMeat(context);
//            long t2 = System.currentTimeMillis();
////            System.out.println("meat构建时间：" + (t2 - t1));
////            System.out.println("meat skyline矩阵构建时间：" + meatMatrixTime);
////            record_node.record_node(Constant.MEAT, meatMatrixTime);
////            long mgtSize = memoryCalculator.getMGTSize();
////            System.out.println("meat内存：" + mgtSize);
////            record_construct.record_construct(Constant.MEAT, (t2 - t1 - meatMatrixTime / 1000000), meatMatrixTime, mgtSize);
//
//            //MeaT的atomic查询:先单点查询，后多属性top-k
//            QueryCase queryCase = new QueryCase(context);
//            // TODO 生成测试用例的数量
//            int caseNum = blockTxNum;
////            Map<String, String> case1 = queryCase.getSingleTransactionQueryCase(caseNum);
////            long time1 = System.nanoTime();
////            for (Map.Entry<String, String> entry : case1.entrySet()) {
////                String txId = entry.getKey();
////                String nodeId = entry.getValue();
////                meat.singleTransactionQuery(txId, nodeId);
////            }
////            long time2 = System.nanoTime();
////            List<Map<String, String>> case9 = queryCase.getTopKQueryCase(caseNum);
////            for (Map<String, String> queries : case9) {
////                meat.propertyRangeQueryByAllBlock(queries, 5);
////            }
////            long time3 = System.nanoTime();
////            System.out.println("atomic总时延：" + (time3 - time1));
////            System.out.println("graph atomic总时延：" + (time3 - time2));
//
//            //path query (graph atomic + naive atomic)
//            Map<String, String> case1 = queryCase.getSingleTransactionQueryCase(caseNum);
//            long time_path_1 = System.nanoTime();
//            //交易查询
//            for (Map.Entry<String, String> entry : case1.entrySet()) {
//                String txId = entry.getKey();
//                String nodeId = entry.getValue();
//                meat.singleTransactionQuery(txId, nodeId);
//            }
//            //node查询
//            Map<String, String> case_node_path = queryCase.getNodeQueryBySingBlockCase(caseNum*3);
//            for (Map.Entry<String, String> entry : case_node_path.entrySet()) {
//                long time_in = System.nanoTime();
//                String nodeId = entry.getKey();
//                String blockId = entry.getValue();
//                meat.nodeQueryBySingleBlock(nodeId, blockId);
//                long time_out = System.nanoTime();
//                record_details.record_detail(Constant.MEAT, Constant.NODESINGLEBLOCK, -(time_in - time_out));
//            }
//            long time_path_2 = System.nanoTime();
//            //属性查询（属性值*3+属性区间*6）
//            Map<String, Map<String, String>> case_value_path = queryCase.getPropertyQuerySingleBlockCase(caseNum*3);
//            for (Map.Entry<String, Map<String, String>> entry : case_value_path.entrySet()) {
//                String blockId = entry.getKey();
//                Map<String, String> queries = entry.getValue();
//                meat.propertyQueryBySingleBlock(queries, blockId);
//            }
//            Map<String, Map<String, String>> case_range_path = queryCase.getPropertyRangeQuerySingleBlockCase(caseNum*6);
//            for (Map.Entry<String, Map<String, String>> entry : case_range_path.entrySet()) {
//                String blockId = entry.getKey();
//                Map<String, String> queries = entry.getValue();
//                meat.propertyRangeQueryBySingleBlock(queries, blockId, 0);
//            }
//            long time_path_3 = System.nanoTime();
//            System.out.println("graph atomic单节点、单区块查询总时延：" + (time_path_3 - time_path_2));
//            System.out.println("atomic单节点、单区块查询总时延：" + (time_path_3 - time_path_1));
//
//        }




//    // atomic查询测试
//    QueryCase queryCase = new QueryCase(context);
//    // TODO 生成测试用例的数量
//    int caseNum = 100;
//    /**
//     * 单节点，交易查询
//     */
//    Map<String, String> case1 = queryCase.getSingleTransactionQueryCase(caseNum);
//    long time1 = System.nanoTime();
//            for (Map.Entry<String, String> entry : case1.entrySet()) {
//        long time_in = System.nanoTime();
//        String txId = entry.getKey();
//        String nodeId = entry.getValue();
//        meat.singleTransactionQuery(txId, nodeId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MEAT, Constant.SINGLETX, -(time_in - time_out));
//        }
//        long time2 = System.nanoTime();
//        System.out.println("meat单节点、单交易查询总时延：" + (time2 - time1));
//        System.out.println("meat单节点、单交易查询平均时延：" + (time2 - time1) / caseNum);
//        record.record(Constant.MEAT, Constant.SINGLETX, time2 - time1, (time2 - time1) / caseNum);
//
//        long time3 = System.nanoTime();
//        for (Map.Entry<String, String> entry : case1.entrySet()) {
//        long time_in = System.nanoTime();
//        String txId = entry.getKey();
//        String nodeId = entry.getValue();
//        merkle.singleTransactionQuery(txId, nodeId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLE, Constant.SINGLETX, -(time_in - time_out));
//        }
//        long time4 = System.nanoTime();
//        System.out.println("merkle-tree单节点、单交易查询总时延：" + (time4 - time3));
//        System.out.println("merkle-tree单节点、单交易查询平均时延：" + (time4 - time3) / caseNum);
//        record.record(Constant.MERKLE, Constant.SINGLETX, time4 - time3, (time4 - time3) / caseNum);
//
//        long time5 = System.nanoTime();
//        for (Map.Entry<String, String> entry : case1.entrySet()) {
//        long time_in = System.nanoTime();
//        String txId = entry.getKey();
//        String nodeId = entry.getValue();
//        merkleBPlus.singleTransactionQuery(txId, nodeId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLEBPLUS, Constant.SINGLETX, -(time_in - time_out));
//        }
//        long time6 = System.nanoTime();
//        System.out.println("merkleB+tree单节点、单交易查询总时延：" + (time6 - time5));
//        System.out.println("merkleB+tree单节点、单交易查询平均时延：" + (time6 - time5) / caseNum);
//        record.record(Constant.MERKLEBPLUS, Constant.SINGLETX, time6 - time5, (time6 - time5) / caseNum);
//
//        long time7 = System.nanoTime();
//        for (Map.Entry<String, String> entry : case1.entrySet()) {
//        long time_in = System.nanoTime();
//        String txId = entry.getKey();
//        String nodeId = entry.getValue();
//        mst.singleTransactionQuery(txId, nodeId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MST, Constant.SINGLETX, -(time_in - time_out));
//        }
//        long time8 = System.nanoTime();
//        System.out.println("mst单节点、单交易查询总时延：" + (time8 - time7));
//        System.out.println("mst单节点、单交易查询平均时延：" + (time8 - time7) / caseNum);
//        record.record(Constant.MST, Constant.SINGLETX, time8 - time7, (time8 - time7) / caseNum);
//
//        System.out.println("---------------------------------------------------------");
//
//        /**
//         * 单节点，单区块查询
//         */
//        Map<String, String> case2 = queryCase.getNodeQueryBySingBlockCase(caseNum);
//        long time9 = System.nanoTime();
//        for (Map.Entry<String, String> entry : case2.entrySet()) {
//        long time_in = System.nanoTime();
//        String nodeId = entry.getKey();
//        String blockId = entry.getValue();
//        meat.nodeQueryBySingleBlock(nodeId, blockId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MEAT, Constant.NODESINGLEBLOCK, -(time_in - time_out));
//        }
//        long time10 = System.nanoTime();
//        System.out.println("meat单节点、单区块查询总时延：" + (time10 - time9));
//        System.out.println("meat单节点、单区块查询平均时延：" + (time10 - time9) / caseNum);
//        record.record(Constant.MEAT, Constant.NODESINGLEBLOCK, time10 - time9, (time10 - time9) / caseNum);
//
//        long time11 = System.nanoTime();
//        for (Map.Entry<String, String> entry : case2.entrySet()) {
//        long time_in = System.nanoTime();
//        String nodeId = entry.getKey();
//        String blockId = entry.getValue();
//        merkle.nodeQueryBySingleBlock(nodeId, blockId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLE, Constant.NODESINGLEBLOCK, -(time_in - time_out));
//        }
//        long time12 = System.nanoTime();
//        System.out.println("merkle-tree单节点、单区块查询总时延：" + (time12 - time11));
//        System.out.println("merkle-tree单节点、单区块查询平均时延：" + (time12 - time11) / caseNum);
//        record.record(Constant.MERKLE, Constant.NODESINGLEBLOCK, time12 - time11, (time12 - time11) / caseNum);
//
//        long time13 = System.nanoTime();
//        for (Map.Entry<String, String> entry : case2.entrySet()) {
//        long time_in = System.nanoTime();
//        String nodeId = entry.getKey();
//        String blockId = entry.getValue();
//        merkleBPlus.nodeQueryBySingleBlock(nodeId, blockId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLEBPLUS, Constant.NODESINGLEBLOCK, -(time_in - time_out));
//        }
//        long time14 = System.nanoTime();
//        System.out.println("merkleB+tree单节点、单区块查询总时延：" + (time14 - time13));
//        System.out.println("merkleB+tree单节点、单区块查询平均时延：" + (time14 - time13) / caseNum);
//        record.record(Constant.MERKLEBPLUS, Constant.NODESINGLEBLOCK, time14 - time13, (time14 - time13) / caseNum);
//
//        long time15 = System.nanoTime();
//        for (Map.Entry<String, String> entry : case2.entrySet()) {
//        long time_in = System.nanoTime();
//        String nodeId = entry.getKey();
//        String blockId = entry.getValue();
//        mst.nodeQueryBySingleBlock(nodeId, blockId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MST, Constant.NODESINGLEBLOCK, -(time_in - time_out));
//        }
//        long time16 = System.nanoTime();
//        System.out.println("mst单节点、单区块查询总时延：" + (time16 - time15));
//        System.out.println("mst单节点、单区块查询平均时延：" + (time16 - time15) / caseNum);
//        record.record(Constant.MST, Constant.NODESINGLEBLOCK, time16 - time15, (time16 - time15) / caseNum);
//
//        System.out.println("---------------------------------------------------------");
//
//        /**
//         * 单节点全区块查询
//         */
//        List<String> case3 = queryCase.getNodeQueryCase(caseNum);
//        long time17 = System.nanoTime();
//        for (String nodeId : case3) {
//        long time_in = System.nanoTime();
//        meat.nodeQueryByAllBlock(nodeId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MEAT, Constant.NODEALLEBLOCK, -(time_in - time_out));
//        }
//        long time18 = System.nanoTime();
//        System.out.println("meat单节点、全区块查询总时延：" + (time18 - time17));
//        System.out.println("meat单节点、全区块查询平均时延：" + (time18 - time17) / caseNum);
//        record.record(Constant.MEAT, Constant.NODEALLEBLOCK, time18 - time17, (time18 - time17) / caseNum);
//
//        long time19 = System.nanoTime();
//        for (String nodeId : case3) {
//        long time_in = System.nanoTime();
//        merkle.nodeQueryByAllBlock(nodeId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLE, Constant.NODEALLEBLOCK, -(time_in - time_out));
//        }
//        long time20 = System.nanoTime();
//        System.out.println("merkle-tree单节点、全区块查询总时延：" + (time20 - time19));
//        System.out.println("merkle-tree单节点、全区块查询平均时延：" + (time20 - time19) / caseNum);
//        record.record(Constant.MERKLE, Constant.NODEALLEBLOCK, time20 - time19, (time20 - time19) / caseNum);
//
//        long time21 = System.nanoTime();
//        for (String nodeId : case3) {
//        long time_in = System.nanoTime();
//        merkleBPlus.nodeQueryByAllBlock(nodeId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLEBPLUS, Constant.NODEALLEBLOCK, -(time_in - time_out));
//        }
//        long time22 = System.nanoTime();
//        System.out.println("merkleB+tree单节点、全区块查询总时延：" + (time22 - time21));
//        System.out.println("merkleB+tree单节点、全区块查询平均时延：" + (time22 - time21) / caseNum);
//        record.record(Constant.MERKLEBPLUS, Constant.NODEALLEBLOCK, time22 - time21, (time22 - time21) / caseNum);
//
//        long time23 = System.nanoTime();
//        for (String nodeId : case3) {
//        long time_in = System.nanoTime();
//        mst.nodeQueryByAllBlock(nodeId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MST, Constant.NODEALLEBLOCK, -(time_in - time_out));
//        }
//        long time24 = System.nanoTime();
//        System.out.println("mst单节点、全区块查询总时延：" + (time24 - time23));
//        System.out.println("mst单节点、全区块查询平均时延：" + (time24 - time23) / caseNum);
//        record.record(Constant.MST, Constant.NODEALLEBLOCK, time24 - time23, (time24 - time23) / caseNum);
//
//        System.out.println("---------------------------------------------------------");
//
//        /**
//         * 单区块属性查询
//         */
//        Map<String, Map<String, String>> case4 = queryCase.getPropertyQuerySingleBlockCase(caseNum);
//        long time25 = System.nanoTime();
//        for (Map.Entry<String, Map<String, String>> entry : case4.entrySet()) {
//        long time_in = System.nanoTime();
//        String blockId = entry.getKey();
//        Map<String, String> queries = entry.getValue();
//        meat.propertyQueryBySingleBlock(queries, blockId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MEAT, Constant.PROVALUESINGLEBLOCK, -(time_in - time_out));
//        }
//        long time26 = System.nanoTime();
//        System.out.println("meat属性值、单区块查询总时延：" + (time26 - time25));
//        System.out.println("meat属性值、单区块查询平均时延：" + (time26 - time25) / caseNum);
//        record.record(Constant.MEAT, Constant.PROVALUESINGLEBLOCK, time26 - time25, (time26 - time25) / caseNum);
//
//        long time27 = System.nanoTime();
//        for (Map.Entry<String, Map<String, String>> entry : case4.entrySet()) {
//        long time_in = System.nanoTime();
//        String blockId = entry.getKey();
//        Map<String, String> queries = entry.getValue();
//        merkle.propertyQueryBySingleBlock(queries, blockId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLE, Constant.PROVALUESINGLEBLOCK, -(time_in - time_out));
//        }
//        long time28 = System.nanoTime();
//        System.out.println("merkle-tree属性值、单区块查询总时延：" + (time28 - time27));
//        System.out.println("merkle-tree属性值、单区块查询平均时延：" + (time28 - time27) / caseNum);
//        record.record(Constant.MERKLE, Constant.PROVALUESINGLEBLOCK, time28 - time27, (time28 - time27) / caseNum);
//
//        long time29 = System.nanoTime();
//        for (Map.Entry<String, Map<String, String>> entry : case4.entrySet()) {
//        long time_in = System.nanoTime();
//        String blockId = entry.getKey();
//        Map<String, String> queries = entry.getValue();
//        merkleBPlus.propertyQueryBySingleBlock(queries, blockId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLEBPLUS, Constant.PROVALUESINGLEBLOCK, -(time_in - time_out));
//        }
//        long time30 = System.nanoTime();
//        System.out.println("merkleB+tree属性值、单区块查询总时延：" + (time30 - time29));
//        System.out.println("merkleB+tree属性值、单区块查询平均时延：" + (time30 - time29) / caseNum);
//        record.record(Constant.MERKLEBPLUS, Constant.PROVALUESINGLEBLOCK, time30 - time29, (time30 - time29) / caseNum);
//
//        long time31 = System.nanoTime();
//        for (Map.Entry<String, Map<String, String>> entry : case4.entrySet()) {
//        long time_in = System.nanoTime();
//        String blockId = entry.getKey();
//        Map<String, String> queries = entry.getValue();
//        queries.put("type", "1");
//        mst.propertyQueryBySingleBlock(queries, blockId);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MST, Constant.PROVALUESINGLEBLOCK, -(time_in - time_out));
//        }
//        long time32 = System.nanoTime();
//        System.out.println("mst属性值、单区块查询总时延：" + (time32 - time31));
//        System.out.println("mst属性值、单区块查询平均时延：" + (time32 - time31) / caseNum);
//        record.record(Constant.MST, Constant.PROVALUESINGLEBLOCK, time32 - time31, (time32 - time31) / caseNum);
//
//        System.out.println("---------------------------------------------------------");
//
//        /**
//         * 全区块属性值查询
//         */
//        List<Map<String, String>> case5 = queryCase.getPropertyQueryCase(caseNum);
//        long time33 = System.nanoTime();
//        for (Map<String, String> queries : case5) {
//        long time_in = System.nanoTime();
//        meat.propertyQueryByAllBlock(queries);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MEAT, Constant.PROVALUEALLBLOCK, -(time_in - time_out));
//        }
//        long time34 = System.nanoTime();
//        System.out.println("meat属性值、多区块查询总时延：" + (time34 - time33));
//        System.out.println("meat属性值、多区块查询平均时延：" + (time34 - time33) / caseNum);
//        record.record(Constant.MEAT, Constant.PROVALUEALLBLOCK, time34 - time33, (time34 - time33) / caseNum);
//
//        long time35 = System.nanoTime();
//        for (Map<String, String> queries : case5) {
//        long time_in = System.nanoTime();
//        merkle.propertyQueryByAllBlock(queries);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLE, Constant.PROVALUEALLBLOCK, -(time_in - time_out));
//        }
//        long time36 = System.nanoTime();
//        System.out.println("merkle-tree属性值、多区块查询总时延：" + (time36 - time35));
//        System.out.println("merkle-tree属性值、多区块查询平均时延：" + (time36 - time35) / caseNum);
//        record.record(Constant.MERKLE, Constant.PROVALUEALLBLOCK, time36 - time35, (time36 - time35) / caseNum);
//
//        long time37 = System.nanoTime();
//        for (Map<String, String> queries : case5) {
//        long time_in = System.nanoTime();
//        merkleBPlus.propertyQueryByAllBlock(queries);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLEBPLUS, Constant.PROVALUEALLBLOCK, -(time_in - time_out));
//        }
//        long time38 = System.nanoTime();
//        System.out.println("merkleB+tree属性值、多区块查询总时延：" + (time38 - time37));
//        System.out.println("merkleB+tree属性值、多区块查询平均时延：" + (time38 - time37) / caseNum);
//        record.record(Constant.MERKLEBPLUS, Constant.PROVALUEALLBLOCK, time38 - time37, (time38 - time37) / caseNum);
//
//        long time39 = System.nanoTime();
//        for (Map<String, String> queries : case5) {
//        long time_in = System.nanoTime();
//        queries.put("type", "1");
//        mst.propertyQueryByAllBlock(queries);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MST, Constant.PROVALUEALLBLOCK, -(time_in - time_out));
//        }
//        long time40 = System.nanoTime();
//        System.out.println("mst属性值、多区块查询总时延：" + (time40 - time39));
//        System.out.println("mst属性值、多区块查询平均时延：" + (time40 - time39) / caseNum);
//        record.record(Constant.MST, Constant.PROVALUEALLBLOCK, time40 - time39, (time40 - time39) / caseNum);
//
//        System.out.println("---------------------------------------------------------");
//
//        /**
//         * 单区块属性范围查询
//         */
//        Map<String, Map<String, String>> case6 = queryCase.getPropertyRangeQuerySingleBlockCase(caseNum);
//        long time41 = System.nanoTime();
//        for (Map.Entry<String, Map<String, String>> entry : case6.entrySet()) {
//        long time_in = System.nanoTime();
//        String blockId = entry.getKey();
//        Map<String, String> queries = entry.getValue();
//        meat.propertyRangeQueryBySingleBlock(queries, blockId, 0);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MEAT, Constant.PRORANGESINGLEBLOCK, -(time_in - time_out));
//        }
//        long time42 = System.nanoTime();
//        System.out.println("meat属性范围、单区块查询总时延：" + (time42 - time41));
//        System.out.println("meat属性范围、单区块查询平均时延：" + (time42 - time41) / caseNum);
//        record.record(Constant.MEAT, Constant.PRORANGESINGLEBLOCK, time42 - time41, (time42 - time41) / caseNum);
//
//        long time43 = System.nanoTime();
//        for (Map.Entry<String, Map<String, String>> entry : case6.entrySet()) {
//        long time_in = System.nanoTime();
//        String blockId = entry.getKey();
//        Map<String, String> queries = entry.getValue();
//        merkle.propertyRangeQueryBySingleBlock(queries, blockId, 0);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLE, Constant.PRORANGESINGLEBLOCK, -(time_in - time_out));
//        }
//        long time44 = System.nanoTime();
//        System.out.println("merkle-tree属性范围、单区块查询总时延：" + (time44 - time43));
//        System.out.println("merkle-tree属性范围、单区块查询平均时延：" + (time44 - time43) / caseNum);
//        record.record(Constant.MERKLE, Constant.PRORANGESINGLEBLOCK, time44 - time43, (time44 - time43) / caseNum);
//
//        long time45 = System.nanoTime();
//        for (Map.Entry<String, Map<String, String>> entry : case6.entrySet()) {
//        long time_in = System.nanoTime();
//        String blockId = entry.getKey();
//        Map<String, String> queries = entry.getValue();
//        merkleBPlus.propertyRangeQueryBySingleBlock(queries, blockId, 0);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLEBPLUS, Constant.PRORANGESINGLEBLOCK, -(time_in - time_out));
//        }
//        long time46 = System.nanoTime();
//        System.out.println("merkleB+tree属性范围、单区块查询总时延：" + (time46 - time45));
//        System.out.println("merkleB+tree属性范围、单区块查询平均时延：" + (time46 - time45) / caseNum);
//        record.record(Constant.MERKLEBPLUS, Constant.PRORANGESINGLEBLOCK, time46 - time45, (time46 - time45) / caseNum);
//
//        long time47 = System.nanoTime();
//        for (Map.Entry<String, Map<String, String>> entry : case6.entrySet()) {
//        long time_in = System.nanoTime();
//        String blockId = entry.getKey();
//        Map<String, String> queries = entry.getValue();
//        queries.put("type", "1");
//        mst.propertyRangeQueryBySingleBlock(queries, blockId, 0);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MST, Constant.PRORANGESINGLEBLOCK, -(time_in - time_out));
//        }
//        long time48 = System.nanoTime();
//        System.out.println("mst属性范围、单区块查询总时延：" + (time48 - time47));
//        System.out.println("mst属性范围、单区块查询平均时延：" + (time48 - time47) / caseNum);
//        record.record(Constant.MST, Constant.PRORANGESINGLEBLOCK, time48 - time47, (time48 - time47) / caseNum);
//
//        System.out.println("---------------------------------------------------------");
//
//        /**
//         * 属性范围，多区块查询
//         */
//        List<Map<String, String>> case7 = queryCase.getPropertyRangeQueryCase(caseNum);
//        long time49 = System.nanoTime();
//        for (Map<String, String> queries : case7) {
//        long time_in = System.nanoTime();
//        meat.propertyRangeQueryByAllBlock(queries, 0);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MEAT, Constant.PRORANGEALLBLOCK, -(time_in - time_out));
//        }
//        long time50 = System.nanoTime();
//        System.out.println("meat属性范围、多区块查询总时延：" + (time50 - time49));
//        System.out.println("meat属性范围、多区块查询平均时延：" + (time50 - time49) / caseNum);
//        record.record(Constant.MEAT, Constant.PRORANGEALLBLOCK, time50 - time49, (time50 - time49) / caseNum);
//
//        long time51 = System.nanoTime();
//        for (Map<String, String> queries : case7) {
//        long time_in = System.nanoTime();
//        merkle.propertyRangeQueryByAllBlock(queries, 0);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLE, Constant.PRORANGEALLBLOCK, -(time_in - time_out));
//        }
//        long time52 = System.nanoTime();
//        System.out.println("merkle-tree属性范围、多区块查询总时延：" + (time52 - time51));
//        System.out.println("merkle-tree属性范围、多区块查询平均时延：" + (time52 - time51) / caseNum);
//        record.record(Constant.MERKLE, Constant.PRORANGEALLBLOCK, time52 - time51, (time52 - time51) / caseNum);
//
//        long time53 = System.nanoTime();
//        for (Map<String, String> queries : case7) {
//        long time_in = System.nanoTime();
//        merkleBPlus.propertyRangeQueryByAllBlock(queries, 0);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLEBPLUS, Constant.PRORANGEALLBLOCK, -(time_in - time_out));
//        }
//        long time54 = System.nanoTime();
//        System.out.println("merkleB+tree属性范围、多区块查询总时延：" + (time54 - time53));
//        System.out.println("merkleB+tree属性范围、多区块查询平均时延：" + (time54 - time53) / caseNum);
//        record.record(Constant.MERKLEBPLUS, Constant.PRORANGEALLBLOCK, time54 - time53, (time54 - time53) / caseNum);
//
//        long time55 = System.nanoTime();
//        for (Map<String, String> queries : case7) {
//        long time_in = System.nanoTime();
//        queries.put("type", "1");
//        mst.propertyRangeQueryByAllBlock(queries, 0);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MST, Constant.PRORANGEALLBLOCK, -(time_in - time_out));
//        }
//        long time56 = System.nanoTime();
//        System.out.println("mst属性范围、多区块查询总时延：" + (time56 - time55));
//        System.out.println("mst属性范围、多区块查询平均时延：" + (time56 - time55) / caseNum);
//        record.record(Constant.MST, Constant.PRORANGEALLBLOCK, time56 - time55, (time56 - time55) / caseNum);
//
//        System.out.println("---------------------------------------------------------");
//
//        /**
//         * 属性topk，单区块查询
//         */
//        Map<String, Map<String, String>> case8 = queryCase.getTopKSingleBlockCase(caseNum);
//        long time57 = System.nanoTime();
//        for (Map.Entry<String, Map<String, String>> entry : case8.entrySet()) {
//        long time_in = System.nanoTime();
//        String blockId = entry.getKey();
//        Map<String, String> queries = entry.getValue();
//        meat.propertyRangeQueryBySingleBlock(queries, blockId, 5);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MEAT, Constant.PROTOPKSINGLEBLOCK, -(time_in - time_out));
//        }
//        long time58 = System.nanoTime();
//        System.out.println("meat属性topk、单区块查询总时延：" + (time58 - time57));
//        System.out.println("meat属性topk、单区块查询平均时延：" + (time58 - time57) / caseNum);
//        record.record(Constant.MEAT, Constant.PROTOPKSINGLEBLOCK, time58 - time57, (time58 - time47) / caseNum);
//
//        long time59 = System.nanoTime();
//        for (Map.Entry<String, Map<String, String>> entry : case8.entrySet()) {
//        long time_in = System.nanoTime();
//        String blockId = entry.getKey();
//        Map<String, String> queries = entry.getValue();
//        merkle.propertyRangeQueryBySingleBlock(queries, blockId, 5);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLE, Constant.PROTOPKSINGLEBLOCK, -(time_in - time_out));
//        }
//        long time60 = System.nanoTime();
//        System.out.println("merkle-tree属性topk、单区块查询总时延：" + (time60 - time59));
//        System.out.println("merkle-tree属性topk、单区块查询平均时延：" + (time60 - time59) / caseNum);
//        record.record(Constant.MERKLE, Constant.PROTOPKSINGLEBLOCK, time60 - time59, (time60 - time59) / caseNum);
//
//        long time61 = System.nanoTime();
//        for (Map.Entry<String, Map<String, String>> entry : case8.entrySet()) {
//        long time_in = System.nanoTime();
//        String blockId = entry.getKey();
//        Map<String, String> queries = entry.getValue();
//        merkleBPlus.propertyRangeQueryBySingleBlock(queries, blockId, 5);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLEBPLUS, Constant.PROTOPKSINGLEBLOCK, -(time_in - time_out));
//        }
//        long time62 = System.nanoTime();
//        System.out.println("merkleB+tree属性topk、单区块查询总时延：" + (time62 - time61));
//        System.out.println("merkleB+tree属性topk、单区块查询平均时延：" + (time62 - time61) / caseNum);
//        record.record(Constant.MERKLEBPLUS, Constant.PROTOPKSINGLEBLOCK, time62 - time61, (time62 - time61) / caseNum);
//
//        long time63 = System.nanoTime();
//        for (Map.Entry<String, Map<String, String>> entry : case8.entrySet()) {
//        long time_in = System.nanoTime();
//        String blockId = entry.getKey();
//        Map<String, String> queries = entry.getValue();
//        queries.put("type", "1");
//        mst.propertyRangeQueryBySingleBlock(queries, blockId, 5);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MST, Constant.PROTOPKSINGLEBLOCK, -(time_in - time_out));
//        }
//        long time64 = System.nanoTime();
//        System.out.println("mst属性topk、单区块查询总时延：" + (time64 - time63));
//        System.out.println("mst属性topk、单区块查询平均时延：" + (time64 - time63) / caseNum);
//        record.record(Constant.MST, Constant.PROTOPKSINGLEBLOCK, time64 - time63, (time64 - time63) / caseNum);
//
//        System.out.println("---------------------------------------------------------");
//
//        /**
//         * 属性topk，多区块查询
//         */
//        List<Map<String, String>> case9 = queryCase.getTopKQueryCase(caseNum);
//        long time65 = System.nanoTime();
//        for (Map<String, String> queries : case9) {
//        long time_in = System.nanoTime();
//        meat.propertyRangeQueryByAllBlock(queries, 5);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MEAT, Constant.PROTOPKALLBLOCK, -(time_in - time_out));
//        }
//        long time66 = System.nanoTime();
//        System.out.println("meat属性topk、多区块查询总时延：" + (time66 - time65));
//        System.out.println("meat属性topk、多区块查询平均时延：" + (time66 - time65) / caseNum);
//        record.record(Constant.MEAT, Constant.PROTOPKALLBLOCK, time66 - time65, (time66 - time65) / caseNum);
//
//        long time67 = System.nanoTime();
//        for (Map<String, String> queries : case9) {
//        long time_in = System.nanoTime();
//        merkle.propertyRangeQueryByAllBlock(queries, 5);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLE, Constant.PROTOPKALLBLOCK, -(time_in - time_out));
//        }
//        long time68 = System.nanoTime();
//        System.out.println("merkle-tree属性topk、多区块查询总时延：" + (time68 - time67));
//        System.out.println("merkle-tree属性topk、多区块查询平均时延：" + (time68 - time67) / caseNum);
//        record.record(Constant.MERKLE, Constant.PROTOPKALLBLOCK, time68 - time67, (time68 - time67) / caseNum);
//
//        long time69 = System.nanoTime();
//        for (Map<String, String> queries : case9) {
//        long time_in = System.nanoTime();
//        merkleBPlus.propertyRangeQueryByAllBlock(queries, 5);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MERKLEBPLUS, Constant.PROTOPKALLBLOCK, -(time_in - time_out));
//        }
//        long time70 = System.nanoTime();
//        System.out.println("merkleB+tree属性topk、多区块查询总时延：" + (time70 - time69));
//        System.out.println("merkleB+tree属性topk、多区块查询平均时延：" + (time70 - time69) / caseNum);
//        record.record(Constant.MERKLEBPLUS, Constant.PROTOPKALLBLOCK, time70 - time69, (time70 - time69) / caseNum);
//
//        long time71 = System.nanoTime();
//        for (Map<String, String> queries : case9) {
//        long time_in = System.nanoTime();
//        queries.put("type", "1");
//        mst.propertyRangeQueryByAllBlock(queries, 5);
//        long time_out = System.nanoTime();
//        record_details.record_detail(Constant.MST, Constant.PROTOPKALLBLOCK, -(time_in - time_out));
//        }
//        long time72 = System.nanoTime();
//        System.out.println("mst属性topk、多区块查询总时延：" + (time72 - time71));
//        System.out.println("mst属性topk、多区块查询平均时延：" + (time72 - time71) / caseNum);
//        record.record(Constant.MST, Constant.PROTOPKALLBLOCK, time72 - time71, (time72 - time71) / caseNum);
//
//        /**
//         * 节点可达性查询
//         */
//        List<Map<String, String>> case10 = queryCase.getNodeAccessQueryCase(caseNum);
//        long time = 0;
//        for (Map<String, String> queries : case10) {
//        Set<String> set = queries.keySet();
//        for (String key : set) {
//        time += meat.nodeAccessQuery(key, queries.get(key));
//        }
//        }
//        System.out.println("meat节点可达性查询总时延：" + time);
//        System.out.println("meat节点可达性查询平均时延：" + time / caseNum);
//        record.record(Constant.MEAT, Constant.NODEACCESS, time, time / caseNum);
//        record_path.record_path(Constant.MEAT, time / caseNum);
//
//        long allTime = 0;
//        for (Map<String, String> queries : case10) {
//        Set<String> set = queries.keySet();
//        for (String key : set) {
//        long t = meat.graphNodeAccess(key, queries.get(key));
//        allTime += t;
//        }
//        }
//        System.out.println("图环境节点可达性查询总时延：" + allTime);
//        System.out.println("图环境节点可达性查询平均时延：" + allTime / caseNum);
//        record.record("图", Constant.NODEACCESS, allTime, allTime / caseNum);
//        record_path.record_path("图", allTime / caseNum);
//
//        long allTime2 = 0;
//        for (Map<String, String> queries : case10) {
//        Set<String> set = queries.keySet();
//        for (String key : set) {
//        long tt = meat.graphOptimalNodeAccess(key, queries.get(key));
//        allTime2 += tt;
//        }
//        }
//        System.out.println("优化-图环境节点可达性查询总时延：" + allTime2);
//        System.out.println("优化-图环境节点可达性查询平均时延：" + allTime2 / caseNum);
//        record.record("优化图", Constant.NODEACCESS, allTime2, allTime2 / caseNum);
//        record_path.record_path("优化图", allTime2 / caseNum);
//
//        long time75 = System.nanoTime();
//        for (Map<String, String> queries : case10) {
//        Set<String> set = queries.keySet();
//        for (String key : set) {
//        merkle.nodeAccessQuery(key, queries.get(key));
//        }
//        }
//        long time76 = System.nanoTime();
//        System.out.println("merkle-tree节点可达性查询总时延：" + (time76 - time75));
//        System.out.println("merkle-tree节点可达性查询平均时延：" + (time76 - time75) / caseNum);
//        record.record(Constant.MERKLE, Constant.NODEACCESS, time76 - time75, (time76 - time75) / caseNum);
//        record_path.record_path(Constant.MERKLE, (time76 - time75) / caseNum);
//
//        long time77 = System.nanoTime();
//        for (Map<String, String> queries : case10) {
//        Set<String> set = queries.keySet();
//        for (String key : set) {
//        merkleBPlus.nodeAccessQuery(key, queries.get(key));
//        }
//        }
//        long time78 = System.nanoTime();
//        System.out.println("merkleB+tree节点可达性查询总时延：" + (time78 - time77));
//        System.out.println("merkleB+tree节点可达性查询平均时延：" + (time78 - time77) / caseNum);
//        record.record(Constant.MERKLEBPLUS, Constant.NODEACCESS, time78 - time77, (time78 - time77) / caseNum);
//        record_path.record_path(Constant.MERKLEBPLUS, (time78 - time77) / caseNum);
//
//        long time79 = System.nanoTime();
//        for (Map<String, String> queries : case10) {
//        Set<String> set = queries.keySet();
//        for (String key : set) {
//        mst.nodeAccessQuery(key, queries.get(key));
//        }
//        }
//        long time80 = System.nanoTime();
//        System.out.println("mst节点可达性查询总时延：" + (time80 - time79));
//        System.out.println("mst节点可达性查询平均时延：" + (time80 - time79) / caseNum);
//        record.record(Constant.MST, Constant.NODEACCESS, time80 - time79, (time80 - time79) / caseNum);
//        record_path.record_path(Constant.MST, (time80 - time79) / caseNum);
