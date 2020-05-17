package World.Objects;

import Agents.Container.AgentContainer;
import World.Area.ModifyLocationEffect;
import Algebra.Vector;
import Tree.PointContainer;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;

public class TeleportArea extends MapObject {

    private TeleportArea connected;

    public TeleportArea(PointContainer area, TeleportArea connected) {
        super(area, new ArrayList<>(), ObjectPerceptType.Teleport);
        setConnected(connected);
    }

    public void setConnected(TeleportArea connected) {
        if(connected != null)
        {
            getEffects().clear();
            getEffects().add(
                    new ModifyLocationEffect(this, getArea()) {
                        @Override
                        public Vector get(AgentContainer<?> agentContainer) {
                            return TeleportArea.this.connected.getArea().getAsPolygon().generateRandomLocation();
                        }
                    }
            );
        }
        this.connected = connected;
    }

    public TeleportArea getConnected()
    {
        return this.connected;
    }

}
