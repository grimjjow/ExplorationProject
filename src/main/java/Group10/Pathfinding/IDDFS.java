package Group10.Pathfinding;

import Group10.Pathfinding.Graph.GraphEdge;
import Group10.Pathfinding.Graph.GraphNode;
import Group10.Pathfinding.Graph.NodeType;

import java.util.*;

public class IDDFS {

    public static boolean checkDFSVertice(GraphNode v) {
        if ((v.getType() != NodeType.WALL) && (v.getType() != NodeType.TELEPORT)) {
            for (GraphEdge e : v.getEdges()) {
                GraphNode w = e.getEndNode();
                if (!((w.getType() != NodeType.WALL) && (w.getType() != NodeType.TELEPORT))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static Stack<GraphNode> findDFSPath(GraphNode start, NodeType type) {

        Stack<GraphNode> stack = new Stack<GraphNode>();
        start.marked = true;
        stack.add(start);
        while (stack.size() != 0) {
            GraphNode v = stack.pop();
            if (v.isMarked()) {
                continue;
            }
            if (v.getType() == type) {
                while (v != null) {
                    stack.push(v);
                    v = v.getParent();
                }
                return stack;
            }
            for (GraphEdge graphEdge : v.getEdges()) {
                if (!graphEdge.endNode.isMarked()) {
                    GraphNode w = graphEdge.endNode;
                    if (checkDFSVertice(w)) {
                        w.setMarked(true);
                        w.setParent(v);
                        stack.add(w);
                    }
                }
            }
        }
        return null;
    }

    public static Stack<GraphNode> findNonCompleteVertice(GraphNode start) {
        Queue<GraphNode> queue = new LinkedList<GraphNode>();
        start.marked = true;
        queue.offer(start);
        while (queue.size() != 0) {
            GraphNode v = queue.poll();
            if (v.getEdges().size() < 8) {
                Stack<GraphNode> s = new Stack<GraphNode>();
                while (v != null) {
                    s.push(v);
                    v = v.getParent();
                }
                return s;
            }
            for (GraphEdge graphEdge : v.getEdges()) {
                if (!graphEdge.endNode.isMarked()) {
                    GraphNode w = graphEdge.endNode;
                    if (checkDFSVertice(w)) {
                        w.setMarked(true);
                        w.setParent(v);
                        queue.offer(w);
                    }
                }
            }
        }
        return null;
    }

    public static List<GraphNode> getReachableNodes(GraphNode start) {

        ArrayList<GraphNode> vertices = new ArrayList<GraphNode>();

        Queue<GraphNode> queue = new LinkedList<GraphNode>();
        start.marked = true;
        queue.offer(start);
        while (queue.size() != 0) {
            GraphNode v = queue.poll();
            for (GraphEdge graphEdge : v.getEdges()) {
                if (!graphEdge.endNode.isMarked()) {
                    GraphNode w = graphEdge.endNode;
                    if (checkDFSVertice(w)) {
                        w.setMarked(true);
                        vertices.add(w);
                        queue.offer(w);
                    }
                }
            }
        }

        return vertices;
    }

    public GraphNode IDS(GraphNode start) {
        // loops through until a goal node is found
        for (int depth = 0; depth < Integer.MAX_VALUE; depth++) {
            GraphNode found = DLS(start, depth);
            if (found != null) {
                return found;
            } else {
                break;
            }
        }
        // this will never be reached as it
        // loops forever until goal is found
        return null;
    }

    public GraphNode DLS(GraphNode current, int depth) {

        if (depth > 0) {
            for (GraphNode child : getReachableNodes(current)) {
                GraphNode found = DLS(child, depth - 1);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
