package robotics.maze.dijkstra;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import robotics.maze.PointType;
import robotics.maze.robotics.projection.MazeMap;

public class MazeMapToVertexListAdapter
{
    public static MutableList<Vertex> adapt(MazeMap mazeMap)
    {
        MutableList<Vertex> vertices = Lists.mutable.empty();
        MutableMap<IntIntPair, Vertex> coordinateVertexMap = Maps.mutable.empty();

        int width = mazeMap.getWidth();
        int height = mazeMap.getHeight();

        for (int row = 0; row < height; row++)
        {
            for (int column = 0; column < width; column++)
            {
                Vertex vertex = new Vertex(row, column, PointType.getPointType(mazeMap.get(row, column)));
                vertices.add(vertex);
                coordinateVertexMap.put(PrimitiveTuples.pair(row, column), vertex);
            }
        }

        MazeMapToVertexListAdapter.addNeighbors(vertices, coordinateVertexMap, width, height);

        return vertices;
    }

    public static void addNeighbors(MutableList<Vertex> vertices, MutableMap<IntIntPair, Vertex> coordinateVertexMap, int width, int height)
    {
        vertices.each(each ->
        {
            if (each.getPointType() != PointType.WALL)
            {
                int x = each.getX();
                int y = each.getY();
                if (x > 0)
                {
                    MazeMapToVertexListAdapter.addNeighbor(
                            each,
                            coordinateVertexMap.get(PrimitiveTuples.pair(x - 1, y)));
                }
                if (x < height - 1)
                {
                    MazeMapToVertexListAdapter.addNeighbor(
                            each,
                            coordinateVertexMap.get(PrimitiveTuples.pair(x + 1, y)));
                }
                if (y > 0)
                {
                    MazeMapToVertexListAdapter.addNeighbor(
                            each,
                            coordinateVertexMap.get(PrimitiveTuples.pair(x, y - 1)));
                }
                if (y < width - 1)
                {
                    MazeMapToVertexListAdapter.addNeighbor(
                            each,
                            coordinateVertexMap.get(PrimitiveTuples.pair(x, y + 1)));
                }
            }
        });
    }

    private static void addNeighbor(Vertex each, Vertex neighbor)
    {
        if (neighbor.getPointType() != PointType.WALL)
        {
            each.addNeighbor(neighbor);
        }
    }
}
