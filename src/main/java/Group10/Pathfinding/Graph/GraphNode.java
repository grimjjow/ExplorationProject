package Group10.Pathfinding.Graph;

import Interop.Geometry.Point;

import java.util.ArrayList;

//this is an example of node class that we need for rrt

public class GraphNode {

    public boolean marked = false;
    NodeType type;

    ArrayList<GraphEdge> edges;
    Point center;
    double radius;
    Integer coordinate[];
    GraphNode parent = null;

    public GraphNode(NodeType none, Point center, double radius, Integer[] coordinate) {
        this.center = center;
        this.radius = radius;
        this.edges = new ArrayList<GraphEdge>();
        this.coordinate = coordinate;
    }

    public ArrayList<GraphEdge> getEdges() {
        return edges;
    }

    public void addEdge(GraphNode endNode, int degrees) {
        GraphEdge edge = new GraphEdge(this, endNode, degrees);
        this.edges.add(edge);
        if (degrees >= 180) {
            degrees = degrees - 180;
        } else {
            degrees = degrees + 180;
        }
        GraphEdge backwards = new GraphEdge(endNode, this, degrees);
        endNode.edges.add(backwards);
    }

    public Point getCenter() {
        return center;
    }

    public Integer[] getCoordinate() {
        return coordinate;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public GraphNode getParent() {
        return parent;
    }

    public void setParent(GraphNode parent) {
        this.parent = parent;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public String toString() {
        return "[" + this.coordinate[0] + "|" + this.coordinate[1] + "]";
    }
}
