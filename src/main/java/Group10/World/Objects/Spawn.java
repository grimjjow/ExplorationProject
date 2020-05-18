package Group10.World.Objects;

import Group10.Container.DataContainer;
import Interop.Percept.Vision.ObjectPerceptType;

public abstract class Spawn extends AbstractObject {

    public Spawn(DataContainer.Polygon area) {
        super(area, ObjectPerceptType.EmptySpace);
    }

    public static class Intruder extends Spawn {
        public Intruder(DataContainer.Polygon area) {
            super(area);
        }
    }
    public static class Guard extends Spawn {
        public Guard(DataContainer.Polygon area) {
            super(area);
        }
    }

}
