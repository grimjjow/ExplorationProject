package Group10.World.Dynamic;

import Group10.Agents.Container.AgentContainer;
import Group10.Algebra.Vector;
import Interop.Percept.Smell.SmellPerceptType;

public class Pheromone extends DynamicObject<AgentContainer<?>> {

    private final SmellPerceptType type;
    private final double initialRadius;
    private final int initialLifetime;

    public Pheromone(SmellPerceptType type, AgentContainer<?> source, Vector center, double radius, int lifetime) {
        super(source, center, radius, lifetime);
        this.initialRadius = radius;
        this.initialLifetime = lifetime;
        this.type = type;
    }

    public double getInitialRadius()
    {
        return this.initialRadius;
    }

    public int getInitialLifetime() {
        return initialLifetime;
    }

    public SmellPerceptType getType() {
        return type;
    }

    @Override
    public Pheromone clone() {
        return new Pheromone(type, getSource(), getCenter(), getRadius(), getLifetime());
    }
}
