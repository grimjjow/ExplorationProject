package Group10.World.Area;

import Group10.Agents.Container.AgentContainer;
import Group10.World.Objects.AbstractObject;
import Group10.Container.DataContainer;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;

public class ModifySpeedProperty extends PropertyArea<Double> {

    private double guardModifier;
    private double intruderModifier;

    public ModifySpeedProperty(AbstractObject parent, DataContainer dataContainer, double guardModifier, double intruderModifier) {
        super(parent, dataContainer);

        this.guardModifier = guardModifier;
        this.intruderModifier = intruderModifier;
    }

    @Override
    public Double get(AgentContainer<?> agentContainer) {

        if(agentContainer.getAgent() instanceof Guard)
        {
            return this.guardModifier;
        }
        else if(agentContainer.getAgent() instanceof Intruder)
        {
            return this.intruderModifier;
        }

        throw new IllegalStateException();
    }
}
