package meat;

import graph.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraphNodeLinkItem {
    private Node node; // 关联的具体node
    private String id;

    // 连接的下层mgt
    private GraphLeaf root;

    public GraphNodeLinkItem(String id, Node node, GraphLeaf leaf) {
        this.id = id;
        this.node = node;
        this.root = leaf;
    }
}
