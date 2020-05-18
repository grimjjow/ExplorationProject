package Group10.World.Area;

import Group10.Agents.Container.AgentContainer;
import Group10.World.Objects.AbstractObject;
import Group10.Container.DataContainer;
import Group10.World.DefaultViewRange;

public class ModifyViewRangeProperty extends PropertyArea<DefaultViewRange> {

    private final DefaultViewRange defaultViewRange;

    public ModifyViewRangeProperty(AbstractObject parent, DataContainer dataContainer, DefaultViewRange defaultViewRange) {
        super(parent, dataContainer);
        this.defaultViewRange = defaultViewRange;
    }

    @Override
    public DefaultViewRange get(AgentContainer<?> agentContainer) {
        return this.defaultViewRange;
    }
}
