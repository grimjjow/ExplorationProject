package Group10.World.Objects;

import Group10.World.Area.ModifyViewProperty;
import Group10.Container.DataContainer;
import Interop.Percept.Vision.ObjectPerceptType;

public class ShadedArea extends AbstractObject {
    public ShadedArea(DataContainer area, double guardModifier, double intruderModifier) {
        super(area, ObjectPerceptType.ShadedArea);
        this.addEffects(new ModifyViewProperty(this, area, guardModifier, intruderModifier));
    }
}
