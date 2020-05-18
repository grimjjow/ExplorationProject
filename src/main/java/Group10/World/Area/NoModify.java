package Group10.World.Area;

import Group10.Agents.Container.AgentContainer;

public class NoModify extends PropertyArea<Double> {

    public NoModify() {
        super(null, null);
    }

    @Override
    public Double get(AgentContainer<?> agentContainer) {
        return 1D;
    }

}
