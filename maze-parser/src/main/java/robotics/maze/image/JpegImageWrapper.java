package robotics.maze.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

public class JpegImageWrapper
implements ImageWrapper
{
    private final int width;
    private final int height;

    private boolean hasAlphaChannel;
    private int pixelLength;

    private byte[] pixels;

    public JpegImageWrapper(BufferedImage image)
    {
        this.pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        this.width = image.getWidth();
        this.height = image.getHeight();

        this.hasAlphaChannel = image.getAlphaRaster() != null;
        pixelLength = hasAlphaChannel ? 4 : 3;
    }

    @Override
    public int getWidth()
    {
        return this.width;
    }

    @Override
    public int getHeight()
    {
        return this.height;
    }

    public void retrieveRgbAt(int x, int y, int rgb[])
    {
        int pos = (y * this.pixelLength * this.width) + (x * this.pixelLength);

        if (this.hasAlphaChannel)
        {
            pos++;
        }

        rgb[BLUE] = (int) pixels[pos++] & 0xff; // blue
        rgb[GREEN] = (int) pixels[pos++] & 0xff; // green
        rgb[RED] = (int) pixels[pos++] & 0xff; // red
    }

    @Override
    public boolean rgbMatchesColorConstraints(int x, int y, MarkerColorRange colorRange)
    {
        int pos = (y * this.pixelLength * this.width) + (x * this.pixelLength);

        if (this.hasAlphaChannel)
        {
            pos++;
        }

        int blue = (int) pixels[pos++] & 0xff; // blue
        int green = (int) pixels[pos++] & 0xff; // green
        int red = (int) pixels[pos++] & 0xff; // red

        return colorRange.checkRGB(red, green, blue);
    }

    public static JpegImageWrapper loadFile(String fileName)
    {
        return JpegImageWrapper.loadFile(new File(fileName));
    }

    public static JpegImageWrapper loadFile(File file)
    {
        BufferedImage image = null;
        try
        {
            image = ImageIO.read(file);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Unable to open file:" + file.getAbsolutePath(), e);
        }

        return new JpegImageWrapper(image);
    }
}
