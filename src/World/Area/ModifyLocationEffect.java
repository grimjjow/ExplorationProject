package World.Area;

import Agents.Container.AgentContainer;
import World.Objects.MapObject;
import Algebra.Vector;
import Tree.PointContainer;

public abstract class ModifyLocationEffect extends EffectArea<Vector> {

    public ModifyLocationEffect(MapObject parent, PointContainer pointContainer) {
        super(parent, pointContainer);
    }

    @Override
    public abstract Vector get(AgentContainer<?> agentContainer);

}
