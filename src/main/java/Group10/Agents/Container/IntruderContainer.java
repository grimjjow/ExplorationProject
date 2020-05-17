package Group10.Agents.Container;

import Group10.Engine.Game;
import Group10.Algebra.Vector;
import Interop.Agent.Intruder;
import Interop.Percept.Vision.FieldOfView;

public class IntruderContainer extends AgentContainer<Intruder> {

    private boolean captured = false;
    private int zoneCounter = 0;

    public IntruderContainer(Intruder agent, Vector position, Vector direction, FieldOfView normalFOV) {
        super(agent, position, direction, normalFOV);
    }

    public int getZoneCounter()
    {
        return this.zoneCounter;
    }

    public boolean isCaptured()
    {
        return this.captured;
    }

    public void setZoneCounter(int zoneCounter)
    {
        this.zoneCounter = zoneCounter;
    }

    public void setCaptured(boolean captured)
    {
        this.captured = captured;
    }

    @Override
    public IntruderContainer clone(Game game) {
        IntruderContainer cloned = new IntruderContainer(getAgent(), getPosition().clone(), getDirection().clone(),
                getFOV(game.getGameMap().getEffectAreas(this)));
        cloned.setZoneCounter(getZoneCounter());
        cloned.setCaptured(isCaptured());
        for(Cooldown cooldown : Cooldown.values())
        {
            cloned.addCooldown(cooldown, this.getCooldown(cooldown));
        }
        return cloned;
    }
}
