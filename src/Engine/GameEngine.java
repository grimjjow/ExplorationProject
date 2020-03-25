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
import java.util.ArrayList;
import java.util.List;
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
import Reader.*;
import Interop.Percept.Scenario.*;
import javafx.scene.layout.BorderPane;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        guards = AgentFactory.createGuards(gameInfo.getNumGuards());
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

                Distance range = new Distance(gameInfo.getViewRangeGuardNormal());
                Angle viewAngle = Angle.fromDegrees(gameInfo.getViewAngle());

                // HERE COMPUTE PERCEPTS
                System.out.println(vision().getAll().size());
                VisionPrecepts vision = new VisionPrecepts(new FieldOfView(range, viewAngle), vision());

                boolean wasLastActionExecuted = info.isLastActionExecuted();

                Action action = guard.getAction(
                        new GuardPercepts(
                                vision,
                                null,
                                null,
                                new AreaPercepts(false, false, false, false),
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

                System.out.println("--- New round ---");
                System.out.println(info.getCurrentPosition().toString());
                System.out.println(info.getDirection().getDegrees());
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

        // agent info
        AgentInfo info = infos.get(0);
        double agentX = info.getCurrentPosition().getX();
        double agentY = info.getCurrentPosition().getY();
        Direction agentDirection = info.getDirection();

        // of line y = slope*x+intercept
        double slope;
        double intercept;

        double viewAngle = gameInfo.getViewAngle();
        double viewRays = gameInfo.getViewRays();
        double steps = viewAngle / viewRays;

        Square[][] gridMatrix = this.grid.getGridArray();

        // if area is normal
        // TODO: check if area is shaded or normal
        double viewRange = gameInfo.getViewRangeGuardNormal();

        Set<ObjectPercept> objectPercepts = new HashSet<>();
        ArrayList<Square> visibleSquare = new ArrayList<>();

        for (double i = -(viewAngle / 2); i < viewAngle / 2; i += steps) {

            // compute the lines (vectors)
            // get endpoints of the 45 vectors
            double targetX = agentX + viewRange * Math.cos(agentDirection.getRadians() + Math.toRadians(i));
            double targetY = agentY + viewRange * Math.sin(agentDirection.getRadians() + Math.toRadians(i));
            slope = (targetY - agentY) / (targetX - agentX);
            intercept = targetY - (slope * targetX);

            ArrayList<Square> vectorSquare = new ArrayList<>();

            // iterating through all squares
            for (int p = 0; p < gridMatrix.length; p++) {
                for (int q = 0; q < gridMatrix[p].length; q++) {

                    double squareX = gridMatrix[p][q].getSX();
                    double squareY = gridMatrix[p][q].getSY();

                    // is on the line
                    if(!visibleSquare.contains(gridMatrix[p][q]))
                        if (Math.round(squareX * slope + intercept) == squareY) {
                            if (agentX < targetX) {
                                if (agentY < targetY) {
                                    //check if agentX <= squareX <= targetX and agentY <= squareY <= targetY
                                    if (squareX > agentX && squareX <= targetX && squareY > agentY && squareY <= targetY) {
                                        vectorSquare.add(gridMatrix[p][q]);
                                    }
                                } else {
                                    //check if agentX <= squareX <= targetX and targetY <= squareY <= agentY
                                    if (squareX > agentX && squareX <= targetX && squareY < agentY && squareY >= targetY) {
                                        vectorSquare.add(gridMatrix[p][q]);
                                    }
                                }
                            } else {
                                if (agentY < targetY) {
                                    //check if targetX <= squareX <= agentX and agentY <= squareY <= targetY
                                    if (squareX < agentX && squareX >= targetX && squareY > agentY && squareY <= targetY) {
                                        vectorSquare.add(gridMatrix[p][q]);
                                    }
                                } else {
                                    //check if targetX <= squareX <= agentX and targetY <= squareY <= agentY
                                    if (squareX < agentX && squareX >= targetX && squareY < agentY && squareY >= targetY) {
                                        vectorSquare.add(gridMatrix[p][q]);
                                    }
                                }

                            }
                        }
                }
            }
            visibleSquare.addAll(vectorSquare);
            ObjectPercept objectPercept = null;
            for (Square square : vectorSquare) {

                // TODO: implement all types of possible areas and if it is a wall we cannot see after it, if it is a target area, we can see through the area
                if (square.getType().equals("Wall")) {
                    objectPercept = new ObjectPercept(ObjectPerceptType.Wall, new Point(square.getSX()-info.getCurrentPosition().getX(), square.getSY()-info.getCurrentPosition().getY()));
                }else{
                    objectPercept = new ObjectPercept(ObjectPerceptType.EmptySpace, new Point(square.getSX()-info.getCurrentPosition().getX(), square.getSY()-info.getCurrentPosition().getY()));
                }
                objectPercepts.add(objectPercept);

            }
        }

        return new ObjectPercepts(objectPercepts);
    }
}
