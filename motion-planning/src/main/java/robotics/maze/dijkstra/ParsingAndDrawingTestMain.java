package robotics.maze.dijkstra;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import robotics.maze.exceptions.AmazeProcessingException;
import robotics.maze.image.JpegImageWrapper;
import robotics.maze.image.MazeImageCreator;
import robotics.maze.projection.MazeParser;
import robotics.maze.projection.MazeParserRunner;
import robotics.maze.projection.projection.MazeMap;
import robotics.maze.utils.FileUtils;
import robotics.maze.utils.Stopwatch;

public class ParsingAndDrawingTestMain
{
    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            System.out.println("Missing an image file name to parse");
            return;
        }

        Stopwatch.enableOutput();
        Stopwatch.start("Loading image");
        JpegImageWrapper imageWrapper = JpegImageWrapper.loadFile(args[0]);

        MazeImageCreator.useRoboVisionPalette();
//        MazeImageCreator.useDefaultPalette();

        MazeParser mazeParser = new MazeParser();

        MazeMap mazeMap = mazeParser.buildFromImage(imageWrapper, 19, 19);

        Stopwatch.report("Maze built");

        MazeParserRunner.printMazeMap(mazeMap);


        try
        {
            MutableList<Vertex> vertices = MazeMapToVertexListAdapter.adapt(mazeMap);
            MutableStack<Vertex> path = DijkstraAlgorithm.findPath(vertices);

            Stopwatch.report("Maze solved");

            MazeImageCreator.drawPathOnImage(
                    mazeMap.getMazeImage(),
                    mazeMap.getOriginalImageCoordinates(),
                    path.collect(vertex -> PrimitiveTuples.pair(vertex.getX(), vertex.getY()), Lists.mutable.of()));

            MazeImageCreator.addRightTextToImage(mazeMap.getMazeImage(),
                    path.collect(vertex -> String.format("%02d-%02d", vertex.getX(), vertex.getY()), Lists.mutable.of()));

            Stopwatch.report("Path drawn");
        }
        catch (AmazeProcessingException e)
        {
            System.out.println("Failed: " + e.getMessage());
        }

        FileUtils.saveImageToFile(mazeMap.getMazeImage(), "parsed_maze_path");

        Stopwatch.report("Wrote path");
        Stopwatch.stop("Done");
    }
}
