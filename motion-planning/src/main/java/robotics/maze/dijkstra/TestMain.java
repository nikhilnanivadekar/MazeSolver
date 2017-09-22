package robotics.maze.dijkstra;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.stack.MutableStack;
import robotics.maze.DifferentialMotor;
import robotics.maze.Ev3Traverser;
import robotics.maze.image.JpegImageWrapper;
import robotics.maze.projection.MazeParser;
import robotics.maze.projection.MazeParserRunner;
import robotics.maze.projection.projection.MazeMap;
import robotics.maze.utils.Constants;
import robotics.maze.utils.FileUtils;

import java.io.IOException;
import java.util.List;

public class TestMain
{
    public static void main(String[] args) throws IOException, InterruptedException
    {
        JpegImageWrapper imageWrapper = JpegImageWrapper.loadFile("C:\\MazeSolver\\MazeSolver\\20170901_225505.jpg");

        MazeParser mazeParser = new MazeParser();

        MazeMap mazeMap = mazeParser.buildFromImage(imageWrapper, 19, 19);

        MazeParserRunner.printMazeMap(mazeMap);

        MutableList<Vertex> vertices = MazeMapToVertexListAdapter.adapt(mazeMap);
        MutableStack<Vertex> path = DijkstraAlgorithm.findPath(vertices);

        FileUtils.saveImageToFile(mazeMap.getMazeImage(), "parsed_maze_path.PNG");
//        MazeParserRunner.printMazeMap(mazeMap);
        List<Vertex> flattenedPath = Ev3Traverser.getFlattenedPath(path);

        DifferentialMotor pilot = new DifferentialMotor(
                Constants.WHEEL_DIAMETER,
                Constants.TRACK_WIDTH,
                "B",
                "A",
                Constants.ROTATE_SPEED,
                Constants.TRAVEL_SPEED,
                Constants.EV3_IP_ADDRESS);

        Ev3Traverser.moveAlongPath(pilot, flattenedPath);

        if (pilot != null)
        {
            pilot.getPilot().stop();
            pilot.getPilot().close();

            pilot.getEv3().disConnect();
        }
    }
}
