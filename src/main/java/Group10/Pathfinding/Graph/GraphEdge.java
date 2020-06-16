package Group10.Pathfinding.Graph;

//this is an example of edge class that we need for rrt

public class GraphEdge {

    GraphNode n1;
    GraphNode n2;
    int degrees;

    public GraphEdge(GraphNode n1, GraphNode n2, int degrees) {
        this.n1 = n1;
        this.n2 = n2;
        this.degrees = degrees;
    }

    public int getDegrees() {
        return degrees;
    }
    public GraphNode getN1() {
        return n1;
    }
    public void setN1(GraphNode n1) {
        this.n1 = n1;
    }
    public GraphNode getN2() {
        return n2;
    }
    public void setN2(GraphNode n2) {
        this.n2 = n2;
    }
}
