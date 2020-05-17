package World.Area;

import Agents.Container.AgentContainer;
import World.Objects.MapObject;
import Tree.Container;
import Tree.PointContainer;

public abstract class EffectArea<T> implements Container<PointContainer> {

    private final MapObject parent;
    private final PointContainer pointContainer;

    public EffectArea(MapObject parent, PointContainer pointContainer)
    {
        this.parent = parent;
        this.pointContainer = pointContainer;
    }

    public MapObject getParent() {
        return parent;
    }

    abstract public T get(AgentContainer<?> agentContainer);

    @Override
    public PointContainer getContainer() {
        return pointContainer;
    }

}
