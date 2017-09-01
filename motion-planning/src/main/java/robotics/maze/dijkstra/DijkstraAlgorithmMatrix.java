package robotics.maze.dijkstra;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;

import java.util.List;

public class DijkstraAlgorithmMatrix
{
    private final int[][] graph;
    private final MatrixParams matrixParams;

    private DijkstraAlgorithmCoordinate dijkstraAlgorithmCoordinate;
    private Coordinate source;
    private Coordinate target;

    public DijkstraAlgorithmMatrix(int[][] graph)
    {
        this.graph = graph;
        this.matrixParams = new MatrixParams(Constants.X_AXIS_SIZE,
                Constants.Y_AXIS_SIZE,
                Constants.X_AXIS_COST,
                Constants.Y_AXIS_COST,
                Constants.DIAGONAL_COST,
                Constants.IS_DIAGONAL_MOVEMENT_ALLOWED,
                Constants.SPACE,
                Constants.OBSTACLE,
                Constants.START,
                Constants.END);
    }

    public void calculateDistanceFromSource()
    {
        Pair<Coordinate, Coordinate> sourceTargetPair = validate(graph, matrixParams);
        source = sourceTargetPair.getOne();
        target = sourceTargetPair.getTwo();
        CoordinateGraph coordinateGraph = getCoordinateGraph(graph, matrixParams);
        dijkstraAlgorithmCoordinate = new DijkstraAlgorithmCoordinate(coordinateGraph);
        dijkstraAlgorithmCoordinate.calculateDistanceFromSource(source);
    }

    private Pair<Coordinate, Coordinate> validate(int[][] graph, MatrixParams matrixParams)
    {
        //currently only adding logic to find source and target
        Coordinate source = null;
        Coordinate target = null;

        for (int x = 0; x < matrixParams.getxAxisSize(); x++)
        {
            for (int y = 0; y < matrixParams.getyAxisSize(); y++)
            {
                if (isStart(graph, x, y))
                {
                    source = new Coordinate(x, y);
                }
                else if (isEnd(graph, x, y))
                {
                    target = new Coordinate(x, y);
                }
            }
        }

        return Tuples.pair(source, target);
    }

    private CoordinateGraph getCoordinateGraph(int[][] graph, MatrixParams matrixParams)
    {
        List<Coordinate> vertices = Lists.mutable.empty();
        List<CoordinateEdge> edges = Lists.mutable.empty();

        for (int x = 0; x < matrixParams.getxAxisSize(); x++)
        {
            for (int y = 0; y < matrixParams.getyAxisSize(); y++)
            {
                vertices.add(new Coordinate(x, y));
                List<CoordinateEdge> coordinateEdges = getEdges(graph, x, y, matrixParams);
                if (Iterate.notEmpty(coordinateEdges))
                {
                    edges.addAll(coordinateEdges);
                }
            }
        }

        return new CoordinateGraph(vertices, edges);
    }

    private List<CoordinateEdge> getEdges(int[][] graph, int x, int y, MatrixParams matrixParams)
    {
        //If current coordinate is not a obstacle only then we can have some edges
        MutableList<CoordinateEdge> edges = Lists.mutable.of();

        if (isNotObstacle(graph, x, y))
        {
            //North
            if (isValidCoordinate(matrixParams, x, y - 1) && isNotObstacle(graph, x, y - 1))
            {
                edges.add(getEdge(x, y, x, y - 1, matrixParams));
            }

            //South
            if (isValidCoordinate(matrixParams, x, y + 1) && isNotObstacle(graph, x, y + 1))
            {
                edges.add(getEdge(x, y, x, y + 1, matrixParams));
            }

            //East
            if (isValidCoordinate(matrixParams, x - 1, y) && isNotObstacle(graph, x - 1, y))
            {
                edges.add(getEdge(x, y, x - 1, y, matrixParams));
            }

            //West
            if (isValidCoordinate(matrixParams, x + 1, y) && isNotObstacle(graph, x + 1, y))
            {
                edges.add(getEdge(x, y, x + 1, y, matrixParams));
            }

            //TODO: if diagonal movement is allowed add NE,NW,SE,SW
        }

        return edges;
    }

    private boolean isStart(int[][] graph, int x, int y)
    {
        return graph[x][y] == matrixParams.getStart();
    }

    private boolean isEnd(int[][] graph, int x, int y)
    {
        return graph[x][y] == matrixParams.getEnd();
    }

    private boolean isSpace(int[][] graph, int x, int y)
    {
        return graph[x][y] == matrixParams.getSpace();
    }

    private boolean isObstacle(int[][] graph, int x, int y)
    {
        return graph[x][y] == matrixParams.getObstacle();
    }

    private boolean isNotObstacle(int[][] graph, int x, int y)
    {
        return !isObstacle(graph, x, y);
    }

    private boolean isValidCoordinate(MatrixParams matrixParams, int x, int y)
    {
        return x >= 0 && x < matrixParams.getxAxisSize() && y >= 0 && y < matrixParams.getyAxisSize();
    }

    private CoordinateEdge getEdge(int xSource, int ySource, int xDestination, int yDestination, MatrixParams matrixParams)
    {
        Coordinate source = new Coordinate(xSource, ySource);
        Coordinate destination = new Coordinate(xDestination, yDestination);
        long weight;

        if (xSource == xDestination)
        {
            //movement along y axis
            weight = matrixParams.getyAxisCost();
        }
        else if (ySource == yDestination)
        {
            //movement along x axis
            weight = matrixParams.getxAxisCost();
        }
        else
        {
            //movement along diagonal
            weight = matrixParams.getDiagonalCost();
        }

        return new CoordinateEdge(source, destination, weight);
    }

    public List<Coordinate> getCoordinatesFromSourceToTarget()
    {
        return dijkstraAlgorithmCoordinate.getPath(target);
    }
}
