package Group10.World.Area;

import Group10.Agents.Container.AgentContainer;
import Group10.World.Objects.MapObject;
import Group10.Tree.PointContainer;
import Group10.World.ViewRange;

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
