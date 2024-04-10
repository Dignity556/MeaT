package query;

import blockchain.Transaction;
import data.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QueryCase {
    private Context context;

    public QueryCase(Context context) {
        this.context = context;
    }
    public List<String> getSingleTransactionQueryCase(int caseNum) {
        List<String> txIds = new ArrayList<>();
        List<Transaction> transactions = context.getTransactions();
        Random random = new Random();
        for (int i = 0; i < caseNum; i++) {
            int index = random.nextInt(transactions.size());
            txIds.add(transactions.get(index).getId());
        }
        return txIds;
    }
}
