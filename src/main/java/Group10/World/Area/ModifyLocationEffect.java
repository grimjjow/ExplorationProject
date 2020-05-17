package Group10.World.Area;

import Group10.Agents.Container.AgentContainer;
import Group10.World.Objects.MapObject;
import Group10.Algebra.Vector;
import Group10.Tree.PointContainer;

public abstract class ModifyLocationEffect extends EffectArea<Vector> {

    public ModifyLocationEffect(MapObject parent, PointContainer pointContainer) {
        super(parent, pointContainer);
    }

    @Override
    public abstract Vector get(AgentContainer<?> agentContainer);

}
