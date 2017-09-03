package robotics.maze.image;

public class CoordinatePoint
{
    private double row;
    private double column;

    /**
     * find the intersection point between a--b and c--d line segments.
     * @param a - the start of the first segment
     * @param b - the end of the firs segment
     * @param c - the start of the second segment
     * @param d - the start og the second segment
     * @return the intersection point between the segments
     */
    static public CoordinatePoint intersection(CoordinatePoint a, CoordinatePoint b, CoordinatePoint c, CoordinatePoint d)
    {
        CoordinatePoint r = a.minus(b);
        CoordinatePoint s = c.minus(d);

        double rs = r.product2D(s);
        double t = d.minus(b).product2D(s) / rs;
        double u = d.minus(b).product2D(r) / rs;

        return new CoordinatePoint(
                b.getRow() + r.getRow() * t,
                b.getColumn() + r.getColumn() * t);

    }

    public CoordinatePoint(double newRow, double newColumn)
    {
        this.row = newRow;
        this.column = newColumn;
    }

    public CoordinatePoint(CoordinatePoint mazePoint, double offset)
    {
        this(mazePoint.getRow()+0.5, mazePoint.getColumn()+0.5);
    }

    public double getRow()
    {
        return this.row;
    }

    public double getColumn()
    {
        return this.column;
    }

    @Override
    public String toString()
    {
        return "{" + this.row + ", " + this.column + '}';
    }

    public CoordinatePoint incrementBy(CoordinatePoint delta)
    {
        return new CoordinatePoint(this.row + delta.row, this.column + delta.column);
    }

    public CoordinatePoint minus(CoordinatePoint other)
    {
        return new CoordinatePoint(this.getRow() - other.getRow(), this.getColumn() - other.getColumn());
    }

    public double product2D(CoordinatePoint other)
    {
        return this.getRow()*other.getColumn() - this.getColumn()*other.getRow();
    }
}
