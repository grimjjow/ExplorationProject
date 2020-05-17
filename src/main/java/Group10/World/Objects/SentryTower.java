package Group10.World.Objects;

import Group10.World.ViewRange;
import Group10.World.Area.ModifySpeedEffect;
import Group10.World.Area.ModifyViewRangeEffect;
import Group10.Tree.PointContainer;
import Interop.Percept.Vision.ObjectPerceptType;

public class SentryTower extends MapObject {

    public SentryTower(PointContainer area, double sentrySlowdownModifier, ViewRange viewRange) {
        super(area, ObjectPerceptType.SentryTower);
        this.addEffects(
                new ModifySpeedEffect(this, area, sentrySlowdownModifier,sentrySlowdownModifier),
                new ModifyViewRangeEffect(this, area, viewRange)
        );
    }

}
