package Group10.World.Dynamic;

import Group10.Algebra.Vector;
import Group10.Container.DataContainer;

public abstract class DynamicObject<T> extends DataContainer.Circle implements Cloneable {

    private T source;
    private int lifetime;
    private double radius;

    public DynamicObject(T source, Vector center, double radius, int lifetime) {
        super(center, -1);
        this.radius = radius;
        this.source = source;
        this.lifetime = lifetime;
    }

    public T getSource() {
        return source;
    }

    @Override
    public double getRadius()
    {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    @Override
    public abstract DynamicObject<T> clone();
}
