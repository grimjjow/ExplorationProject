package Group10.World.Area;

import Group10.Agents.Container.AgentContainer;
import Group10.World.Objects.AbstractObject;
import Group10.Algebra.Vector;
import Group10.Container.DataContainer;

public abstract class ModifyLocationProperty extends PropertyArea<Vector> {

    public ModifyLocationProperty(AbstractObject parent, DataContainer dataContainer) {
        super(parent, dataContainer);
    }

    @Override
    public abstract Vector get(AgentContainer<?> agentContainer);

}
