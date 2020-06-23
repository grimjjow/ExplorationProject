package Group10.Agents.AStar;

public class Node {
	private int X;
	private int Y;
	private int Type = 0; //-1 = emptyExplored, 0 = normal, 1 = spawn, 2 = wall, 3 = door, 4 = window, 5 = sentry,
						  //6 = shaded area, 7 = teleport, 8 = target
	private double distance = 1000; //To center of node
	
	public Node NeighborTop;
	public Node NeighborTopRight;
	public Node NeighborRight;
	public Node NeighborBottomRight;
	public Node NeighborBottom;
	public Node NeighborBottomLeft;
	public Node NeighborLeft;
	public Node NeighborTopLeft;
	
	public double gCost; //Distance from Start to This
	public double hCost; //Distance from This to End
	public double fCost; //gCost + hCost
	
	public Node parentNode;
	
	public int i;
	public int j;
	
	public Node(int X, int Y) {
		this.X = X;
		this.Y = Y;
	}
	
	public void calcFCost() //Method to calculate fCost
    {
        fCost = gCost + hCost;
    }

	public Node getNode() {
		return this;
	}
	
	public void setCoord(int X, int Y) {
		this.X = X;
		this.Y = Y;
	}
	
	public int getX() {
		return X;
	}
	
	public int getY() {
		return Y;
	}
	
	public void setType(int Type) {
		this.Type = Type;
	}
	
	public int getType() {
		return Type;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public Node[] getNeighbors() {
		Node[] array = new Node[8];
		array[0] = NeighborTop;
		array[1] = NeighborTopRight;
		array[2] = NeighborRight;
		array[3] = NeighborBottomRight;
		array[4] = NeighborBottom;
		array[5] = NeighborBottomLeft;
		array[6] = NeighborLeft;
		array[7] = NeighborTopLeft;
		return array;
	}
}
