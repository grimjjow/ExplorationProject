package Group10.World.Objects;

import Group10.Container.DataContainer;
import Interop.Percept.Vision.ObjectPerceptType;

public class TargetArea extends AbstractObject {

    public TargetArea(DataContainer.Polygon area) {
        super(area, ObjectPerceptType.TargetArea);
    }

}
