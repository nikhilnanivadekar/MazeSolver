package projection;

public enum FeatureType
{
    EMPTY(0), CORNER(1), WALL(2), START(3), STOP(4);

    private int indicator;

    FeatureType(int newIndicator)
    {
        this.indicator = newIndicator;
    }

    public int getIndicator()
    {
        return this.indicator;
    }
}
