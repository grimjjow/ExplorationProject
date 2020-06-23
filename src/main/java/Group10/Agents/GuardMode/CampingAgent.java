package Group10.Agents.GuardMode;

import Group10.Agents.Functionalities.ActionsQueue;
import Group10.Algebra.Vector;
import Group10.Engine.Game;
import Interop.Action.GuardAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Agent.Guard;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.VisionPrecepts;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

// this is a sample guard that has a very simple chasing approach and camps around the target
// Todo:
// improve chasing
// make the guard camp not specifically the target, but the set area around it

public class CampingAgent implements Guard {

    private Angle rotation = Angle.fromRadians(0);
    private boolean foundTargetArea = false;

    private Queue<ActionsQueue<GuardAction>> chasing = new LinkedList<>();
    private Queue<ActionsQueue<GuardAction>> guarding = new LinkedList<>();

    @Override
    public GuardAction getAction(GuardPercepts percepts) {

        Vector intruderPosition = seenIntruder(percepts);

        if (intruderPosition != null || !chasing.isEmpty()) {
            if (intruderPosition != null) {
                guarding.clear();
                chasing.clear();
                chasing.addAll(
                        moveToPoint(percepts, new Vector(0, 1), new Vector.Origin(), intruderPosition)
                );
            }

            if (!chasing.isEmpty()) {
                return chasing.poll().getAction();
            }
        } else {
            VisionPrecepts vision = percepts.getVision();
            Set<ObjectPercept> objectPercepts = vision.getObjects().getAll();

            if (!foundTargetArea) {
                for (ObjectPercept object : objectPercepts) {
                    if (object.getType().equals(ObjectPerceptType.TargetArea)) {
                        foundTargetArea = true;
                        break;
                    }
                }
            }
            if (foundTargetArea) {
                if (guarding.isEmpty()) {
                    guarding.addAll(guardTarget(percepts));
                }

                return guarding.poll().getAction();
            }
        }

        if (!percepts.wasLastActionExecuted()) {
            Angle newRotation = Angle.fromRadians(
                    percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble()
            );
            return new Rotate(newRotation);
        } else {
            Distance movingDistance = new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts));

            return new Move(movingDistance);
        }
    }

    private double getSpeedModifier(GuardPercepts guardPercepts) {

        SlowDownModifiers slowDownModifiers = guardPercepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers();

        if (guardPercepts.getAreaPercepts().isInWindow()) {

            return slowDownModifiers.getInWindow();
        } else if (guardPercepts.getAreaPercepts().isInSentryTower()) {

            return slowDownModifiers.getInSentryTower();
        } else if (guardPercepts.getAreaPercepts().isInDoor()) {

            return slowDownModifiers.getInDoor();
        }

        return 1;
    }

    protected Queue<ActionsQueue<GuardAction>> moveToPoint(GuardPercepts percepts, Vector direction, Vector source, Vector target) {

        Queue<ActionsQueue<GuardAction>> actionsQueues = new LinkedList<>();

        Vector moveDirection = target.sub(source);
        double rotationAngle = direction.angledSigned(moveDirection);

        if (Math.abs(rotationAngle) > 1E-1) {
            actionsQueues.add(ActionsQueue.add(this, new Rotate(Angle.fromRadians(rotationAngle))));
        }

        final double maxMoveDistance = percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts);
        final double distance = target.distance(source);

        final int fullMoves = (int) (distance / maxMoveDistance);
        final double remainder = distance % percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue();

        for (int i = 0; i < fullMoves; i++) {
            actionsQueues.add(
                    ActionsQueue.add(this, new Move(new Distance(maxMoveDistance)))
            );
        }
        if (remainder > 0) {
            actionsQueues.add(
                    ActionsQueue.add(this, new Move(new Distance(remainder)))
            );
        }

        return actionsQueues;
    }

    public Vector seenIntruder(GuardPercepts percepts) {

        Set<ObjectPercept> intruders = percepts.getVision().getObjects()
                .filter(e -> e.getType() == ObjectPerceptType.Intruder)
                .getAll();

        if (!intruders.isEmpty()) {
            Vector centre = new Vector.Origin();
            for (ObjectPercept e : intruders) {
                centre = centre.add(Vector.from(e.getPoint()));
            }
            return centre.mul(1D / intruders.size());
        }

        return null;
    }

    private Queue<ActionsQueue<GuardAction>> guardTarget(GuardPercepts percepts) {

        Set<ObjectPercept> guardView = percepts.getVision()
                .getObjects()
                .filter(e -> e.getType() == ObjectPerceptType.TargetArea)
                .getAll();

        Vector max = null;

        final Vector vectorDirection = new Vector(0, 1);

        for (ObjectPercept object : guardView) {
            Vector tmp = Vector.from(object.getPoint());
            if (max == null || max.length() < tmp.length() && vectorDirection.angle(max) > vectorDirection.angle(tmp)) {
                max = tmp;
            }
        }
        if (max != null && max.length() >= 0.1) {
            return moveToPoint(percepts, new Vector(0, 1), new Vector.Origin(), max);
        }

        Angle newRotation = Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble());

        rotation = Angle.fromRadians(rotation.getRadians() + newRotation.getRadians());

        if (rotation.getDegrees() > 360) {
            rotation = Angle.fromDegrees(rotation.getDegrees() - 360);
        } else if (rotation.getDegrees() < 0) {
            rotation = Angle.fromDegrees(rotation.getDegrees() + 360);
        }
        Queue<ActionsQueue<GuardAction>> actions = new LinkedList<>();
        actions.add(ActionsQueue.add(this, new Rotate(newRotation)));
        return actions;
    }
}
