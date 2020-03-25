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

    public GuardAction getAction(GuardPercepts percepts) {

        //boolean lastActionExecuted = percepts.wasLastActionExecuted();
        //AreaPercepts area = percepts.getAreaPercepts();
        ScenarioGuardPercepts scenario = percepts.getScenarioGuardPercepts();
        //SmellPercepts smells = percepts.getSmells();
        //SoundPercepts sounds = percepts.getSounds();
        VisionPrecepts vision = percepts.getVision();
        ObjectPercepts objects = vision.getObjects();

        for(ObjectPercept objectPercept : objects.getAll()){

            if(objectPercept.getType() == ObjectPerceptType.Wall){
                System.out.println("found a wall");
                System.out.println(objectPercept.toString());
            }
        }

        return new Move(scenario.getMaxMoveDistanceGuard());
       // return new Rotate(scenario.getScenarioPercepts().getMaxRotationAngle());
    }
}
