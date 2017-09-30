package robotics.maze.projection;

import robotics.maze.enums.PointType;

import static robotics.maze.image.ImageWrapper.BLUE;
import static robotics.maze.image.ImageWrapper.GREEN;
import static robotics.maze.image.ImageWrapper.RED;

public class MazeFeature
{
    private PointType type;
    private int tag = -1;
    private int column;
    private int row;

    public MazeFeature(int newColumn, int newRow, PointType newType)
    {
        this.type = newType;
        this.column = newColumn;
        this.row = newRow;
    }

    public PointType getType()
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

    public int getColumn()
    {
        return this.column;
    }

    public int getRow()
    {
        return this.row;
    }
}
