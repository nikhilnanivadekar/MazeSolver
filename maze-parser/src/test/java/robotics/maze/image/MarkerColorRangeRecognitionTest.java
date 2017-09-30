package robotics.maze.image;

import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.impl.factory.Lists;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import robotics.maze.image.MarkerColorRange;

public class MarkerColorRangeRecognitionTest
{
    private static ListIterable<MarkerColorRange> allColorRanges;

    @BeforeClass
    public static void setUpRanges()
    {
        allColorRanges = Lists.immutable.of(
                MarkerColorRange.CORNER_MARKER,
                MarkerColorRange.EMPTY_MARKER,
                MarkerColorRange.START_MARKER,
                MarkerColorRange.STOP_MARKER,
                MarkerColorRange.WALL_MARKER
        );
    }

    @Test
    public void testGreen()
    {
        this.pickedByRange(MarkerColorRange.START_MARKER, 0, 255, 0);
        this.pickedByRange(MarkerColorRange.START_MARKER, 97, 127, 61);
        this.pickedByRange(MarkerColorRange.START_MARKER, 168, 197, 135); // a bit too pale
        this.pickedByRange(MarkerColorRange.START_MARKER, 102, 134, 65);
        this.pickedByRange(MarkerColorRange.START_MARKER, 46, 142, 90);
        this.pickedByRange(MarkerColorRange.START_MARKER, 102, 225, 102);

        this.pickedByRange(MarkerColorRange.START_MARKER, 127, 174, 94);
        this.pickedByRange(MarkerColorRange.START_MARKER, 124, 171, 91);
        this.pickedByRange(MarkerColorRange.START_MARKER, 123, 170, 90);

        this.pickedByRange(MarkerColorRange.START_MARKER, 20, 89, 61);
        this.pickedByRange(MarkerColorRange.START_MARKER, 20, 85, 58);
        this.pickedByRange(MarkerColorRange.START_MARKER,  6, 47, 26);
        this.pickedByRange(MarkerColorRange.START_MARKER,  9, 65, 40);

        // lime color
        this.pickedByRange(MarkerColorRange.START_MARKER,  153, 172, 31);
        this.pickedByRange(MarkerColorRange.START_MARKER,  151, 158, 30);
        this.pickedByRange(MarkerColorRange.START_MARKER,  155, 169, 34);
    }

    @Test
    public void testRed()
    {
        this.pickedByRange(MarkerColorRange.CORNER_MARKER, 255, 0, 0);
        this.pickedByRange(MarkerColorRange.CORNER_MARKER, 255, 86, 86);
        this.pickedByRange(MarkerColorRange.CORNER_MARKER, 255, 71, 71);
    }

    @Test
    public void testBlue()
    {
        this.pickedByRange(MarkerColorRange.STOP_MARKER, 0, 0, 255);
        this.pickedByRange(MarkerColorRange.STOP_MARKER, 20, 20, 255);
        this.pickedByRange(MarkerColorRange.STOP_MARKER, 102, 136, 214);

        this.pickedByRange(MarkerColorRange.STOP_MARKER, 142, 190, 255);

        this.pickedByRange(MarkerColorRange.STOP_MARKER, 26, 34, 60);
        this.pickedByRange(MarkerColorRange.STOP_MARKER, 39, 53, 80);
        this.pickedByRange(MarkerColorRange.STOP_MARKER, 24, 30, 53);
    }

    @Test
    public void testWhite()
    {
        this.pickedByRange(MarkerColorRange.EMPTY_MARKER, 255, 255, 255);
        this.pickedByRange(MarkerColorRange.EMPTY_MARKER, 250, 251, 250);
        this.pickedByRange(MarkerColorRange.EMPTY_MARKER, 250, 250, 230);
    }

    @Test
    public void testBlack()
    {
        this.pickedByRange(MarkerColorRange.WALL_MARKER, 0, 0, 0);
        this.pickedByRange(MarkerColorRange.WALL_MARKER, 35, 35, 35);
        this.pickedByRange(MarkerColorRange.WALL_MARKER, 76, 76, 76);
        this.pickedByRange(MarkerColorRange.WALL_MARKER, 81, 81, 81);
    }

    private void pickedByRange(MarkerColorRange colorRange, int red, int green, int blue)
    {
        Assert.assertTrue("Color matches the expected range", colorRange.checkRGB(red, green, blue));

        allColorRanges.reject(colorRange::equals).forEach(each -> Assert.assertFalse("Color doesn't match other ranges", each.checkRGB(red, green, blue)));
    }
}
