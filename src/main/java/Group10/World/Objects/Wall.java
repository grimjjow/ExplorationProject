package Group10.World.Objects;

import Group10.Tree.PointContainer;
import Interop.Percept.Vision.ObjectPerceptType;

public class Wall extends MapObject {

    public Wall(PointContainer.Polygon area) {
        super(area, ObjectPerceptType.Wall);
    }

}
