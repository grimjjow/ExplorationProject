package Group10.Agents.AStar;

import java.util.ArrayList;
import java.util.Collections;

public class AStarAlgo {
	
	private ArrayList<Node> open; //Contains Nodes that have calculated FCost but are not checked yet
	private ArrayList<Node> closed; //Contains Nodes that have been checked

	//Costs for each step
    private final int MOVE_DIAGONAL_COST = 14; //sqrt(200) = approx. 14
    private final int MOVE_STRAIGHT_COST = 10;
    
    //Pointer pointing to the node its currently on
    public Node current;
	
	public AStarAlgo() {
		
	}
	
	public ArrayList<Node> findPath(Node startNode, Node endNode, Grid grid) {
		
		//Initializing open and closed lists
        open = new ArrayList<Node>(); //open contains only startNode
        open.add(startNode);
        closed = new ArrayList<Node>(); //closed is empty
        
        for(int i=0;i<grid.getWidth();i++) {
        	for(int j=0;j<grid.getHeight();j++) {
        		Node tempNode = grid.getNode(i, j);
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
		while(open.size()>0) {
			current = getSmallestFCost();
			if (current.getX() == endNode.getX() && current.getY() == endNode.getY())
            {
                return calcPath(endNode); //If current == endNode then return path
            }
			//Removing current from open and adding it to closed
            open.remove(current);
            closed.add(current);
            
            for(Node neighbor : current.getNeighbors()) {
            	if(neighbor != null) {
	            	 if (closed.contains(neighbor) || neighbor.getType() == 2)
	                 {
	                     continue; //If neighbor in closed or not walkable, skip it
	                 }
	            	 else if ((calculateDistanceCost(current, neighbor) + current.gCost) < neighbor.gCost) { //If new gCost is lower than old gCost{
	            		//Replace costs
	                     neighbor.gCost = calculateDistanceCost(current, neighbor) + current.gCost;
	                     neighbor.hCost = calculateDistanceCost(neighbor, endNode);
	                     neighbor.calcFCost();
	
	                     //Set parent node
	                     neighbor.parentNode = current;
	            	 }
	            	 //If neighbor is not in open, add it to open
	                 if (!open.contains(neighbor))
	                 {
	                     open.add(neighbor);
	                 }
            	}
            }
		}
		//If this is reached, nothing was returned meaning no path was found
		//System.out.println("No path found");
		return null;
	}
	
	private ArrayList<Node> calcPath(Node endNode){ //Calculate final path
		ArrayList<Node> path = new ArrayList<Node>(); //List containing final path
		path.add(endNode);
        Node currentNode = endNode;
        
        while (currentNode.parentNode != null)
        {
            path.add(currentNode.parentNode);
            currentNode = currentNode.parentNode;
        }
        Collections.reverse(path); //Reversing list
		return path;
	}
	
	public int calculateDistanceCost(Node a, Node b) //Calculate distance between two Nodes
    {
        int xDistance = Math.abs(a.getX() - b.getX());
        int yDistance = Math.abs(a.getY() - b.getY());
        int remaining = Math.abs(xDistance - yDistance);
        return MOVE_DIAGONAL_COST * Math.min(xDistance, yDistance) + MOVE_STRAIGHT_COST * remaining; //Walk straight as much as it can and then walk diagonally
    }
	
	public Node getSmallestFCost() { //Returns node with the smallest fCost
		Node smallest = open.get(0);
		for(Node N : open) {
			if(N.fCost < smallest.fCost) {
				smallest = N;
			}
		}
		return smallest;
	}
	
}
