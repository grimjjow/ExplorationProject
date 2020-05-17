package Group10.World.Objects;

import Group10.World.Area.ModifyViewEffect;
import Group10.Tree.PointContainer;
import Interop.Percept.Vision.ObjectPerceptType;

public class ShadedArea extends MapObject {
    public ShadedArea(PointContainer area, double guardModifier, double intruderModifier) {
        super(area, ObjectPerceptType.ShadedArea);
        this.addEffects(new ModifyViewEffect(this, area, guardModifier, intruderModifier));
    }
}
