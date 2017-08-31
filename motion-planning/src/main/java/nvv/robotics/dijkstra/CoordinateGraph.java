package nvv.robotics.dijkstra;

import java.util.List;

public class CoordinateGraph
{
    private final List<Coordinate> vertices;
    private final List<CoordinateEdge> edges;

    public CoordinateGraph(List<Coordinate> vertices, List<CoordinateEdge> edges)
    {
        this.vertices = vertices;
        this.edges = edges;
    }

    public List<Coordinate> getVertices()
    {
        return vertices;
    }

    public List<CoordinateEdge> getEdges()
    {
        return edges;
    }
}
