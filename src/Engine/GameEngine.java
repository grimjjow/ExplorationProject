package Engine;

import AreaProperty.AreaProperty;
import Agents.*;
import Environment.Environment;
import Environment.Square;
import GUI.EnvironmentGUI;
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
import Reader.*;
import Interop.Percept.Scenario.*;
import javafx.scene.layout.BorderPane;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
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
            AgentInfo info = new AgentInfo(new Point(12, 12), Direction.fromDegrees(0), guard);
            infos.add(info);
        }



    }

    public void update(){
        do {
            for (Interop.Agent.Guard guard : guards) {
                AgentInfo info = infos.get(0);

                // HERE COMPUTE PERCEPTS

                Action action = guard.getAction(new GuardPercepts(null, null, null, new AreaPercepts(false, false, false, false), new ScenarioGuardPercepts(scenarioPercepts, gameInfo.getMaxMoveDistanceGuard()), true));

                // HERE COMPUTE ACTION
                if(action instanceof Move) {
                    Distance distance = ((Move) action).getDistance();
                    Point currentPosition = info.getCurrentPosition();
                    Direction direction = info.getTargetDirection();

                    double endX = currentPosition.getX() + distance.getValue() * Math.cos(direction.getRadians());
                    double endY = currentPosition.getY() + distance.getValue() * Math.sin(direction.getRadians());

                    // Check if end point is not a wall
                    Point checkPoint = new Point(endX, endY);

                    //if()


                    info.setCurrentPosition(new Point(endX, endY));
                    info.setLastAction(action);
                } else if(action instanceof Rotate) {
                    Angle angle = ((Rotate) action).getAngle();
                    Direction direction = info.getTargetDirection();
                    Direction newDirection;

                    if(direction.getDegrees() + angle.getDegrees() < 0) {
                        newDirection = Direction.fromDegrees(360 + direction.getDegrees() + angle.getDegrees());
                    } else if(direction.getDegrees() + angle.getDegrees() >= 360) {
                        newDirection = Direction.fromDegrees(-360 + direction.getDegrees() + angle.getDegrees());
                    } else {
                        newDirection = Direction.fromDegrees(direction.getDegrees() + angle.getDegrees());
                    }

                    info.setTargetDirection(newDirection);
                    info.setLastAction(action);
                    info.setLastActionExecuted(true);
                }

                System.out.println("--- New round ---");
                System.out.println(info.getCurrentPosition().toString());
                System.out.println(info.getTargetDirection().getDegrees());
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
        this.grid = this.env.getGrid();
        Square[][] matrix = this.env.getGrid().getGridArray();
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

}
