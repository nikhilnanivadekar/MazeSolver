package projection;

import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.primitive.IntIntPairImpl;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

import java.util.Objects;
import java.util.Optional;

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

    public MazeFeature createAndSetFeature(int column, int row, FeatureType featureType)
    {
        this.mazeFeatures[row][column] = new MazeFeature(column, row, featureType);
        return this.mazeFeatures[row][column];
    }

    public MazeFeature findAnyNeighbor(int x, int y, final FeatureType featureType)
    {
        return this.findAnyMatchingNeighbor(x, y, other -> featureType == other);
    }

    public MazeFeature findAnyNeighborNot(int x, int y, FeatureType featureType)
    {
        return this.findAnyMatchingNeighbor(x, y, other -> featureType != other);
    }

    private MazeFeature findAnyMatchingNeighbor(int x, int y, Predicate<FeatureType> condition)
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

    static ListIterable<IntIntPair> neighbourhood = Lists.immutable.of(
            PrimitiveTuples.pair(-1, -1), PrimitiveTuples.pair(-1, 0), PrimitiveTuples.pair(-1, +1),
            PrimitiveTuples.pair( 0, -1), PrimitiveTuples.pair( 0, 0), PrimitiveTuples.pair( 0, +1),
            PrimitiveTuples.pair(+1, -1), PrimitiveTuples.pair(+1, 0), PrimitiveTuples.pair(+1, +1)
            );

    public FeatureType getCommonFeatureTypeAround(int row, int column, int radius)
    {
        Optional<FeatureType> popularFeature = neighbourhood
                .asLazy()
                .collect(each -> this.getFeatureAt(row + each.getOne()*radius, column + each.getTwo()*radius))
                .reject(Objects::isNull)
                .collect(MazeFeature::getType)
                .reject(each -> each == FeatureType.EMPTY)
                .toSortedBag()
                .getFirstOptional();

        return popularFeature.orElse(FeatureType.EMPTY);
    }

    private MazeFeature getMatchingFeatureOrNull(int x, int y, Predicate<FeatureType> condition)
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
