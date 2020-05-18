package Group10.World.Area;

import Group10.Agents.Container.AgentContainer;
import Group10.World.Objects.AbstractObject;
import Group10.Container.DataContainer;
import Interop.Percept.Sound.SoundPerceptType;

public class SoundProperty extends PropertyArea<Double> {

    private SoundPerceptType type;
    private double radius;

    public SoundProperty(AbstractObject parent, DataContainer dataContainer, SoundPerceptType type, double radius) {
        super(parent, dataContainer);
        this.type = type;
        this.radius = radius;
    }

    public SoundPerceptType getType()
    {
        return this.type;
    }

    @Override
    public Double get(AgentContainer<?> agentContainer) {
        return this.radius;
    }

}
