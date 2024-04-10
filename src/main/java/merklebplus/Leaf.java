package merklebplus;

import blockchain.Block;
import blockchain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Leaf {
    private Transaction transaction;
    private byte[] hashId;
    private Leaf father;
    private Leaf left;
    private Leaf right;
    private Block block;
    private String id;

    public Leaf(Transaction transaction) {
        this.transaction = transaction;
        try {
            this.hashId = calculateSHA256(transaction.getId().toString());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        this.block = transaction.getBeLongBlock();
    }

    public static byte[] calculateSHA256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    }
}
