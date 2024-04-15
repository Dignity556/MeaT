package meat;

import graph.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GraphNodeLink implements Serializable {
    private Map<Node, GraphNodeLinkItem> items = new HashMap<>();
}
