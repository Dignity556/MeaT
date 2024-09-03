package mst;

import data.Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * 顶级类，包含创建和查询方法
 * mst在整个链上创建索引，因此具体实现与其他结构有所不同
 */
public class MST {
    private Context context;

    private MSTTree mstTree;
    public void createMST(Context context) {
        this.context = context;
        MSTTree mstTree = new MSTTree();
        mstTree.createMST(context);
        this.mstTree = mstTree;
        System.out.println("mst");
    }



}
