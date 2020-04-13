package Agents;

import AreaProperty.Wall;
import Environment.Square;
import Interop.Action.*;
import Interop.Geometry.*;
import Interop.Percept.*;
import Interop.Percept.Scenario.ScenarioGuardPercepts;
import Interop.Percept.Smell.SmellPercepts;
import Interop.Percept.Sound.SoundPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.ObjectPercepts;
import Interop.Percept.Vision.VisionPrecepts;

public class BoltzmannGuard implements Interop.Agent.Guard{

    public BoltzmannGuard() {
        System.out.println("This is the boltzmann guard");
    }


    public GuardAction getAction(GuardPercepts percepts) {

        boolean lastActionExecuted = percepts.wasLastActionExecuted();
        //AreaPercepts area = percepts.getAreaPercepts();
        ScenarioGuardPercepts scenario = percepts.getScenarioGuardPercepts();
        //SmellPercepts smells = percepts.getSmells();
        //SoundPercepts sounds = percepts.getSounds();
        VisionPrecepts vision = percepts.getVision();
        ObjectPercepts objects = vision.getObjects();

        GuardAction action = new NoAction();
        Distance distance = scenario.getMaxMoveDistanceGuard();
        System.out.println("Distance " + distance.getValue());
        Angle angle = scenario.getScenarioPercepts().getMaxRotationAngle();

        double temperature = 5;
        double [][] actionArray = new double[3][2];

        double actionMoveSingle = Math.exp(evaluateAction(new Move(distance), objects)*temperature);
        double actionRotateLeftSingle = Math.exp(evaluateAction(new Rotate(angle), objects)*temperature);
        double actionRotateRightSingle = Math.exp(evaluateAction(new Rotate(Angle.fromRadians(-angle.getRadians())),objects)*temperature);

        double sumOfAllActions = actionMoveSingle + actionRotateLeftSingle + actionRotateRightSingle;

        // move
        actionArray[0][0] = 1;
        actionArray[0][1] = actionMoveSingle/sumOfAllActions;
        // rotate left
        actionArray[1][0] = 2;
        actionArray[1][1] = actionRotateLeftSingle/sumOfAllActions;
        // rotate right
        actionArray[2][0] = 3;
        actionArray[2][1] = actionRotateRightSingle/sumOfAllActions;

        double maxAction = Double.NEGATIVE_INFINITY;
        double whatAction = 0;

        for(int i = 0; i<actionArray.length; i++){
            System.out.println("Max Action Score before: " + maxAction + " " + actionArray[i][1]);
            if(maxAction < actionArray[i][1]){
                maxAction = actionArray[i][1];
                whatAction = i;
            }
        }
        if(whatAction == 0){
            action = new Move(distance);
        }

        if(whatAction == 1){
            action = new Rotate(angle);
        }

        if(whatAction == 2){
            action = new Rotate(Angle.fromRadians(-angle.getRadians()));
        }
        return action;
    }


    public double evaluateAction(GuardAction action, ObjectPercepts objects){

        int countWalls = 0;

        if(action instanceof Move){
            // check if future square is visited/explored/unexplored
            for(ObjectPercept objectPercept : objects.getAll()){
                if(objectPercept.getType() == ObjectPerceptType.Wall){
                    countWalls++;
                    System.out.println("distance value to wall: " + objectPercept.getPoint().getDistanceFromOrigin().getValue());
                    /*if(objectPercept.getPoint().getDistanceFromOrigin().getValue() < 2.5){
                        countWalls = countWalls/10000;
                    }*/
                }
            }
            // evaluation based on countWalls
            return -countWalls;

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
            return countWalls;
        }
        return 0;
    }
}
