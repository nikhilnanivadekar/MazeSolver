package robotics.maze.projection;

import robotics.maze.image.JpegImageWrapper;
import robotics.maze.projection.projection.MazeMap;

public class MazeParserRunner
{
    public static void main(String[] args)
    {
        String path = "";
//        String fileName = "maze1_straight.jpg";
//        String fileName = "maze1_perspective.jpg";
//        String fileName = "maze3_topdown.jpg";
//        String fileName = "maze3_perspective.jpg";
//        String fileName = "maze3_perspective_extreme.jpg";
//        String fileName = "maze5_perspective.jpg";
        String fileName = "maze5_perspective_extreme.jpg";
//        String fileName = "maze5_perspective_extreme_left.jpg";
//        String fileName = "maze5_perspective_extreme_right.jpg";

        JpegImageWrapper imageWrapper = JpegImageWrapper.loadFile(path + fileName);

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
