package Agents;

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

public class Guard implements Interop.Agent.Guard {

    private int statecounter = 0;

    public Guard(){
        System.out.println("Simple Guard");
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

        switch(statecounter){
            case 0:
                System.out.println("Move until get stuck");
                // if last executed failed, then change to statecounter 1 and rotate 45 degrees
                if(!lastActionExecuted){
                    // stuck, because of wall -> rotate
                    statecounter = 1;
                }else{
                    action = new Move(distance);
                }
                break;
            case 1:
                System.out.println("Rotate");
                action = new Rotate(angle);
                if(lastActionExecuted){
                    // rotate again
                    statecounter = 0;
                }else{
                    action = new Rotate(angle);
                }
                break;
        }

        return action;
    }
}
