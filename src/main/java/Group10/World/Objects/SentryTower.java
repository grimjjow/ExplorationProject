package Group10.World.Objects;

import Group10.World.DefaultViewRange;
import Group10.World.Area.ModifySpeedProperty;
import Group10.World.Area.ModifyViewRangeProperty;
import Group10.Container.DataContainer;
import Interop.Percept.Vision.ObjectPerceptType;

public class SentryTower extends AbstractObject {

    public SentryTower(DataContainer area, double sentrySlowdownModifier, DefaultViewRange defaultViewRange) {
        super(area, ObjectPerceptType.SentryTower);
        this.addEffects(
                new ModifySpeedProperty(this, area, sentrySlowdownModifier,sentrySlowdownModifier),
                new ModifyViewRangeProperty(this, area, defaultViewRange)
        );
    }

}
