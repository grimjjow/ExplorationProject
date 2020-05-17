package Group10.World.Objects;

import Group10.Tree.PointContainer;
import Interop.Percept.Vision.ObjectPerceptType;

public class TargetArea extends MapObject {

    public TargetArea(PointContainer.Polygon area) {
        super(area, ObjectPerceptType.TargetArea);
    }

}
