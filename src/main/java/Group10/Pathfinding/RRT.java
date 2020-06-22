package Group10.Pathfinding;

import Group10.Algebra.Vector;
import Group10.Pathfinding.Graph.GraphNode;
import Group10.Pathfinding.Graph.NodeType;
import Group10.World.GameMap;
import Group10.World.Objects.AbstractObject;
import Group10.World.Reader.Reader;
import Interop.Geometry.Point;
import Interop.Percept.Vision.ObjectPerceptType;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class RRT {
    private File mapFile = new File("src/main/java/Group10/World/Maps/test_3.map");
    private GameMap gameMap = Reader.parseFile(mapFile.getAbsolutePath());

    private ArrayList<GraphNode> nodes;
    private Random random;
    private double mapWidth = gameMap.getGameSettings().getWidth();
    private double mapHeigth = gameMap.getGameSettings().getHeight();

    public RRT() {
        this.nodes = new ArrayList<>();
        this.random = new Random();
    }

    public static void main(String[] args) {
        RRT rrt = new RRT();
        rrt.generateGraph();
    }

    public void generateGraph() {
        int counter = 300;
        double x = random.nextDouble() * mapWidth;
        double y = random.nextDouble() * mapHeigth;
        nodes.add(new GraphNode(NodeType.EMPTY, new Point(x, y), 1.0, new Integer[]{(int) Math.ceil(x), (int) Math.ceil(y)}));
        //graph.addVertex(new Vertex(new Point(random.nextDouble() * mapWidth, random.nextDouble() * mapHeigth)));

        while (true && counter > 0) {
            x = random.nextDouble() * mapWidth;
            y = random.nextDouble() * mapHeigth;
            GraphNode newNode = new GraphNode(NodeType.EMPTY, new Point(x, y), 1.0, new Integer[]{(int) Math.ceil(x), (int) Math.ceil(y)});

            System.out.println("New: " + newNode);

            // Is obstacle ?
            if (!isObstacle(newNode)) {
                GraphNode nearestNode = nearest(newNode);
                newNode.setParent(nearestNode);
                newNode.addEdge(nearestNode, 0);
                nodes.add(newNode);

                if (isInTarget(newNode)) {
                    System.out.println("In target !");
                    break;
                }

                counter--;
            }
        }
    }

    public GraphNode nearest(GraphNode newNode) {
        GraphNode nearest = nodes.get(0);

        for (GraphNode n : nodes) {
            if (n.getCenter().getDistance(newNode.getCenter()).getValue() < nearest.getCenter().getDistance(newNode.getCenter()).getValue()) {
                nearest = n;
            }
        }

        System.out.println("Nearest: " + nearest);

        return nearest;
    }

    public boolean isObstacle(GraphNode newNode) {
        for (AbstractObject o : gameMap.getObjects().stream().filter(o -> o.getType() == ObjectPerceptType.Wall).collect(Collectors.toList())) {
            double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
            for (Vector point : o.getArea().getAsPolygon().getPoints()) {
                if (point.getX() > maxX)
                    maxX = point.getX();
                if (point.getX() < minX)
                    minX = point.getX();
                if (point.getY() > maxY)
                    maxY = point.getY();
                if (point.getY() < minY)
                    minY = point.getY();
            }

            if (newNode.getCenter().getX() < maxX && newNode.getCenter().getX() > minX && newNode.getCenter().getY() < maxY && newNode.getCenter().getY() > minY)
                return true;
        }
        return false;
    }

    public boolean isInTarget(GraphNode newNode) {
        for (AbstractObject o : gameMap.getObjects().stream().filter(o -> o.getType() == ObjectPerceptType.TargetArea).collect(Collectors.toList())) {
            double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
            for (Vector point : o.getArea().getAsPolygon().getPoints()) {
                if (point.getX() > maxX)
                    maxX = point.getX();
                if (point.getX() < minX)
                    minX = point.getX();
                if (point.getY() > maxY)
                    maxY = point.getY();
                if (point.getY() < minY)
                    minY = point.getY();
            }

            if (newNode.getCenter().getX() < maxX && newNode.getCenter().getX() > minX && newNode.getCenter().getY() < maxY && newNode.getCenter().getY() > minY)
                return true;
        }
        return false;
    }
}
