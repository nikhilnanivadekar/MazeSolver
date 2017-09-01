package nvv.robotics.projection;

public enum PointType
{
    EMPTY(0),
    WALL(1),
    START(2),
    STOP(3),;

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
                return WALL;
            case 2:
                return START;
            case 3:
                return STOP;
        }
        throw new IllegalArgumentException("Unknown colorId passed:" + colorId);
    }
}
