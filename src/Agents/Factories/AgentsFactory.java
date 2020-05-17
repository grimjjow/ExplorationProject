package Agents.Factories;

import Agents.Factories.DefaultAgentFactory;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;

import java.util.List;

public class AgentsFactory {

    private final static DefaultAgentFactory agentFactory = new DefaultAgentFactory();

    static public List<Intruder> createIntruders(int number) {
        return agentFactory.createIntruders(number);
    }
    static public List<Guard> createGuards(int number) {
        return agentFactory.createGuards(number);
    }
}

