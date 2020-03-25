package GUI;

import Agents.AgentInfo;
import AreaProperty.AreaProperty;
import Environment.Environment;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class EnvironmentGUI {

    BorderPane pane = new BorderPane();
    Rectangle shape;
    Environment env;

    ArrayList<AgentGUI> agentsGUI = null;
    private ArrayList<AgentGUI> agents;

    public EnvironmentGUI(Environment env) {
        this.env = env;
        this.shape = new Rectangle(env.getWidth(), env.getHeight(), Color.LIGHTGRAY);
        this.pane.getChildren().add(shape);

        for (AreaProperty pr : env.getInitialProperties()) {
            PropertyGUI property = new PropertyGUI(pr, env.getScalingFactor());
            this.pane.getChildren().add(property.getShape());
            //this.pane.getChildren().add(property.getCircleShape());
        }
    }
    public BorderPane getPane() {	return this.pane; 	}
    public void addAgentsGUI(ArrayList<AgentInfo> info) {
        this.agents = new ArrayList<AgentGUI>();
        for(AgentInfo i : info) {
            AgentGUI agent = new AgentGUI(i, this.env.getGameInfo(), env.scalingFactor);
            agents.add(agent);
            this.pane.getChildren().add(agent.getCircle());
            this.pane.getChildren().add(agent.getDirection());
            this.pane.getChildren().add(agent.getArc());
        }
    }
    public AgentGUI getAgent(int i) {
        return this.agentsGUI.get(i);
    }
    public ArrayList<AgentGUI> getAgents(){
        return this.agents;
    }
}
