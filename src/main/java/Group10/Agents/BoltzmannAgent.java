package Group10.Agents;

import Group10.Agents.Learning.Memory;
import Group10.Algebra.Maths;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.rmi.MarshalException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Float.NaN;

public class BoltzmannAgent implements Guard{

    Memory memory = new Memory();
    private boolean justSawIntruder = false;
    private final boolean rotateAgain = false;
    private Point intruderPoint = null;
    private int catchedIntruders = 0;
    private Point currentLoc = new Point(0,0);
    private Angle currentDir = Direction.fromRadians(1.5*Math.PI);
    double count = 0;



    public BoltzmannAgent() {
        if(Game.DEBUG) System.out.println("This is the boltzmann guard");
    }


    public GuardAction getAction(GuardPercepts percepts) {
       // System.out.println(memory);
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
        // TODO: is this useful? -> at some point yes; when using boltzmann in different strategies

        Angle angleToSentryTower;
        // iterate through vision to check for sentry tower
        for (ObjectPercept objectPercept : objects.getAll()) {
            if(objectPercept.getType() == ObjectPerceptType.SentryTower) {
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
                    Distance newdistance = new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue());
                    // check if direction in degrees is over 180 (pi in radians)
                    System.out.println(direction.getDegrees());
                    //if(direction.getRadians()>Math.PI){
                        // then turn left

                    //}
                    if(direction.getDegrees() < 5  || direction.getDegrees() > 350){
                        System.out.println("Hear and move");
                        return new Move(newdistance);
                    }
                    else if(direction.getRadians()<= Math.PI/4 || direction.getRadians()>= 3*Math.PI/2){
                        System.out.println("we hear but dont rotate to much");
                        return new Rotate(Angle.fromRadians(direction.getRadians()));
                    }
                    else if(direction.getRadians() >= Math.PI){
                        //Rotate right
                        System.out.println("Rotate Right");
                        return new Rotate(Angle.fromRadians(Math.PI/4));
                    }
                    else if(direction.getRadians() <= Math.PI){
                        //Rotate left
                        System.out.println("Rotate Left");
                        return new Rotate(Angle.fromRadians(-Math.PI/4));
                    }
                    else {
                        return new Rotate(Angle.fromRadians(direction.getRadians()));
                    }
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
        if(area.isInDoor() || area.isInWindow()){
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
        if(whatAction == 0){
            action = new Move(distance);
            updateCurrentLoc(distance);
        }

        if(whatAction == 1 || !lastActionExecuted){
              double rotate = percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble();
              action = new Rotate(Angle.fromRadians(rotate));
              updateCurrentDir(Angle.fromRadians(rotate));
        }

        //if(Game.DEBUG) System.out.println("Action: " + action.toString());
        count++;
        return action;
    }


    // TODO: check for explored space
    public double evaluateAction(GuardAction action, ObjectPercepts objects){

        int countWalls = 0;
        ObjectPercept temp;
        if(action instanceof Move){
            // check if future square is visited/explored/unexplored
            for(ObjectPercept objectPercept : objects.getAll()){
                if(objectPercept.getType() == ObjectPerceptType.Wall){

                    temp = new ObjectPercept(ObjectPerceptType.Wall,findRelativePoint(objectPercept, objects)); //here you calculate relative wall positions
                    addMemory(temp);
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
                    temp = new ObjectPercept(ObjectPerceptType.Wall,findRelativePoint(objectPercept, objects)); //here you calculate relative wall positions
                    addMemory(temp);
                    countWalls++;
                }
            }
            // evaluation based on countWalls
            return Math.exp(countWalls/10);
        }
        return 0;
    }

    private void updateCurrentLoc(Distance distance){
        double cos = Math.cos(currentDir.getRadians());
        double xbar = distance.getValue() * cos * -1;
        //System.out.println(xbar);
        double sin = Math.sin(currentDir.getRadians());
        double ybar = distance.getValue() * sin;
        //System.out.println(ybar);
        this.currentLoc = new Point(currentLoc.getX() + xbar,currentLoc.getY() + ybar);
        ObjectPercept memoryLoc = new ObjectPercept(ObjectPerceptType.EmptySpace,currentLoc);
        addMemory(memoryLoc);
        //System.out.println(currentLoc);
    }


    private void updateCurrentDir(Angle angle){
        //System.out.println(currentDir.getRadians()/Math.PI);
        this.currentDir = Angle.fromRadians((currentDir.getRadians()+angle.getRadians())%(2*Math.PI));
    }


    /**
     * @param  o ObjectPercept
     * @return objects relative point to our memory-origin
     * */
    public Point findRelativePoint(ObjectPercept o, ObjectPercepts objects){
        //Angle angleToWall = findAngle(o.getPoint(), objects);
        double cos = Math.cos(findRelativeAngle(currentDir));
        double xbar = findDistance(currentLoc,o.getPoint()) * cos * -1;
        //System.out.println(cos);
        double sin = Math.sin(findRelativeAngle(currentDir));
        double ybar = findDistance(currentLoc,o.getPoint()) * sin;

        xbar = currentLoc.getX() + xbar;
        System.out.println("x: " + xbar);
        ybar = currentLoc.getY() + ybar;
        System.out.println("y: " + ybar);
        //System.out.println(sin);
        Point relativePoint = null;
        if (xbar != NaN && ybar != NaN) {
            relativePoint = new Point(xbar,ybar);
        }

        //System.out.println(" x: " + relativePoint.getX() + " \n y: " + relativePoint.getY());
        return relativePoint;
    }


    public double findRelativeAngle(Angle angle)
    {
        double theta = Math.abs(currentDir.getRadians() + angle.getRadians()%(2*Math.PI));
        return theta;
    }

    /**
     * Given a point and the vision percepts
     *
     * @param point
     * @param objectPercepts
     * @return
     */


    public Angle findAngle(Point point, ObjectPercepts objectPercepts){ //This method is faulty!

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


    public double findDistance(Point centerPt, Point targetPt){
        return Math.sqrt((targetPt.getY()-centerPt.getY())*(targetPt.getY()-centerPt.getY()) + (targetPt.getX()-centerPt.getX()) * (targetPt.getX()-centerPt.getX()));
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

    public boolean addMemory(ObjectPercept objectPercept){
        if (!memory.contains(objectPercept)){
            memory.add(objectPercept);
            exportToCsv(memory, "guardMemory.csv");
            return true;
        } else return false;
    }

    public Memory getMemory() {
        return memory;
    }

    private static void exportToCsv(List<ObjectPercept> memory, String filename) {

        try (PrintWriter writer = new PrintWriter(new File(filename))) {

            StringBuilder sb = new StringBuilder();
            sb.append(" Point_X, Point_Y, Object Type\n");
            for (ObjectPercept o : memory) {
                sb.append(o.getPoint().getX());
                sb.append(",");
                sb.append(o.getPoint().getY());
                sb.append(",");
                sb.append(o.getType());
                sb.append("\n");
            }


            writer.write(sb.toString());


        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }
}
