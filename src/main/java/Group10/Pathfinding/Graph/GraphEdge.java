package Group10.Pathfinding.Graph;

public class GraphEdge {

	public GraphNode endNode;
	public GraphNode startNode;
	int angle;

	public GraphEdge(GraphNode startNode, GraphNode endNode, int angle) {
		this.startNode = startNode;
		this.endNode = endNode;
		this.angle = angle;
	}
	public int getAngle() {
		return angle;
	}
	public GraphNode getStartNode() {
		return startNode;
	}
	public void setStartNode(GraphNode startNode) {
		this.startNode = startNode;
	}
	public GraphNode getEndNode() {
		return endNode;
	}
	public void setEndNode(GraphNode endNode) {
		this.endNode = endNode;
	}
}
