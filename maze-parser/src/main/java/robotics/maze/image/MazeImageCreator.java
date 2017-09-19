package robotics.maze.image;

import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.factory.primitive.ObjectIntMaps;
import robotics.maze.enums.PointType;
import robotics.maze.projection.ParsedMazeImage;
import robotics.maze.projection.projection.CoordinatePoint;

import java.awt.*;
import java.awt.image.BufferedImage;

import static robotics.maze.enums.PointType.*;

public class MazeImageCreator
{
    static private MazeImagePalette defaultPalette;
    static private MazeImagePalette roboVisionPalette;
    static private MazeImagePalette selectedPalette;

    static
    {
        defaultPalette = new MazeImagePalette()
                .setTexColor(Color.BLACK)
                .setGridLineColor(Color.MAGENTA)
                .setFont(new Font("Consolas", Font.BOLD, 10))
                .colorForType(EMPTY,   toIntRgb(255, 255, 255))
                .colorForType(CORNER,  toIntRgb(255,   0,   0))
                .colorForType(WALL,    toIntRgb(  0,   0,   0))
                .colorForType(START,   toIntRgb(  0, 255,   0))
                .colorForType(FINISH,  toIntRgb(  0,   0, 255))
                .colorForType(VISITED, toIntRgb(255, 255, 224))
                .colorForType(PATH,    toIntRgb( 34, 139,  34));

        roboVisionPalette = new MazeImagePalette()
                .setTexColor(Color.WHITE ) // 237, 238, 239
                .setGridLineColor(Color.WHITE)
                .setFont(new Font("Consolas", Font.BOLD, 10))
                .colorForType(EMPTY,   toIntRgb(212,  13,   0))
                .colorForType(CORNER,  toIntRgb(225, 126, 110))
                .colorForType(WALL,    toIntRgb( 43,   9,  15))
                .colorForType(START,   toIntRgb( 83,  24,  30))
                .colorForType(FINISH,  toIntRgb(139,  10,  10))
                .colorForType(VISITED, toIntRgb(255, 255, 224))
                .colorForType(PATH,    toIntRgb(255, 225, 233));

        selectedPalette = defaultPalette;
    }

    public static void useDefaultPalette()
    {
        selectedPalette = defaultPalette;
    }

    public static void useRoboVisionPalette()
    {
        selectedPalette = roboVisionPalette;
    }

    private static int toIntRgb(int red, int green, int blue)
    {
        int alpha = 255;
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static BufferedImage createImageFromParsedMaze(ParsedMazeImage parsedMaze)
    {
        int width = parsedMaze.getWidth();
        int height = parsedMaze.getHeight();

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int row = 0; row < height; row++)
        {
            for (int column = 0; column < width; column++)
            {
                PointType pointType = parsedMaze.getFeatureAt(row, column).getType();

                bi.setRGB(column, row, selectedPalette.getColorForType(pointType));
            }
        }

        return bi;
    }


    private static void drawLine(Graphics g, CoordinatePoint from, CoordinatePoint to)
    {
        g.drawLine((int) from.getColumn(), (int) from.getRow(), (int) to.getColumn(), (int) to.getRow());
    }

    public static void overlayGridOnImage(BufferedImage bi, CoordinatePoint[][] grid)
    {
        int rowCount = grid.length;
        int columnCount = grid[0].length;

        Graphics g = bi.getGraphics();
        g.setColor(selectedPalette.getGridLineColor());

        for (int curRow = 0; curRow < rowCount; curRow++)
        {
            drawLine(g, grid[curRow][0], grid[curRow][columnCount - 1]);
        }
        for (int curCol = 0; curCol < columnCount; curCol++)
        {
            drawLine(g, grid[0][curCol], grid[rowCount - 1][curCol]);
        }
    }

    public static void addTextToImage(BufferedImage bi, ListIterable<String> text)
    {
        Graphics g = bi.getGraphics();

        int lineHeight = bi.getHeight()/50;
        float fontHeight = (float) (0.8 * lineHeight);
        Font font = selectedPalette.getFont().deriveFont(fontHeight);

        g.setColor(selectedPalette.getTextColor());
        g.setFont(font);

        text.forEachWithIndex((s, i) -> g.drawString("0" + i + ": " + s, 20, (i+1)*lineHeight));
    }

    public static void drawPathOnImage(BufferedImage bi, CoordinatePoint[][] grid, ListIterable<IntIntPair> pathPointsOnGrid)
    {
        Graphics g = bi.getGraphics();

        g.setColor(new Color(selectedPalette.getColorForType(PointType.PATH), true));

        ((Graphics2D) g).setStroke(new BasicStroke(10));

        CoordinatePoint currentPoint = grid[pathPointsOnGrid.get(0).getOne()][pathPointsOnGrid.get(0).getTwo()];
        for (int i = 1; i < pathPointsOnGrid.size(); i++)
        {
            CoordinatePoint nextPoint = grid[pathPointsOnGrid.get(i).getOne()][pathPointsOnGrid.get(i).getTwo()];
            drawLine(g, currentPoint, nextPoint);
            currentPoint = nextPoint;
        }
    }

    private static class MazeImagePalette
    {
        private Color textColor;
        private Color gridLineColor;

        private Font font;

        private MutableObjectIntMap<PointType> mazeFeaturePalette = ObjectIntMaps.mutable.of();

        public MazeImagePalette()
        {
        }

        public MazeImagePalette setTexColor(Color newTextColor)
        {
            this.textColor = newTextColor;
            return this;
        }

        public MazeImagePalette setGridLineColor(Color newGridLineColor)
        {
            this.gridLineColor = newGridLineColor;
            return this;
        }

        public MazeImagePalette setFont(Font newFont)
        {
            this.font = newFont;
            return this;
        }

        public MazeImagePalette colorForType(PointType pointType, int color)
        {
            this.mazeFeaturePalette.put(pointType, color);
            return this;
        }

        public int getColorForType(PointType pointType)
        {
            return this.mazeFeaturePalette.get(pointType);
        }

        public Color getTextColor()
        {
            return this.textColor;
        }

        public Color getGridLineColor()
        {
            return this.gridLineColor;
        }

        public Font getFont()
        {
            return this.font;
        }
    }
}
