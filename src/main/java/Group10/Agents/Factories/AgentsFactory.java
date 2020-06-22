package Group10.Agents.Factories;

import Interop.Agent.Guard;
import Interop.Agent.Intruder;

import java.util.List;

public class AgentsFactory {

    private final static MainFactory agentFactory = new MainFactory();

    static public List<Intruder> createIntruders(int number) {
        return agentFactory.createIntruders(number);
    }
    static public List<Guard> createGuards(int number) {
        return agentFactory.createGuards(number);
    }
}

