package Agents;

import Interop.Agent.Guard;

import java.util.ArrayList;
import java.util.List;

public class AgentFactory {


    static public List<Guard> createGuards(int number, String guardname) {

        List<Guard> guards = new ArrayList<Guard>();
        if (guardname.equals("RandomGuard")) {
            for (int i = 0; i < number; i++) {
                Agents.Guard guard = new Agents.Guard();
                guards.add(guard);
            }
        } else {
            for (int i = 0; i < number; i++) {
                Agents.RandomGuard guard = new Agents.RandomGuard();
                guards.add(guard);
            }
        }
        return guards;
    }
}
