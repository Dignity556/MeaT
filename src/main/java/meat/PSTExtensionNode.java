package meat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PSTExtensionNode {
    private String id;
    private PSTBranchNodeItem preItem;
    private MerkleGraphTree rootMGT; // 根extension与mgt建立联系
    private String property;
    private PSTBranchNode nextItem;

    public void setNextBranch(PSTBranchNode branchNode){
        this.nextItem = branchNode;
        branchNode.setPrevious(this);
    }

    public void setPreBranchItem(PSTBranchNodeItem branchNodeItem){
        this.preItem = branchNodeItem;
        branchNodeItem.setNextExtension(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PSTBranchNodeItem getPreItem() {
        return preItem;
    }

    public void setPreItem(PSTBranchNodeItem preItem) {
        this.preItem = preItem;
    }

    public MerkleGraphTree getRootMGT() {
        return rootMGT;
    }

    public void setRootMGT(MerkleGraphTree rootMGT) {
        this.rootMGT = rootMGT;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public PSTBranchNode getNextItem() {
        return nextItem;
    }

    public void setNextItem(PSTBranchNode nextItem) {
        this.nextItem = nextItem;
    }
}
