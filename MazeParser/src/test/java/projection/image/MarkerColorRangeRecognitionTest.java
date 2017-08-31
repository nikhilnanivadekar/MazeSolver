package projection.image;

import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.impl.factory.Lists;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static projection.image.MarkerColorRange.*;

public class MarkerColorRangeRecognitionTest
{
    private static ListIterable<MarkerColorRange> allColorRanges;

    @BeforeClass
    public static void setUpRanges()
    {
        allColorRanges = Lists.immutable.of(
                MarkerColorRange.CORNER_MARKER,
                MarkerColorRange.EMPTY_MARKER,
                START_MARKER,
                MarkerColorRange.STOP_MARKER,
                MarkerColorRange.WALL_MARKER
        );
    }

    @Test
    public void testGreen()
    {
        this.pickedByRange(START_MARKER,   0, 255,   0);
        this.pickedByRange(START_MARKER,  97, 127,  61);
        this.pickedByRange(START_MARKER, 168, 197, 135); // a bit too pale
        this.pickedByRange(START_MARKER, 102, 134,  65);
        this.pickedByRange(START_MARKER,  46, 142,  90);
        this.pickedByRange(START_MARKER, 102, 225, 102);

        this.pickedByRange(START_MARKER, 127, 174, 94);
        this.pickedByRange(START_MARKER, 124, 171, 91);
        this.pickedByRange(START_MARKER, 123, 170, 90);
    }

    @Test
    public void testRed()
    {
        this.pickedByRange(CORNER_MARKER,   255, 0,   0);
        this.pickedByRange(CORNER_MARKER,   255, 86,   86);
        this.pickedByRange(CORNER_MARKER,   255, 71,   71);
    }

    @Test
    public void testBlue()
    {
        this.pickedByRange(STOP_MARKER,   0, 0,   255);
        this.pickedByRange(STOP_MARKER,   20, 20,   255);
        this.pickedByRange(STOP_MARKER,   102, 136, 214);

        this.pickedByRange(STOP_MARKER,   142, 190, 255);
    }

    @Test
    public void testWhite()
    {
        this.pickedByRange(EMPTY_MARKER,   255, 255,   255);
        this.pickedByRange(EMPTY_MARKER,   250, 251,   250);
        this.pickedByRange(EMPTY_MARKER,   250, 250, 230);
    }

    @Test
    public void testBlack()
    {
        this.pickedByRange(WALL_MARKER,   0, 0,   0);
        this.pickedByRange(WALL_MARKER,   35, 35,   35);
        this.pickedByRange(WALL_MARKER,   76, 76,   76);
        this.pickedByRange(WALL_MARKER,   81, 81,   81);
    }

    private void pickedByRange(MarkerColorRange colorRange, int red, int green, int blue)
    {
        Assert.assertTrue(colorRange.checkRGB(red, green, blue));

        allColorRanges.reject(colorRange::equals).forEach(each -> Assert.assertFalse(each.checkRGB(red, green, blue)));
    }
}
