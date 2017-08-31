package projection;

public class MazeMap
{
    private static final int EMPTY = 0;
    private static final int WALL = 1;
    private static final int START = 2;
    private static final int STOP = 3;

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
        this.cells[row][col] = EMPTY;
    }

    public void setWall(int row, int col)
    {
        this.cells[row][col] = WALL;
    }

    public void setStart(int row, int col)
    {
        this.cells[row][col] = START;
    }

    public void setStop(int row, int col)
    {
        this.cells[row][col] = STOP;
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
