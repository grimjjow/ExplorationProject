package Group10.Agents.Learning;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Graph<T> {

    // We use Hashmap to store the edges in the graph
    private final Map<T, List<T>> map = new HashMap<>();

    // This function adds a new vertex to the graph
    public void addVertex(T s)
    {
        map.put(s, new LinkedList<T>());
    }

    // This function adds the edge
    // between source to destination
    public void addEdge(T source,
                        T destination,
                        boolean bidirectional)
    {

        if (!map.containsKey(source))
            addVertex(source);

        if (!map.containsKey(destination))
            addVertex(destination);

        map.get(source).add(destination);
        if (bidirectional == true) {
            map.get(destination).add(source);
        }
    }

    // This function gives the count of vertices
    public double getVertexCount()
    {
        System.out.println("The graph has "
                + map.keySet().size()
                + " vertex");
        return  map.keySet().size();
    }

    // This function gives the count of edges
    public double getEdgesCount(boolean bidirection)
    {
        int count = 0;
        for (T v : map.keySet()) {
            count += map.get(v).size();
        }
        if (bidirection == true) {
            count = count / 2;
        }
        System.out.println("The graph has "
                + count
                + " edges.");
        return count;
    }

    // This function gives whether
    // a vertex is present or not.
    public boolean hasVertex(T s)
    {   Boolean hasVertex = false;
        if (map.containsKey(s)) {
            System.out.println("The graph contains "
                    + s + " as a vertex.");
            hasVertex = true;
        }
        else {
            System.out.println("The graph does not contain "
                    + s + " as a vertex.");
        }
        return hasVertex;
    }

    // This function gives whether an edge is present or not.
    public boolean hasEdge(T s, T d)
    {   Boolean hasEdge = false;
        if (map.get(s).contains(d)) {
            System.out.println("The graph has an edge between "
                    + s + " and " + d + ".");
            hasEdge = true;
        }
        else {
            System.out.println("The graph has no edge between "
                    + s + " and " + d + ".");
        }
        return hasEdge;
    }

    // Prints the adjancency list of each vertex.
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        for (T v : map.keySet()) {
            builder.append( "Key: " + v.toString() + ": ");
            for (T w : map.get(v)) {
                builder.append(" Value: " + w.toString() + " ");
            }
            builder.append("\n");
        }

        return (builder.toString());
    }

    public List<T> getEdge(T v){
        return map.get(v);
    }

}
