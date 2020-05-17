package Agents.Factories;

import Agents.BoltzmannAgent;
import Agents.RandomAgent;
import Agents.RandomIntruderAgent;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultAgentFactory {

    public List<Intruder> createIntruders(int number) {
        List<Intruder> intruders = new ArrayList<>();
        for(int i = 0; i < number; i++)
        {
            intruders.add(new RandomIntruderAgent());
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