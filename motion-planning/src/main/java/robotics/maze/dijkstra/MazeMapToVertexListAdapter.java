package robotics.maze.dijkstra;

import robotics.maze.robotics.projection.MazeMap;
import robotics.maze.robotics.projection.PointType;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

public class MazeMapToVertexListAdapter
{
    public static MutableList<Vertex> adapt(MazeMap mazeMap)
    {
        MutableList<Vertex> vertices = Lists.mutable.empty();

        int width = mazeMap.getWidth();
        int height = mazeMap.getHeight();

        for (int row = 0; row < height; row++)
        {
            for (int column = 0; column < width; column++)
            {
                new Vertex(row, column, PointType.getPointType(mazeMap.get(row, column)));
            }
        }

        return vertices;
    }
}
