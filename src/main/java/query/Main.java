package query;

import data.Context;
import data.DataProcessor;
import data.KaggleDataProcessor;
import meat.Meat;
import merkle.Merkle;
import merklebplus.MerkleBPlus;
import mst.MST;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 加载数据
        String dataPath = "./2500.csv";
        DataProcessor dataProcessor = new KaggleDataProcessor();
        Context context = dataProcessor.getDataContext(dataPath);

        // 生成索引
        Meat meat = new Meat();
        meat.createMeat(context);

        Merkle merkle = new Merkle();
        merkle.createMerkle(context);

        MerkleBPlus merkleBPlus = new MerkleBPlus();
        merkleBPlus.createMerkleBPlus(context);

        MST mst = new MST();
        mst.createMST(context);

        // 查询测试
        QueryCase queryCase = new QueryCase(context);
        List<String> case1 = queryCase.getSingleTransactionQueryCase(100);
        for (String txId : case1) {
            meat.singleTransactionQuery(txId);
        }

    }
}
