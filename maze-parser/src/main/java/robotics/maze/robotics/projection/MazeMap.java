package robotics.maze.robotics.projection;

public class MazeMap
{
    private int[][] cells;
    private int width;
    private int height;

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
        this.cells[row][col] = PointType.STOP.getColorId();
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
}
