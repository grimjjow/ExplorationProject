package Group10.World.Area;

import Group10.Agents.Container.AgentContainer;
import Group10.World.Objects.MapObject;
import Group10.Tree.PointContainer;
import Interop.Percept.Sound.SoundPerceptType;

public class SoundEffect extends EffectArea<Double> {

    private SoundPerceptType type;
    private double radius;

    public SoundEffect(MapObject parent, PointContainer pointContainer, SoundPerceptType type, double radius) {
        super(parent, pointContainer);
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
