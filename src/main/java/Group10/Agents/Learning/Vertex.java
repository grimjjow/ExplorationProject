package Group10.Agents.Learning;

import Group10.Pathfinding.AbstractNode;
import Group10.Pathfinding.Network;
import Interop.Geometry.Point;

public class Vertex extends AbstractNode {

    Point point;
    private double cost, heuristic, function;
    private boolean isTarget;

    Vertex(Point point) {
        this.point = point;
    }

    @Override
    public void calculateNeighbours(Network network) {

    }

    @Override
    public double distanceTo(AbstractNode dest) {
        return 0;
    }

    @Override
    public double heuristic(AbstractNode dest) {
        return 0;
    }

    // add overriding equals and hashCode methods

    // Prints the vertex.
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();


        builder.append("x: " + this.getPoint().getX() + ": ");

        builder.append("y: " + this.getPoint().getX() + " ");

        builder.append("\n");


        return (builder.toString());
    }

    public Point getPoint() {
        return point;
    }
}
