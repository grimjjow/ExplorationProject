package World.Objects;

import Tree.PointContainer;
import Interop.Percept.Vision.ObjectPerceptType;

public class TargetArea extends MapObject {

    public TargetArea(PointContainer.Polygon area) {
        super(area, ObjectPerceptType.TargetArea);
    }

}
