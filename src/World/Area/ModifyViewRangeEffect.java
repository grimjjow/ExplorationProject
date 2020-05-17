package World.Area;

import Agents.Container.AgentContainer;
import World.Objects.MapObject;
import Tree.PointContainer;
import World.ViewRange;

public class ModifyViewRangeEffect extends EffectArea<ViewRange> {

    private final ViewRange viewRange;

    public ModifyViewRangeEffect(MapObject parent, PointContainer pointContainer, ViewRange viewRange) {
        super(parent, pointContainer);
        this.viewRange = viewRange;
    }

    @Override
    public ViewRange get(AgentContainer<?> agentContainer) {
        return this.viewRange;
    }
}
