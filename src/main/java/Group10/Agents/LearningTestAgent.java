package Group10.Agents;

import Group10.Agents.Learning.Memory;
import Group10.Agents.Learning.Reinforcement;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static Interop.Percept.Smell.SmellPerceptType.Pheromone1;

public class LearningTestAgent implements Guard {

    private final boolean justSawIntruder = false;
    private final boolean justSmelledPheromone = false;
    private final boolean seeIntruder = false;
    private final boolean moveFirst = false;
    private final boolean rotate = true;
    private final Point intruderPoint = null;
    private Distance deltaDistance;
    private Point currentLoc = new Point(0,0);
    private Direction currentDir = Direction.fromRadians(1.5*Math.PI);
    double count = 0;
    public Memory memory;
    Reinforcement r;


    public LearningTestAgent() {
        memory = new Memory();
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
        Distance distance = new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue());
        Angle maxAngle = scenario.getScenarioPercepts().getMaxRotationAngle();

        // creating a random angle from -45 - 45 degrees
        Random rand = new Random();
        // nextInt as provided by Random is exclusive of the top value so you need to add 1
        int randomNum = rand.nextInt(((int) maxAngle.getDegrees()) + 1) + (int) maxAngle.getDegrees();
        Angle randomAngle = Angle.fromDegrees(randomNum);

        count = 0;
        boolean target = false;
        for (ObjectPercept o : objects.getAll()) {
            if (o.getType() == ObjectPerceptType.Wall) {
                count++;
            }
            if (o.getType() == ObjectPerceptType.TargetArea){
                System.out.println("TARGET");
                Memory myMemory = new Memory(memory.getMemory());
                myMemory.setTargetReached(true);
                r = new Reinforcement(myMemory.getMemory(), myMemory);
                memory.clear();
            }
        }
        if (count > 1) {
            Angle rotate = Angle.fromRadians(scenarioPercepts.getMaxRotationAngle().getRadians() * Math.random());
            updateCurrentDir(rotate);
            return new Rotate(rotate);
        } else {
            Distance d = new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue());
            currentLoc = memory.updateCurrentLoc(currentLoc, d, currentDir);
            ObjectPercept obj = new ObjectPercept(ObjectPerceptType.EmptySpace, currentLoc);
            memory.add(obj);
            return new Move(d);
        }
    }
    public void updateCurrentDir(Angle angle){
        this.currentDir = Direction.fromRadians((currentDir.getRadians()+angle.getRadians())%(2*Math.PI));
        //System.out.println("New currDir: " + currentDir.getRadians()/Math.PI);
    }



}
