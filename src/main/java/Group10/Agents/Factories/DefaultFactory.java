package Group10.Agents.Factories;

import Group10.Agents.BoltzmannAgent;
import Group10.Agents.RandomAgent;
import Group10.Agents.RandomIntruderAgent;
import Group10.Agents.ToTargetAgent;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultFactory {

    public List<Intruder> createIntruders(int number) {
        List<Intruder> intruders = new ArrayList<>();
        for(int i = 0; i < number; i++)
        {
            intruders.add(new ToTargetAgent());
        }
        return intruders;
    }

    public List<Guard> createGuards(int number) {
        List<Guard> guards = new ArrayList<>();
        for(int i = 0; i < number; i++)
        {
            guards.add(new BoltzmannAgent());
        }
        return guards;
    }
}