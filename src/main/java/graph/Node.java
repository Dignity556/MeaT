package graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Node {
    private String nodeId;
    private String attribute;

    public Node(String id) {
        this.nodeId = id;
    }

    @Override
    public boolean equals(Object obj) {
        Node n = (Node)obj;
        return this.nodeId.equals(n.getNodeId());
    }
}
