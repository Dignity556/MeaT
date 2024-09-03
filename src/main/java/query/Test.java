package query;

import blockchain.Block;
import blockchain.Transaction;
import data.Context;
import data.DataProcessor;
import data.NBADataProcessor;
import data.TradeDataProcessor;
import meat.Meat;
import merkle.Merkle;
import merkle.MerkleTree;
import merklebplus.MerkleBPlus;
import merklebplus.MerkleBPlusTree;
import mst.MST;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//也是main，只不过是test版本
public class Test {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        System.setProperty("java.vm.name", "Java HotSpot(TM) ");
        // 加载数据
        String dataPath = "./data/nba.csv";
        DataProcessor nbaProcessor = new NBADataProcessor();
        // record变量，每个查询的
        Record record_mtquery=new Record();
        Record record_construct=new Record();

        /*
           开始实验
         */
        int blockTxNum = 100;
        for (; blockTxNum <= 4000; blockTxNum *= 2) {
            record_mtquery.create_mtquery_sheet(blockTxNum);
            record_construct.createconstructsheet(blockTxNum);
            Context context = nbaProcessor.getDataContext(dataPath, blockTxNum);
            MemoryCalculator memoryCalculator = new MemoryCalculator(context);
            //meat
            Meat meat = new Meat();
            long t1 = System.currentTimeMillis();
            String[] nba_filter={"game_id", "home", "win_or_lose", "t_point", "t_fieldgoal", "t_x3point", "t_freegoal", "t_offrebound", "t_totalrebound", "t_assist", "t_steal", "t_block", "t_turnover", "t_fouls", "o_point", "o_fieldgoal", "o_x3point", "o_freegoal", "o_offrebound", "o_totalrebound", "o_assist", "o_steal", "o_block", "o_turnover", "o_fouls"};
            long meatMatrixTime=meat.createMeat(context,nba_filter);
            long t2 = System.currentTimeMillis();
            long mgtSize = memoryCalculator.getMGTSize();
            System.out.println("meat构建时间：" + (t2 - t1 -meatMatrixTime/1000000));
            record_construct.record_construct("meat-tskyline",(t2-t1-meatMatrixTime/1000000),meatMatrixTime,mgtSize);


            //traceability skyline
            long start=System.currentTimeMillis();
            Meat.traceability_skyline(context,meat);
            long end=System.currentTimeMillis();
            record_construct.record_construct("meat-normal",0,(end-start),0);


//            //测试用例
//            QueryCase queryCase = new QueryCase(context);
//            int caseNum = 10;
//            ArrayList<Long> times_meat=new ArrayList<>();
//            ArrayList<Long> times_meat_mtquery=new ArrayList<>();
//            for (int i=2; i<23; i+=2)
//            {
//                Map<String, Map<String, String>> cases = queryCase.getPropertyQuerySingleBlockCase(caseNum,i);
//                List<Map<String,String>> case_all=queryCase.getPropertyQueryCase(caseNum,i);
//                long time25 = System.nanoTime();
//                for (Map.Entry<String, Map<String, String>> entry : cases.entrySet()) {
//                    String blockId = entry.getKey();
//                    Map<String, String> queries = entry.getValue();
//                    meat.propertyQueryBySingleBlock(queries, blockId);
//                }
//                long time26 = System.nanoTime();
//                long time_in=System.nanoTime();
//                for (Map<String, String> queries : case_all) {
//                    meat.propertyQueryByAllBlock(queries);
//                }
//                long time_out=System.nanoTime();
//                System.out.println("meat" + i + "属性查询时间为" + (time26 - time25) / caseNum);
//                System.out.println("meat" + i + "mtquery查询时间为" + (time_out - time_in) / caseNum);
//                times_meat.add((time26 - time25) / caseNum);
//                times_meat_mtquery.add((time_out - time_in) / caseNum);
//            }
//            record_mtquery.record_mtquery(Constant.MEAT,times_meat);
//            record_mtquery.record_mtquery("meat_mtquery",times_meat_mtquery);


            //Merkle树的构建与属性查询
            long t3 = System.currentTimeMillis();
            Merkle merkle = new Merkle();
            long merkleMatrixTime = merkle.createMerkle(context);
            long t4 = System.currentTimeMillis();
//            long merkleSkyline= MerkleTree.traceability_skyline(context,merkle);
//            System.out.println("Merkle树Skyline查询："+merkleSkyline);
            System.out.println("merkle-tree构建时间：" + (t4 - t3));


            long start_mt=System.currentTimeMillis();
            MerkleTree.traceability_skyline(context,merkle);
            long end_mt=System.currentTimeMillis();
            long merkleSize = memoryCalculator.getMerkleSize();
            record_construct.record_construct("merkle",(t4 - t3),(end_mt-start_mt),merkleSize);


//            ArrayList<Long> times_merkle=new ArrayList<>();
//            ArrayList<Long> times_merkle_mtquery=new ArrayList<>();
//
//            for (int i=2; i<23; i+=2)
//            {
//                Map<String, Map<String, String>> cases = queryCase.getPropertyQuerySingleBlockCase(caseNum,i);
//                List<Map<String,String>> case_all=queryCase.getPropertyQueryCase(caseNum,i);
//                long time27 = System.nanoTime();
//                for (Map.Entry<String, Map<String, String>> entry : cases.entrySet()) {
//                    String blockId = entry.getKey();
//                    Map<String, String> queries = entry.getValue();
//                    merkle.propertyQueryBySingleBlock(queries, blockId);
//                }
//                long time28 = System.nanoTime();
//
//                long time_in = System.nanoTime();
//                for (Map<String, String> queries : case_all) {
//                    merkle.propertyQueryByAllBlock(queries);
//                }
//                long time_out = System.nanoTime();
//
////                System.out.println("merkle-tree" + i + "属性查询时间为" + (time28 - time27));
//                System.out.println("merkle-tree" + i + "属性查询时间为" + (time28 - time27) / caseNum);
//                System.out.println("merkle-tree" + i + "mtquery查询时间为" + (time_out - time_in) / caseNum);
//                times_merkle.add((time28 - time27) / caseNum);
//                times_merkle_mtquery.add((time_out - time_in) / caseNum);
//            }
//            record_mtquery.record_mtquery(Constant.MERKLE,times_merkle);
//            record_mtquery.record_mtquery("mt_mtquery",times_merkle_mtquery);


            //MB+T的构建与属性查询
            long t5 = System.currentTimeMillis();
            MerkleBPlus merkleBPlus = new MerkleBPlus();
            long merkleBPlusMatrixTime = merkleBPlus.createMerkleBPlus(context);
            long t6 = System.currentTimeMillis();
            long mbpSkyline= MerkleBPlusTree.traceability_skyline(context,merkleBPlus);
//            System.out.println("MBT树Skyline查询："+mbpSkyline);
            System.out.println("merkleB+tree构建时间：" + (t6 - t5));

            long start_mbt=System.currentTimeMillis();
            MerkleBPlusTree.traceability_skyline(context,merkleBPlus);
            long end_mbt=System.currentTimeMillis();
            long merkleBPlusSize = memoryCalculator.getMerkleBPlusSize();
            record_construct.record_construct("MB+T",(t6 - t5),(end_mbt-start_mbt),merkleBPlusSize);


//            ArrayList<Long> times_merklebplus=new ArrayList<>();
//            ArrayList<Long> times_merklebplus_mtquery=new ArrayList<>();
//            for (int i=2; i<23; i+=2) {
//                Map<String, Map<String, String>> cases = queryCase.getPropertyQuerySingleBlockCase(caseNum, i);
//                List<Map<String,String>> case_all=queryCase.getPropertyQueryCase(caseNum,i);
//                long time29 = System.nanoTime();
//                for (Map.Entry<String, Map<String, String>> entry : cases.entrySet()) {
//                    String blockId = entry.getKey();
//                    Map<String, String> queries = entry.getValue();
//                    merkleBPlus.propertyQueryBySingleBlock(queries, blockId);
//                }
//                long time30 = System.nanoTime();
//                long time_in = System.nanoTime();
//                for (Map<String, String> queries : case_all) {
//                    merkleBPlus.propertyQueryByAllBlock(queries);
//                }
//                long time_out = System.nanoTime();
//                System.out.println("merkleB+tree" + i + "属性查询时间为"+  (time30 - time29) / caseNum);
//                System.out.println("merkleB+tree" + i + "mtquery查询时间为"+  (time_out - time_in) / caseNum);
//                times_merklebplus.add((time30-time29) / caseNum);
//                times_merklebplus_mtquery.add((time_out-time_in)/caseNum);
//            }
//            record_mtquery.record_mtquery(Constant.MERKLEBPLUS,times_merklebplus);
//            record_mtquery.record_mtquery("mbp_mtquery",times_merklebplus_mtquery);

            //MST的构建与属性查询
            long t7 = System.currentTimeMillis();
            MST mst = new MST();
            long mstMatrixTime = mst.createMST(context);
            long t8 = System.currentTimeMillis();
//            long mstSkyline=mst.traceability_skyline(context,mst);
//            System.out.println("MST树Skyline查询："+mstSkyline);
            System.out.println("mst构建时间：" + (t8 - t7));

            long start_mst=System.currentTimeMillis();
            mst.traceability_skyline(context,mst);
            long end_mst=System.currentTimeMillis();
            long mstSize = memoryCalculator.getMSTSize(mst);
            record_construct.record_construct("mst",(t8 - t7),(end_mst-start_mst),(mstSize+merkleSize));

//            ArrayList<Long> times_mst=new ArrayList<>();
//            ArrayList<Long> times_mst_mtquery=new ArrayList<>();
//            for (int i=2; i<23; i+=2) {
//                long time31 = System.nanoTime();
//                Map<String, Map<String, String>> cases = queryCase.getPropertyQuerySingleBlockCase(caseNum, i);
//                List<Map<String,String>> case_all=queryCase.getPropertyQueryCase(caseNum,i);
//                for (Map.Entry<String, Map<String, String>> entry : cases.entrySet()) {
//                    String blockId = entry.getKey();
//                    Map<String, String> queries = entry.getValue();
//                    mst.propertyQueryBySingleBlock(queries, blockId);
//                }
//                long time32 = System.nanoTime();
//                long time_in = System.nanoTime();
//                for (Map<String, String> queries : case_all) {
//                    mst.propertyQueryByAllBlock(queries);
//                }
//                long time_out = System.nanoTime();
//                System.out.println("mst" + i + "属性查询时间为"+  (time32 - time31) / caseNum);
//                System.out.println("mst" + i + "mtquery查询时间为"+  (time_out - time_in) / caseNum);
//                times_mst.add((time32 - time31) / caseNum);
//                times_mst_mtquery.add((time_out - time_in) / caseNum);
//            }
//            record_mtquery.record_mtquery(Constant.MST,times_mst);
//            record_mtquery.record_mtquery("mst_mtquery",times_mst_mtquery);


            System.out.println("MeaT的一般skyline矩阵："+(end-start));
            System.out.println("TSKYLINE："+meatMatrixTime/1000000);
            System.out.println("Merkle的skyline矩阵："+(end_mt-start_mt));
            System.out.println("MBT的skyline矩阵："+(end_mbt-start_mbt));
            System.out.println("MST的skyline矩阵："+(end_mst-start_mst));

        }
        System.gc();
        record_mtquery.save_mtquery();
        record_construct.save_construct();
    }

}
