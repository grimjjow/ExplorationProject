package Group10.World;

import Group10.Engine.Game;
import Group10.Algebra.Maths;
import Group10.Agents.Container.AgentContainer;
import Group10.Agents.Container.IntruderContainer;
import Group10.World.Area.PropertyArea;
import Group10.World.Dynamic.DynamicObject;
import Group10.World.Objects.AbstractObject;
import Group10.Algebra.Vector;
import Group10.Container.DataContainer;
import Interop.Agent.Guard;
import Interop.Percept.Vision.FieldOfView;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameMap {

    private final static boolean RAYS = true;

    private final GameSettings gameSettings;

    private final double rayTemp;
    
    private List<AbstractObject> abstractObjects;

    private List<DynamicObject<?>> dynamicObjects = new ArrayList<>();

    private Game game;

    public GameMap(GameSettings gameSettings, List<AbstractObject> abstractObjects)
    {
        this.gameSettings = gameSettings;
        this.abstractObjects = abstractObjects;

        this.rayTemp = this.calculateRayTemp();
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    public GameSettings getGameSettings() {
        return gameSettings;
    }

   
    public int calculateRays(FieldOfView fov)
    {
        return (int) Math.ceil(this.rayTemp * fov.getRange().getValue() * fov.getViewAngle().getRadians());
    }

    private double calculateRayTemp() {

        double min = Math.min(AgentContainer._RADIUS,  // radius of agent
                gameSettings.getScenarioPercepts().getRadiusPheromone().getValue() / gameSettings.getPheromoneExpireRounds());

        Queue<DataContainer> containers = this.abstractObjects.stream()
                .map(e -> {
                    List<DataContainer> dataContainers = new ArrayList<>();
                    dataContainers.add(e.getContainer());
                    dataContainers.addAll(e.getProperties().stream().map(PropertyArea::getContainer).collect(Collectors.toList()));
                    return dataContainers;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(LinkedList::new));

        for(DataContainer container : containers)
        {
            if(container instanceof DataContainer.Circle)
            {
                min = Math.min(min, container.getAsCircle().getRadius());
            }
            else if(container instanceof DataContainer.Polygon)
            {
                for(DataContainer.Line line : container.getAsPolygon().getLines())
                {
                    min = Math.min(min, line.getStart().distance(line.getEnd()) / 2);
                }
            }
            else
            {
                throw new IllegalArgumentException(String.format("Container error: %s", container.getClass().getName()));
            }
        }

        return 2D / min;
    }


    public List<AbstractObject> getFilteredObjects(AgentContainer<?> agentContainer, Predicate<AbstractObject> filter) {

        if(gameSettings.getViewAngle().getRadians() >= Math.PI) {
            if(filter == null)
            {
                return this.abstractObjects;
            }
            return this.abstractObjects.stream().filter(filter).collect(Collectors.toList());
        }

        Vector end = agentContainer.getPosition().add(agentContainer.getDirection());
        DataContainer.Line line = new DataContainer.Line(agentContainer.getPosition(), end);
        Vector normal = line.getNormal();
        Vector a = agentContainer.getPosition().add(normal);
        Vector b = agentContainer.getPosition().sub(normal);

        final double sign = Math.signum((b.getX() - a.getX()) * (end.getY() - a.getY()) - (b.getY() - a.getY()) * (end.getX() - a.getX()));

        assert sign != 0;

        Function<Vector, Boolean> keep = c -> {
            double r = (b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY()) * (c.getX() - a.getX());

            if(sign > 0)
            {
                return Maths.abcheck(r, 0);
            }
            else if(sign < 0)
            {
                return Maths.bacheck(r, 0);
            }
            return true;
        };

        Stream<AbstractObject> stream = this.abstractObjects
                .stream();

        if(filter != null)
        {
            stream = stream.filter(filter);
        }

        return stream
                .filter(e -> {

                    // --- Note: This only supports polygons as of now.
                    if(!(e.getContainer() instanceof DataContainer.Polygon)) return true;

                    for(Vector c : e.getArea().getAsPolygon().getPoints())
                    {
                        if(keep.apply(c)) return true;
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }

    public <T extends AbstractObject> List<T> getObjects(Class<T> clazz)
    {
        return this.abstractObjects.stream()
                .filter(e -> clazz.isAssignableFrom(e.getClass()))
                .map(object -> (T) object).collect(Collectors.toList());
    }

    public List<AbstractObject> getObjects()
    {
        return this.abstractObjects;
    }

    public List<DynamicObject<?>> getDynamicObjects() {
        return dynamicObjects;
    }

    public <A extends DynamicObject<?>> List<DynamicObject> getDynamicObjects(Class<A> clazz) {
        return getDynamicObjects().stream().filter(e -> clazz.isAssignableFrom(e.getClass())).collect(Collectors.toList());
    }

    public <T, A extends AbstractObject> boolean isInMapObject(AgentContainer<T> agentContainer, Class<A> clazz) {
        return this.abstractObjects.stream()
                .filter(e -> clazz.isAssignableFrom(e.getClass()))
                .anyMatch(e -> DataContainer.intersect(agentContainer.getShape(), e.getContainer()));
    }

    public Set<PropertyArea> getPropertyAreas(AgentContainer<?> agent)
    {
        return this.abstractObjects.stream()
                .filter(e -> !e.getProperties().isEmpty())
                .filter(e -> DataContainer.intersect(agent.getShape(), e.getContainer()))
                .flatMap((Function<AbstractObject, Stream<PropertyArea>>) object -> object.getProperties().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean isMoveIntersecting(AgentContainer<?> agentContainer, DataContainer.Polygon agentMove){
        for (AbstractObject e : getFilteredObjects(agentContainer, e -> e.getType().isSolid())) {
            if (DataContainer.intersect(e.getContainer(), agentMove)) {
                return true;
            }
        }
        return false;
    }

    public Set<ObjectPercept> getObjectPerceptsInLine(List<AbstractObject> filteredObjects, AgentContainer agentContainer, FieldOfView fov, DataContainer.Line line) {

        Map<Vector, ObjectPerceptType> objectPoints = new HashMap<>();

        for (AbstractObject mo : filteredObjects) {
            for (Vector point : DataContainer.intersectionPoints(mo.getContainer(), line)) {
                Vector relative = point
                        .sub(agentContainer.getPosition()) // move relative to agent
                        .rotated(agentContainer.getDirection().getClockDirection()); //rotated back
                if(relative.length() > 0 && fov.isInView(relative.toVexing()))
                {
                    objectPoints.put(relative, mo.getType());
                }
            }
        }

        for (IntruderContainer intruder : this.game.getIntruders()) {
            if(intruder == agentContainer || intruder.isCaptured()) continue;
            for (Vector point : DataContainer.intersectionPoints(intruder.getShape(), line)) {
                Vector relative = point
                        .sub(agentContainer.getPosition()) // move relative to agent
                        .rotated(agentContainer.getDirection().getClockDirection()); //rotated back
                if(relative.length() > 0 && fov.isInView(relative.toVexing()))
                {
                    objectPoints.put(relative, ObjectPerceptType.Intruder);
                }
            }
        }

        for (AgentContainer<Guard> guard : this.game.getGuards()) {
            if(guard == agentContainer) continue;
            for (Vector point : DataContainer.intersectionPoints(guard.getShape(), line)) {
                Vector relative = point
                        .sub(agentContainer.getPosition())
                        .rotated(agentContainer.getDirection().getClockDirection());
                if(relative.length() > 0 && fov.isInView(relative.toVexing()))
                {
                    objectPoints.put(relative, ObjectPerceptType.Guard);
                }
            }
        }

        List<Map.Entry<Vector, ObjectPerceptType>> entries = objectPoints.entrySet()
                .stream()
                .sorted(Comparator.comparingDouble(e -> line.getStart().distance(e.getKey())))
                .filter(e -> e.getKey().distance(agentContainer.getPosition()) > 0)
                .collect(Collectors.toList());

        Set<ObjectPercept> retSet = new HashSet<>();

        for (Map.Entry<Vector, ObjectPerceptType> entry : entries) {
            retSet.add(new ObjectPercept(entry.getValue(), entry.getKey().toVexing()));
            if (entry.getValue().isOpaque())
            {
                break;
            }
        }

        return retSet;
    }



    public <T> Set<ObjectPercept> getObjectPerceptsForAgent(AgentContainer<T> agentContainer, FieldOfView fov, DefaultViewRange defaultViewRange) {
        Set<ObjectPercept> objectsInSight = new HashSet<>();
        //System.out.println("angle-a: " + agentContainer.getDirection().getClockDirection());
        List<AbstractObject> filteredObjects = getFilteredObjects(agentContainer, null);
        for (Vector[] ray : getAgentVisionCone(agentContainer, fov, defaultViewRange)) {
            objectsInSight.addAll(
                    getObjectPerceptsInLine(filteredObjects, agentContainer, fov, new DataContainer.Line(ray[0], ray[1]))
                            .stream()
                            .filter(e -> fov.isInView(e.getPoint()))
                            .collect(Collectors.toList())
            );
        }

        return objectsInSight;
    }

    public <T> Set<Vector[]> getAgentVisionCone(AgentContainer<T> agentContainer, FieldOfView fov, DefaultViewRange defaultViewRange) {
        double range = fov.getRange().getValue();
        final double viewAngle = fov.getViewAngle().getRadians();

        Vector direction = agentContainer.getDirection().normalise();
        Vector startOfRay = agentContainer.getPosition();

        if(defaultViewRange != null)
        {
            startOfRay = startOfRay.add(direction.mul(defaultViewRange.getFrom()));
            range = defaultViewRange.getTo();
        }

        Vector ray = direction.mul(range).rotated(-viewAngle/2);

        int viewRays = RAYS ? this.calculateRays(fov) : gameSettings.get___viewRays();
        double stepAngle = viewAngle / viewRays;
        Set<Vector[]> objectsInSight = new HashSet<>();
        for (int rayNum = 0; rayNum < viewRays; rayNum++) {
            Vector endOfRay = startOfRay.add(ray.rotated((stepAngle * rayNum)));
            objectsInSight.add(new Vector[]{ startOfRay, endOfRay });
        }
        return objectsInSight;
    }

}
