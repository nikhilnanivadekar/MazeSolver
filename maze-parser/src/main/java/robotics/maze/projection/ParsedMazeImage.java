package robotics.maze.projection;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.Interval;
import robotics.maze.enums.PointType;

public class ParsedMazeImage
{
    private MazeFeature[][] mazeFeatures;
    private int width;
    private int height;

    public ParsedMazeImage(int newImageWidth, int newImageHeight)
    {
        this.width = newImageWidth;
        this.height = newImageHeight;
        this.mazeFeatures = new MazeFeature[newImageHeight][newImageWidth];
    }

    public MazeFeature createAndSetFeature(int column, int row, PointType PointType)
    {
        this.mazeFeatures[row][column] = new MazeFeature(column, row, PointType);
        return this.mazeFeatures[row][column];
    }

    public MazeFeature findAnyNeighbor(int x, int y, PointType PointType)
    {
        return this.findAnyMatchingNeighbor(x, y, other -> PointType == other);
    }

    public MazeFeature findAnyNeighborNot(int x, int y, PointType PointType)
    {
        return this.findAnyMatchingNeighbor(x, y, other -> PointType != other);
    }

    private MazeFeature findAnyMatchingNeighbor(int x, int y, Predicate<PointType> condition)
    {
        MazeFeature found = null;

        if (found == null) { found = this.getMatchingFeatureOrNull(x-1, y-1, condition); }
        if (found == null) { found = this.getMatchingFeatureOrNull(x+0, y-1, condition); }
        if (found == null) { found = this.getMatchingFeatureOrNull(x+1, y-1, condition); }
        if (found == null) { found = this.getMatchingFeatureOrNull(x-1, y+0, condition); }
        if (found == null) { found = this.getMatchingFeatureOrNull(x+1, y+0, condition); }
        if (found == null) { found = this.getMatchingFeatureOrNull(x-1, y+1, condition); }
        if (found == null) { found = this.getMatchingFeatureOrNull(x+0, y+1, condition); }
        if (found == null) { found = this.getMatchingFeatureOrNull(x+1, y+1, condition); }

        return found;
    }

    public PointType getCommonPointTypeInTheArea(int rowFrom, int rowTo, int colFrom, int colTo)
    {
        int area = (rowTo - rowFrom) * (colTo - colFrom);

        MutableList<MazeFeature> neighbourhood = Lists.mutable.of();
        for (int row = rowFrom; row < rowTo; row++)
        {
            for (int col = colFrom; col < colTo; col++)
            {
                neighbourhood.add(this.getFeatureAt(row, col));
            }
        }

        Interval.fromTo(rowFrom, rowTo)
                .flatCollect(
                        row -> Interval.fromTo(colFrom, colTo)
                                       .collect(col -> this.getFeatureAt(row, col)));

        MutableList<ObjectIntPair<PointType>> popularFeature = neighbourhood
                .asLazy()
                .collect(MazeFeature::getType)
                .reject(each -> each == PointType.EMPTY)
                .toSortedBag()
                .topOccurrences(1);

        if (popularFeature.size() < 1)
        {
            return PointType.EMPTY;
        }

        return popularFeature.get(0).getTwo() * 20 > area ? popularFeature.get(0).getOne() : PointType.EMPTY;
    }

    private MazeFeature getMatchingFeatureOrNull(int x, int y, Predicate<PointType> condition)
    {
        if (this.goodCoordinates(x, y))
        {
            MazeFeature featureAtLocation = this.mazeFeatures[y][x];

            if (featureAtLocation != null && condition.accept(featureAtLocation.getType()))
            {
                return featureAtLocation;
            }
        }

        return null;
    }

    private boolean goodCoordinates(int x, int y)
    {
        return x >= 0 && y >= 0 && x < this.width && y < this.height;
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }




    public MazeFeature getFeatureAt(int row, int column)
    {
        if (!goodCoordinates(column, row))
        {
            return null;
        }

        return this.mazeFeatures[row][column];
    }
}
