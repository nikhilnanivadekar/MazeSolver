package robotics.maze.image;

public class TestImageWrapper
implements ImageWrapper
{
    @Override
    public int getWidth()
    {
        return 0;
    }

    @Override
    public int getHeight()
    {
        return 0;
    }

    @Override
    public boolean rgbMatchesColorConstraints(int x, int y, MarkerColorRange colorRange)
    {
        return false;
    }

    @Override
    public void retrieveRgbAt(int x, int y, int[] rgb)
    {
    }
}
