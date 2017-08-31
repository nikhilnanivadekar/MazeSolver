package nvv.robotics.dijkstra;

import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DijkstraAlgorithm
{
    private final List<Vertex> vertices;
    private final List<Edge> edges;
    private final Set<Vertex> settledNodes = Sets.mutable.empty();
    private final Set<Vertex> unsettledNodes = Sets.mutable.empty();
    private final Map<Vertex, Vertex> predecessors = Maps.mutable.empty();
    private final Map<Vertex, Long> distanceFromSource = Maps.mutable.empty();

    public DijkstraAlgorithm(Graph graph)
    {
        vertices = Lists.mutable.withAll(graph.getVertices());
        edges = Lists.mutable.withAll(graph.getEdges());
    }

    public void calculateDistanceFromSource(Vertex source)
    {
        initialize(source);

        while (unsettledNodes.size() > 0)
        {
            Vertex node = getMinimumUnsettledNode();
            settledNodes.add(node);
            unsettledNodes.remove(node);
            updateMinimumDistanceFromSource(node);
        }
    }

    private void initialize(Vertex source)
    {
        for (Vertex vertex : vertices)
        {
            distanceFromSource.put(vertex, Long.MAX_VALUE);
        }
        distanceFromSource.put(source, Long.valueOf(0));
        unsettledNodes.add(source);
    }

    private Vertex getMinimumUnsettledNode()
    {
        Vertex minimumUnsettledNode = null;

        for (Vertex vertex : unsettledNodes)
        {
            if (null == minimumUnsettledNode)
            {
                minimumUnsettledNode = vertex;
            }
            else
            {
                if (distanceFromSource.get(vertex) < distanceFromSource.get(minimumUnsettledNode))
                {
                    minimumUnsettledNode = vertex;
                }
            }
        }

        return minimumUnsettledNode;
    }

    private void updateMinimumDistanceFromSource(Vertex node)
    {
        List<Vertex> unsettledNeighbours = getUnsettledNeighbours(node);
        for (Vertex unsettledNeighbour : unsettledNeighbours)
        {
            if (distanceFromSource.get(unsettledNeighbour) > distanceFromSource.get(node) + getDistance(node, unsettledNeighbour))
            {
                distanceFromSource.put(unsettledNeighbour, distanceFromSource.get(node) + getDistance(node, unsettledNeighbour));
                predecessors.put(unsettledNeighbour, node);
                unsettledNodes.add(unsettledNeighbour);
            }
        }
    }

    private List<Vertex> getUnsettledNeighbours(Vertex node)
    {
        List<Vertex> unsettledNeighbours = Lists.mutable.empty();

        for (Edge edge : edges)
        {
            if (edge.getSource().equals(node) && !settledNodes.contains(edge.getDestination()))
            {
                unsettledNeighbours.add(edge.getDestination());
            }
        }

        return unsettledNeighbours;
    }

    private long getDistance(Vertex source, Vertex target)
    {
        for (Edge edge : edges)
        {
            if (edge.getSource().equals(source) && edge.getDestination().equals(target))
            {
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Was not able to find distance");
    }

    public List<Vertex> getPath(Vertex target)
    {
        List<Vertex> path = Lists.mutable.empty();
        Vertex step = target;
        // check if a path exists
        if (predecessors.get(step) == null)
        {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null)
        {
            step = predecessors.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }
}
