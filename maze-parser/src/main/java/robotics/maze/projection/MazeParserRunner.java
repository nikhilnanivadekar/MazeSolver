package robotics.maze.projection;

import robotics.maze.image.JpegImageWrapper;
import robotics.maze.projection.projection.MazeMap;

public class MazeParserRunner
{
    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            System.out.println("Missing an image file name to parse");
            return;
        }

        JpegImageWrapper imageWrapper = JpegImageWrapper.loadFile(args[0]);

        MazeParser mazeParser = new MazeParser();

        MazeMap mazeMap = mazeParser.buildFromImage(imageWrapper, 19, 19);

        printMazeMap(mazeMap);
    }

    public static void printMazeMap(MazeMap mazeMap)
    {
        int width = mazeMap.getWidth();
        int height = mazeMap.getHeight();

        System.out.println("WxH: " + width + "x" + height);
        for (int row = 0; row < height; row++)
        {
            for (int column = 0; column < width; column++)
            {
                System.out.print(mazeMap.get(row, column) + " ");
            }

            System.out.println();
        }
    }
}
