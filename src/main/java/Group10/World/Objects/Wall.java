package Group10.World.Objects;

import Group10.Container.DataContainer;
import Interop.Percept.Vision.ObjectPerceptType;

public class Wall extends AbstractObject {

    public Wall(DataContainer.Polygon area) {
        super(area, ObjectPerceptType.Wall);
    }

}
