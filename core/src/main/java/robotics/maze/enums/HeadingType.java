package robotics.maze.enums;

public enum HeadingType
{
    POSITIVE_Y,
    NEGATIVE_Y,
    POSITIVE_X,
    NEGATIVE_X;

    /**
     * The co-ordinate axis are:
     * <pre>
     *          -X
     *     -Y __|__ +Y
     *          |
     *          +X
     * </pre>
     */
    public int getDegrees(HeadingType nextHeading)
    {
        if (this.isNextHeadingCompliment(nextHeading))
        {
            return 180;
        }
        if (this.needsNegativeNinetyRotation(nextHeading))
        {
            return -90;
        }
        if (this.needsPositiveNinetyRotation(nextHeading))
        {
            return 90;
        }
        return 0;
    }

    private boolean isNextHeadingCompliment(HeadingType nextHeading)
    {
        return (this == POSITIVE_X && nextHeading == NEGATIVE_X)
                || (this == POSITIVE_Y && nextHeading == NEGATIVE_Y)
                || (this == NEGATIVE_X && nextHeading == POSITIVE_X)
                || (this == NEGATIVE_Y && nextHeading == POSITIVE_Y);
    }

    private boolean needsPositiveNinetyRotation(HeadingType nextHeading)
    {
        return (this == POSITIVE_X && nextHeading == POSITIVE_Y)
                || (this == POSITIVE_Y && nextHeading == NEGATIVE_X)
                || (this == NEGATIVE_X && nextHeading == NEGATIVE_Y)
                || (this == NEGATIVE_Y && nextHeading == POSITIVE_X);
    }

    private boolean needsNegativeNinetyRotation(HeadingType nextHeading)
    {
        return (this == POSITIVE_X && nextHeading == NEGATIVE_Y)
                || (this == NEGATIVE_Y && nextHeading == NEGATIVE_X)
                || (this == NEGATIVE_X && nextHeading == POSITIVE_Y)
                || (this == POSITIVE_Y && nextHeading == POSITIVE_X);
    }
}
