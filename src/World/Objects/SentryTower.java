package World.Objects;

import World.ViewRange;
import World.Area.ModifySpeedEffect;
import World.Area.ModifyViewRangeEffect;
import Tree.PointContainer;
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
