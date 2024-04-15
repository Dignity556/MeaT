package blockchain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import meat.MerkleGraphTree;
import merkle.MerkleTree;
import merklebplus.MerkleBPlusTree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Block implements Serializable {
    private String id;
    private List<Transaction> transactions = new ArrayList<>();
    private byte[] hashRoot;

    // 保存不同的索引结构
    private MerkleGraphTree mgt;
    private MerkleTree merkleTree;
    private MerkleBPlusTree merkleBPlusTree;

    public Block(String id) {
        this.id = id;
    }
}
