package GUI;

import Environment.Environment;
import Interop.Geometry.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import Agents.*;

public class AgentGUI {

    private AgentInfo agent;
    private Ellipse shape;
    double scalingFactor;

    public AgentGUI(AgentInfo agent, double scalingFactor) {
        this.agent = agent;
        this.scalingFactor = scalingFactor;
        this.shape = new Ellipse(scalingFactor/2, scalingFactor/2);
        Point p = randomPosition(scalingFactor);
        // arrow direction
        double x2 = p.getX() + (scalingFactor)*Math.cos(agent.getTargetDirection().getRadians());
        double y2 = p.getY() + (scalingFactor)*Math.sin(agent.getTargetDirection().getRadians());
        Point p2 = new Point(x2, y2);
        this.shape.setCenterX(p.getX());
        this.shape.setCenterY(p.getY());
        //add check condition for checking if there is already an object
        if(agent.getAgent().getClass() == Guard.class) {
            this.shape.setFill(Color.BLUE);
        }else{
            System.out.println("not guard");
        }
    }
    public void move(){
        //todo
    }
    public Ellipse getShape() {	return this.shape;	}

    public Point getPosition() {
        return new Point(this.getShape().getCenterX(),
                this.getShape().getCenterY());
    }
    public void setPosition(Point p) {
        this.shape.setCenterX(p.getX());;
        this.shape.setCenterY(p.getY());
    }
    public Point randomPosition(double scalingFactor) {
        double x = (scalingFactor/2)  + Math.random()*(Environment.width - scalingFactor);
        double y = (scalingFactor/2) + Math.random()*(Environment.height - scalingFactor);
        Point p = new Point(x, y);
        return p;
    }
}
