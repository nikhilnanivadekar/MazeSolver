package projection.image;

public interface ImageWrapper
{
    int getWidth();

    int getHeight();

    boolean pixelMatchesColorRange(int x, int y, MarkerColorRange colorRange);
}
