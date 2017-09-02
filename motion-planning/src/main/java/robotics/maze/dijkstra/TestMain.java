package robotics.maze.dijkstra;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import robotics.maze.robotics.image.JpegImageWrapper;
import robotics.maze.robotics.projection.MazeMap;
import robotics.maze.robotics.projection.MazeParser;
import robotics.maze.robotics.projection.MazeParserRunner;

import java.util.Set;

public class TestMain
{
    public static void main(String[] args)
    {
        JpegImageWrapper imageWrapper = JpegImageWrapper.loadFile("C:\\MazeSolver\\MazeSolver\\20170901_225505.jpg");

        MazeParser mazeParser = new MazeParser();

        MazeMap mazeMap = mazeParser.buildFromImage(imageWrapper, 19, 19);

        MazeParserRunner.printMazeMap(mazeMap);

        MutableList<Vertex> vertices = MazeMapToVertexListAdapter.adapt(mazeMap);
        Pair<MutableStack<Vertex>, Set<Vertex>> path = DijkstraAlgorithm.findPath(vertices);
        path.getTwo().forEach(each -> mazeMap.setVisited(each.getX(), each.getY()));
        path.getOne().each(each -> mazeMap.setPath(each.getX(), each.getY()));

        MazeParserRunner.printMazeMap(mazeMap);
        MazeParser.writeSolvedMazeAsImage(mazeMap);
    }
}
