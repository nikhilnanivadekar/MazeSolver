package robotics.maze.dijkstra;

import robotics.maze.robotics.projection.PointType;

public class Vertex
{
    private final int x;
    private final int y;
    private final PointType pointType;

    public Vertex(int x, int y, PointType pointType)
    {
        this.x = x;
        this.y = y;
        this.pointType = pointType;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public PointType getPointType()
    {
        return pointType;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex vertex = (Vertex) o;

        if (x != vertex.x) return false;
        if (y != vertex.y) return false;
        return pointType == vertex.pointType;
    }

    @Override
    public int hashCode()
    {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + pointType.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "Vertex(" +
                "x=" + x +
                ", y=" + y +
                ", pointType=" + pointType +
                ')';
    }
}
