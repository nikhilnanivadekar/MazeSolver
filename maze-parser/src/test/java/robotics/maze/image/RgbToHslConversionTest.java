package robotics.maze.image;

import org.junit.Assert;
import org.junit.Test;
import robotics.maze.image.MarkerColorRange;

public class RgbToHslConversionTest
{

    @Test
    public void testBunchOfColors()
    {
        this.assertRgbToHsl("black",    0,  0,  0, 0.0,0.0, 0.0);
        this.assertRgbToHsl("White",  255,255,255,   0,0.0,1.00);
        this.assertRgbToHsl("Red",    255,  0,  0,   0,1.00,0.50);
        this.assertRgbToHsl("Lime",     0,255,  0, 120,1.00,0.50);
        this.assertRgbToHsl("Blue",     0,  0,255, 240,1.00,0.50);
        this.assertRgbToHsl("Yellow", 255,255,  0,  60,1.00,0.50);
        this.assertRgbToHsl("Cyan",     0,255,255, 180,1.00,0.50);
        this.assertRgbToHsl("Magenta",255,  0,255, 300,1.00,0.50);
        this.assertRgbToHsl("Silver", 192,192,192,   0,0.0, 0.753);
        this.assertRgbToHsl("Gray",   128,128,128,   0,0.0, 0.502);
        this.assertRgbToHsl("Maroon", 128,  0,  0,   0,1.00,0.251);
        this.assertRgbToHsl("Olive",  128,128,  0,  60,1.00,0.251);
        this.assertRgbToHsl("Green",    0,128,  0, 120,1.00,0.251);
        this.assertRgbToHsl("Purple", 128,  0,128, 300,1.00,0.251);
        this.assertRgbToHsl("Teal",	  0,128,128, 180,1.00,0.251);
        this.assertRgbToHsl("Navy",	  0,  0,128, 240,1.00,0.251);
    }

    private void assertRgbToHsl(String colorName, int red, int green, int blue, double h, double s, double l)
    {
        double[] hsl = MarkerColorRange.rgbToHsl(red, green, blue);

        Assert.assertArrayEquals(colorName, new double[] {h, s, l}, hsl, 0.0001);
    }
}
