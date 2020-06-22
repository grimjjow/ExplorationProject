package Group10.Pathfinding.Graph;

import java.util.HashMap;

;

public class Graph {

    HashMap<String, GraphNode> nodes;

    int minX = 0;
    int minY = 0;
    int maxX = 0;
    int maxY = 0;

    public Graph() {
        this.nodes = new HashMap<String, GraphNode>();
    }

    public static Integer[] getCoordinate(int degrees, Integer[] currentPosition) {

        int x = currentPosition[0];
        int y = currentPosition[1];

        switch (degrees) {
            case 0:
                return new Integer[]{x, y + 1};
            case 45:
                return new Integer[]{x - 1, y + 1};
            case 90:
                return new Integer[]{x - 1, y};
            case 135:
                return new Integer[]{x - 1, y - 1};
            case 180:
                return new Integer[]{x, y - 1};
            case 225:
                return new Integer[]{x + 1, y - 1};
            case 270:
                return new Integer[]{x + 1, y};
            case 315:
                return new Integer[]{x + 1, y + 1};
            default:
                return null;
        }
    }

    public GraphNode getVertice(Integer[] position) {
        String key = position[0] + " " + position[1];
        return nodes.get(key);
    }

    public void addVertice(GraphNode node) {

        checkAddedNode(node);

        Integer[] xy = node.getCoordinate();
        if (xy[0] < minX) {
            minX = xy[0];
        }
        if (xy[0] > maxX) {
            maxX = xy[0];
        }
        if (xy[1] < minY) {
            minY = xy[1];
        }
        if (xy[1] > maxY) {
            maxY = xy[1];
        }

        Integer[] position = node.getCoordinate();
        String key = position[0] + " " + position[1];
        this.nodes.put(key, node);
    }

    public boolean checkNode(Integer[] coordinate) {

        String key = coordinate[0] + " " + coordinate[1];
        return nodes.containsKey(key);
    }

    public void unMark() {
        for (String key : nodes.keySet()) {
            GraphNode node = nodes.get(key);
            node.setMarked(false);
            node.setParent(null);
        }
    }

    private void checkAddedNode(GraphNode node) {
        for (int i = 0; i <= 315; i += 45) {
            Integer[] position = getCoordinate(i, node.coordinate);
            if (checkNode(position)) {
                node.addEdge(getVertice(position), i);
            }

        }

    }

    public void detected() {
        for (String key : nodes.keySet()) {
            if (nodes.get(key).getType() == NodeType.SEEN) {
                nodes.get(key).setType(NodeType.EMPTY);
            }
        }
    }
}
