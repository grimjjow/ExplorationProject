/*package Agents;

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

public class BoltzmannGuard {

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
        Angle angle = scenario.getScenarioPercepts().getMaxRotationAngle();

        double temperature = 0;
        double actionMoveSingle = Math.exp(evaluateMove(new Move(distance, objects))*temperature);
        double actionRotateLeftSingle = Math.exp(evaluateMove(new Rotate(angle, objects))*temperature);
        double actionRotateRightSingle = Math.exp(evaluateMove(new Rotate(Angle.fromRadians(-angle.getRadians()),objects))*temperature);

        double sumOfAllActions = actionMoveSingle + actionRotateLeftSingle + actionRotateRightSingle;

        double [][] actionArray = new double[3][2];
        // move
        actionArray[0][0] = 1;
        actionArray[0][1] = actionMoveSingle/sumOfAllActions;
        // rotate left
        actionArray[1][0] = 2;
        actionArray[1][1] = actionRotateLeftSingle/sumOfAllActions;
        // rotate right
        actionArray[2][0] = 3;
        actionArray[2][1] = actionMoveSingle/sumOfAllActions;

        double maxAction = Double.NEGATIVE_INFINITY;
        double whatAction = 0;

        for(int i = 0; i<actionArray.length; i++){

            if(maxAction < actionArray[i][1]){
                maxAction = actionArray[i][1];
                whatAction = i;
            }
        }
        if(whatAction == 1){
            action = new Move(distance);
        }

        if(whatAction == 2){
            action = new Rotate(angle);
        }

        if(whatAction == 3){
            action = new Rotate(Angle.fromRadians(-angle.getRadians()));
        }


        return action;
    }


    public double evaluateMove(GuardAction action, ObjectPercepts objects){

        int countWalls = 0;

        if(action instanceof Move){
            // check if future square is visited/explored/unexplored
            for(ObjectPercept objectPercept : objects.getAll()){
                if(objectPercept.getType().equals("Wall")){
                    countWalls++;
                }
            }
            // evaluation based on countWalls
            return -countWalls;

        }
        if(action instanceof Rotate){
            // check if after rotation, the future move is visited/explored/unexplored

        }
    }
}
*/