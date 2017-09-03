package robotics.maze.enums;

public enum PointType
{
    EMPTY(0),
    CORNER(1),
    WALL(2),
    START(3),
    FINISH(4),
    VISITED(5),
    PATH(6);

    private final int colorId;

    PointType(int colorId)
    {
        this.colorId = colorId;
    }

    public int getColorId()
    {
        return this.colorId;
    }

    public static PointType getPointType(int colorId)
    {
        switch (colorId)
        {
            case 0:
                return EMPTY;
            case 1:
                return CORNER;
            case 2:
                return WALL;
            case 3:
                return START;
            case 4:
                return FINISH;
            case 5:
                return VISITED;
            case 6:
                return PATH;
        }
        throw new IllegalArgumentException("Unknown colorId passed:" + colorId);
    }
}
