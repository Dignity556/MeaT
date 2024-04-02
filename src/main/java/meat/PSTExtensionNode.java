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
}
