package robotics.maze.dijkstra;

import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DijkstraAlgorithmCoordinate
{
    private final List<Coordinate> vertices;
    private final List<CoordinateEdge> edges;
    private final Set<Coordinate> settledNodes = Sets.mutable.empty();
    private final Set<Coordinate> unsettledNodes = Sets.mutable.empty();
    private final Map<Coordinate, Coordinate> predecessors = Maps.mutable.empty();
    private final Map<Coordinate, Long> distanceFromSource = Maps.mutable.empty();

    public DijkstraAlgorithmCoordinate(CoordinateGraph coordinateGraph)
    {
        vertices = Lists.mutable.withAll(coordinateGraph.getVertices());
        edges = Lists.mutable.withAll(coordinateGraph.getEdges());
    }

    public void calculateDistanceFromSource(Coordinate source)
    {
        initialize(source);

        while (unsettledNodes.size() > 0)
        {
            Coordinate node = getMinimumUnsettledNode();
            settledNodes.add(node);
            unsettledNodes.remove(node);
            updateMinimumDistanceFromSource(node);
        }
    }

    private void initialize(Coordinate source)
    {
        for (Coordinate Coordinate : vertices)
        {
            distanceFromSource.put(Coordinate, Long.MAX_VALUE);
        }
        distanceFromSource.put(source, Long.valueOf(0));
        unsettledNodes.add(source);
    }

    private Coordinate getMinimumUnsettledNode()
    {
        Coordinate minimumUnsettledNode = null;

        for (Coordinate Coordinate : unsettledNodes)
        {
            if (null == minimumUnsettledNode)
            {
                minimumUnsettledNode = Coordinate;
            }
            else
            {
                if (distanceFromSource.get(Coordinate) < distanceFromSource.get(minimumUnsettledNode))
                {
                    minimumUnsettledNode = Coordinate;
                }
            }
        }

        return minimumUnsettledNode;
    }

    private void updateMinimumDistanceFromSource(Coordinate node)
    {
        List<Coordinate> unsettledNeighbours = getUnsettledNeighbours(node);
        for (Coordinate unsettledNeighbour : unsettledNeighbours)
        {
            if (distanceFromSource.get(unsettledNeighbour) > distanceFromSource.get(node) + getDistance(node, unsettledNeighbour))
            {
                distanceFromSource.put(unsettledNeighbour, distanceFromSource.get(node) + getDistance(node, unsettledNeighbour));
                predecessors.put(unsettledNeighbour, node);
                unsettledNodes.add(unsettledNeighbour);
            }
        }
    }

    private List<Coordinate> getUnsettledNeighbours(Coordinate node)
    {
        List<Coordinate> unsettledNeighbours = Lists.mutable.empty();

        for (CoordinateEdge edge : edges)
        {
            if (edge.getSource().equals(node) && !settledNodes.contains(edge.getDestination()))
            {
                unsettledNeighbours.add(edge.getDestination());
            }
        }

        return unsettledNeighbours;
    }

    private long getDistance(Coordinate source, Coordinate target)
    {
        for (CoordinateEdge edge : edges)
        {
            if (edge.getSource().equals(source) && edge.getDestination().equals(target))
            {
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Was not able to find distance");
    }

    public List<Coordinate> getPath(Coordinate target)
    {
        List<Coordinate> path = Lists.mutable.empty();
        Coordinate step = target;
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
