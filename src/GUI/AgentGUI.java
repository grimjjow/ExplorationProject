package GUI;

import Engine.GameInfo;
import Environment.Environment;
import Interop.Geometry.Direction;
import Interop.Geometry.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Ellipse;
import Agents.*;
import javafx.scene.shape.Line;

public class AgentGUI {

    private AgentInfo agentInfo;
    private GameInfo gameInfo;
    private Ellipse circle;
    private Line direction;
    private Arc arc;
    double scalingFactor;

    public AgentGUI(AgentInfo agentInfo, GameInfo gameInfo, double scalingFactor) {

        this.agentInfo = agentInfo;
        this.gameInfo = gameInfo;
        this.scalingFactor = scalingFactor;
        this.circle = new Ellipse(scalingFactor/2, scalingFactor/2);
        this.circle.setCenterX(this.agentInfo.getCurrentPosition().getX() * this.scalingFactor);
        this.circle.setCenterY(this.agentInfo.getCurrentPosition().getY() * this.scalingFactor);
        Point start = new Point(this.circle.getCenterX(), this.circle.getCenterY());;
        double x2 = start.getX() + (scalingFactor)*Math.cos(agentInfo.getDirection().getRadians());
        double y2 = start.getY() + (scalingFactor)*Math.sin(agentInfo.getDirection().getRadians());
        Point end = new Point(x2, y2);
        this.direction = new Line(start.getX(), start.getY(), end.getX() + 53, end.getY());
        this.direction.setStrokeWidth(1.5);
        this.direction.setStroke(Color.BLACK);
        this.circle.setCenterX(start.getX());
        this.circle.setCenterY(start.getY());
        this.circle.setFill(Color.BLACK);
        this.arc = new Arc();
        this.arc.setCenterX(start.getX());
        this.arc.setCenterY(start.getY());
        this.arc.setRadiusX(80.0f);
        this.arc.setRadiusY(80.0f);
        this.arc.setStartAngle(agentInfo.getDirection().getDegrees() - this.gameInfo.getViewAngle()/2);
        this.arc.setLength(this.gameInfo.getViewAngle());
        this.arc.setType(ArcType.ROUND);
        this.arc.setFill(Color.rgb(200, 0, 0, 0.4));


    }
    public Ellipse getCircle() {	return this.circle;	}

    public Line getDirection() {
        return this.direction;
    }

    public Arc getArc() {
        return this.arc;
    }

    public void setDirection(Direction d) {
        this.direction.setStartX(this.circle.getCenterX());
        this.direction.setStartY(this.circle.getCenterY());
        this.direction.setEndX(this.circle.getCenterX() + this.scalingFactor * Math.cos(d.getRadians()));
        this.direction.setEndY(this.circle.getCenterY() + this.scalingFactor * Math.sin(d.getRadians()));
    }

    public Point getPosition() {
        return new Point(this.getCircle().getCenterX(),
                this.getCircle().getCenterY());
    }
    public void setPosition(Point p) {
        this.circle.setCenterX(p.getX());;
        this.circle.setCenterY(p.getY());
    }
}
