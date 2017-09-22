package robotics.maze.image;

import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.factory.Maps;
import robotics.maze.enums.PointType;
import robotics.maze.projection.MazeFeature;
import robotics.maze.projection.ParsedMazeImage;
import robotics.maze.projection.projection.CoordinatePoint;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MazeImageCreator
{
    // defaultPalette = 0, roboVisionPalette = 1
    static private MazeImagePalette defaultPalette;
    static private MazeImagePalette roboVisionPalette;
    static private MazeImagePalette selectedPalette;

    static
    {
        defaultPalette = new MazeImagePalette()
                .setTexColor(Color.BLACK)
                .setGridLineColor(Color.MAGENTA)
                .setFont(new Font("Consolas", Font.BOLD, 10))
                .colorForType(PointType.EMPTY, new ColorDescriptor(255, 255, 255))
                .colorForType(PointType.CORNER, new ColorDescriptor(255, 0, 0))
                .colorForType(PointType.WALL, new ColorDescriptor(0, 0, 0))
                .colorForType(PointType.START, new ColorDescriptor(0, 255, 0))
                .colorForType(PointType.FINISH, new ColorDescriptor(0, 0, 255))
                .colorForType(PointType.VISITED, new ColorDescriptor(255, 255, 224))
                .colorForType(PointType.PATH, new ColorDescriptor(34, 139, 34));

        roboVisionPalette = new MazeImagePalette()
                .setTexColor(Color.WHITE) // 237, 238, 239
                .setGridLineColor(Color.WHITE)
                .setFont(new Font("Consolas", Font.BOLD, 10))
                .colorForType(PointType.EMPTY, new ColorDescriptor(212, 13, 0))
                .colorForType(PointType.CORNER, new ColorDescriptor(225, 126, 110))
                .colorForType(PointType.WALL, new ColorDescriptor(43, 9, 15))
                .colorForType(PointType.START, new ColorDescriptor(83, 24, 30))
                .colorForType(PointType.FINISH, new ColorDescriptor(139, 10, 10))
                .colorForType(PointType.VISITED, new ColorDescriptor(255, 255, 224))
                .colorForType(PointType.PATH, new ColorDescriptor(255, 225, 233));

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

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int row = 0; row < height; row++)
        {
            for (int column = 0; column < width; column++)
            {
                MazeFeature feature = parsedMaze.getFeatureAt(row, column);
                PointType pointType = feature.getType();

                ColorDescriptor colorDescriptor = selectedPalette.getColorDescriptorForType(pointType);

                if (pointType == PointType.EMPTY)
                {
                    int shading = feature.getShading();

                    int newRed = colorDescriptor.getRed() - shading;
                    int newGreen = colorDescriptor.getGreen() - shading;
                    int newBlue = colorDescriptor.getBlue() - shading;

                    bi.setRGB(column, row, toIntRgb(
                            newRed > 0 ? newRed : 0,
                            newGreen > 0 ? newGreen : 0,
                            newBlue > 0 ? newBlue : 0));
                }
                else
                {
                    bi.setRGB(column, row, colorDescriptor.getColorAsInt());
                }
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

        int lineHeight = bi.getHeight() / 50;
        float fontHeight = (float) (0.8 * lineHeight);
        Font font = selectedPalette.getFont().deriveFont(fontHeight);

        g.setColor(selectedPalette.getTextColor());
        g.setFont(font);

        text.forEachWithIndex((s, i) -> g.drawString(String.format("%03d", i) + ": " + s, 20, (i + 1) * lineHeight));
    }

    public static void addRightTextToImage(BufferedImage bi, ListIterable<String> text)
    {
        Graphics g = bi.getGraphics();

        int lineHeight = bi.getHeight() / 50;
        float fontHeight = (float) (0.8 * lineHeight);
        Font font = selectedPalette.getFont().deriveFont(fontHeight);

        g.setColor(selectedPalette.getTextColor());
        g.setFont(font);

        if (text.size() > 0)
        {
            int leftMargin = bi.getWidth() - g.getFontMetrics().stringWidth(text.get(0) + "000:00") - 20;
            text.forEachWithIndex((s, i) -> g.drawString(String.format("%03d", i) + ": " + s, leftMargin, (i + 1) * lineHeight));
        }
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

        private MutableMap<PointType, ColorDescriptor> mazeFeaturePalette = Maps.mutable.of();

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

        public MazeImagePalette colorForType(PointType pointType, ColorDescriptor color)
        {
            this.mazeFeaturePalette.put(pointType, color);
            return this;
        }

        public int getColorForType(PointType pointType)
        {
            return this.mazeFeaturePalette.get(pointType).getColorAsInt();
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

        public ColorDescriptor getColorDescriptorForType(PointType pointType)
        {
            return this.mazeFeaturePalette.get(pointType);
        }
    }

    static private class ColorDescriptor
    {
        private int red;
        private int green;
        private int blue;

        private int colorAsInt;

        public ColorDescriptor(int newRed, int newGreen, int newBlue)
        {
            this.red = newRed;
            this.green = newGreen;
            this.blue = newBlue;

            this.colorAsInt = toIntRgb(this.red, this.green, this.blue);
        }

        public int getRed()
        {
            return this.red;
        }

        public int getGreen()
        {
            return this.green;
        }

        public int getBlue()
        {
            return this.blue;
        }

        public int getColorAsInt()
        {
            return this.colorAsInt;
        }
    }
}
