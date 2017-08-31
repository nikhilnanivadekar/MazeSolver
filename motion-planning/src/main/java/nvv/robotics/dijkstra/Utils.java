package nvv.robotics.dijkstra;

public final class Utils
{
    public static int[][] getGraph3By3()
    {
        /*
        * S 1 1
        * 3 3 1
        * 0 3 E
        * */
        int[][] graph = new int[3][3];
        graph[0][0] = Constants.START;
        graph[1][0] = Constants.OBSTACLE;
        graph[2][0] = Constants.OBSTACLE;

        graph[0][1] = Constants.PATH;
        graph[1][1] = Constants.PATH;
        graph[2][1] = Constants.OBSTACLE;

        graph[0][2] = Constants.SPACE;
        graph[1][2] = Constants.PATH;
        graph[2][2] = Constants.END;

        return graph;
    }

    public static int[][] getGraph5By5()
    {
        /*
        * S 1 1 0 0
        * P P P P P
        * 1 1 1 1 P
        * 0 0 P P P
        * 0 1 E 1 0
        * */
        int[][] graph = new int[5][5];
        graph[0][0] = Constants.START;
        graph[1][0] = Constants.OBSTACLE;
        graph[2][0] = Constants.OBSTACLE;
        graph[3][0] = Constants.SPACE;
        graph[4][0] = Constants.SPACE;

        graph[0][1] = Constants.PATH;
        graph[1][1] = Constants.PATH;
        graph[2][1] = Constants.PATH;
        graph[3][1] = Constants.PATH;
        graph[4][1] = Constants.PATH;

        graph[0][2] = Constants.OBSTACLE;
        graph[1][2] = Constants.OBSTACLE;
        graph[2][2] = Constants.OBSTACLE;
        graph[3][2] = Constants.OBSTACLE;
        graph[4][2] = Constants.PATH;

        graph[0][3] = Constants.SPACE;
        graph[1][3] = Constants.SPACE;
        graph[2][3] = Constants.PATH;
        graph[3][3] = Constants.PATH;
        graph[4][3] = Constants.PATH;

        graph[0][4] = Constants.SPACE;
        graph[1][4] = Constants.OBSTACLE;
        graph[2][4] = Constants.END;
        graph[3][4] = Constants.OBSTACLE;
        graph[4][4] = Constants.SPACE;

        return graph;
    }
}
