package Group10.Agents.Container;

import Group10.Engine.Game;
import Group10.World.Area.PropertyArea;
import Group10.World.Area.ModifyViewProperty;
import Group10.World.Area.ModifyViewRangeProperty;
import Group10.Algebra.Vector;
import Group10.Container.DataContainer;
import Interop.Geometry.Distance;
import Interop.Percept.Vision.FieldOfView;

import java.util.*;

public abstract class AgentContainer<T> {

    public final static double _RADIUS = 0.5;

    private T agent;
    private FieldOfView normalFOV;
    private DataContainer.Circle shape = null;
    private Vector direction = null;

    public Map<Cooldown, Integer> cooldowns = new HashMap<>();

    public AgentContainer(T agent, Vector position, Vector direction, FieldOfView normalFOV)
    {
        this.agent = agent;
        this.shape = new DataContainer.Circle(position, _RADIUS);
        this.direction = direction;
        this.normalFOV = normalFOV;

        assert (this.direction.length() - 1) < 1E-9;
    }

    public T getAgent()
    {
        return this.agent;
    }

    public DataContainer.Circle getShape()
    {
        return shape;
    }

    public Vector getPosition()
    {
        return this.shape.getCenter();
    }

    public Vector getDirection()
    {
        return direction;
    }

    public FieldOfView getFOV(Set<PropertyArea> areas)
    {
        Optional<ModifyViewRangeProperty> viewRangeEffect = areas.stream()
                .filter(a -> a instanceof ModifyViewRangeProperty)
                .map(a -> (ModifyViewRangeProperty) a).findAny();

        if(viewRangeEffect.isPresent())
        {
            return new FieldOfView(new Distance(viewRangeEffect.get().get(this).getTo()), normalFOV.getViewAngle());
        }

        //---
        Optional<ModifyViewProperty> viewAffectedArea = areas.stream()
                .filter(a -> a instanceof ModifyViewProperty)
                .map(a -> (ModifyViewProperty) a).findAny();

        if(viewAffectedArea.isPresent())
        {
            return new FieldOfView(new Distance(viewAffectedArea.get().get(this) * normalFOV.getRange().getValue()), normalFOV.getViewAngle());
        }

        return this.normalFOV;
    }

    public void moveTo(Vector position)
    {
        this.shape.translate(position.sub(this.getPosition()));
    }

    public void move(double distance)
    {
        this.shape.translate(this.direction.mul(distance, distance));
    }

    /**
     * Turns the agent by a certain amount of radians and returns the updated direction.
     * @param theta
     * @return
     */
    public Vector rotate(double theta)
    {
        this.direction = direction.rotated(theta);
        return this.direction;
    }

    public int getCooldown(Cooldown cooldown)
    {
        return this.cooldowns.getOrDefault(cooldown, 0);
    }

    public boolean isCoolingDown()
    {
        return !this.cooldowns.isEmpty();
    }

    public boolean hasCooldown(Cooldown cooldown)
    {
        return this.cooldowns.containsKey(cooldown);
    }

    public void addCooldown(Cooldown cooldown, int rounds)
    {
        this.cooldowns.put(cooldown, rounds);
    }

    public void cooldown()
    {
        Iterator<Map.Entry<Cooldown, Integer>> iterator = this.cooldowns.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<Cooldown, Integer> entry = iterator.next();
            if(entry.getValue() - 1 <= 0)
            {
                iterator.remove();
            }
            else
            {
                this.cooldowns.put(entry.getKey(), entry.getValue() - 1);
            }
        }
    }

    public enum Cooldown
    {
        SPRINTING,
        PHEROMONE
    }

    @Override
    public String toString() {
        return "AgentContainer{" +
                "agent=" + agent +
                ", normalFOV=" + String.format("FieldOfView{range=%.16f, viewAngle=%.16f (rad)}", normalFOV.getRange().getValue(), normalFOV.getViewAngle().getRadians()) +
                ", shape=" + shape +
                ", direction=" + direction +
                ", cooldowns=" + cooldowns +
                '}';
    }

    public abstract AgentContainer<T> clone(Game game);
}
