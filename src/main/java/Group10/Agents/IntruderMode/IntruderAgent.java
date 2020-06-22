package Group10.Agents.IntruderMode;


import Group10.Agents.AgentActions.ActionsType;
import Group10.Pathfinding.Graph.GraphEdge;
import Group10.Pathfinding.Graph.Graph;
import Group10.Pathfinding.Graph.GraphNode;
import Group10.Pathfinding.Graph.NodeType;
import Group10.Pathfinding.IDDFS;
import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.NoAction;
import Interop.Action.Rotate;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.VisionPrecepts;

import java.util.*;

public class IntruderAgent implements Intruder {

    ActionsType lastActionsType = null;
    Graph map;
    GraphNode currentPosition;
    double viewAngle;
    double radius;
    Angle angle;
    Queue<ActionsType> actionsTypeList = new LinkedList<ActionsType>();
    Queue<Double> distanceCounter = new LinkedList<Double>();
    boolean foundTarget = false;
    int foundTeleport = 0;
    boolean escape = false;

    public IntruderAgent() {
    }

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {

        if(this.map == null || percepts.getAreaPercepts().isJustTeleported()) {
            viewAngle = percepts.getVision().getFieldOfView().getViewAngle().getDegrees();
            radius = Math.sqrt(Math.pow(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue(),2)/2)/2;
            angle = Angle.fromDegrees(0);
            this.map = new Graph();
            currentPosition = new GraphNode(NodeType.EMPTY, new Point(0, 0), radius, new Integer[] {0,0});
            map.addVertice(currentPosition);
            lastActionsType = null;
            if(percepts.wasLastActionExecuted()) {
                actionsTypeList.offer(ActionsType.MOVE);
            }
            else {
                actionsTypeList.offer(ActionsType.RIGHT);
            }
        }
        if(distanceCounter.size() != 0) {
            return new Move(new Distance(distanceCounter.poll()));
        }
        if(percepts.wasLastActionExecuted()) {
            updateState();
        }
        else {
            actionsTypeList = new LinkedList<ActionsType>();
            actionsTypeList.offer(ActionsType.LEFT);
            actionsTypeList.offer(ActionsType.MOVE);
        }
        createNewVerticesInSight(percepts.getVision());

        evaluateVision(percepts.getVision());

        if(actionsTypeList.size() == 0) {
            getNextAction(percepts);
            if(actionsTypeList.size() == 0) {
                actionsTypeList.offer(ActionsType.MOVE);
            }
            return returnAction(actionsTypeList.poll(), percepts);
        }
        else {
            return returnAction(actionsTypeList.poll(), percepts);
        }
    }

    private IntruderAction returnAction(ActionsType actionsType, IntruderPercepts percepts) {
        switch(actionsType) {
            case LEFT:
                return turnLeft();
            case RIGHT:
                return turnRight();
            case MOVE:
                double maxMoveDistance = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue();
                if(percepts.getAreaPercepts().isInSentryTower()) {
                    maxMoveDistance = maxMoveDistance * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getSlowDownModifiers().getInSentryTower();
                }
                else if(percepts.getAreaPercepts().isInDoor()) {
                    maxMoveDistance = maxMoveDistance * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getSlowDownModifiers().getInDoor();
                }
                else if(percepts.getAreaPercepts().isInWindow()) {
                    maxMoveDistance = maxMoveDistance * percepts.getScenarioIntruderPercepts().getScenarioPercepts().getSlowDownModifiers().getInWindow();
                }
                else {
                    maxMoveDistance = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue();
                }
                return forward(maxMoveDistance);
            default:
                return new NoAction();
        }
    }

    private void evaluateVision(VisionPrecepts vision) {
        Set<ObjectPercept> objectPercepts = vision.getObjects().getAll();
        for(ObjectPercept percept : objectPercepts) {
            double addX = currentPosition.getCenter().getX();
            double addY = currentPosition.getCenter().getY();
            Point rotatePoint = truePoint(percept.getPoint());
            Point truePoint = new Point(rotatePoint.getX() + addX, rotatePoint.getY() + addY);
            int l = 1;
            GraphNode gNode = getRelativeNode(truePoint);
            switch(percept.getType()) {
                case Door:
                    gNode.setType(NodeType.DOOR);
                    break;
                case EmptySpace:
                    gNode.setType(NodeType.EMPTY);
                    break;
                case SentryTower:
                    gNode.setType(NodeType.SENTRY_TOWER);
                    break;
                case ShadedArea:
                    gNode.setType(NodeType.SHADED_AREA);
                    break;
                case TargetArea:
                    gNode.setType(NodeType.TARGET_AREA);
                    foundTarget = true;
                    break;
                case Teleport:
                    gNode.setType(NodeType.TELEPORT);
                    if(foundTeleport == 0) foundTeleport = 1;
                    break;
                case Wall:
                    gNode.setType(NodeType.WALL);
                    break;
                case Window:
                    gNode.setType(NodeType.WINDOW);
                    break;
                case Guard:
                    map.detected();
                    escape = true;
                    gNode.setType(NodeType.SEEN);
                    break;
                case Intruder:
                    Random rand = new Random();
                    int r = rand.nextInt(2);
                    if(r == 0) {
                        actionsTypeList.add(ActionsType.LEFT);
                        actionsTypeList.add(ActionsType.MOVE);
                    }
                    else {
                        actionsTypeList.add(ActionsType.RIGHT);
                        actionsTypeList.add(ActionsType.MOVE);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void findEscape() {

        actionsTypeList = new LinkedList<ActionsType>();

        for (int i = 0; i < 10; i++) {
            actionsTypeList.offer(ActionsType.LEFT);
            actionsTypeList.offer(ActionsType.LEFT);
            actionsTypeList.offer(ActionsType.MOVE);
            actionsTypeList.offer(ActionsType.MOVE);
            actionsTypeList.offer(ActionsType.MOVE);
            actionsTypeList.offer(ActionsType.MOVE);
            actionsTypeList.offer(ActionsType.RIGHT);
            actionsTypeList.offer(ActionsType.RIGHT);
            actionsTypeList.offer(ActionsType.MOVE);
            actionsTypeList.offer(ActionsType.MOVE);
            actionsTypeList.offer(ActionsType.MOVE);
            actionsTypeList.offer(ActionsType.MOVE);
        }
    }

    private void getNextAction(IntruderPercepts percepts) {
        if(foundTeleport == 1) {
            foundTeleport = 2;
            map.unMark();
            Stack<GraphNode> path = IDDFS.findDFSPath(currentPosition, NodeType.TELEPORT);
            generateActionList(path);
        }
        if(escape) {
            escape = false;
            findEscape();
        }
        if(foundTarget) {
            map.unMark();
            Stack<GraphNode> target = IDDFS.findDFSPath(currentPosition, NodeType.TARGET_AREA);
            if(target != null) {
                generateActionList(target);
            }
        }

        List<ActionsType> actionsTypeSpace = new ArrayList<ActionsType>();
        actionsTypeSpace.add(ActionsType.LEFT);
        actionsTypeSpace.add(ActionsType.RIGHT);
        Integer[] nextPosition = Graph.getCoordinate((int)angle.getDegrees(), currentPosition.getCoordinate());
        GraphNode nextVertice = map.getVertice(nextPosition);
        if(IDDFS.checkDFSVertice(nextVertice)) {
            actionsTypeSpace.add(ActionsType.MOVE);
        }

        double actionValue = 0;
        ActionsType selectedActionsType = null;
        for(ActionsType actionsType : actionsTypeSpace) {
            Angle angle = Angle.fromRadians(this.angle.getRadians());
            GraphNode currentPosition = this.currentPosition;
            switch(actionsType) {
                case LEFT:
                    angle = Angle.fromDegrees(getTrueAngle(this.angle.getDegrees() +45));
                    break;
                case RIGHT:
                    angle = Angle.fromDegrees(getTrueAngle(this.angle.getDegrees() -45));
                    break;
                case MOVE:
                    Integer[] newPosition = Graph.getCoordinate((int)angle.getDegrees(), currentPosition.getCoordinate());
                    currentPosition = map.getVertice(newPosition);
                    break;
            }
            double value = simulateAction(percepts.getVision(), angle, currentPosition);
            if(value > actionValue) {
                actionValue = value;
                selectedActionsType = actionsType;
            }
        }

        if(selectedActionsType == null) {
            map.unMark();
            Stack<GraphNode> path = IDDFS.findNonCompleteVertice(currentPosition);
            if(path == null) {
                map.unMark();
                path = IDDFS.findDFSPath(currentPosition, NodeType.TELEPORT);
            }
            generateActionList(path);
        }
        else {
            actionsTypeList.offer(selectedActionsType);
        }
    }

    private void generateActionList(Stack<GraphNode> path) {
        if(path == null) {
            actionsTypeList.add(ActionsType.MOVE);
            return;
        }
        GraphNode start = path.pop();
        int currentDegrees = (int)angle.getDegrees();
        while(path.size() != 0) {
            GraphNode end = path.pop();
            for(GraphEdge e : start.getEdges()) {
                if(e.getEndNode() == end) {
                    int degrees = e.getAngle();
                    double add;
                    if(currentDegrees > 180) {
                        add = 360 - currentDegrees;
                    }
                    else {
                        add = -currentDegrees;
                    }
                    while(Math.abs(currentDegrees - degrees)>2) {
                        if(getTrueAngle(degrees + add) < 180) {
                            actionsTypeList.add(ActionsType.LEFT);
                            currentDegrees = (int)getTrueAngle(currentDegrees +45);
                        }
                        else {
                            actionsTypeList.add(ActionsType.RIGHT);
                            currentDegrees = (int)getTrueAngle(currentDegrees -45);
                        }
                    }
                    break;
                }
            }
            start = end;
            actionsTypeList.add(ActionsType.MOVE);
        }
    }


    public double simulateAction(VisionPrecepts percepts, Angle angle, GraphNode currentPosition) {
        int vertexMultiply = 1;
        double result = vertexMultiply*numberNewVerticesInSight(angle,currentPosition, percepts);
        return result;
    }

    private int numberNewVerticesInSight(Angle angle, GraphNode currentPosition, VisionPrecepts percepts) {
        int count = 0;
        int samples = 10;
        double viewRange = percepts.getFieldOfView().getRange().getValue();
        double step = viewRange / samples;
        if(step > radius) {
            samples = (int)(viewRange / radius);
            step = radius;
        }
        double currentAngle = (viewAngle+4)/2;
        while(currentAngle >= -viewAngle/2) {
            double finalAngle = 0;
            if(angle.getDegrees() > 180) finalAngle = getTrueAngle(currentAngle + (angle.getDegrees()-360) + 90);
            else finalAngle = getTrueAngle(currentAngle + angle.getDegrees() + 90);

            Angle pAngle = Angle.fromDegrees(finalAngle);
            for(int i=1; i < samples+2; i++) {
                double x=i*step*Math.cos(pAngle.getRadians());
                double y=i*step*Math.sin(pAngle.getRadians());
                x += currentPosition.getCenter().getX();
                y += currentPosition.getCenter().getY();
                Integer[] position = getRelativeVerticeCoordinate(new Point(x,y));
                if(!map.checkNode(position)) {
                    count ++;
                }
            }
            currentAngle = currentAngle -1;
        }
        return count;
    }

    private Rotate turnLeft() {
        lastActionsType = ActionsType.LEFT;
        return new Rotate(Angle.fromDegrees(-45));
    }

    private Rotate turnRight() {
        lastActionsType = ActionsType.RIGHT;
        return new Rotate(Angle.fromDegrees(+45));
    }

    private Move forward(double maxDistance) {
        this.lastActionsType = ActionsType.MOVE;
        double distance;

        if(this.angle.getDegrees() % 90 == 0) {
            distance = 2*radius;
        }
        else {
            distance = Math.sqrt(2*Math.pow(2*radius, 2));
        }
        while(distance > maxDistance) {
            distanceCounter.offer(maxDistance);
            distance = distance - maxDistance;
        }
        return new Move(new Distance(distance));
    }

    private void updateState() {
        if(lastActionsType != null) {
            switch(lastActionsType) {
                case LEFT:
                    this.angle = Angle.fromDegrees(getTrueAngle(this.angle.getDegrees() +45));
                    break;
                case RIGHT:
                    this.angle = Angle.fromDegrees(getTrueAngle(this.angle.getDegrees() -45));
                    break;
                case MOVE:
                    Integer[] newPosition = Graph.getCoordinate((int)angle.getDegrees(), currentPosition.getCoordinate());
                    currentPosition = map.getVertice(newPosition);
                    break;
            }
        }
    }

    private void createNewVerticesInSight(VisionPrecepts percepts) {
        int samples = 10;
        double viewRange = percepts.getFieldOfView().getRange().getValue();
        double step = viewRange / samples;
        if(step > radius) {
            samples = (int)(viewRange / radius);
            step = radius;
        }
        double currentAngle = (viewAngle+4)/2;
        while(currentAngle >= -(viewAngle+4)/2) {
            double finalAngle = 0;
            if(angle.getDegrees() > 180) finalAngle = getTrueAngle(currentAngle + (angle.getDegrees()-360) + 90);
            else finalAngle = getTrueAngle(currentAngle + angle.getDegrees() + 90);

            Angle pAngle = Angle.fromDegrees(finalAngle);
            for(int i=1; i < samples+2; i++) {
                double x=i*step*Math.cos(pAngle.getRadians());
                double y=i*step*Math.sin(pAngle.getRadians());
                x += currentPosition.getCenter().getX();
                y += currentPosition.getCenter().getY();
                Integer[] position = getRelativeVerticeCoordinate(new Point(x,y));
                if(!map.checkNode(position)) {
                    map.addVertice(new GraphNode(NodeType.EMPTY, new Point(2*radius*position[0], 2*radius*position[1]), radius, position));
                }
            }
            if(radius < 0.05) {
                currentAngle = currentAngle -.1;
            }
            else {
                currentAngle = currentAngle -1;
            }
        }
    }

    private double getTrueAngle(double angle) {
        if(angle < 0) {
            angle = 360 + angle;
        }
        else if(angle > 360) {
            angle = 0 + (angle - 360);
        }
        else if(angle == 360) {
            angle = 0;
        }
        return angle;
    }

    private GraphNode getRelativeNode(Point point) {
        Integer[] position = getRelativeVerticeCoordinate(point);
        //System.out.println(position[0] + ";" + position[1]);
        return map.getVertice(position);
    }


    private Integer[] getRelativeVerticeCoordinate(Point point) {
        //Point truePoint = truePoint(point);
        //double x = truePoint.getX();
        //double y = truePoint.getY();
        double x = point.getX();
        double y = point.getY();
        int xVertice = 0;
        int yVertice = 0;
        if(!(Math.abs(x)<=radius)) {
            if(x<0) {
                xVertice--;
                x = x + radius;
            }
            else {
                xVertice++;
                x = x - radius;
            }
            while(Math.abs(x) >= 2*radius) {
                if(x<0) {
                    xVertice--;
                    x = x + 2*radius;
                }
                else {
                    xVertice++;
                    x = x - 2*radius;
                }
            }
        }
        if(!(Math.abs(y)<=radius)) {
            if(y<0) {
                yVertice--;
                y = y + radius;
            }
            else {
                yVertice++;
                y = y - radius;
            }
            while(Math.abs(y) >= 2*radius) {
                if(y<0) {
                    yVertice--;
                    y = y + 2*radius;
                }
                else {
                    yVertice++;
                    y = y - 2*radius;
                }
            }
        }
        return new Integer[]{xVertice, yVertice};
    }


    private Point truePoint(Point point) {
        double x = -point.getX();
        double y = point.getY();

        double x_ = x*Math.cos(angle.getRadians()) - y * Math.sin(angle.getRadians());
        double y_ = y*Math.cos(angle.getRadians()) + x * Math.sin(angle.getRadians());
        return new Point(x_, y_);
    }

    private Point getCenterPoint(int degrees) {
        double x = currentPosition.getCenter().getX();
        double y = currentPosition.getCenter().getY();

        switch(degrees) {
            case 0:
                return new Point(x, y+2*radius);
            case 45:
                return new Point(x+2*radius, y+2*radius);
            case 90:
                return new Point(x+2*radius, y);
            case 135:
                return new Point(x+2*radius, y-2*radius);
            case 180:
                return new Point(x, y-2*radius);
            case 225:
                return new Point(x-2*radius, y-2*radius);
            case 270:
                return new Point(x-2*radius, y);
            case 315:
                return new Point(x-2*radius, y+2*radius);
            default:
                return null;
        }
    }
}

