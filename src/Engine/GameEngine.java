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

import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    ArrayList<AreaProperty> properties;
    ArrayList<AgentInfo> infos;

    List<Interop.Agent.Guard> guards;

    Reader readEnv;
    static GameInfo gameInfo;

    Object agent;

    double widthBound;
    double heightBound;
    private ScenarioPercepts scenarioPercepts;
    private EnvironmentGUI environmentGUI;
    private BorderPane envPane;
    private Environment env;
    private String path;

    public GameEngine(String path) {

        this.path = path;
        readEnv = new Reader(path);
        gameInfo = readEnv.getInfo();
        properties = readEnv.getProperties();

        guards = AgentFactory.createGuards(gameInfo.getNumGuards());
        infos = new ArrayList<AgentInfo>();

        for (Interop.Agent.Guard guard : guards) {
            AgentInfo info = new AgentInfo(new Point(12, 12), Direction.fromDegrees(0), guard);
            infos.add(info);
        }

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
