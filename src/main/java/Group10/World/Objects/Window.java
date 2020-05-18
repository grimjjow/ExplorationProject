package Group10.World.Objects;

import Group10.World.Area.ModifySpeedProperty;
import Group10.World.Area.ModifyViewProperty;
import Group10.World.Area.SoundProperty;
import Group10.Container.DataContainer;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Vision.ObjectPerceptType;

public class Window extends AbstractObject {

    public Window(DataContainer.Polygon area,
                  double guardViewModifier, double intruderViewModifier,
                  double soundRadius,
                  double guardSpeedModifier, double intruderSpeedModifier) {
        super(area, ObjectPerceptType.Window);
        this.addEffects(new ModifyViewProperty(this, area, guardViewModifier, intruderViewModifier),
                new SoundProperty(this, area, SoundPerceptType.Noise, soundRadius),
                new ModifySpeedProperty(this, area, guardSpeedModifier, intruderSpeedModifier));
    }

}
