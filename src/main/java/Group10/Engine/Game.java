package Group10.Engine;

import Group10.Agents.Container.AgentContainer;
import Group10.Agents.Container.GuardContainer;
import Group10.Agents.Container.IntruderContainer;
import Group10.Agents.Factories.DefaultFactory;
import Group10.World.GameMap;
import Group10.World.GameSettings;
import Group10.World.DefaultViewRange;
import Group10.World.Area.*;
import Group10.World.Dynamic.DynamicObject;
import Group10.World.Dynamic.Pheromone;
import Group10.World.Dynamic.Sound;
import Group10.World.Objects.*;
import Group10.Algebra.Vector;
import Group10.Container.DataContainer;
import Interop.Action.*;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Percept.AreaPercepts;
import Interop.Percept.GuardPercepts;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Scenario.ScenarioGuardPercepts;
import Interop.Percept.Scenario.ScenarioIntruderPercepts;
import Interop.Percept.Scenario.ScenarioPercepts;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Smell.SmellPercepts;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Sound.SoundPercepts;
import Interop.Percept.Vision.FieldOfView;
import Interop.Percept.Vision.ObjectPercepts;
import Interop.Percept.Vision.VisionPrecepts;
import Interop.Utils.Utils;
import Interop.Utils.WriteInFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Game implements Runnable {

    public final static Boolean DEBUG = true;
    public final static Random _RANDOM;
    public final static long _RANDOM_SEED = System.nanoTime();
    static {
        if(DEBUG) System.out.println("seed: " + _RANDOM_SEED);
        _RANDOM = new Random(_RANDOM_SEED);
    }

    private GameMap gameMap;
    private ScenarioPercepts scenarioPercepts;
    protected GameSettings settings;

    protected List<GuardContainer> guards = new ArrayList<>();
    protected List<IntruderContainer> intruders = new ArrayList<>();

    private Map<AgentContainer<?>, Boolean> actionSuccess = new HashMap<>();
    private Set<AgentContainer<?>> justTeleported = new HashSet<>();

    private Team winner = null;

    private AtomicBoolean runningLoop = new AtomicBoolean(false);
    private final AtomicInteger ticks;
    private long lastTick = System.nanoTime();
    private final Callback<Game> turnTickCallback;

    //---
    private final boolean queryIntent;
    private Semaphore lock = new Semaphore(1);

    private File filepath = new File("src/data.txt");
    private WriteInFile data = new WriteInFile(filepath,true);

    public Game(GameMap gameMap, final boolean queryIntent)
    {
        this(gameMap, new DefaultFactory(), queryIntent, -1, null);
    }

    public Game(GameMap gameMap, DefaultFactory agentFactory, final boolean queryIntent)
    {
        this(gameMap, agentFactory, queryIntent, -1, null);
    }


    public Game(GameMap gameMap, DefaultFactory agentFactory, final boolean queryIntent, int ticks,
                Callback<Game> turnTickCallback)
    {
        gameMap.setGame(this);
        this.turnTickCallback = turnTickCallback;
        this.ticks = new AtomicInteger(ticks);


        this.queryIntent = queryIntent;
        this.gameMap = gameMap;
        this.scenarioPercepts = gameMap.getGameSettings().getScenarioPercepts();
        this.settings = gameMap.getGameSettings();
        List<AbstractObject> solids = this.getGameMap().getObjects().stream().filter(e -> e.getType().isSolid()).collect(Collectors.toList());

        {

            Spawn.Guard guardSpawn = gameMap.getObjects(Spawn.Guard.class).get(0);
            List<DataContainer.Circle> usedSpawns = new ArrayList<>();
            agentFactory.createGuards(settings.getNumGuards()).forEach(a -> {
                Vector spawn = generateRandomSpawnLocation(guardSpawn.getArea().getAsPolygon(),
                        new DataContainer.Circle(new Vector.Origin(), AgentContainer._RADIUS), solids, usedSpawns);
                GuardContainer guardContainer = new GuardContainer(a, spawn, new Vector(0, 1).normalise(),
                        new FieldOfView(settings.getGuardViewRangeNormal(), settings.getViewAngle()));
                this.guards.add(guardContainer);
                usedSpawns.add(guardContainer.getShape());
            });
        }

        {
            Spawn.Intruder intruderSpawn = gameMap.getObjects(Spawn.Intruder.class).get(0);
            List<DataContainer.Circle> usedSpawns = new ArrayList<>();
            agentFactory.createIntruders(settings.getNumIntruders()).forEach(e -> {
                Vector spawn = generateRandomSpawnLocation(intruderSpawn.getArea().getAsPolygon(),
                        new DataContainer.Circle(new Vector.Origin(), AgentContainer._RADIUS), solids, usedSpawns);
                IntruderContainer intruderContainer = new IntruderContainer(e, spawn, new Vector(0, 1).normalise(),
                        new FieldOfView(settings.getIntruderViewRangeNormal(), settings.getViewAngle()));
                this.intruders.add(intruderContainer);
                usedSpawns.add(intruderContainer.getShape());
            });
        }
    }

    public AtomicInteger getTicks() {
        return ticks;
    }

    public Map<AgentContainer<?>, Boolean> getActionSuccess() {
        return actionSuccess;
    }

    /**
     * Generates a random point within the area. The circle is the one that is supposed to be placed inside. If the
     * circle is not intersecting with anything in the avoid list, it might be placed along the border of the area.
     * @param area The area it should be placed inside in.
     * @param circle The circle that should be placed.
     * @param avoid The objects the circle is not allowed to intersect with.
     * @param occupied The circles that already have been placed, if no other objects need to be placed just pass an EmptyList.
     * @return A point where the circle can be placed without conflicts.
     */
    public static Vector generateRandomSpawnLocation(DataContainer.Polygon area, DataContainer.Circle circle,
                                                     List<AbstractObject> avoid, List<DataContainer.Circle> occupied)
    {

        final Vector[] point = new Vector[] { circle.getCenter() };
        final double radius = circle.getRadius();

        /*
          Note: Okay, the problem with generating a random location like this is that it might run into the issue
          of infinitely looping if it is actually impossible to place the circle inside. The packing density with circles
          of a square is roughly ~90%, and thus if all areas added up plus the new one are bigger than that it is impossible
          to place it inside.

          Source: http://datagenetics.com/blog/june32014/index.html
         */
        if(area.getArea() * 0.90 < occupied.stream().mapToDouble(DataContainer::getArea).sum() + circle.getArea())
        {
            throw new IllegalArgumentException(String.format("The area can only hold %d circles with radius %.2f. The new " +
                    "circle will not be able to fit.", (int) ((area.getArea() * 0.9D) / circle.getArea()), circle.getRadius()));
        }

        int i = 0;

        do
        {

            do {
                i++;

                if(i % 1000 == 0)
                {
                    System.err.printf("Game#generateRandomSpawnLocation tried %d to generate a valid location. - This " +
                            "can happen if the spawn area for either intruders or guards does not support the specified amount " +
                            "of agents.\n", i);
                }

                point[0] = area.generateRandomLocation();
            }
            while (occupied.stream().anyMatch(e -> e.getCenter().distance(point[0]) <= radius * 2D));

        }
        while (avoid.stream().anyMatch(e -> DataContainer.intersect(e.getContainer(), new DataContainer.Circle(point[0], radius))));

        return point[0];

    }

    /**
     * This method is mainly used for UI updates or for other threads accessing any data structures in an async manner.
     * The method will acquire a mutex, and stop the game controller from updating during the method call.
     *
     * @param callback The method which should be called once the lock has been acquired.
     */
    public void query(QueryUpdate callback, boolean safeRead)
    {
        if(safeRead)
        {
            callback.call(null);
            return;
        }
        else
        {
            if(!this.queryIntent)
            {
                throw new IllegalArgumentException("queryIntent=false and safeRead=false. Please indicate a queryIntent " +
                        "so that the controller can properly lock itself or perform a safe-read operation.");
            }
        }

        try {
            lock.acquireUninterruptibly();
            callback.call(lock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<GuardContainer> getGuards() {
        return guards;
    }

    public List<IntruderContainer> getIntruders() {
        return intruders;
    }

    public GameMap getGameMap()
    {
        return gameMap;
    }

    public AtomicBoolean getRunningLoop() {
        return runningLoop;
    }

    /**
     * @return Returns the winner of the match, otherwise null.
     */
    public Team getWinner()
    {
        return winner;
    }

    /**
     * Runs the game controller in a loop.
     */
    @Override
    public void run()
    {
        runningLoop.set(true);
        while (this.winner == null && runningLoop.get())
        {
            // --- at 0 ticks pause, if -1 we want to go as fas as possible
            if(ticks.get() == 0 ){
                continue;
            }

            this.winner = this.turn();
            if(this.turnTickCallback != null)
            {
                this.turnTickCallback.call(this);
            }

            // --- > 0 -> restrict
            if(this.ticks.get() > 0)
            {
                long delta = System.nanoTime() - lastTick;
                long frameTime = (long) (1E+9D / this.ticks.get());
                lastTick = System.nanoTime();

                if(delta < frameTime)
                {
                    try {
                        Thread.sleep(TimeUnit.NANOSECONDS.toMillis(frameTime - delta));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    /**
     * Checks whether any of the teams fulfil their win ropcondition.
     * @return A team that has won, otherwise null.
     */
    private Team checkForWinner()
    {
        final long intrudersCaptured = intruders.stream().filter(IntruderContainer::isCaptured).count();
        final long intrudersWins = intruders.stream().filter(e -> e.getZoneCounter() >= settings.getTurnsInTargetAreaToWin()).count();

        if(intrudersWins > 0)
        {
            return Team.INTRUDERS;
        }

        switch (settings.getScenarioPercepts().getGameMode())
        {
            case CaptureOneIntruder:
                if(intrudersCaptured > 0)
                {
                    return Team.GUARDS;
                }

                break;
            case CaptureAllIntruders:
                if(intrudersCaptured == intruders.size() && !intruders.isEmpty())
                {
                    return Team.GUARDS;
                }
                break;
        }
        return null;
    }

    /**
     * Executes one full turn of the game.
     * @return
     */
    public final Team turn()
    {
        lockin(this::cooldown);

        // Note: Intruders move first.
        for(IntruderContainer intruder : this.intruders)
        {
            if(!(intruder.isCaptured()))
            {

                lockin(() -> {
                    final IntruderAction action = intruder.getAgent().getAction(this.generateIntruderPercepts(intruder));
                    actionSuccess.put(intruder, executeAction(intruder, action));
                    try {
                        data.WriteToFile( "intruder vector position " + intruder.getPosition().toString() + "\n");
                        data.WriteToFile( "Intruder Action " + action.toString() + "\n \n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });

                if((winner = checkForWinner()) != null)
                {
                    return winner;
                }
            }
        }

        for(GuardContainer guard : this.guards)
        {

            lockin(() -> {
                final GuardAction action = guard.getAgent().getAction(this.generateGuardPercepts(guard));
                actionSuccess.put(guard, executeAction(guard, action));
                try {
                    data.WriteToFile("Guard Position and length " + guard.getPosition().toString() + "\n");
                    data.WriteToFile("Guard Action " +action.toString()+ "\n \n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

            if((winner = checkForWinner()) != null)
            {
                return winner;
            }
        }

        return null;
    }

    private void lockin(Runnable callable)
    {
        if(this.queryIntent)
        {
            try {
                this.lock.acquire();
                callable.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                this.lock.release();
            }
        }
        else {
            callable.run();
        }

    }

    protected  <T> boolean executeAction(AgentContainer<T> agentContainer, Action action)
    {

        boolean isGuard = agentContainer.getAgent() instanceof Guard;
        boolean isIntruder = agentContainer.getAgent() instanceof Intruder;

        assert isGuard != isIntruder : "What m8?";

        if(action instanceof NoAction)
        {
            return true;
        }

        if(agentContainer.isCoolingDown())
        {
            return false;
        }

        //@performance cleanup
        Set<PropertyArea> propertyAreas = gameMap.getPropertyAreas(agentContainer);
        Optional<PropertyArea> modifySpeedEffect = propertyAreas.stream().filter(e -> e instanceof ModifySpeedProperty).findAny();
        Optional<PropertyArea> soundEffect = propertyAreas.stream().filter(e -> e instanceof SoundProperty).findAny();
        Optional<PropertyArea> modifyViewEffect = propertyAreas.stream().filter(e -> e instanceof ModifyViewProperty).findAny();
        //---


        //--- check if intruder is in target area
        if(isIntruder)
        {
            IntruderContainer intruderContainer = (IntruderContainer) agentContainer;
            if(gameMap.getObjects(TargetArea.class).stream().anyMatch(e -> DataContainer.intersect(e.getContainer(), agentContainer.getShape())))
            {
                intruderContainer.setZoneCounter(intruderContainer.getZoneCounter() + 1);
            }
            else
            {
                intruderContainer.setZoneCounter(0);
            }
        } else
        //--- check if guard is close enough to capture
        {
            FieldOfView fov = agentContainer.getFOV(this.getGameMap().getPropertyAreas(agentContainer));
            this.intruders.stream()
                    .filter(e -> e.getPosition().distance(agentContainer.getPosition()) <= settings.getScenarioPercepts().getCaptureDistance().getValue())
                    .filter(e -> Math.abs(e.getDirection().angle(agentContainer.getDirection())) <= fov.getViewAngle().getRadians() / 2)
                    .forEach(e -> e.setCaptured(true));
        }

        if(action instanceof Move || action instanceof Sprint)
        {
            final double slowdownModifier = (double) modifySpeedEffect.orElseGet(NoModify::new).get(agentContainer);
            double distance = ((action instanceof Move) ?
                    ((Move) action).getDistance().getValue() : ((Sprint) action).getDistance().getValue());

            assert distance != -1;

            final double minSprint = isGuard ?
                    settings.getGuardMaxMoveDistance().getValue() : settings.getIntruderMaxMoveDistance().getValue();
            final double maxSprint = (isGuard ?
                    settings.getGuardMaxMoveDistance().getValue() : settings.getIntruderMaxSprintDistance().getValue()) * slowdownModifier;

            if(distance > maxSprint)
            {
                return false;
            }

            final boolean isSprinting = (distance > minSprint);

            //--- check for movement collision
            {
                // --- To check for collisions, we create a bounding box. The length of this box has to be the
                // distance the agent wants to move
                // + the radius of the agent; because the center of the agent is moved
                final double length = distance + agentContainer.getShape().getRadius();


                final Vector end = agentContainer.getPosition().add(agentContainer.getDirection().mul(length));
                DataContainer.Line line = new DataContainer.Line(agentContainer.getPosition(), end);

                final Vector move = agentContainer.getDirection().mul(length);

                Vector pointA = agentContainer.getPosition().add(line.getNormal());
                Vector pointB = pointA.add(move);
                Vector pointD = agentContainer.getPosition().sub(line.getNormal());
                Vector pointC = pointD.add(move);

                DataContainer.Polygon quadrilateral = new DataContainer.Polygon(pointA, pointB, pointC, pointD);
                if(gameMap.isMoveIntersecting(agentContainer, quadrilateral))
                {
                    return false;
                }
            }

            if(isSprinting)
            {
                agentContainer.addCooldown(AgentContainer.Cooldown.SPRINTING, settings.getSprintCooldown());
            }

            //--- move and then get new effects
            gameMap.getDynamicObjects().add(new Sound(SoundPerceptType.Noise, agentContainer, settings.getMoveMaxSoundRadius().getValue(), 1));
            agentContainer.move(distance);
            Set<PropertyArea> movedPropertyAreas = gameMap.getPropertyAreas(agentContainer);
            soundEffect = movedPropertyAreas.stream().filter(e -> e instanceof SoundProperty).findAny();


            Optional<PropertyArea> locationEffect = movedPropertyAreas.stream().filter(e -> e instanceof ModifyLocationProperty).findAny();

            if(!justTeleported.contains(agentContainer) && locationEffect.isPresent())
            {
                if(locationEffect.get().getParent() instanceof TeleportArea)
                {
                    DataContainer.Polygon connectedArea = ((TeleportArea) locationEffect.get().getParent()).getConnected().getArea().getAsPolygon();
                    List<AbstractObject> solids = this.getGameMap().getObjects().stream().filter(e -> e.getType().isSolid()).collect(Collectors.toList());

                    final Vector position = generateRandomSpawnLocation(connectedArea, agentContainer.getShape(), solids,
                            new ArrayList<>());
                    agentContainer.moveTo(position);
                    justTeleported.add(agentContainer);
                }
            }
            else if(justTeleported.contains(agentContainer) && !locationEffect.isPresent())
            {
                justTeleported.remove(agentContainer);
            }

            soundEffect.ifPresent(effectArea -> {
                SoundProperty s = (SoundProperty) effectArea;
                gameMap.getDynamicObjects().add(new Sound(s.getType(), agentContainer,
                        s.get(agentContainer) * (distance / maxSprint),
                        1
                ));

            });

            return true;
        }
        else if(action instanceof Rotate)
        {
            Rotate rotate = (Rotate) action;
            if(Math.abs(rotate.getAngle().getRadians()) > settings.getScenarioPercepts().getMaxRotationAngle().getRadians())
            {
                return false;
            }

            agentContainer.rotate(rotate.getAngle().getRadians());
            return true;
        }
        else if(action instanceof Yell)
        {
            gameMap.getDynamicObjects().add(new Sound(
                    SoundPerceptType.Yell,
                    agentContainer,
                    settings.getYellSoundRadius().getValue(),
                    1
            ));
            return true;
        }
        else if(action instanceof DropPheromone)
        {

            //--- check whether there is already one in this place
            if(gameMap.getDynamicObjects(Pheromone.class).stream()
                    .filter(e -> e.getSource().getClass().isAssignableFrom(agentContainer.getClass()))
                    .anyMatch(e -> DataContainer.intersect(e.getAsCircle(),
                            new DataContainer.Circle(agentContainer.getPosition(), scenarioPercepts.getRadiusPheromone().getValue())))
            )
            {
                return false;
            }

            agentContainer.addCooldown(AgentContainer.Cooldown.PHEROMONE, gameMap.getGameSettings().getScenarioPercepts().getPheromoneCooldown());

            DropPheromone dropPheromone = (DropPheromone) action;

            gameMap.getDynamicObjects().add(new Pheromone(
                    dropPheromone.getType(),
                    agentContainer,
                    agentContainer.getPosition(),
                    scenarioPercepts.getRadiusPheromone().getValue(),
                    settings.getPheromoneExpireRounds()
            ));
            return true;
        }

        throw new IllegalArgumentException(String.format("Tried to execute an unsupported action: %s", action));

    }

    private void cooldown()
    {
        // --- iterate over dynamic objects (sounds) and adjust lifetime or remove
        {
            Iterator<DynamicObject<?>> iterator = gameMap.getDynamicObjects().iterator();
            while (iterator.hasNext()) {
                DynamicObject<?> e = iterator.next();
                e.setLifetime(e.getLifetime() - 1);
                if(e.getLifetime() <= 0)
                {
                    iterator.remove();
                }
                else if(e instanceof Pheromone)
                {
                    Pheromone p = (Pheromone)e;
                    e.setRadius(p.getInitialRadius() * (p.getLifetime() / (double) p.getInitialLifetime()));
                }
            }
        }

        // --- sprint cooldown
        {
            this.intruders.forEach(AgentContainer::cooldown);
            this.guards.forEach(AgentContainer::cooldown);
        }

    }

    protected GuardPercepts generateGuardPercepts(GuardContainer guard)
    {
        return new GuardPercepts(
                generateVisionPercepts(guard),
                generateSoundPercepts(guard),
                generateSmellPercepts(guard),
                generateAreaPercepts(guard),
                new ScenarioGuardPercepts(this.settings.getScenarioPercepts(), this.settings.getGuardMaxMoveDistance()),
                this.actionSuccess.getOrDefault(guard, true)
        );
    }

    protected IntruderPercepts generateIntruderPercepts(IntruderContainer intruder)
    {

        final Vector direction = this.gameMap.getObjects(TargetArea.class).get(0).getContainer().getCenter()
                .sub(intruder.getPosition()).normalise();

        final double angle = Math.acos(intruder.getDirection().dot(direction));

        return new IntruderPercepts(
                Direction.fromRadians(angle),
                generateVisionPercepts(intruder),
                generateSoundPercepts(intruder),
                generateSmellPercepts(intruder),
                generateAreaPercepts(intruder),
                new ScenarioIntruderPercepts(
                        this.settings.getScenarioPercepts(),
                        this.settings.getTurnsInTargetAreaToWin(),
                        this.settings.getIntruderMaxMoveDistance(),
                        this.settings.getIntruderMaxSprintDistance(),
                        intruder.getCooldown(AgentContainer.Cooldown.SPRINTING)
                ),
                this.actionSuccess.getOrDefault(intruder, true)
        );
    }

    private <T> VisionPrecepts generateVisionPercepts(AgentContainer<T> agentContainer)
    {
        Set<PropertyArea> propertyAreas = gameMap.getPropertyAreas(agentContainer);
        final FieldOfView fov = agentContainer.getFOV(propertyAreas);

        Optional<ModifyViewRangeProperty> viewRangeEffect = propertyAreas.stream()
                .filter(a -> a instanceof ModifyViewRangeProperty)
                .map(a -> (ModifyViewRangeProperty) a).findAny();

        DefaultViewRange defaultViewRange = null;
        if(viewRangeEffect.isPresent())
        {
            defaultViewRange = viewRangeEffect.get().get(agentContainer);
        }

        return new VisionPrecepts(
                fov,
                new ObjectPercepts(gameMap.getObjectPerceptsForAgent(agentContainer, fov, defaultViewRange))
        );
    }

    private <T> AreaPercepts generateAreaPercepts(AgentContainer<T> agentContainer)
    {
        return new AreaPercepts(
                gameMap.isInMapObject(agentContainer, Window.class),
                gameMap.isInMapObject(agentContainer, Door.class),
                gameMap.isInMapObject(agentContainer, SentryTower.class),
                justTeleported.contains(agentContainer)
        );
    }

    private <T> SoundPercepts generateSoundPercepts(AgentContainer<T> agentContainer)
    {
        return new SoundPercepts(this.gameMap.getDynamicObjects().stream()
                .filter(e -> e instanceof Sound)
                .filter(e -> agentContainer.getPosition().distance(e.getCenter()) <= e.getRadius())
                .map(dynamicObject -> {
                    Sound sound = (Sound) dynamicObject;
                    double angle = (_RANDOM.nextBoolean() ? 1 : -1) * (0.174533 * _RANDOM.nextDouble());
                    return new SoundPercept(
                            sound.getType(),
                            Direction.fromRadians(Utils.mod((dynamicObject.getCenter().getClockDirection() - agentContainer.getPosition().getClockDirection()) + angle, Utils.TAU))
                    );
                }).collect(Collectors.toUnmodifiableSet()));
    }

    private <T> SmellPercepts generateSmellPercepts(AgentContainer<T> agentContainer)
    {
        return new SmellPercepts(this.gameMap.getDynamicObjects().stream()
                .filter(e -> e instanceof Pheromone && agentContainer.getClass().isAssignableFrom(e.getSource().getClass()))
                .filter(e -> DataContainer.intersect(e.getAsCircle(), agentContainer.getShape()))
                .map(dynamicObject -> {
                    Pheromone pheromone = (Pheromone) dynamicObject;
                    return new SmellPercept(
                            pheromone.getType(),
                            new Distance(dynamicObject.getCenter().distance(agentContainer.getPosition()))
                    );
                }).collect(Collectors.toUnmodifiableSet()));
    }

    public enum Team
    {
        INTRUDERS,
        GUARDS
    }

    public interface QueryUpdate
    {
        /**
         * Is called once the game logic thread has been locked, and operations can be sable performed. -
         *  Note (!!!): It is the responsibility of the caller to release {@link Semaphore#release()} the lock once
         *  it has completed its operations.
         * @param lock
         */
        void call(Semaphore lock);
    }

}

