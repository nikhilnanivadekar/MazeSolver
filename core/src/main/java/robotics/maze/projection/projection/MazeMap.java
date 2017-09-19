package robotics.maze.projection.projection;

import robotics.maze.enums.PointType;

import java.awt.image.BufferedImage;

public class MazeMap
{
    private int[][] cells;
    private int width;
    private int height;

    private BufferedImage mazeImage;

    private CoordinatePoint[][] originalImageCoordinates;

    public MazeMap(int newWidth, int newHeight)
    {
        this.width = newWidth;
        this.height = newHeight;

        this.cells = new int[newHeight][newWidth];
    }

    public void setEmpty(int row, int col)
    {
        this.cells[row][col] = PointType.EMPTY.getColorId();
    }

    public void setWall(int row, int col)
    {
        this.cells[row][col] = PointType.WALL.getColorId();
    }

    public void setStart(int row, int col)
    {
        this.cells[row][col] = PointType.START.getColorId();
    }

    public void setStop(int row, int col)
    {
        this.cells[row][col] = PointType.FINISH.getColorId();
    }

    public void setPath(int row, int col)
    {
        this.cells[row][col] = PointType.PATH.getColorId();
    }

    public void setVisited(int row, int col)
    {
        this.cells[row][col] = PointType.VISITED.getColorId();
    }

    public int get(int row, int col)
    {
        return this.cells[row][col];
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public BufferedImage getMazeImage()
    {
        return this.mazeImage;
    }

    public void setMazeImage(BufferedImage newMazeImage)
    {
        this.mazeImage = newMazeImage;
    }

    public CoordinatePoint[][] getOriginalImageCoordinates()
    {
        return originalImageCoordinates;
    }

    public void setOriginalImageCoordinates(CoordinatePoint[][] originalImageCoordinates)
    {
        this.originalImageCoordinates = originalImageCoordinates;
    }
}
