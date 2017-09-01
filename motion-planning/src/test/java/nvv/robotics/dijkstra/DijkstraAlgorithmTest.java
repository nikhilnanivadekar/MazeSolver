package nvv.robotics.dijkstra;

import org.junit.Test;
import robotics.maze.dijkstra.DijkstraAlgorithm;
import robotics.maze.dijkstra.Edge;
import robotics.maze.dijkstra.Graph;
import robotics.maze.dijkstra.Vertex;

import java.util.ArrayList;
import java.util.List;

public class DijkstraAlgorithmTest
{
    private static final String VERTEX_ID_PREFIX = "VertexId_";
    private static final String VERTEX_NAME_PREFIX = "VertexName_";
    private static final String EDGE_ID_PREFIX = "EdgeId_";
    private static final String EDGE_NAME_PREFIX = "EdgeName_";

    @Test
    public void test4Vertices()
    {
        List<Vertex> vertices = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        int numVertices = 4;

        for (int i = 1; i <= numVertices; i++)
        {
            vertices.add(getVertex(i));
        }

        addEdge(edges, 1, 2, 10);
        addEdge(edges, 1, 3, 100);
        addEdge(edges, 1, 4, 20);

        addEdge(edges, 2, 3, 20);
        addEdge(edges, 3, 4, 30);
        Graph graph = new Graph(vertices, edges);
        DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(graph);
        dijkstraAlgorithm.calculateDistanceFromSource(getVertex(1));
        System.out.println(dijkstraAlgorithm.getPath(getVertex(3)));
    }

    private Vertex getVertex(int vertexNumber)
    {
//        return new Vertex(0, 0);
        return null;
    }

    private void addEdge(List<Edge> edges, int sourceVertexId, int targetVertexId, long weight)
    {
        int edgeNumber = edges.size() + 1;
        edges.add(new Edge(EDGE_ID_PREFIX + edgeNumber, EDGE_NAME_PREFIX + edgeNumber, getVertex(sourceVertexId), getVertex(targetVertexId), weight));
    }
}

