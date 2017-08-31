package nvv.robotics.image;

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
    public boolean pixelMatchesColorRange(int x, int y, MarkerColorRange colorRange)
    {
        return false;
    }
}
