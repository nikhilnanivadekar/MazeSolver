package robotics.maze.image;

import org.junit.Assert;
import org.junit.Test;
import robotics.maze.projection.projection.CoordinatePoint;

public class LineTest
{
    @Test
    public void lineTroughTwoPoints()
    {
        CoordinatePoint p1 = new CoordinatePoint(3, 1);
        CoordinatePoint p2 = new CoordinatePoint(5, -2);

        Line l = new Line(p1, p2);

        this.assertLine(-2.0/3.0, 11.0/3.0, l);

        l = new Line(new CoordinatePoint(7, -8), new CoordinatePoint(8, -5));

        this.assertLine(1.0/3.0, 29.0/3.0, l);

        l = new Line(new CoordinatePoint(2, 3), new CoordinatePoint(-4, 7));
        this.assertLine(-1.5, 6.5, l);
    }

    @Test
    public void parallelLineThroughPoint()
    {
        Line l = new Line(2, 1);

        Line lPar = l.parallelThroughPoint(new CoordinatePoint(4, 3));

        this.assertLine(2.0, -2.0, lPar);

        l = new Line(3, 5);
        lPar = l.parallelThroughPoint(new CoordinatePoint(7, 1));

        this.assertLine(3.0, 4.0, lPar);

        l = new Line(4, 3);
        lPar = l.parallelThroughPoint(new CoordinatePoint(9, 5));

        this.assertLine(4.0, -11.0, lPar);
    }

    @Test
    public void lineIntersection()
    {
        Line l1 = new Line(2.0, 3.0);
        Line l2 = new Line(-0.5, 7.0);

        this.assertPoint(6.2, 1.6, l1.intersectWith(l2));

        l1 = new Line(3.0, 6.0);
        l2 = new Line(-1.0, 12.0);

        this.assertPoint(10.5, 1.5, l1.intersectWith(l2));
    }

    private void assertLine(double slope, double intercept, Line line)
    {
        Assert.assertEquals("Slope", slope, line.getM(), 0.000001);
        Assert.assertEquals("y-intercept", intercept, line.getB(), 0.000001);
    }

    private void assertPoint(double row, double column, CoordinatePoint intersection)
    {
        Assert.assertEquals("row", row, intersection.getRow(), 0.00001);
        Assert.assertEquals("column", column, intersection.getColumn(), 0.00001);
    }

}
