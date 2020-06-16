package Group10.Agents;

import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Vision.ObjectPercepts;
import Interop.Geometry.Point;
import Group10.Algebra.Line;
import Group10.Algebra.Vector;

public class IntruderAgent implements Intruder {

    private Point[] twoSubsequentLocaitons = new Point[2];
    private Direction[] twoSubsequentDirections = new Direction[2];
    public boolean sprintingMode;

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        return null;
    }

    // this gives a relative location of the target
    public Point getTargetLocation(IntruderPercepts percepts) {

        if (twoSubsequentLocaitons[1] != null) {
            Line first = new Line(twoSubsequentLocaitons[0], new Vector(twoSubsequentLocaitons[0], twoSubsequentDirections[0]).toPoint());
            Line second = new Line(twoSubsequentLocaitons[1], new Vector(twoSubsequentLocaitons[1], twoSubsequentDirections[1]).toPoint());
            return first.getIntersection(second);
        }

        return new Point(0, 0);
    }

    public IntruderAction moveForward(IntruderPercepts percepts) {
        if (sprintingMode) {

           //add escaping here and push to sequence

            return new Move(percepts.getScenarioIntruderPercepts().getMaxSprintDistanceIntruder());
        } else {

            //add rrt here and push to sequence

            return new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());
        }
    }

    public void pushToSequence(Point a, Direction d) {
        if (twoSubsequentLocaitons[1] != null) {
            twoSubsequentDirections[0] = Direction.fromRadians(twoSubsequentDirections[1].getRadians());
            twoSubsequentLocaitons[0] = new Vector(twoSubsequentLocaitons[1]).toPoint();
        }
        twoSubsequentDirections[1] = Direction.fromRadians(d.getRadians());
        twoSubsequentLocaitons[1] = a;
    }

    public Point getLastLocation() {
        if (twoSubsequentLocaitons[1] != null) return twoSubsequentLocaitons[1];
        else return twoSubsequentLocaitons[0];
    }
}
