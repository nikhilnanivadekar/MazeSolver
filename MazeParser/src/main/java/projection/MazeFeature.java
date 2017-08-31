package projection;

public class MazeFeature
{
    private FeatureType type;
    private int tag = -1;
    private int x;
    private int y;

    public MazeFeature(int newX, int newY, FeatureType newType)
    {
        this.type = newType;
        this.x = newX;
        this.y = newY;
    }

    public FeatureType getType()
    {
        return this.type;
    }

    public int getTag()
    {
        return this.tag;
    }

    public boolean isNotTagged()
    {
        return this.tag < 0;
    }

    public boolean isTagged()
    {
        return this.tag > -1;
    }

    public void setTag(int newTag)
    {
        this.tag = newTag;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }
}
