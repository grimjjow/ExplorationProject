package Group10.Agents;

import Interop.Geometry.Direction;
import Interop.Geometry.Point;
import Group10.Algebra.Vector;

public class AgentInfo {

    private Point position;
    private Direction direction;

    public AgentInfo(Point position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    public AgentInfo(Direction direction){
        this.position = new Point(0,0);
        this.direction = direction;
    }

    public Point getRealPosition() {
        return position;
    }

    public Point getPosition() {
        return new Point(Math.round(position.getX()),Math.round(position.getY()));
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setPosition(double x, double y) {
        this.position = new Point(x,y);
    }

    public int getX(){
        return (int)Math.round(position.getX());
    }

    public int getY(){
        return (int)Math.round(position.getY());
    }

    public double getRealX(){
        return position.getX();
    }

    public double getRealY(){
        return position.getY();
    }

    public Direction getAgentDirection() {
        return direction;
    }

    public Vector getAgentVectorPosition(){
        return new Vector(position);
    }

    public void setAgentDirection(Direction direction) {
        this.direction = direction;
    }

}
