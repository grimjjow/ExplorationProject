package Group10.Agents.Container;

import Group10.Algebra.Vector;
import Group10.Container.DataContainer;
import Group10.Engine.Game;
import Group10.World.Area.ModifyViewProperty;
import Group10.World.Area.ModifyViewRangeProperty;
import Group10.World.Area.PropertyArea;
import Interop.Geometry.Distance;
import Interop.Percept.Vision.FieldOfView;

import java.util.*;

public abstract class AgentContainer<T> {

    public final static double _RADIUS = 0.5;
    public Map<Cooldown, Integer> cooldowns = new HashMap<>();
    private T agent;
    private FieldOfView normalFOV;
    private DataContainer.Circle shape = null;
    private Vector direction = null;

    public AgentContainer(T agent, Vector position, Vector direction, FieldOfView normalFOV) {
        this.agent = agent;
        this.shape = new DataContainer.Circle(position, _RADIUS);
        this.direction = direction;
        this.normalFOV = normalFOV;

        assert (this.direction.length() - 1) < 1E-9;
    }

    public T getAgent() {
        return this.agent;
    }

    public DataContainer.Circle getShape() {
        return shape;
    }

    public Vector getPosition() {
        return this.shape.getCenter();
    }

    public Vector getDirection() {
        return direction;
    }

    /**
     * Generate the field of view by check the effect caused by the areas which are intersecting with the agent.
     *
     * @param areas The areas which are intersecting with the agent.
     *
     * @return A specific field of view if there is an effect, or the normal one otherwise
     */
    public FieldOfView getFOV(Set<PropertyArea> areas) {
        Optional<ModifyViewRangeProperty> viewRangeEffect = areas.stream()
                .filter(a -> a instanceof ModifyViewRangeProperty)
                .map(a -> (ModifyViewRangeProperty) a).findAny();

        if (viewRangeEffect.isPresent()) {
            return new FieldOfView(new Distance(viewRangeEffect.get().get(this).getTo()), normalFOV.getViewAngle());
        }

        Optional<ModifyViewProperty> viewAffectedArea = areas.stream()
                .filter(a -> a instanceof ModifyViewProperty)
                .map(a -> (ModifyViewProperty) a).findAny();

        if (viewAffectedArea.isPresent()) {
            return new FieldOfView(new Distance(viewAffectedArea.get().get(this) * normalFOV.getRange().getValue()), normalFOV.getViewAngle());
        }

        return this.normalFOV;
    }

    /**
     * Move the agent to as specific target.
     *
     * @param position The movement target.
     */
    public void moveTo(Vector position) {
        this.shape.translate(position.sub(this.getPosition()));
    }

    /**
     * Move the agent by a certain distance.
     *
     * @param distance The movement distance.
     */
    public void move(double distance) {
        this.shape.translate(this.direction.mul(distance, distance));
    }

    /**
     * Turns the agent by a certain amount of radians.
     *
     * @param theta The rotation angle.
     * @return The updated direction.
     */
    public Vector rotate(double theta) {
        this.direction = direction.rotated(theta);
        return this.direction;
    }

    public int getCooldown(Cooldown cooldown) {
        return this.cooldowns.getOrDefault(cooldown, 0);
    }

    /**
     * Check if th agent have a cooldown.
     *
     * @return True if there is a cooldown, false otherwise.
     */
    public boolean isCoolingDown() {
        return !this.cooldowns.isEmpty();
    }

    public boolean hasCooldown(Cooldown cooldown) {
        return this.cooldowns.containsKey(cooldown);
    }

    public void addCooldown(Cooldown cooldown, int rounds) {
        this.cooldowns.put(cooldown, rounds);
    }

    /**
     * Update cooldowns or delete them if the are finish.
     */
    public void cooldown() {
        Iterator<Map.Entry<Cooldown, Integer>> iterator = this.cooldowns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Cooldown, Integer> entry = iterator.next();
            if (entry.getValue() - 1 <= 0) {
                iterator.remove();
            } else {
                this.cooldowns.put(entry.getKey(), entry.getValue() - 1);
            }
        }
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

    public enum Cooldown {
        SPRINTING,
        PHEROMONE
    }
}
