package blockchain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import meat.MerkleGraphTree;
import merkle.MerkleTree;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Block {
    private String id;
    private List<Transaction> transactions = new ArrayList<>();
    private byte[] hashRoot;

    private MerkleGraphTree mgt;
    private MerkleTree merkleTree;

    public Block(String id) {
        this.id = id;
    }
}
