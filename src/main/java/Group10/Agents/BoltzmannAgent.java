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
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Sound.SoundPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.ObjectPercepts;
import Interop.Percept.Vision.VisionPrecepts;

import java.util.ArrayList;
import java.util.Random;

public class BoltzmannAgent implements Guard{

    private ArrayList<int[]> memory;
    private boolean justSawIntruder = false;
    private boolean rotateAgain = false;
    private Point intruderPoint = null;
    private int catchedIntruders = 0;


    public BoltzmannAgent() {
        if(Game.DEBUG) System.out.println("This is the boltzmann guard");
    }


    public GuardAction getAction(GuardPercepts percepts) {

        boolean lastActionExecuted = percepts.wasLastActionExecuted();
        AreaPercepts area = percepts.getAreaPercepts();
        ScenarioGuardPercepts scenario = percepts.getScenarioGuardPercepts();
        ScenarioPercepts scenarioPercepts = scenario.getScenarioPercepts();
        //SmellPercepts smells = percepts.getSmells();
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


////////////////   winning condition    ///////////////////////////////////

        if(intruderPoint!=null && justSawIntruder){
            Distance dist = new Distance(new Point(0,0), intruderPoint);
            //System.out.println(Math.abs(scenarioPercepts.getCaptureDistance().getValue()));
            if(Math.abs(dist.getValue())<=Math.abs(scenarioPercepts.getCaptureDistance().getValue())){
                catchedIntruders++;
                System.out.println("Catched Intruder");
                intruderPoint = null;
                justSawIntruder = false;
            }
        }

        // TODO: do for all intruders:
        // set:
        // intruderPoint = null
        // justSawintruder = false
        // introduce new counter, remove intruder from field


//////////////// look for sentry tower  ///////////////////////////////////
        // TODO: is this useful?

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


//////////////// chasing the intruder (visual) ///////////////////////////////////

        ObjectPercept lastknown;

        // iterate through vision to check for intruder
        for (ObjectPercept objectPercept : objects.getAll()) {
            if (objectPercept.getType() == ObjectPerceptType.Intruder) {
                lastknown = objectPercept;
                intruderPoint = lastknown.getPoint();
                GuardAction chase = chaseIntruder(lastknown, objects);
                justSawIntruder = true;
                return chase;
            }
            // else explore
        }
        /*// move to last known position of intruder - after rotation
        if (justRotated) {
            justRotated = false;
            return new Move(intruderPoint.getDistanceFromOrigin());
        }*/

        if(justSawIntruder){
            /*for (ObjectPercept objectPercept : objects.getAll()) {
                // move or rotate
                if(objectPercept.getType() != ObjectPerceptType.Wall){
                    Distance newDistance = new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts));
                    return new Move(newDistance);
                }
            }*/

            // check if we still hear him, although we cannot see him
            for(SoundPercept soundPercept : sounds.getAll()) {
                if(soundPercept.getType() == SoundPerceptType.Noise){
                    Direction direction = soundPercept.getDirection();
                    // check if direction in degrees is over 180 (pi in radians)
                    //if(direction.getRadians()>Math.PI){
                        // then turn left

                    //}
                    if(direction.getRadians() >= maxAngle.getRadians()){
                        return new Rotate(maxAngle);
                    }
                    return new Rotate(Angle.fromRadians(direction.getRadians()));
                }
            }
            justSawIntruder = false;
        }
//////////////// chasing the intruder 2 (sound) ////////////////////////////////

        for(SoundPercept soundPercept : sounds.getAll()){
            // if we haven't seen an intruder
        }


//////////////// Boltzmann ///////////////////////////////////

        // TODO: remember and update previous temp
        double temperature = 5;
        double[][] actionArray = new double[3][2];

        // check if agent is inside a door or window -> then always move not rotate
        if (area.isInDoor() || area.isInWindow()) {
            Distance newDistance = new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts));
            // if guard, gets stuck in intruder wall or window
            if(!lastActionExecuted){
                return new Rotate(Angle.fromRadians(Math.PI/4));
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
    public double evaluateAction(GuardAction action, ObjectPercepts objects){

        int countWalls = 0;

        if(action instanceof Move){
            // check if future square is visited/explored/unexplored
            for(ObjectPercept objectPercept : objects.getAll()){
                if(objectPercept.getType() == ObjectPerceptType.Wall){
                    countWalls++;
                    /*if(objectPercept.getPoint().getDistanceFromOrigin().getValue() < 2.5){
                        countWalls = countWalls/10000;
                    }*/
                }
            }
            // evaluation based on countWalls
            return Math.exp(-countWalls/10);

        }
        if(action instanceof Rotate){
            // check if after rotation, the future move is visited/explored/unexplored
            // check if future square is visited/explored/unexplored
            for(ObjectPercept objectPercept : objects.getAll()){
                if(objectPercept.getType() == ObjectPerceptType.Wall){
                    countWalls++;
                }
            }
            // evaluation based on countWalls
            return Math.exp(countWalls/10);
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
    public Angle findAngle(Point point, ObjectPercepts objectPercepts){

        double yMean = 0;
        double xMean = 0;

        // calculate middle point
        for(ObjectPercept objectPercept : objectPercepts.getAll()){
            xMean += objectPercept.getPoint().getX();
            yMean += objectPercept.getPoint().getY();
        }

        xMean = xMean/objectPercepts.getAll().size();
        yMean = yMean/objectPercepts.getAll().size();

        double m = (yMean-point.getY())/(xMean-point.getX());
        double angle = Math.atan(m);

        return Angle.fromRadians(angle);
    }

    /**
     * chaseIntruder checks when the intruder is in out visual field if he is right infront of us -> return move
     *                                                                  or if he have to rotate a bit such that he is right infront of us
     * @param objectPercept
     * @param objects
     * @return
     */
    public GuardAction chaseIntruder(ObjectPercept objectPercept, ObjectPercepts objects){
        Point posIntruder = objectPercept.getPoint();
        Angle angle = findAngle(posIntruder, objects);
        // if the angle is in this range, then we do not have to change direction
        return new Move(posIntruder.getDistanceFromOrigin());
        /*if((angle.getDegrees() <= 30) && (angle.getDegrees() >= -30)){
            return new Move(objectPercept.getPoint().getDistanceFromOrigin());
        }
        else{
            // rotate such that the intruder is right in front of us
            justRotated  = true;
            return new Rotate(angle);
        }*/
    }


    private double getSpeedModifier(GuardPercepts guardPercepts)
    {
        SlowDownModifiers slowDownModifiers =  guardPercepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers();
        if(guardPercepts.getAreaPercepts().isInWindow())
        {
            return slowDownModifiers.getInWindow();
        }
        else if(guardPercepts.getAreaPercepts().isInSentryTower())
        {
            return slowDownModifiers.getInSentryTower();
        }
        else if(guardPercepts.getAreaPercepts().isInDoor())
        {
            return slowDownModifiers.getInDoor();
        }

        return 1;
    }

    public double changeTemp(double startTemp){

        double newTemp = 0;

        // decrease temperature over time to get from exploration to exploitation
        // TODO: make better function
        newTemp = startTemp*0.9;

        return newTemp;
    }

    public ArrayList<int[]> getMemory() {
        return memory;
    }
    // for now: store all the nodes visited
    public ArrayList<int[]> updateMemory(){

        if(memory == null){
            memory = new ArrayList<>();
            int[] firstIter = new int[]{0, 0};
            memory.add(firstIter);
        }else{


            // get last action executed, update coordinates and add to the list
        }
        return memory;
    }
}
