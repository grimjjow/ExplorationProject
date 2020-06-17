package Group10.Agents.Learning;

import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPercepts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.io.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Memory extends ArrayList {

    List<ObjectPercept> memory = new ArrayList();

    public Memory() {

    }
    public Memory(List<ObjectPercept> memory) {
        this.memory = memory;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param o element to be appended to this list
     * @return {@code true}
     */

    public boolean add(ObjectPercept o) {
           // System.out.println(o + " has been added");
        return super.add(o);

    }

}
