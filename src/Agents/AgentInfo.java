package Agents;

import Interop.Action.Action;
import Interop.Geometry.Direction;
import Interop.Geometry.Point;

public class AgentInfo {

    private Point currentPosition;
    private boolean lastActionExecuted = false;
    private Direction agentDirection;
    private Object agent;
    private Action lastAction;


    public AgentInfo(Point position, Direction agentDirection, Object agent) {
        setCurrentPosition(position);
        this.agentDirection = agentDirection;
        this.agent = agent;
    }

    public boolean isLastActionExecuted() {
        return lastActionExecuted;
    }

    public void setLastActionExecuted(boolean lastActionExecuted) {
        this.lastActionExecuted = lastActionExecuted;
    }

    public Direction getDirection() {
        return agentDirection;
    }

    public void setTargetDirection(Direction targetDirection) {
        this.agentDirection = targetDirection;
    }

    public Object getAgent() {
        return agent;
    }

    public Action getLastAction() {
        return lastAction;
    }

    public void setLastAction(Action lastAction) {
        this.lastAction = lastAction;
    }

    public Point getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Point currentPosition) {
        this.currentPosition = currentPosition;
    }
}
