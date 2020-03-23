package Agents;

import Interop.Agent.Guard;

import java.util.ArrayList;
import java.util.List;

public class AgentFactory {

    static public List<Guard> createGuards(int number) {

        List<Guard> guards = new ArrayList<Guard>();
        for(int i = 0; i < number; i++) {
            Agents.Guard guard =  new Agents.Guard();
            guards.add(guard);
        }
        return guards;
    }
}
