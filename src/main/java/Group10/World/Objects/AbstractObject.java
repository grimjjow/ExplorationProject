package Group10.World.Objects;

import Group10.World.Area.PropertyArea;
import Group10.Container.Container;
import Group10.Container.DataContainer;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbstractObject implements Container<DataContainer> {

    private final DataContainer area;
    private final List<PropertyArea> properties;

    private ObjectPerceptType type;

    public AbstractObject(DataContainer area, ObjectPerceptType type)
    {
        this(area, new ArrayList<>(), type);
        this.type = type;
    }

    public AbstractObject(DataContainer area, List<PropertyArea> properties, ObjectPerceptType type)
    {
        this.area = area;
        this.properties = properties;
        this.type = type;
    }

    public ObjectPerceptType getType() {
        return type;
    }

    @Override
    public DataContainer getContainer() {
        return this.area;
    }

    public void addEffects(PropertyArea<?>...effects)
    {
        this.properties.addAll(Arrays.asList(effects));
    }

    public List<PropertyArea> getProperties() {
        return this.properties;
    }

    public boolean has(Class<PropertyArea> clazz)
    {
        return this.properties.stream().anyMatch(e -> clazz.isAssignableFrom(e.getClass()));
    }

    public DataContainer getArea() {
        return area;
    }

}
