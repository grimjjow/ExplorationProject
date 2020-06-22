package Group10.Pathfinding.Grid;

public class GridNode {

	private int X;
	private int Y;
	private int Type = 0; //0 = normal, 1 = spawn, 2 = wall
	
	public GridNode NeighborTop;
	public GridNode NeighborTopRight;
	public GridNode NeighborRight;
	public GridNode NeighborBottomRight;
	public GridNode NeighborBottom;
	public GridNode NeighborBottomLeft;
	public GridNode NeighborLeft;
	public GridNode NeighborTopLeft;
	
	public double gCost; //Distance from Start to This
	public double hCost; //Distance from This to End
	public double fCost; //gCost + hCost
	
	public GridNode parentNode;
	
	public GridNode(int X, int Y) {
		this.X = X;
		this.Y = Y;
	}
	
	public void calcFCost() //Method to calculate fCost
    {
        fCost = gCost + hCost;
    }

	public GridNode getNode() {
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
	
	public GridNode[] getNeighbors() {
		GridNode[] array = new GridNode[8];
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
