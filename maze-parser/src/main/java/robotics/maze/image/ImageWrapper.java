package robotics.maze.image;

public interface ImageWrapper
{
    public static final int RED = 0;
    public static final int GREEN = 1;
    public static final int BLUE = 2;
    int getWidth();

    int getHeight();

    boolean rgbMatchesColorConstraints(int x, int y, MarkerColorRange colorRange);

    void retrieveRgbAt(int x, int y, int rgb[]);
}
