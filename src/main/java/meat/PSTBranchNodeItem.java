package meat;

import blockchain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PSTBranchNodeItem {
    private String propertyItem;
    private ArrayList<Transaction> preTransactions = new ArrayList<>();
    private PSTLeafNode nextLeaf;
    private PSTExtensionNode nextExtension;
    private String id;
}
