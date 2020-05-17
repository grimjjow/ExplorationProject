package Pathfinding;

import java.util.ArrayList;

public abstract class AbstractNode {

    private AbstractNode parent;
    private ArrayList<AbstractNode> neighbours;
    private double cost, heuristic, function;
    private boolean valid;

    public abstract void calculateNeighbours(Network network);

    public abstract double distanceTo(AbstractNode dest);

    public abstract double heuristic(AbstractNode dest);

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
    }

    public double getFunction() {
        return function;
    }

    public void setFunction(double function) {
        this.function = function;
    }



    public ArrayList<AbstractNode> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(ArrayList<AbstractNode> neighbours) {
        this.neighbours = neighbours;
    }

    public AbstractNode getParent() {
        return parent;
    }

    public void setParent(AbstractNode parent) {
        this.parent = parent;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void reverseValidation(){
        valid = !valid;
    }

}