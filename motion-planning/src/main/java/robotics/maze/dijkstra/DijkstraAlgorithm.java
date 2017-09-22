package robotics.maze.dijkstra;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.Stacks;
import org.eclipse.collections.impl.factory.primitive.ObjectDoubleMaps;
import robotics.maze.enums.PointType;

import java.util.Set;

public class DijkstraAlgorithm
{
    public static MutableStack<Vertex> findPath(MutableList<Vertex> vertices)
    {
        Vertex start = vertices.detect(each -> PointType.START == each.getPointType());
        Vertex end = vertices.detect(each -> PointType.FINISH == each.getPointType());
        if (start == null)
        {
            throw new IllegalStateException("No start point specified!");
        }
        if (end == null)
        {
            throw new IllegalStateException("No end point specified!");
        }
        if (start == end)
        {
            System.out.println("Start Point = End Point NOTHING to do!");
        }

        MutableObjectDoubleMap<Vertex> vertexCostMap = ObjectDoubleMaps.mutable.empty();
        vertices.each(each -> vertexCostMap.put(each, Double.MAX_VALUE));
        MutableMap<Vertex, Vertex> vertexBackPointerMap = Maps.mutable.empty();
        Set<Vertex> visitedVertices = Sets.mutable.with(start);
        MutableSet<Vertex> verticesToSearch = Sets.mutable.with(start);

        boolean reachedGoal = false;
        vertexCostMap.put(start, 0);

        while (!reachedGoal && verticesToSearch.notEmpty())
        {
            Vertex searchVertex = verticesToSearch.minBy(vertexCostMap::get);
            if (end == searchVertex)
            {
                reachedGoal = true;
            }
            else
            {
                visitedVertices.add(searchVertex);
                MutableSet<Vertex> neighbors = searchVertex.getNeighbors();

                neighbors.forEachWith((neighbor, finish) ->
                {
                    if (!visitedVertices.contains(neighbor))
                    {
                        double costToSuccessor = vertexCostMap.get(searchVertex) + DijkstraAlgorithm.getCost(searchVertex, neighbor);
                        if (vertexCostMap.get(neighbor) >= costToSuccessor)
                        {
                            vertexCostMap.put(neighbor, costToSuccessor);
                            vertexBackPointerMap.put(neighbor, searchVertex);
                        }
                        if (!verticesToSearch.contains(neighbor))
                        {
                            verticesToSearch.add(neighbor);
                        }
                    }
                }, end);

                verticesToSearch.remove(searchVertex);
            }
        }
        MutableStack<Vertex> path = Stacks.mutable.empty();

        boolean isPathComplete = false;
        if (reachedGoal)
        {
            path.push(end);

            while (!isPathComplete)
            {
                if (start == path.peek())
                {
                    isPathComplete = true;
                }
                else
                {
                    path.push(vertexBackPointerMap.get(path.peek()));
                }
            }
        }
        else
        {
            System.out.println("Could not find feasible path between Start and End");
            throw new IllegalStateException("Could not find feasible path between Start and End");
        }

        return path;
    }

    public static double getCost(Vertex currentVertex, Vertex successor)
    {
        int x = currentVertex.getX();
        int successorX = successor.getX();
        int y = currentVertex.getY();
        int successorY = successor.getY();
        return Math.sqrt(((successorY - y) * (successorY - y)) + ((successorX - x) * (successorX - x)));
    }
}
