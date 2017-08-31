package nvv.robotics.dijkstra;

import org.junit.Test;

public class DijkstraAlgorithmMatrixTest
{
    @Test
    public void test4VerticesNoObstacle()
    {
        /*
        * S 0
        * 0 E
        * */
        int[][] graph = new int[2][2];
        graph[0][0] = Constants.START;
        graph[0][1] = Constants.SPACE;
        graph[1][0] = Constants.SPACE;
        graph[1][1] = Constants.END;

        DijkstraAlgorithmMatrix dijkstraAlgorithmMatrix = new DijkstraAlgorithmMatrix(graph);
        dijkstraAlgorithmMatrix.calculateDistanceFromSource();
        System.out.println(dijkstraAlgorithmMatrix.getCoordinatesFromSourceToTarget());
    }

    @Test
    public void test4VerticesObstacle()
    {
        /*
        * S 1
        * 0 E
        * */
        int[][] graph = new int[2][2];
        graph[0][0] = Constants.START;
        graph[0][1] = Constants.SPACE;
        graph[1][0] = Constants.OBSTACLE;
        graph[1][1] = Constants.END;

        DijkstraAlgorithmMatrix dijkstraAlgorithmMatrix = new DijkstraAlgorithmMatrix(graph);
        dijkstraAlgorithmMatrix.calculateDistanceFromSource();
        System.out.println(dijkstraAlgorithmMatrix.getCoordinatesFromSourceToTarget());
    }

    @Test
    public void test4VerticesNoPath()
    {
        /*
        * S 1
        * 1 E
        * */
        int[][] graph = new int[2][2];
        graph[0][0] = Constants.START;
        graph[0][1] = Constants.OBSTACLE;
        graph[1][0] = Constants.OBSTACLE;
        graph[1][1] = Constants.END;

        DijkstraAlgorithmMatrix dijkstraAlgorithmMatrix = new DijkstraAlgorithmMatrix(graph);
        dijkstraAlgorithmMatrix.calculateDistanceFromSource();
        System.out.println(dijkstraAlgorithmMatrix.getCoordinatesFromSourceToTarget());
    }
}
