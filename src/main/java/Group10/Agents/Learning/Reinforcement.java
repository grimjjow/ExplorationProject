package Group10.Agents.Learning;

import Interop.Percept.Vision.ObjectPercept;

import java.util.LinkedList;

public class Reinforcement {
    private final Graph G;
    Memory memory;

    LinkedList<Vertex> path;

    public Reinforcement(LinkedList<ObjectPercept> memoryList, Memory memory) {
        G = new Graph();
        this.memory = memory;
        addMemory(memoryList, memory);
        //add all memories into graph; traverse to all targetReached nodes; prefer most common one.
        //Density traversal can be useful
        //System.out.println(G.toString());
    }

    public boolean addMemory(LinkedList<ObjectPercept> memoryList, Memory memory) {
        if (memory.isTargetReached()) {
            for (int i = 1; i < memoryList.size(); i++) {
                Vertex v1 = new Vertex(memoryList.get(i - 1).getPoint());
                Vertex v2 = new Vertex(memoryList.get(i).getPoint());
                G.addEdge(v1, v2, false);
            }
            System.out.println(G.toString());
            return true;
        } else System.out.println("Memory does not contain the Target state");
        return false;
    }

    public LinkedList<Vertex> findPath(Vertex v) {
        if (G.hasVertex(v)) {
            LinkedList<Vertex> path = (LinkedList<Vertex>) G.getEdge(v);
            System.out.println(path);
        }
        return path;
    }


}
