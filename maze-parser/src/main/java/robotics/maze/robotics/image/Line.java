package robotics.maze.robotics.image;

public class Line
{
    private double m;
    private double b;

    public Line(double slope, double yIntercept)
    {
        this.m = slope;
        this.b = yIntercept;
    }

    public Line(CoordinatePoint point, double slope)
    {
        this.m = slope;
        this.b = point.getRow() - this.getM()*point.getColumn();
    }

    public Line(CoordinatePoint p1, CoordinatePoint p2)
    {
        this.m = (p1.getRow()-p2.getRow())/(p1.getColumn()-p2.getColumn());

        this.b = p1.getRow() - this.m * p1.getColumn();
    }

    public Line parallelThroughPoint(CoordinatePoint p)
    {
        return new Line(p, this.getM());
//        return new Line(this.getM(), p.getRow() - this.getM()*p.getColumn());
    }

    public double getM()
    {
        return this.m;
    }

    public double getB()
    {
        return this.b;
    }

    public CoordinatePoint intersectWith(Line another)
    {
        double x = (another.getB()-this.getB())/(this.getM()-another.getM());
        return new CoordinatePoint(this.getM()*x + this.getB(), x);
    }

    @Override
    public String toString()
    {
        return "y = " + this.m + "*x  + " + this.b;
    }
}
