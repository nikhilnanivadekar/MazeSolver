package robotics.maze.image;

public class MazePoint
{
    private int row;
    private int column;

    public MazePoint(int newRow, int newColumn)
    {
        this.row = newRow;
        this.column = newColumn;
    }

    public int getRow()
    {
        return this.row;
    }

    public int getColumn()
    {
        return this.column;
    }

    @Override
    public String toString()
    {
        return "{" + row + ", " + column + '}';
    }
}
