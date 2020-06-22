package Group10.Agents;

import Group10.Engine.Game;
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

import java.util.Random;

import static Interop.Percept.Smell.SmellPerceptType.Pheromone1;
import static Interop.Percept.Smell.SmellPerceptType.Pheromone2;

public class BoltzmannAgent implements Guard {

    private boolean justSawIntruder = false;
    private boolean justSmelledPheromone = false;
    private boolean moveFirst = false;
    private boolean rotate = true;
    private boolean dropAType1 = false;
    private boolean yell = true;
    private boolean dropType2 = false;
    private boolean continueAfterDroppingPheromone2 = false;
    private boolean right;

    private Distance deltaDistance;
    private Angle currentDir = Direction.fromRadians(1.5*Math.PI);
    private GuardAction lastAction;
    private Point intruderPoint;

    private int countExploredMoves = 0;
    private int catchedIntruders = 0;
    private int count = 0;
    double LastknownAngle = 0;
    private Point Lastpoint;
    private Point CurrentPoint;


    public BoltzmannAgent() {
        if (Game.DEBUG) System.out.println("This is the boltzmann guard");
    }


    /**
     * This represents the rule-based agent:
     *              1.) check if intruder is in visual field -> pursuit
     *              2.) if not, check if a yell is perceived -> move towards it
     *              3.) if not, check if any pheromones are perceived (of type 1) -> move towards/around pheromone
     *              4.) if not, check if any pheromones are perceived (of type 2),  -> rotate away
     *              5.) if not, check if any other guards are perceived (we don't need all the guards exploring one point)
     *              6.) if not, check if a sentry tower is perceived -> move to it
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
        int randomNum = rand.nextInt(90)-45;
        Angle randomAngle = Angle.fromDegrees(randomNum);

        // drop every 29th move a new pheromone of type 2
        // if we drop it at every case than we take to much time
        /*if(dropType2 && (countExploredMoves % 29 == 0)){
            dropType2 = false;
            continueAfterDroppingPheromone2 = true;
            //System.out.println("Dropping pheromone 2");
            return new DropPheromone(Pheromone2);
        }
        if(continueAfterDroppingPheromone2){
            continueAfterDroppingPheromone2 =  false;
            //System.out.println("Continuing last action after dropping pheromone 2");
            return lastAction;
        }*/

//////////////// check if intruder is in visual field -> pursuit (RULE 1) ///////////////////////////////////

        ////Correct Rotation in multiple turns
        //Check if we need to rotate multiple turns
        if(LastknownAngle != 0) {
            LastknownAngle = LastknownAngle % 2*Math.PI;
            //Check if intruder is right infront of us
            //It would be in interval [-10,10] Degrees
            if (LastknownAngle <= Math.PI / 36 || LastknownAngle >= 70 * Math.PI / 36) {
                //Intruder is right infront of us and we don't need this
                LastknownAngle = 0;
                System.out.println("interval [-10,10]");
                return new Move(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard());
            }
            //Check if intruder is on the right in one turn
            //Interval [315,350]
            //Check if correct - angle returns right rotation
            if (LastknownAngle > percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * 7) {
                Angle rotate = Angle.fromRadians(Math.PI*2 - LastknownAngle);
                LastknownAngle = 0;
                System.out.println("interval [315,350] ");
                return new Rotate(rotate);
            }
            //Check if is on the right behind us
            //interval[180,315]
            if(LastknownAngle > Math.PI){
                //Max rotation
                Angle rotate = Angle.fromRadians(-percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians());
                LastknownAngle += percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians();
                System.out.println("interval[180,315]" + LastknownAngle);
                return  new Rotate(rotate);
            }
            //Check if intruder is left behind us
            //interval [45,180]
            if (LastknownAngle > Math.PI/4) {
                LastknownAngle -= percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians();
                System.out.println("interval [45,180]" + LastknownAngle);
                return new Rotate(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle());
            }
            //
            if(LastknownAngle > 0);
            double lastrotation = LastknownAngle;
            LastknownAngle = 0;
            System.out.println("interval[10,45]");
            return new Rotate(Angle.fromRadians(lastrotation));
        }
//////////////// chasing the intruder (visual) ///////////////////////////////////

        ObjectPercept lastknown;

        // iterate through vision to check for intruder
        for (ObjectPercept objectPercept : objects.getAll()) {
            if (objectPercept.getType() == ObjectPerceptType.Intruder) {
                //System.out.println("SEE INTRUDER");
                lastknown = objectPercept;
                Point intruderPoint = lastknown.getPoint();
                justSawIntruder = true;
                Distance dis = new Distance(intruderPoint.getDistanceFromOrigin().getValue() * getSpeedModifier(percepts));
                return new Move(dis);
            }
            // else explore
        }

        if (justSawIntruder) {
            if(yell){
                yell = false;
                //System.out.println("yelling");
                return new Yell();
            }
            // check if we still hear him, although we cannot see him
            for (SoundPercept soundPercept : sounds.getAll()) {
                if (soundPercept.getType() == SoundPerceptType.Noise) {
                    //System.out.println("Can't see him anymore, but hear him");
                    Direction direction = soundPercept.getDirection();
                    Distance newdistance = percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard();
                    //Save the angle in radians, is between [0,2*Math.PI]
                    LastknownAngle = (direction.getRadians() * 15) % Math.PI * 2;
                    System.out.println("LastknownAngle = " + LastknownAngle);
                    return correctRotation(newdistance, direction);
                }
            }
            justSawIntruder = false;
            //System.out.println("Dropping Pheromone 1");
            return new DropPheromone(Pheromone1);
        }

//////////////// if not, check if a yell is perceived -> move towards it (Rule 2) ////////////////////////////////

        for(SoundPercept soundPercept : sounds.getAll()){
            if(soundPercept.getType() == SoundPerceptType.Yell){
                //System.out.println("I perceive a yell");
                Direction directionToYell = soundPercept.getDirection();
                Distance distanceToYell = new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue());
                return correctRotation(distanceToYell, directionToYell);
            }
            yell = true;
        }


//////////////// chasing the intruder 3 (smell) ////////////////////////////////
        // note that an agent just perceives the distance to a pheromone
        if (justSmelledPheromone) {
            // since we only rotate: move now the distance
            if (moveFirst) {
                moveFirst = false;
                //System.out.println("Moving in some direction");
                return new Move(new Distance(deltaDistance.getValue()/2));
            }
            // check if pheromone is still perceived
            for (SmellPercept smellPercept : smells.getAll()) {
                if (smellPercept.getType() == SmellPerceptType.Pheromone1) {
                    //System.out.println("Moved to the correct direction");
                    // we still perceive the same pheromone -> this means that the agent is walking towards/around the
                    // source, the intruder
                    justSmelledPheromone = false;
                    rotate = false;
                }
            }
            if(rotate){
                //System.out.println("turned into wrong direction");
                moveFirst = true;
                return new Rotate(maxAngle);
            }
        }


        for (SmellPercept smellPercept : smells.getAll()) {
            if (smellPercept.getType() == SmellPerceptType.Pheromone1) {
                deltaDistance = smellPercept.getDistance();
                justSmelledPheromone = true;
                moveFirst = true;
                //System.out.println("Perceiving pheromone 1");
                return new Rotate(maxAngle);
            }
        }
//////////////// check for pheromone type 2 ////////////////////////////////

        for(SmellPercept smellPercept : smells.getAll()){
                if((smellPercept.getType() == SmellPerceptType.Pheromone2) && (smellPercept.getDistance().getValue()>=1.5)){
                    //System.out.println("Perceiving pheromone 2");
                    return new Rotate(randomAngle);
                }
        }

//////////////// look for other guards (4) ////////////////////////////////

        for(ObjectPercept objectPercept : objects.getAll()){
            if(objectPercept.getType() == ObjectPerceptType.Guard){
                //System.out.println("RULE 3");
                Point guardPoint = objectPercept.getPoint();
                Angle angleToGuard = findAngle(guardPoint);
                //System.out.println("Angle in degrees away from the guard " + Angle.fromDegrees(-angleToGuard.getDegrees()).getDegrees());
                return new Rotate(Angle.fromRadians(-angleToGuard.getRadians()));
            }
        }

//////////////// look for sentry tower (5) ///////////////////////////////////

        Angle angleToSentryTower;
        // iterate through vision to check for sentry tower
        for (ObjectPercept objectPercept : objects.getAll()) {
            if (objectPercept.getType() == ObjectPerceptType.SentryTower) {
                //System.out.println("Perceiving Sentry Tower");
                angleToSentryTower = findAngle(objectPercept.getPoint());
                // then the sentry tower will be in our current direction
                if (angleToSentryTower.getDegrees() < 1) {
                    return new Move(objectPercept.getPoint().getDistanceFromOrigin());
                }
                return new Rotate(angleToSentryTower);
            }
        }

        // rotate if in sentry tower and currently not seeing intruder
        if (area.isInSentryTower() && justSawIntruder) {
            return new Rotate(maxAngle);
        }

//////////////// Boltzmann ///////////////////////////////////

        // check if agent is inside a door or window -> then always move not rotate
        if (area.isInDoor() || area.isInWindow()) {
            Distance newDistance = new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts));
            // if guard, gets stuck in intruder wall or window
            if (!lastActionExecuted) {
                return new Rotate(Angle.fromRadians(Math.PI / 4));
            }
            return new Move(newDistance);
        }

        double temperature = 5;
        double[][] actionArray = new double[3][2];

        double actionMoveSingle = Math.exp(evaluateAction(new Move(distance), objects) * temperature);
        double actionRotateSingle = Math.exp(evaluateAction(new Rotate(Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble())), objects) * temperature);

        double sumOfAllActions = actionMoveSingle + actionRotateSingle;

        // move
        actionArray[0][0] = 1;
        actionArray[0][1] = actionMoveSingle / sumOfAllActions;
        // rotate right
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
            dropType2 = true;
            countExploredMoves++;
            // set count to 0 is a rotation is complete: if there are no more walls in visual field
            if(objects.getAll().isEmpty()){
                count = 0;
            } else {
                for (ObjectPercept objectPercept : objects.getAll()) {
                    if (objectPercept.getType() == ObjectPerceptType.Wall) {
                        break;
                    } else {
                        count = 0;
                    }
                }
            }
            action = new Move(distance);
        }


        if (whatAction == 1 || !lastActionExecuted) {
            if(count == 0) {
                count++;
                int number =  rand.nextInt(2);
                if (number == 1) {
                    // turn right
                    right = true;
                    dropType2 = true;
                    countExploredMoves++;
                    double rotate = percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble();
                    action = new Rotate(Angle.fromRadians(rotate));
                    updateCurrentDir(Angle.fromRadians(rotate));
                } else {
                    // turn left
                    right = false;
                    dropType2 = true;
                    countExploredMoves++;
                    double rotate = -percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble();
                    action = new Rotate(Angle.fromRadians(rotate));
                    updateCurrentDir(Angle.fromRadians(rotate));
                }
                // complete a rotation: avoids to rotate in different direction if wall is infront of us
            }else{
                if (right) {
                    // turn right
                    dropType2 = true;
                    countExploredMoves++;
                    double rotate = percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble();
                    action = new Rotate(Angle.fromRadians(rotate));
                    updateCurrentDir(Angle.fromRadians(rotate));
                } else {
                    // turn left
                    dropType2 = true;
                    countExploredMoves++;
                    double rotate = -percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble();
                    action = new Rotate(Angle.fromRadians(rotate));
                    updateCurrentDir(Angle.fromRadians(rotate));
                }
            }

        }

        //if(Game.DEBUG) System.out.println("Action: " + action.toString());
        lastAction = action;
        return action;

    }

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
                    //System.out.println("\nWall seen at x: " + objectPercept.getPoint().getX() + " y: " + objectPercept.getPoint().getY() + " angle: " + findAngle(objectPercept.getPoint()).getRadians());
                    countWalls++;
                }
            }
            // evaluation based on countWalls
            return Math.exp(countWalls / 10);
        }
        return 0;
    }

    /**
     * Given a point, this method return the relative angle of the point from the y-axis. (counter-clockwise, in radians)
     * There's a method findRelativeAngle which returns the relative angle of a point to agents current direction. if negative, can convert to positive by (2*PI+(-angle))
     * @param point
     * @return Angle
     */
    public Angle findAngle(Point point) {
        Angle theta = Angle.fromRadians((Math.atan2(point.getY(),point.getX()) - Math.PI/2)%(Math.PI*2) );
        return theta;
    }


    /**
     * This method is used to find the relative angle between our current direction and given point
     * returns a double value because if value is negative then the given point is on agents current left, if positive then on its right
     * Hence angle cannot be negative so use this (+-) information to decide which side (counter/clockwise) to rotate
     *
     * @param point
     * @return
     */

    public double findRelativeAngle(Point point){
        double theta =Math.atan2(point.getY(),point.getX()) - Math.PI/2; // to set direction North
        theta = (currentDir.getRadians() - theta);
        return theta;
    }

    public void updateCurrentDir(Angle angle){
        this.currentDir = Angle.fromRadians((currentDir.getRadians()+angle.getRadians())%(2*Math.PI));
        //System.out.println("New currDir: " + currentDir.getRadians()/Math.PI);
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

    public GuardAction correctRotation(Distance newDistance, Direction direction) {
        if (direction.getDegrees() < 10 || direction.getDegrees() > 350) {
            return new Move(newDistance);
        } else if (direction.getRadians() <= Math.PI / 4 || direction.getRadians() >= 7 * Math.PI / 4) {
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

    public Angle CalculatePath(Point LastknownPoint, Point CurrentKnownPoint){
        // Substract CurrentPoint from Lastpoint
        double newx = CurrentKnownPoint.getX() - LastknownPoint.getX();
        double newy = CurrentKnownPoint.getY() - LastknownPoint.getY();
        Angle angle = Angle.fromRadians(findRelativeAngle(new Point(newx,newy)));
        return angle;
    }

}
