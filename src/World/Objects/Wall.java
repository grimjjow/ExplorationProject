package World.Objects;

import Tree.PointContainer;
import Interop.Percept.Vision.ObjectPerceptType;

public class Wall extends MapObject {

    public Wall(PointContainer.Polygon area) {
        super(area, ObjectPerceptType.Wall);
    }

}
