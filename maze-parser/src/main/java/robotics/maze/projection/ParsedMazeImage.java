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
        MazeFeature newFeature = new MazeFeature(column, row, PointType);
        this.mazeFeatures[row][column] = newFeature;
        return newFeature;
    }

    public MazeFeature findAnyNeighbor(int x, int y, PointType pointType)
    {
        return this.findAnyMatchingNeighbor(x, y, other -> pointType == other);
    }

    public MazeFeature findAnyNeighborNot(int column, int row, PointType pointType)
    {
        return this.findAnyMatchingNeighbor(column, row, other -> pointType != other);
    }

    private MazeFeature findAnyMatchingNeighbor(int column, int row, Predicate<PointType> condition)
    {
        MazeFeature found = null;

        if (found == null) { found = this.getMatchingFeatureOrNull(column-1, row-1, condition); }
        if (found == null) { found = this.getMatchingFeatureOrNull(column+0, row-1, condition); }
        if (found == null) { found = this.getMatchingFeatureOrNull(column+1, row-1, condition); }
        if (found == null) { found = this.getMatchingFeatureOrNull(column-1, row+0, condition); }
        if (found == null) { found = this.getMatchingFeatureOrNull(column+1, row+0, condition); }
        if (found == null) { found = this.getMatchingFeatureOrNull(column-1, row+1, condition); }
        if (found == null) { found = this.getMatchingFeatureOrNull(column+0, row+1, condition); }
        if (found == null) { found = this.getMatchingFeatureOrNull(column+1, row+1, condition); }

        return found;
    }

    public PointType getPredominantPointTypeInTheArea(int rowFrom, int rowTo, int colFrom, int colTo)
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

        MutableList<ObjectIntPair<PointType>> popularFeature = neighbourhood
                .asLazy()
                .collect(MazeFeature::getType)
                .reject(each -> each == PointType.EMPTY)
                .toBag()
                .topOccurrences(1);

        if (popularFeature.size() < 1)
        {
            return PointType.EMPTY;
        }

        return popularFeature.get(0).getTwo() * 20 > area ? popularFeature.get(0).getOne() : PointType.EMPTY;
    }

    private MazeFeature getMatchingFeatureOrNull(int column, int row, Predicate<PointType> condition)
    {
        if (this.goodCoordinates(column, row))
        {
            MazeFeature featureAtLocation = this.mazeFeatures[row][column];

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
