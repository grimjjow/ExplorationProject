package Group10.Pathfinding.Graph;

import java.util.ArrayList;
import Interop.Geometry.Point;

//this is an example of node class that we need for rrt

public class GraphNode {

    ArrayList<GraphEdge> edges;
    Point center;
    double radius;
    Integer coordinate[];
    boolean marked = false;
    GraphNode parent = null;

    public GraphNode(Point center, double radius, Integer[] coordinate) {
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
        if(degrees >= 180) {
            degrees = degrees - 180;
        }
        else {
            degrees = degrees + 180;
        }
        GraphEdge reverseedge = new GraphEdge(endNode, this, degrees);
        endNode.edges.add(reverseedge);
    }

    public boolean isInside(Point point) {
        double xmin = center.getX() - radius;
        double xmax = center.getX() + radius;
        double ymin = center.getY() - radius;
        double ymax = center.getY() + radius;
        double x = point.getX();
        double y = point.getY();
        if((xmin <= x) && (xmax >= x)) {
            if((ymin <= y) && (ymax >= y)) {
                return true;
            }
            else return false;
        }
        else return false;
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

    public String toString() {
        return "[" + this.coordinate[0] + "|" + this.coordinate[1] + "]";
    }
}
