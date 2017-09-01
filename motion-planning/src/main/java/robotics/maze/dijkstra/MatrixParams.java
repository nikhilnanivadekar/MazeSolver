package robotics.maze.dijkstra;

public class MatrixParams
{
    private final int xAxisSize;
    private final int yAxisSize;
    private final int xAxisCost;
    private final int yAxisCost;
    private final int diagonalCost;
    private final boolean isDiagonalMovementAllowed;
    private final int space;
    private final int obstacle;
    private final int start;
    private final int end;

    public MatrixParams(int xAxisSize, int yAxisSize, int xAxisCost, int yAxisCost, int diagonalCost, boolean isDiagonalMovementAllowed, int space, int obstacle, int start, int end)
    {
        this.xAxisSize = xAxisSize;
        this.yAxisSize = yAxisSize;
        this.xAxisCost = xAxisCost;
        this.yAxisCost = yAxisCost;
        this.diagonalCost = diagonalCost;
        this.isDiagonalMovementAllowed = isDiagonalMovementAllowed;
        this.space = space;
        this.obstacle = obstacle;
        this.start = start;
        this.end = end;
    }

    public int getxAxisSize()
    {
        return xAxisSize;
    }

    public int getyAxisSize()
    {
        return yAxisSize;
    }

    public int getxAxisCost()
    {
        return xAxisCost;
    }

    public int getyAxisCost()
    {
        return yAxisCost;
    }

    public int getDiagonalCost()
    {
        return diagonalCost;
    }

    public boolean isDiagonalMovementAllowed()
    {
        return isDiagonalMovementAllowed;
    }

    public int getSpace()
    {
        return space;
    }

    public int getObstacle()
    {
        return obstacle;
    }

    public int getStart()
    {
        return start;
    }

    public int getEnd()
    {
        return end;
    }
}
