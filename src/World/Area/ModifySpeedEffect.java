package World.Area;

import Agents.Container.AgentContainer;
import World.Objects.MapObject;
import Tree.PointContainer;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;

public class ModifySpeedEffect extends EffectArea<Double> {

    private double guardModifier;
    private double intruderModifier;

    public ModifySpeedEffect(MapObject parent, PointContainer pointContainer, double guardModifier, double intruderModifier) {
        super(parent, pointContainer);

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
