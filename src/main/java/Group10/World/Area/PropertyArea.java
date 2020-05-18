package Group10.World.Area;

import Group10.Agents.Container.AgentContainer;
import Group10.World.Objects.AbstractObject;
import Group10.Container.Container;
import Group10.Container.DataContainer;

public abstract class PropertyArea<T> implements Container<DataContainer> {

    private final AbstractObject parent;
    private final DataContainer dataContainer;

    public PropertyArea(AbstractObject parent, DataContainer dataContainer)
    {
        this.parent = parent;
        this.dataContainer = dataContainer;
    }

    public AbstractObject getParent() {
        return parent;
    }

    abstract public T get(AgentContainer<?> agentContainer);

    @Override
    public DataContainer getContainer() {
        return dataContainer;
    }

}
