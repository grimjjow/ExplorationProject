package Group10.World.Objects;

import Group10.World.Area.EffectArea;
import Group10.Tree.Container;
import Group10.Tree.PointContainer;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapObject implements Container<PointContainer> {

    private final PointContainer area;
    private final List<EffectArea> effects;

    private ObjectPerceptType type;

    public MapObject(PointContainer area, ObjectPerceptType type)
    {
        this(area, new ArrayList<>(), type);
        this.type = type;
    }

    public MapObject(PointContainer area, List<EffectArea> effects, ObjectPerceptType type)
    {
        this.area = area;
        this.effects = effects;
        this.type = type;
    }

    public ObjectPerceptType getType() {
        return type;
    }

    @Override
    public PointContainer getContainer() {
        return this.area;
    }

    public void addEffects(EffectArea<?> ...effects)
    {
        this.effects.addAll(Arrays.asList(effects));
    }

    public List<EffectArea> getEffects() {
        return this.effects;
    }

    public boolean has(Class<EffectArea> clazz)
    {
        return this.effects.stream().anyMatch(e -> clazz.isAssignableFrom(e.getClass()));
    }

    public PointContainer getArea() {
        return area;
    }

}
