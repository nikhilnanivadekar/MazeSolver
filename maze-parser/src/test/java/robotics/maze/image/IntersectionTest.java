package robotics.maze.image;

import org.junit.Assert;
import org.junit.Test;
import robotics.maze.projection.projection.CoordinatePoint;

public class IntersectionTest
{
    @Test
    public void simpleIntersection()
    {
        CoordinatePoint a = new CoordinatePoint(1.0, 2.0);
        CoordinatePoint b = new CoordinatePoint(3.0, 2.0);
        CoordinatePoint c = new CoordinatePoint(2.0, 1.0);
        CoordinatePoint d = new CoordinatePoint(2.0, 3.0);

        CoordinatePoint intersection = CoordinatePoint.segmentIntersection(a, b, c, d);

        this.assertPoint(2.0, 2.0, intersection);
    }

    @Test
    public void alsoSimpleIntersection()
    {
        CoordinatePoint a = new CoordinatePoint(2.0, 1.0);
        CoordinatePoint b = new CoordinatePoint(5.0, 4.0);
        CoordinatePoint c = new CoordinatePoint(3.0, 4.0);
        CoordinatePoint d = new CoordinatePoint(5.0, 2.0);

        CoordinatePoint intersection = CoordinatePoint.segmentIntersection(a, b, c, d);

        this.assertPoint(4.0, 3.0, intersection);
    }

    @Test
    public void outsideIntersection()
    {
        CoordinatePoint a = new CoordinatePoint(2.0, 1.0);
        CoordinatePoint b = new CoordinatePoint(4.0, 3.0);
        CoordinatePoint c = new CoordinatePoint(4.0, 5.0);
        CoordinatePoint d = new CoordinatePoint(6.0, 3.0);

        CoordinatePoint intersection = CoordinatePoint.segmentIntersection(a, b, c, d);

        this.assertPoint(5.0, 4.0, intersection);
    }

    private void assertPoint(double row, double column, CoordinatePoint intersection)
    {
        Assert.assertEquals("row", row, intersection.getRow(), 0.00001);
        Assert.assertEquals("column", column, intersection.getColumn(), 0.00001);
    }
}
