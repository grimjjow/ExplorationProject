package Group10.World.Objects;

import Group10.Agents.Container.AgentContainer;
import Group10.World.Area.ModifyLocationProperty;
import Group10.Algebra.Vector;
import Group10.Container.DataContainer;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;

public class TeleportArea extends AbstractObject {

    private TeleportArea connected;

    public TeleportArea(DataContainer area, TeleportArea connected) {
        super(area, new ArrayList<>(), ObjectPerceptType.Teleport);
        setConnected(connected);
    }

    public void setConnected(TeleportArea connected) {
        if(connected != null)
        {
            getProperties().clear();
            getProperties().add(
                    new ModifyLocationProperty(this, getArea()) {
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
