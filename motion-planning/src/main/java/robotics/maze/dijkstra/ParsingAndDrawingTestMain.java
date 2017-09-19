package robotics.maze.dijkstra;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import robotics.maze.image.JpegImageWrapper;
import robotics.maze.image.MazeImageCreator;
import robotics.maze.projection.MazeParser;
import robotics.maze.projection.MazeParserRunner;
import robotics.maze.projection.projection.MazeMap;
import robotics.maze.utils.FileUtils;

import java.util.Set;

public class ParsingAndDrawingTestMain
{
    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            System.out.println("Missing an image file name to parse");
            return;
        }

        JpegImageWrapper imageWrapper = JpegImageWrapper.loadFile(args[0]);

        MazeImageCreator.useRoboVisionPalette();

        MazeParser mazeParser = new MazeParser();

        MazeMap mazeMap = mazeParser.buildFromImage(imageWrapper, 19, 19);

        MazeParserRunner.printMazeMap(mazeMap);

        MutableList<Vertex> vertices = MazeMapToVertexListAdapter.adapt(mazeMap);
        Pair<MutableStack<Vertex>, Set<Vertex>> path = DijkstraAlgorithm.findPath(vertices);

        MazeImageCreator.drawPathOnImage(
                mazeMap.getMazeImage(),
                mazeMap.getOriginalImageCoordinates(),
                path.getOne().collect(vertex -> PrimitiveTuples.pair(vertex.getX(), vertex.getY()), Lists.mutable.of()));

        FileUtils.saveImageToFile(mazeMap.getMazeImage(), "parsed_maze_path.PNG");
    }
}
