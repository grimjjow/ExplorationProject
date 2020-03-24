package Agents;

import Interop.Action.Action;
import Interop.Geometry.Direction;
import Interop.Geometry.Point;

public class AgentInfo {

    private Point currentPosition;
    private boolean lastActionExecuted = false;
    private Direction targetDirection;
    private Object agent;
    private int penalty;
    private Action lastAction;
    private int inTarget;


    public AgentInfo(Point position, Direction targetDirection, Object agent) {
        setCurrentPosition(position);
        this.targetDirection = targetDirection;
        this.agent = agent;
    }

    public boolean isLastActionExecuted() {
        return lastActionExecuted;
    }

    public void setLastActionExecuted(boolean lastActionExecuted) {
        this.lastActionExecuted = lastActionExecuted;
    }

    public Direction getTargetDirection() {
        return targetDirection;
    }

    public void setTargetDirection(Direction targetDirection) {
        this.targetDirection = targetDirection;
    }

    public Object getAgent() {
        return agent;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public Action getLastAction() {
        return lastAction;
    }

    public void setLastAction(Action lastAction) {
        this.lastAction = lastAction;
    }

    public int getInTarget(){
        return inTarget;
    }

    public void addInTarget(double a){
        this.inTarget += a;
    }

    public Point getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Point currentPosition) {
        this.currentPosition = currentPosition;
    }
}
