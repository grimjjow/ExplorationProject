package Group10.Agents;

import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Percept.IntruderPercepts;

public class ToTargetAgent implements Intruder {

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {

        Direction direction = percepts.getTargetDirection();
        Angle angle = percepts.getVision().getFieldOfView().getViewAngle();
        Distance range = percepts.getVision().getFieldOfView().getRange();
        double movement = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue();

        if (direction.equals(angle.getRadians())) {
            return new Rotate(Angle.fromRadians(direction.getRadians() - angle.getRadians()));
        }
        else {

            return new Move(new Distance(movement));
        }
    }
}
