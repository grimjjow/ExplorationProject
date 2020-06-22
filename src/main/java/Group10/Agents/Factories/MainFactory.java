package Group10.Agents.Factories;

import Group10.Agents.GuardMode.CampingAgent;
import Group10.Agents.IntruderMode.IntruderAgent;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;

import java.util.ArrayList;
import java.util.List;

public class MainFactory {

    public List<Intruder> createIntruders(int number) {
        List<Intruder> intruders = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            intruders.add(new IntruderAgent());
        }
        return intruders;
    }

    public List<Guard> createGuards(int number) {
        List<Guard> guards = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            guards.add(new CampingAgent());
        }
        return guards;
    }

}