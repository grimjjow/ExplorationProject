package Group10.Pathfinding.Grid;

import java.util.List;

import Interop.Geometry.Direction;
import Interop.Geometry.Point;

public class Grid {
	public final static int GRIDHEIGHT = 400;
	public final static int GRIDWIDTH = 400;
	
	private Point spawnLocation;
	private Point myLocation;
	private Direction myDirection;
	private int[] myGridLocation = new int[2];
	
	private GridNode[][] nodeGrid = new GridNode[GRIDWIDTH][GRIDHEIGHT];
	
	public Grid(Point spawnLocation) {
		this.spawnLocation = spawnLocation;
		myLocation = spawnLocation;
		myDirection = Direction.fromRadians(0);
		createGrid();
	}
	
	private void createGrid() {
		for(int i=0;i<GRIDWIDTH;i++) {
			for(int j=0;j<GRIDHEIGHT;j++) {
				GridNode tempNode = new GridNode(i,j);
				
				if(i==GRIDWIDTH/2 && j==GRIDHEIGHT/2) {
					tempNode.setType(1);
				}
				else {
					tempNode.setType(0);
				}

				nodeGrid[i][j] = tempNode;
			}
		}
		
		for(int i=0;i<nodeGrid.length;i++) {
			for(int j=0;j<nodeGrid[0].length;j++) {
				if(i>0 && j>0 && i<GRIDWIDTH-1 && j<GRIDHEIGHT-1) {
					nodeGrid[i][j].NeighborTop = nodeGrid[i][j+1];
					nodeGrid[i][j].NeighborTopRight = nodeGrid[i+1][j+1];
					nodeGrid[i][j].NeighborRight = nodeGrid[i+1][j];
					nodeGrid[i][j].NeighborBottomRight = nodeGrid[i+1][j-1];
					nodeGrid[i][j].NeighborBottom = nodeGrid[i][j-1];
					nodeGrid[i][j].NeighborBottomLeft = nodeGrid[i-1][j-1];
					nodeGrid[i][j].NeighborLeft = nodeGrid[i-1][j];
					nodeGrid[i][j].NeighborTopLeft = nodeGrid[i-1][j+1];
				}
				else if(i==0 && j>0 && j<GRIDHEIGHT-1) {
					nodeGrid[i][j].NeighborTop = nodeGrid[i][j+1];
					nodeGrid[i][j].NeighborTopRight = nodeGrid[i+1][j+1];
					nodeGrid[i][j].NeighborRight = nodeGrid[i+1][j];
					nodeGrid[i][j].NeighborBottomRight = nodeGrid[i+1][j-1];
					nodeGrid[i][j].NeighborBottom = nodeGrid[i][j-1];
				}
				else if(j==0 && i>0 && i<GRIDWIDTH-1) {
					nodeGrid[i][j].NeighborTop = nodeGrid[i][j+1];
					nodeGrid[i][j].NeighborTopRight = nodeGrid[i+1][j+1];
					nodeGrid[i][j].NeighborRight = nodeGrid[i+1][j];
					nodeGrid[i][j].NeighborLeft = nodeGrid[i-1][j];
					nodeGrid[i][j].NeighborTopLeft = nodeGrid[i-1][j+1];
				}
				else if(i==GRIDWIDTH-1 && j>0 && j<GRIDHEIGHT-1) {
					nodeGrid[i][j].NeighborTop = nodeGrid[i][j+1];
					nodeGrid[i][j].NeighborBottom = nodeGrid[i][j-1];
					nodeGrid[i][j].NeighborBottomLeft = nodeGrid[i-1][j-1];
					nodeGrid[i][j].NeighborLeft = nodeGrid[i-1][j];
					nodeGrid[i][j].NeighborTopLeft = nodeGrid[i-1][j+1];
				}
				else if(j==GRIDHEIGHT-1 && i>0 && i<GRIDWIDTH-1) {
					nodeGrid[i][j].NeighborRight = nodeGrid[i+1][j];
					nodeGrid[i][j].NeighborBottomRight = nodeGrid[i+1][j-1];
					nodeGrid[i][j].NeighborBottom = nodeGrid[i][j-1];
					nodeGrid[i][j].NeighborBottomLeft = nodeGrid[i-1][j-1];
					nodeGrid[i][j].NeighborLeft = nodeGrid[i-1][j];
				}
				else if(i==0 && j==0) {
					nodeGrid[i][j].NeighborTop = nodeGrid[i][j+1];
					nodeGrid[i][j].NeighborTopRight = nodeGrid[i+1][j+1];
					nodeGrid[i][j].NeighborRight = nodeGrid[i+1][j];
				}
				else if(i==0 && j==GRIDHEIGHT-1) {
					nodeGrid[i][j].NeighborRight = nodeGrid[i+1][j];
					nodeGrid[i][j].NeighborBottomRight = nodeGrid[i+1][j-1];
					nodeGrid[i][j].NeighborBottom = nodeGrid[i][j-1];
				}
				else if(i==GRIDWIDTH-1 && j==0) {
					nodeGrid[i][j].NeighborTop = nodeGrid[i][j+1];
					nodeGrid[i][j].NeighborLeft = nodeGrid[i-1][j];
					nodeGrid[i][j].NeighborTopLeft = nodeGrid[i-1][j+1];
				}
				else if(i==GRIDWIDTH-1 && j==GRIDHEIGHT-1) {
					nodeGrid[i][j].NeighborBottom = nodeGrid[i][j-1];
					nodeGrid[i][j].NeighborBottomLeft = nodeGrid[i-1][j-1];
					nodeGrid[i][j].NeighborLeft = nodeGrid[i-1][j];
				}
			}
		}
	}
	
	public int getWidth() {
		return GRIDWIDTH;
	}
	
	public int getHeight() {
		return GRIDHEIGHT;
	}
	
	public void setNodeGrid(int i, int j, GridNode value) {
		nodeGrid[i][j] = value;
	}
	
	public GridNode[][] getNodeGrid() {
		return nodeGrid;
	}
	
	public GridNode getNode(int i, int j) {
		return nodeGrid[i][j];
	}
	
	public void updateMyLocation(double move) {
		int quadrant;
		if(myDirection.getRadians()>0 && myDirection.getRadians()<=Math.PI/2) {
			quadrant = 1;
		}
		else if(myDirection.getRadians()>Math.PI/2 && myDirection.getRadians()<=Math.PI) {
			quadrant = 2;
		}
		else if(myDirection.getRadians()>Math.PI && myDirection.getRadians()<=(Math.PI*(3.0/2.0))) {
			quadrant = 3;
		}
		else if(myDirection.getRadians()>(Math.PI*(3.0/2.0)) && myDirection.getRadians()<=Math.PI*2) {
			quadrant = 4;
		}
		else {
			quadrant = 0;
		}
		
		//System.out.println(quadrant);
		
		switch(quadrant) {
		case 1:
			myLocation = new Point(myLocation.getX()-Math.abs((Math.cos(((Math.PI/2)-myDirection.getRadians()))*move)),
					myLocation.getY()-Math.abs((Math.sin(((Math.PI/2)-myDirection.getRadians()))*move)));
			break;
		case 2:
			myLocation = new Point(myLocation.getX()-Math.abs((Math.cos(((Math.PI/2)-((Math.PI)-myDirection.getRadians())))*move)),
					myLocation.getY()+Math.abs((Math.sin(((Math.PI/2)-((Math.PI)-myDirection.getRadians())))*move)));
			//System.out.println(myLocation + "  " + Math.abs((Math.cos(((Math.PI/2)-(Math.PI)-myDirection.getRadians()))*move)));
			break;
		case 3:
			myLocation = new Point(myLocation.getX()+Math.abs((Math.cos((Math.PI*(3.0/2.0))-myDirection.getRadians())*move)),
					myLocation.getY()+Math.abs((Math.sin((Math.PI*(3.0/2.0))-myDirection.getRadians())*move)));
			break;
		case 4:
			myLocation = new Point(myLocation.getX()+Math.abs((Math.cos(((Math.PI/2)-((Math.PI*2)-myDirection.getRadians())))*move)),
					myLocation.getY()-Math.abs((Math.sin(((Math.PI/2)-((Math.PI*2)-myDirection.getRadians())))*move)));
			break;
		case 0:
			break;
		}
		
		//myLocation = new Point(myLocation.getX()+(((Math.PI/2) - Math.cos(myDirection.getRadians()))*move),
		//		myLocation.getY()+(((Math.PI/2) - Math.sin(myDirection.getRadians()))*move));
		System.out.println(Math.round(myLocation.getX()) + "  " + Math.round(myLocation.getY()));
	}
	
	public Point getMyLocation() {
		return myLocation;
	}
	
	public Point getMyLocationRounded() {
		Point result = new Point(Math.round(myLocation.getX()),Math.round(myLocation.getY()));
		return result;
	}
	
	public void updateMyDirection(double orientation) {
		//System.out.println(orientation);
		if(myDirection.getRadians() + orientation >= Math.PI * 2) {
			double tempRadians = myDirection.getRadians() + orientation;
			while(tempRadians >=Math.PI*2) {
				tempRadians = tempRadians - (Math.PI*2);
			}
			myDirection = Direction.fromRadians(tempRadians);
		}
		else if(myDirection.getRadians() + orientation < 0) {
			double tempRadians = myDirection.getRadians() + orientation;
			while(tempRadians < 0) {
				tempRadians = tempRadians + (Math.PI*2);
			}
			myDirection = Direction.fromRadians(tempRadians);
		}
		else {
			myDirection = Direction.fromRadians(myDirection.getRadians() + orientation);
		}
		//System.out.println(myDirection.getDegrees());
	}
	
	public Direction getMyDirection() {
		return myDirection;
	}
}
