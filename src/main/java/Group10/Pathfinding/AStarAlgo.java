package Group10.Pathfinding;

import Group10.Pathfinding.Grid.Grid;
import Group10.Pathfinding.Grid.GridNode;

import java.util.ArrayList;
import java.util.Collections;

public class AStarAlgo {

    //Costs for each step
    private final int MOVE_DIAGONAL_COST = 14; //sqrt(200) = approx. 14
    private final int MOVE_STRAIGHT_COST = 10;
    //Pointer pointing to the node its currently on
    public GridNode current;
    private ArrayList<GridNode> open; //Contains Nodes that have calculated FCost but are not checked yet
    private ArrayList<GridNode> closed; //Contains Nodes that have been checked

    public AStarAlgo() {

    }

    public ArrayList<GridNode> findPath(GridNode startNode, GridNode endNode, Grid grid) {

        //Initializing open and closed lists
        open = new ArrayList<GridNode>(); //open contains only startNode
        open.add(startNode);
        closed = new ArrayList<GridNode>(); //closed is empty

        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                GridNode tempNode = grid.getNode(i, j);
                tempNode.gCost = Integer.MAX_VALUE;
                tempNode.calcFCost();
                tempNode.parentNode = null;
            }
        }

        //calculating costs of startNode
        startNode.gCost = 0;
        startNode.hCost = calculateDistanceCost(startNode, endNode);
        startNode.calcFCost();

        //While open is not empty
        while (open.size() > 0) {
            current = getSmallestFCost();
            if (current.getX() == endNode.getX() && current.getY() == endNode.getY()) {
                return calcPath(endNode); //If current == endNode then return path
            }
            //Removing current from open and adding it to closed
            open.remove(current);
            closed.add(current);

            for (GridNode neighbor : current.getNeighbors()) {
                if (neighbor != null) {
                    if (closed.contains(neighbor) || neighbor.getType() == 2) {
                        continue; //If neighbor in closed or not walkable, skip it
                    } else if ((calculateDistanceCost(current, neighbor) + current.gCost) < neighbor.gCost) { //If new gCost is lower than old gCost{
                        //Replace costs
                        neighbor.gCost = calculateDistanceCost(current, neighbor) + current.gCost;
                        neighbor.hCost = calculateDistanceCost(neighbor, endNode);
                        neighbor.calcFCost();

                        //Set parent node
                        neighbor.parentNode = current;
                    }
                    //If neighbor is not in open, add it to open
                    if (!open.contains(neighbor)) {
                        open.add(neighbor);
                    }
                }
            }
        }
        //If this is reached, nothing was returned meaning no path was found
        System.out.println("No path found");
        return null;
    }

    private ArrayList<GridNode> calcPath(GridNode endNode) { //Calculate final path
        ArrayList<GridNode> path = new ArrayList<GridNode>(); //List containing final path
        path.add(endNode);
        GridNode currentNode = endNode;

        while (currentNode.parentNode != null) {
            path.add(currentNode.parentNode);
            currentNode = currentNode.parentNode;
        }
        Collections.reverse(path); //Reversing list
        return path;
    }

    public int calculateDistanceCost(GridNode a, GridNode b) //Calculate distance between two Nodes
    {
        int xDistance = Math.abs(a.getX() - b.getX());
        int yDistance = Math.abs(a.getY() - b.getY());
        int remaining = Math.abs(xDistance - yDistance);
        return MOVE_DIAGONAL_COST * Math.min(xDistance, yDistance) + MOVE_STRAIGHT_COST * remaining; //Walk straight as much as it can and then walk diagonally
    }

    public GridNode getSmallestFCost() { //Returns node with the smallest fCost
        GridNode smallest = open.get(0);
        for (GridNode N : open) {
            if (N.fCost < smallest.fCost) {
                smallest = N;
            }
        }
        return smallest;
    }

}
