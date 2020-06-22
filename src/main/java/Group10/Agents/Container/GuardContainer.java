package Group10.Agents.Container;

import Group10.Algebra.Vector;
import Group10.Engine.Game;
import Interop.Agent.Guard;
import Interop.Percept.Vision.FieldOfView;

public class GuardContainer extends AgentContainer<Guard> {

    public GuardContainer(Guard agent, Vector position, Vector direction, FieldOfView normalFOV) {
        super(agent, position, direction, normalFOV);
    }

    @Override
    public GuardContainer clone(Game game) {
        GuardContainer cloned = new GuardContainer(getAgent(), getPosition().clone(), getDirection().clone(),
                getFOV(game.getGameMap().getPropertyAreas(this)));
        for (Cooldown cooldown : Cooldown.values()) {
            cloned.addCooldown(cooldown, this.getCooldown(cooldown));
        }
        return cloned;
    }

}
