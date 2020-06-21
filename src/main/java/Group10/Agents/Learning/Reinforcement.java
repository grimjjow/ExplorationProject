package Group10.Agents.Learning;

import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

public class Reinforcement {
    Graph G;
    Memory memory;

    public Reinforcement(Memory memory) {

        this.memory = memory;
           if (memory.isTargetReached()){
               for (int i = 0; i < memory.size(); i++){
                   ObjectPercept o = (ObjectPercept) memory.get(i);
                   Vertex v = new Vertex(o.getPoint());
                   G.addVertex(v);
               }
           }

           //add all memories into graph; traverse to all targetReached nodes; prefer most common one.
        //Density traversal can be useful
    }
}
