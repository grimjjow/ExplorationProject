package Group10.Agents;

import Group10.Engine.Game;
import Group10.World.Dynamic.Pheromone;
import Interop.Action.*;
import Interop.Agent.Guard;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.AreaPercepts;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Scenario.ScenarioGuardPercepts;
import Interop.Percept.Scenario.ScenarioPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Smell.SmellPerceptType;
import Interop.Percept.Smell.SmellPercepts;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Sound.SoundPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.ObjectPercepts;
import Interop.Percept.Vision.VisionPrecepts;
import com.sun.security.jgss.GSSUtil;

import java.util.ArrayList;
import java.util.Random;

import static Interop.Percept.Smell.SmellPerceptType.Pheromone1;

public class BoltzmannAgent implements Guard {

    private ArrayList<int[]> memory;
    private boolean justSawIntruder = false;
    private boolean justSmelledPheromone = false;
    private boolean seeIntruder = false;
    private boolean moveFirst = false;
    private boolean rotate = true;
    private Point intruderPoint = null;
    private Distance deltaDistance;

    public BoltzmannAgent() {
        if (Game.DEBUG) System.out.println("This is the boltzmann guard");
    }


    /**
     * This represents the rule-based agent:
     *              1.) check if intruder is in visual field -> pursuit
     *              2.) if not, check if any pheromones are perceived (of type 1) -> move towards/around pheromone
     *              3.) if not, check if any other guards are perceived (we don't need all the guards exploring one point)
     *              4.) if not, check if a sentry tower is perceived -> move to it
     * @param percepts The precepts represent the world as perceived by that agent.
     * @return
     */
    public GuardAction getAction(GuardPercepts percepts) {

        boolean lastActionExecuted = percepts.wasLastActionExecuted();
        AreaPercepts area = percepts.getAreaPercepts();
        ScenarioGuardPercepts scenario = percepts.getScenarioGuardPercepts();
        ScenarioPercepts scenarioPercepts = scenario.getScenarioPercepts();
        SmellPercepts smells = percepts.getSmells();
        SoundPercepts sounds = percepts.getSounds();
        VisionPrecepts vision = percepts.getVision();
        ObjectPercepts objects = vision.getObjects();

        GuardAction action = new NoAction();
        Distance distance = new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts));
        Angle maxAngle = scenario.getScenarioPercepts().getMaxRotationAngle();

        // creating a random angle from -45 - 45 degrees
        Random rand = new Random();
        // nextInt as provided by Random is exclusive of the top value so you need to add 1
        int randomNum = rand.nextInt(((int) maxAngle.getDegrees()) + 1) + (int) maxAngle.getDegrees();
        Angle randomAngle = Angle.fromDegrees(randomNum);

        /*for(ObjectPercept objectPercept : objects.getAll()){
            if(Game.DEBUG) System.out.println(objectPercept);
        }*/

        // drop the pheromone type 1 on the last position of the intruder
        if (seeIntruder) {
            seeIntruder = false;
            System.out.println("Drop Pheromone 1");
            return new DropPheromone(Pheromone1);
        }


////////////////   winning condition    ///////////////////////////////////

        if (intruderPoint != null && justSawIntruder) {
            Distance dist = new Distance(new Point(0, 0), intruderPoint);
            //System.out.println(Math.abs(scenarioPercepts.getCaptureDistance().getValue()));
            if (Math.abs(dist.getValue()) <= Math.abs(scenarioPercepts.getCaptureDistance().getValue())) {
                System.out.println("Catched Intruder");
                intruderPoint = null;
                justSawIntruder = false;
            }
        }

//////////////// chasing the intruder (visual) ///////////////////////////////////

        ObjectPercept lastknown;

        // iterate through vision to check for intruder
        for (ObjectPercept objectPercept : objects.getAll()) {
            if (objectPercept.getType() == ObjectPerceptType.Intruder) {
                lastknown = objectPercept;
                intruderPoint = lastknown.getPoint();
                justSawIntruder = true;
                seeIntruder = true;
                Distance dis = new Distance(intruderPoint.getDistanceFromOrigin().getValue() * getSpeedModifier(percepts));
                Move chase = new Move(dis);
                return chase;
            }
            seeIntruder = false;
            // else explore
        }
        /*// move to last known position of intruder - after rotation
        if (justRotated) {
            justRotated = false;
            return new Move(intruderPoint.getDistanceFromOrigin());
        }*/

        if (justSawIntruder) {
            /*for (ObjectPercept objectPercept : objects.getAll()) {
                // move or rotate
                if(objectPercept.getType() != ObjectPerceptType.Wall){
                    Distance newDistance = new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts));
                    return new Move(newDistance);
                }
            }*/

            // check if we still hear him, although we cannot see him
            for (SoundPercept soundPercept : sounds.getAll()) {
                if (soundPercept.getType() == SoundPerceptType.Noise) {
                    Direction direction = soundPercept.getDirection();
                    Distance newdistance = new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue());
                    return correctRotation(newdistance, direction);
                }
            }
            justSawIntruder = false;

        }


//////////////// chasing the intruder 2 (sound) ////////////////////////////////

        // TODO: problem here is that the noise could be caused by a guard
        /*for(SoundPercept soundPercept : sounds.getAll()){
            // if we haven't seen an intruder
            if(soundPercept.getType() == SoundPerceptType.Noise){
                Direction dir = soundPercept.getDirection();
                return correctRoation( , dir)
            }
        }*/

//////////////// chasing the intruder 3 (smell) ////////////////////////////////
        // note that an agent just perceives the distance to a pheromone
        if (justSmelledPheromone) {
            // since we only rotate: move now the distance
            if (moveFirst) {
                moveFirst = false;
                System.out.println("Moving in some direction");
                return new Move(new Distance(deltaDistance.getValue()/2));
            }
            // check if pheromone is still perceived
            for (SmellPercept smellPercept : smells.getAll()) {
                if (smellPercept.getType() == SmellPerceptType.Pheromone1) {
                    System.out.println("Moved to the correct direction");
                    // we still perceive the same pheromone -> this means that the agent is walking towards/around the
                    // source, the intruder
                    justSmelledPheromone = false;
                    rotate = false;
                }
            }
            if(rotate){
                System.out.println("turned into wrong direction");
                moveFirst = true;
                return new Rotate(maxAngle);
            }
        }


        for (SmellPercept smellPercept : smells.getAll()) {
            if (smellPercept.getType() == SmellPerceptType.Pheromone1) {
                deltaDistance = smellPercept.getDistance();
                justSmelledPheromone = true;
                moveFirst = true;
                System.out.println("First time smelling a pheromone");
                return new Rotate(maxAngle);
            }
        }
//////////////// look for other guards (3) ////////////////////////////////

        for(ObjectPercept objectPercept : objects.getAll()){
            if(objectPercept.getType() == ObjectPerceptType.Guard){
                Point guardPoint = objectPercept.getPoint();
                Angle angleToGuard = findAngle(guardPoint, objects); // TODO: findAngle is not working perferctly, test again
                System.out.println("Angle in degrees away from the guard " + Angle.fromDegrees(-angleToGuard.getDegrees()).getDegrees());
                return new Rotate(Angle.fromRadians(-angleToGuard.getRadians()));
            }
        }

//////////////// look for sentry tower  ///////////////////////////////////

        Angle angleToSentryTower;
        // iterate through vision to check for sentry tower
        for (ObjectPercept objectPercept : objects.getAll()) {
            if (objectPercept.getType() == ObjectPerceptType.SentryTower) {
                angleToSentryTower = findAngle(objectPercept.getPoint(), objects);
                // then the sentry tower will be in our current direction
                if (angleToSentryTower.getDegrees() < 1) {
                    return new Move(objectPercept.getPoint().getDistanceFromOrigin());
                }
                return new Rotate(angleToSentryTower);
            }
        }

        // rotate if in sentry tower and currently not seeing intruder
        if (area.isInSentryTower() && !seeIntruder) {
            return new Rotate(maxAngle);
        }

//////////////// Boltzmann ///////////////////////////////////

        // TODO: remember and update previous temp
        double temperature = 5;
        double[][] actionArray = new double[3][2];

        // check if agent is inside a door or window -> then always move not rotate
        if (area.isInDoor() || area.isInWindow()) {
            Distance newDistance = new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts));
            // if guard, gets stuck in intruder wall or window
            if (!lastActionExecuted) {
                return new Rotate(Angle.fromRadians(Math.PI / 4));
            }
            return new Move(newDistance);
        }

        double actionMoveSingle = Math.exp(evaluateAction(new Move(distance), objects) * temperature);
        double actionRotateSingle = Math.exp(evaluateAction(new Rotate(Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble())), objects) * temperature);

        double sumOfAllActions = actionMoveSingle + actionRotateSingle;

        // move
        actionArray[0][0] = 1;
        actionArray[0][1] = actionMoveSingle / sumOfAllActions;
        // rotate
        actionArray[1][0] = 2;
        actionArray[1][1] = actionRotateSingle / sumOfAllActions;


        double maxAction = Double.NEGATIVE_INFINITY;
        double whatAction = 0;

        for (int i = 0; i < actionArray.length; i++) {
            //System.out.println("Max action: " + maxAction + " " + actionArray[i][1]);
            if (maxAction < actionArray[i][1]) {
                maxAction = actionArray[i][1];
                whatAction = i;
            }
        }
        if (whatAction == 0) {
            action = new Move(distance);
        }

        if (whatAction == 1 || !lastActionExecuted) {
            action = new Rotate(Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble()));

        }

        //if(Game.DEBUG) System.out.println("Action: " + action.toString());
        return action;


    }

    // TODO: check for explored space
    public double evaluateAction(GuardAction action, ObjectPercepts objects) {

        int countWalls = 0;

        if (action instanceof Move) {
            // check if future square is visited/explored/unexplored
            for (ObjectPercept objectPercept : objects.getAll()) {
                if (objectPercept.getType() == ObjectPerceptType.Wall) {
                    countWalls++;
                    /*if(objectPercept.getPoint().getDistanceFromOrigin().getValue() < 2.5){
                        countWalls = countWalls/10000;
                    }*/
                }
            }
            // evaluation based on countWalls
            return Math.exp(-countWalls / 10);

        }
        if (action instanceof Rotate) {
            // check if after rotation, the future move is visited/explored/unexplored
            // check if future square is visited/explored/unexplored
            for (ObjectPercept objectPercept : objects.getAll()) {
                if (objectPercept.getType() == ObjectPerceptType.Wall) {
                    countWalls++;
                }
            }
            // evaluation based on countWalls
            return Math.exp(countWalls / 10);
        }
        return 0;
    }

    /**
     * Given a point and the vision percepts
     *
     * @param point
     * @param objectPercepts
     * @return
     */
    public Angle findAngle(Point point, ObjectPercepts objectPercepts) {

        double yMean = 0;
        double xMean = 0;

        // calculate middle point
        for (ObjectPercept objectPercept : objectPercepts.getAll()) {
            xMean += objectPercept.getPoint().getX();
            yMean += objectPercept.getPoint().getY();
        }

        xMean = xMean / objectPercepts.getAll().size();
        yMean = yMean / objectPercepts.getAll().size();

        double m = (yMean - point.getY()) / (xMean - point.getX());
        double angle = Math.atan(m);

        return Angle.fromRadians(angle);
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

    public GuardAction correctRotation(Distance newdistance, Direction direction) {
        if (direction.getDegrees() < 5 || direction.getDegrees() > 350) {
            return new Move(newdistance);
        } else if (direction.getRadians() <= Math.PI / 4 || direction.getRadians() >= 3 * Math.PI / 2) {
            return new Rotate(Angle.fromRadians(direction.getRadians()));
        } else if (direction.getRadians() >= Math.PI) {
            //Rotate right
            return new Rotate(Angle.fromRadians(Math.PI / 4));
        } else if (direction.getRadians() <= Math.PI) {
            //Rotate left
            return new Rotate(Angle.fromRadians(-Math.PI / 4));
        } else {
            return new Rotate(Angle.fromRadians(direction.getRadians()));
        }
    }
}
