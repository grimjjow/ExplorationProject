package Engine;

import AreaProperty.AreaProperty;
import Agents.*;
import Environment.Environment;

import GUI.EnvironmentGUI;
import Interop.Geometry.Direction;
import Interop.Geometry.Point;
import Reader.*;
import Interop.Percept.Scenario.*;
import javafx.scene.layout.BorderPane;

import java.sql.SQLOutput;
import java.util.*;

import Environment.Square;
import Interop.Action.Action;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.AreaPercepts;
import Interop.Percept.GuardPercepts;
import Environment.Grid;
import Interop.Percept.Vision.*;
import AreaProperty.Teleport;
import Reader.*;
import Interop.Percept.Scenario.*;
import javafx.scene.layout.BorderPane;
import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    ArrayList<AreaProperty> properties;
    ArrayList<AgentInfo> infos;

    List<Interop.Agent.Guard> guards;

    public Reader readEnv;

    static GameInfo gameInfo;

    Object agent;

    double widthBound;
    double heightBound;
    private ScenarioPercepts scenarioPercepts;
    private EnvironmentGUI environmentGUI;
    private BorderPane envPane;
    private Environment env;
    private String path;
    private Grid grid;

    public GameEngine(String path) {

        this.path = path;
        readEnv = new Reader(path);
        gameInfo = readEnv.getInfo();
        grid = new Grid((int)gameInfo.getWidth(), (int)gameInfo.getHeight(), 1, new float[]{-1, -1}, readEnv);

        GameMode gameMode = (gameInfo.getGameMode() == 0) ? GameMode.CaptureOneIntruder : GameMode.CaptureAllIntruders;
        scenarioPercepts = new ScenarioPercepts(
                gameMode,
                new Distance(gameInfo.getCaptureDistance()),
                Angle.fromDegrees(gameInfo.getMaxRotationAngle()),
                new SlowDownModifiers(gameInfo.getSlowDownModifierWindow(), gameInfo.getSlowDownModifierDoor(), gameInfo.getSlowDownModifierSentryTower()),
                new Distance(gameInfo.getRadiusPheromone()),
                gameInfo.getPheromoneCoolDown()
        );
        properties = readEnv.getProperties();
        // calling normal guard
        guards = AgentFactory.createGuards(gameInfo.getNumGuards(), "Guard");
        infos = new ArrayList<AgentInfo>();

        for (Interop.Agent.Guard guard : guards) {
            AgentInfo info = new AgentInfo(new Point(11, 11), Direction.fromDegrees(0), guard);
            infos.add(info);
        }
    }

    public void update(){
        do {
            for (Interop.Agent.Guard guard : guards) {
                AgentInfo info = infos.get(0);

                System.out.println("---- New round -----");
                System.out.println("Position: " + info.getCurrentPosition().toString());
                System.out.println("Direction: " + info.getDirection().getDegrees());

                Distance range = new Distance(gameInfo.getViewRangeGuardNormal());
                Angle viewAngle = Angle.fromDegrees(gameInfo.getViewAngle());

                // HERE COMPUTE PERCEPTS
                VisionPrecepts vision = new VisionPrecepts(new FieldOfView(range, viewAngle), vision());

                boolean wasLastActionExecuted = info.isLastActionExecuted();
                boolean inWindow = false;
                boolean inDoor = false;
                boolean inSentryTower = false;

                Square currentSquare = grid.getSquare(new float[]{(float)info.getCurrentPosition().getX(),(float) info.getCurrentPosition().getY()});
                // checking the area type of our current position
                if(currentSquare.getType().equals("Window")){
                    inWindow = true;
                }
                if(currentSquare.getType().equals("Door")){
                    inDoor = true;
                }
                if(currentSquare.getType().equals("Sentry")){
                    inSentryTower = true;
                }
                if((currentSquare.getType().equals("Teleport")) && (info.isTeleported())){
                    info.setTeleported(false);
                }

                System.out.println("--- Agent action ---");
                Action action = guard.getAction(
                        new GuardPercepts(
                                vision,
                                null,
                                null,
                                new AreaPercepts(inWindow, inDoor, inSentryTower, info.isTeleported()),
                                new ScenarioGuardPercepts(scenarioPercepts, gameInfo.getMaxMoveDistanceGuard()),
                                wasLastActionExecuted)
                );

                // HERE COMPUTE ACTION
                if(action instanceof Move) {
                    Distance distance = ((Move) action).getDistance();
                    Point currentPosition = info.getCurrentPosition();
                    Direction direction = info.getDirection();

                    double endX = currentPosition.getX() + distance.getValue() * Math.cos(direction.getRadians());
                    double endY = currentPosition.getY() + distance.getValue() * Math.sin(direction.getRadians());

                    // Check if end point is not a wall
                    Point checkPoint = new Point(endX, endY);

                    if(grid.getSquare(new float[]{(float)endX, (float)endY}).getWalkable()){
                        info.setCurrentPosition(new Point(endX, endY));
                        info.setLastAction(action);
                        info.setLastActionExecuted(true);
                    } else{
                        info.setLastActionExecuted(false);
                        info.setLastAction(action);
                    }

                } else if(action instanceof Rotate) {
                    Angle angle = ((Rotate) action).getAngle();
                    Direction direction = info.getDirection();
                    Direction newDirection;

                    if(direction.getDegrees() + angle.getDegrees() < 0) {
                        newDirection = Direction.fromDegrees(360 + direction.getDegrees() + angle.getDegrees());
                    } else if(direction.getDegrees() + angle.getDegrees() >= 360) {
                        newDirection = Direction.fromDegrees(-360 + direction.getDegrees() + angle.getDegrees());
                    } else {
                        newDirection = Direction.fromDegrees(direction.getDegrees() + angle.getDegrees());
                    }

                    info.setDirection(newDirection);
                    info.setLastAction(action);
                    info.setLastActionExecuted(true);
                }

                // check if the current position is inside of a teleport area -> if yes then move to new position
                currentSquare = grid.getSquare(new float[]{(float)info.getCurrentPosition().getX(),(float) info.getCurrentPosition().getY()});

                if(currentSquare.getType().equals("Teleport")){
                    System.out.println("--- Teleporation ---");
                    Teleport teleport = (Teleport) currentSquare.getAreaProperty();
                    info.setTeleported(true);
                    info.setCurrentPosition(teleport.getTeleportTo());
                }
            }
        } while (true);
    }

    public void setHeightBound(double hb) {
        this.heightBound = hb;
    }

    public void setWidthBound(double wb) {
        this.widthBound = wb;
    }

    public void createEnvironment(String path) {

        this.env = new Environment(path, this.widthBound, this.heightBound);
        this.environmentGUI = new EnvironmentGUI(this.env);
        this.environmentGUI.addAgentsGUI(infos);
        this.envPane = this.environmentGUI.getPane();

        //this.grid = this.env.getGrid();
        //Square[][] matrix = this.env.getGrid().getGridArray();
      /*  for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){

                System.out.println("this is i, j " + matrix[i][j].getType());
            }
        }*/

    }

    public EnvironmentGUI getEnvironmentGUI() {
        return this.environmentGUI;
    }

    public Environment getEnv() {
        return this.env;
    }

    public void setEnv(Environment env) {
        this.environmentGUI = new EnvironmentGUI(env);
    }

    public BorderPane getEnvPane() {
        return this.envPane;
    }

    public ObjectPercepts vision() {
        // Agent infos
        AgentInfo info = infos.get(0);
        double agentX = info.getCurrentPosition().getX();
        double agentY = info.getCurrentPosition().getY();
        Square currentSquare = this.grid.getSquare(new float[]{(float) agentX, (float) agentY});
        Direction agentDirection = info.getDirection();

        // Slope and intercept of the line passing through agent and end point of the ray
        double slope;
        double intercept;

        // Game infos
        double viewRange;
        double viewAngle = gameInfo.getViewAngle();
        double viewRays = gameInfo.getViewRays();
        double steps = viewAngle / viewRays;

        // Matrix of all square of the grid
        Square[][] gridMatrix = this.grid.getGridArray();

        if(this.grid.getSquare(new float[]{(float)agentX, (float)agentY}).getType().equals("Shade"))
            viewRange = gameInfo.getViewRangeGuardShaded();
        else
            viewRange = gameInfo.getViewRangeGuardNormal();

        Set<ObjectPercept> objectPercepts = new HashSet<>();
        ArrayList<Square> visibleSquare = new ArrayList<>();

        // Go through all rays
        for (double i = -(viewAngle / 2); i < viewAngle / 2; i += steps) {

            // Get end point of the current ray
            double targetX = agentX + viewRange * Math.cos(agentDirection.getRadians() + Math.toRadians(i));
            double targetY = agentY + viewRange * Math.sin(agentDirection.getRadians() + Math.toRadians(i));

            // Compute the line
            slope = (targetY - agentY) / (targetX - agentX);
            intercept = targetY - (slope * targetX);

            TreeMap<Double, Square> vectorSquare = new TreeMap<>();

            // Go through all the square of the grid
            for (Square[] matrix : gridMatrix) {
                for (Square square : matrix) {

                    // Store the square and its X and Y
                    double squareX = square.getSX();
                    double squareY = square.getSY();

                    // If it is not already seen by an other ray
                    if (!visibleSquare.contains(square)) {
                        // Check if it is on the computed line
                        if (Math.round(squareX * slope + intercept) == squareY) {
                            // Check if it is between the agent (excluded) and the end point (included)
                            if (agentX < targetX) {
                                if (agentY < targetY) {
                                    if (squareX > agentX && squareX <= targetX && squareY > agentY && squareY <= targetY) {
                                        vectorSquare.put(new Point(squareX - agentX, squareY - agentY).getDistanceFromOrigin().getValue(), square);
                                    }
                                } else {
                                    if (squareX > agentX && squareX <= targetX && squareY < agentY && squareY >= targetY) {
                                        vectorSquare.put(new Point(squareX - agentX, squareY - agentY).getDistanceFromOrigin().getValue(), square);
                                    }
                                }
                            } else {
                                if (agentY < targetY) {
                                    if (squareX < agentX && squareX >= targetX && squareY > agentY && squareY <= targetY) {
                                        vectorSquare.put(new Point(squareX - agentX, squareY - agentY).getDistanceFromOrigin().getValue(), square);
                                    }
                                } else {
                                    if (squareX < agentX && squareX >= targetX && squareY < agentY && squareY >= targetY) {
                                        vectorSquare.put(new Point(squareX - agentX, squareY - agentY).getDistanceFromOrigin().getValue(), square);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ObjectPercept objectPercept = null;

            label:
            for (Square square : vectorSquare.values()) {
                // Add to the visibleSquare list to removed duplicate
                visibleSquare.add(square);

                switch (square.getType()) {
                    case "Door":
                        objectPercept = new ObjectPercept(ObjectPerceptType.Door, new Point(square.getSX() - info.getCurrentPosition().getX(), square.getSY() - info.getCurrentPosition().getY()));
                        // Break if we are not in because it is opaque from outside
                        if (!currentSquare.getType().equals("Door")) {
                            objectPercepts.add(objectPercept);
                            break label;
                        }
                        break;
                    case "Sentry":
                        objectPercept = new ObjectPercept(ObjectPerceptType.SentryTower, new Point(square.getSX() - info.getCurrentPosition().getX(), square.getSY() - info.getCurrentPosition().getY()));
                        // Break if we are not in because it is opaque from outside
                        if (!currentSquare.getType().equals("Sentry")) {
                            objectPercepts.add(objectPercept);
                            break label;
                        }
                        break;
                    case "Shade":
                        objectPercept = new ObjectPercept(ObjectPerceptType.ShadedArea, new Point(square.getSX() - info.getCurrentPosition().getX(), square.getSY() - info.getCurrentPosition().getY()));
                        // Break if we are not in because it is opaque from outside
                        if (!currentSquare.getType().equals("Shade")) {
                            objectPercepts.add(objectPercept);
                            break label;
                        }
                        break;
                    case "Teleport":
                        objectPercept = new ObjectPercept(ObjectPerceptType.Teleport, new Point(square.getSX() - info.getCurrentPosition().getX(), square.getSY() - info.getCurrentPosition().getY()));
                        break;
                    case "Wall":
                        objectPercept = new ObjectPercept(ObjectPerceptType.Wall, new Point(square.getSX() - info.getCurrentPosition().getX(), square.getSY() - info.getCurrentPosition().getY()));
                        // Break because it is opaque
                        objectPercepts.add(objectPercept);
                        break label;
                    case "Window":
                        objectPercept = new ObjectPercept(ObjectPerceptType.Window, new Point(square.getSX() - info.getCurrentPosition().getX(), square.getSY() - info.getCurrentPosition().getY()));
                        break;
                    default:
                        objectPercept = new ObjectPercept(ObjectPerceptType.EmptySpace, new Point(square.getSX() - info.getCurrentPosition().getX(), square.getSY() - info.getCurrentPosition().getY()));
                        break;
                }
                objectPercepts.add(objectPercept);
            }
        }

        System.out.print("Vision: ");
        for(Square square : visibleSquare) {
            System.out.print(" " + square.getType());
        }
        System.out.print("\n");
        return new ObjectPercepts(objectPercepts);
    }
}
