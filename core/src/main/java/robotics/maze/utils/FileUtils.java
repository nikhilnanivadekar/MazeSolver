package robotics.maze.utils;

import org.eclipse.collections.api.stack.MutableStack;
import robotics.maze.dijkstra.Vertex;
import robotics.maze.enums.PointType;
import robotics.maze.exceptions.AmazeProcessingException;
import robotics.maze.projection.projection.MazeMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Set;

public class FileUtils
{
    public static File downloadAndSaveMedia(String mediaUrl, long id) throws IOException
    {
        InputStream in = new BufferedInputStream(new URL(mediaUrl).openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1 != (n = in.read(buf)))
        {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();
        String fileName = id + ".jpg";
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(response);
        fos.close();
        return new File(fileName);
    }

    public static File saveImageToFile(BufferedImage bi, String fileName)
    {
        try
        {
            File output = new File(fileName);
            boolean cache = ImageIO.getUseCache();
            ImageIO.setUseCache(false);
            ImageIO.write(bi, "PNG", output);
            ImageIO.setUseCache(cache);
            return output;
        }
        catch (IOException e)
        {
            throw new AmazeProcessingException("Failed to write to file'" + fileName + "' because of \"" + e.getMessage() +'"', e);
        }
    }

    public static void writeSolvedMaze(MutableStack<Vertex> path, Set<Vertex> visitedVertices, MazeMap mazeMap)
    {
        FileUtils.markMazeMapWithVisitedVertices(visitedVertices, mazeMap);
        FileUtils.markMazeMapWithPath(path, mazeMap);

        int width = mazeMap.getWidth();
        int height = mazeMap.getHeight();

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int row = 0; row < height; row++)
        {
            for (int column = 0; column < width; column++)
            {
                int red = 0;
                int green = 0;
                int blue = 0;
                int alpha = 255;
                switch (PointType.getPointType(mazeMap.get(row, column)))
                {
                    case EMPTY:
                        red = green = blue = 255;
                        break;
                    case CORNER:
                        red = 255;
                        break;
                    case WALL:
                        break;
                    case START:
                        green = 255;
                        break;
                    case FINISH:
                        blue = 255;
                        break;
                    case VISITED:
                        red = 245;
                        green = 245;
                        blue = 245;
                        break;
                    case PATH:
                        red = 34;
                        green = 139;
                        blue = 34;
                }

                int colorWithAlpha = (alpha << 24) | (red << 16) | (green << 8) | blue;

                bi.setRGB(column, row, colorWithAlpha);
            }
        }

        Image scaledInstance = bi.getScaledInstance(900, 900, Image.SCALE_DEFAULT);
        BufferedImage bufferedImage = new BufferedImage(scaledInstance.getWidth(null), scaledInstance.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(scaledInstance, 0, 0, null);
        bGr.dispose();

       FileUtils.saveImageToFile(bufferedImage, "solved_maze.PNG");
    }

    private static void markMazeMapWithPath(MutableStack<Vertex> path, MazeMap mazeMap)
    {
        path.each(each -> mazeMap.setPath(each.getX(), each.getY()));
    }

    private static void markMazeMapWithVisitedVertices(Set<Vertex> visitedVertices, MazeMap mazeMap)
    {
        visitedVertices.forEach(each ->
        {
            if (each.getPointType() == PointType.START
                    || each.getPointType() == PointType.FINISH)
            {
                return;
            }
            mazeMap.setVisited(each.getX(), each.getY());
        });
    }
}
