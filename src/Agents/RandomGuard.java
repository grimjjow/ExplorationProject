package Agents;

import Interop.Action.GuardAction;
import Interop.Action.Move;
import Interop.Action.NoAction;
import Interop.Action.Rotate;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Scenario.ScenarioGuardPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.ObjectPercepts;
import Interop.Percept.Vision.VisionPrecepts;


public class RandomGuard implements Interop.Agent.Guard {

    public RandomGuard() {
        System.out.println("Random Guard");
    }

    /**
     * it makes a move (80% probability),  it makes a rotate (20% probability)
     *
     * @param percepts The precepts represent the world as perceived by that agent.
     * @return GuardAction
     */
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

        int randomNumber = (int) (Math.random() * 10);

        // 0.2 chance to rotate and 0.8 chance to move
        if(randomNumber>=2){
            action = new Move(distance);
        }
        else{
            action = new Rotate(angle);
        }
        return action;
    }
}
