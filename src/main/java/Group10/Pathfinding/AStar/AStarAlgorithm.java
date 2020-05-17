package Group10.Pathfinding.AStar;

import Group10.Pathfinding.AbstractNode;
import Group10.Pathfinding.Network;

import java.util.ArrayList;


public class AStarAlgorithm {

    private Network network;
    private ArrayList<AbstractNode> path;

    private AbstractNode start;
    private AbstractNode end;

    private ArrayList<AbstractNode> openList;
    private ArrayList<AbstractNode> closedList;

    public AStarAlgorithm(Network network) {
        this.network = network;
    }

    public void solve() {

        if (start == null && end == null) {
            return;
        }

        if (start.equals(end)) {
            this.path = new ArrayList<>();
            return;
        }

        this.path = new ArrayList<>();

        this.openList = new ArrayList<>();
        this.closedList = new ArrayList<>();

        this.openList.add(start);

        while (!openList.isEmpty()) {
            AbstractNode current = getLowestF();

            if (current.equals(end)) {
                retracePath(current);
                break;
            }

            openList.remove(current);
            closedList.add(current);

            for (AbstractNode n : current.getNeighbours()) {

                if (closedList.contains(n) || !n.isValid()) {
                    continue;
                }

                double tempScore = current.getCost() + current.distanceTo(n);

                if (openList.contains(n)) {
                    if (tempScore < n.getCost()) {
                        n.setCost(tempScore);
                        n.setParent(current);
                    }
                } else {
                    n.setCost(tempScore);
                    openList.add(n);
                    n.setParent(current);
                }

                n.setHeuristic(n.heuristic(end));
                n.setFunction(n.getCost() + n.getHeuristic());

            }

        }
    }

    public void reset() {
        this.start = null;
        this.end = null;
        this.path = null;
        this.openList = null;
        this.closedList = null;
        for (AbstractNode n : network.getNodes()) {
            n.setValid(true);
        }
    }

    private void retracePath(AbstractNode current) {
        AbstractNode temp = current;
        this.path.add(current);

        while (temp.getParent() != null) {
            this.path.add(temp.getParent());
            temp = temp.getParent();
        }

        this.path.add(start);
    }

    private AbstractNode getLowestF() {
        AbstractNode lowest = openList.get(0);
        for (AbstractNode n : openList) {
            if (n.getFunction()< lowest.getFunction()) {
                lowest = n;
            }
        }
        return lowest;
    }

    public Network getNetwork() {
        return network;
    }

    public ArrayList<AbstractNode> getPath() {
        return path;
    }

    public AbstractNode getStart() {
        return start;
    }

    public AbstractNode getEnd() {
        return end;
    }

    public void setStart(AbstractNode start) {
        this.start = start;
    }

    public void setEnd(AbstractNode end) {
        this.end = end;
    }

}