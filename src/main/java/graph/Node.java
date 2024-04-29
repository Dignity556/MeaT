package graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Node {
    private String nodeId;
    private String attribute;
    private List<Node> neighbors;

    public Node(String id) {
        this.nodeId = id;
        neighbors = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        Node n = (Node)obj;
        return this.nodeId.equals(n.getNodeId());
    }

    @Override
    public int hashCode() {
        return this.nodeId.hashCode();
    }
}
