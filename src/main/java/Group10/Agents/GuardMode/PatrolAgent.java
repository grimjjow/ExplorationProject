package Group10.Agents.GuardMode;

import Group10.Agents.Functionalities.AgentActions;
import Group10.Agents.Functionalities.AgentPerception;
import Group10.Algebra.Vector;
import Interop.Action.Action;
import Interop.Action.GuardAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Agent.Guard;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Percepts;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.ObjectPercepts;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class PatrolAgent implements Guard {

    private Vector start;
    private Queue<GuardAction> actions = new LinkedList<>();

    public GuardAction getAction(GuardPercepts percepts) {
        if (!actions.isEmpty()) {
            return actions.poll();
        }
        if (!getIntruder(percepts).getAll().isEmpty()) {

            if (start == null) {
                start = meanPosition(getIntruder(percepts));
                return (GuardAction) new Pursuit().getAction(percepts);
            } else {
                Vector now = meanPosition(getIntruder(percepts));
                Vector dir = now.sub(start).unitVector();
                double dist = now.sub(start).length();
                Vector target = dir.mul(dist * 4);

                actions.addAll(nextAction(percepts, target));
                start = now;
                return actions.poll();
            }
        } else {
            return (GuardAction) new Explore().getAction(percepts);
        }
    }


    public static ObjectPercepts getIntruder(Percepts percepts) {
        return percepts.getVision().getObjects().filter(objectPercept -> objectPercept.getType() == ObjectPerceptType.Intruder);
    }

    public static Set<SoundPercept> getSoundYell(Percepts percept) {
        return percept.getSounds().filter(soundPercept -> soundPercept.getType() == SoundPerceptType.Yell).getAll();
    }

    public Vector meanPosition(ObjectPercepts calc) {
        Vector mean = new Vector(0, 0);
        for (ObjectPercept op : calc.getAll()) {
            mean = mean.add(new Vector(op.getPoint().getX(), op.getPoint().getY()));

        }
        return mean.div(calc.getAll().size());
    }

    public Queue<GuardAction> nextAction(GuardPercepts percepts, Vector target) {

        double angle = Math.atan2(target.getY(), target.getX()) - Math.atan2(1, 0);
        int fullRotation = (int) (angle / percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians());
        double remain = (angle % percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians());

        LinkedList<GuardAction> actions = new LinkedList<>();
        for (int i = 0; i < fullRotation; i++) {
            actions.push(new Rotate(Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Math.signum(angle))));
        }
        if (Math.abs(remain) > 0) {
            actions.push(new Rotate(Angle.fromRadians(remain)));
        }
        double distance = target.length();
        int fullMoves = (int) (distance / percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue());
        double leftMoves = (double) (distance % percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue());
        for (int i = 0; i < fullMoves; i++) {
            actions.push(new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue())));
        }
        if (leftMoves > 0) {
            actions.push(new Move(new Distance(leftMoves)));
        }
        return actions;
    }

    class Explore implements Mode {

        private boolean rotationRand = new Random().nextBoolean();
        private boolean seeWall = false;
        private double wallDistance = Double.POSITIVE_INFINITY;

        public Action getAction(Percepts percepts) {
            if (percepts.wasLastActionExecuted() && !seeWall) {

                return AgentActions.getValidMove(
                        Math.min(AgentActions.getMaxMoveDistance(percepts), wallDistance - 0.7),
                        percepts
                );

            } else {

                return randomRotation(percepts);

            }
        }

        public boolean checkUpdate(Percepts percepts) {
            return true;
        }

        public void updateAgent(Percepts percepts) {

            ObjectPercepts wallPercepts = AgentPerception.getWallPercepts(percepts);

            if (wallPercepts.getAll().size() > 0) {
                wallDistance = AgentPerception.getMeanDistance(wallPercepts);
            } else {
                wallDistance = Double.POSITIVE_INFINITY;
            }

            seeWall = wallDistance < 2;

            if (wallDistance > 8 && Math.random() > 0.95) rotationRand = !rotationRand;

        }

        private Action randomRotation(Percepts percepts) {

            double rand = (2.0 - Math.random());

            rand = rand * (rotationRand ? -1.0 : 1.0);

            return AgentActions.getMaxRotate(percepts, rand);

        }


    }

    class Pursuit implements Mode {

        public Action getAction(Percepts percepts) {

            ObjectPercepts intruderPercepts = AgentPerception.getIntruderPercepts(percepts);

            double towards = -1 * AgentPerception.getMeanDirection(intruderPercepts);
            if(Math.abs(towards) < Math.random() * 2) return AgentActions.getValidMove(
                    AgentPerception.getMeanDistance(intruderPercepts), percepts
            );

            return AgentActions.getValidRotate(towards, percepts);

        }

        public boolean checkUpdate(Percepts percepts) {
            return AgentPerception
                    .getIntruderPercepts(percepts)
                    .getAll()
                    .size() > 0;
        }

        public void updateAgent(Percepts percepts) {
        }
    }

}
