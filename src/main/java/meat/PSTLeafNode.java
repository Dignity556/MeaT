package meat;

import blockchain.Transaction;
import graph.Edge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PSTLeafNode {
    private String id;
    private Transaction transaction;
    private ArrayList<Edge> edges = new ArrayList<>();
    private PSTBranchNodeItem preBranch;
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
}
