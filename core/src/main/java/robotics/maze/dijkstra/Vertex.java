package robotics.maze.dijkstra;

import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;
import robotics.maze.enums.PointType;

public class Vertex
{
    private final int x;
    private final int y;
    private final PointType pointType;
    private MutableSet<Vertex> neighbors = Sets.mutable.withInitialCapacity(4);

    public Vertex(int x, int y, PointType pointType)
    {
        this.x = x;
        this.y = y;
        this.pointType = pointType;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public PointType getPointType()
    {
        return pointType;
    }

    public MutableSet<Vertex> getNeighbors()
    {
        return neighbors;
    }

    public void addNeighbor(Vertex neighbor)
    {
        this.neighbors.add(neighbor);
    }
}
