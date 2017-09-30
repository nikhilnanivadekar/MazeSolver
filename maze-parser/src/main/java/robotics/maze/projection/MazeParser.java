package robotics.maze.projection;

import org.eclipse.collections.api.block.function.primitive.DoubleFunction;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.api.tuple.primitive.ObjectLongPair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import robotics.maze.enums.PointType;
import robotics.maze.exceptions.AmazeProcessingException;
import robotics.maze.image.ImageWrapper;
import robotics.maze.image.Line;
import robotics.maze.image.MarkerColorRange;
import robotics.maze.image.MazeImageCreator;
import robotics.maze.projection.projection.CoordinatePoint;
import robotics.maze.projection.projection.MazeMap;
import robotics.maze.utils.Stopwatch;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

import static robotics.maze.enums.PointType.*;

public class MazeParser
{
    public MazeMap buildFromImage(ImageWrapper imageWrapper, int targetWidth, int targetHeight)
    {
        Stopwatch.report("Starting parsing");

        int imageWidth = imageWrapper.getWidth();
        int imageHeight = imageWrapper.getHeight();

        ParsedMazeImage parsedMaze = new ParsedMazeImage(imageWidth, imageHeight);

        MutableList<ListIterable<MazeFeature>> cornerBoundaries = Lists.mutable.of();

        BufferedImage bi = MazeImageCreator.createCanvas(parsedMaze);

        int[] rgb = new int[3];

        MutableList<MazeFeature> cornerPoints = Lists.mutable.of();

        for (int column = 0; column < imageWidth; column++)
        {
            for (int row = 0; row < imageHeight; row++)
            {
                imageWrapper.retrieveRgbAt(column, row, rgb);

                PointType pointType;
                if (MarkerColorRange.WALL_MARKER.checkRGB(rgb))
                {
                    pointType = WALL;
                }
                else if (MarkerColorRange.CORNER_MARKER.checkRGB(rgb))
                {
                    pointType = CORNER;
                }
                else if (MarkerColorRange.START_MARKER.checkRGB(rgb))
                {
                    pointType = START;
                }
                else if (MarkerColorRange.STOP_MARKER.checkRGB(rgb))
                {
                    pointType = FINISH;
                }
                else
                {
                    pointType = EMPTY;
                }

                MazeFeature mazeFeature = parsedMaze.createAndSetFeature(column, row, pointType);
                if (pointType == CORNER)
                {
                    cornerPoints.add(mazeFeature);
                }

                MazeImageCreator.setPixel(bi, column, row, pointType, rgb);
            }
        }

        Stopwatch.report("Done initial parsing");

        cornerPoints.forEachWithIndex(
                (feature, index) -> {
                    if (feature.isNotTagged())
                    {
                        // fill the corner area with tags
                        cornerBoundaries.add(this.floodFill(parsedMaze, feature.getRow(), feature.getColumn(), index));
                    }
                }
        );

        Stopwatch.report("Filled corner areas");

//        FileUtils.saveImageToFile(bi, "parsed_maze.PNG");
//        Stopwatch.report("Parsed and written to file");

        MutableList<CoordinatePoint> cornerCenters = cornerBoundaries
                .select(boundary -> boundary.size() > 2)
                .collect(this::findCenterAndDiameter)
                .sortThisByLong(pair -> -pair.getTwo())
                .take(4)
                .collect(ObjectLongPair::getOne);

        if (cornerCenters.size() < 4)
        {
            throw new AmazeProcessingException("Detected corner markers for " + cornerCenters.size() + " corners, 4 expected");
        }

        Stopwatch.report("Selected 4 candidate corners");

        cornerCenters.sortThisByDouble(CoordinatePoint::getRow);

        CoordinatePoint ulCorner;
        CoordinatePoint urCorner;
        CoordinatePoint llCorner;
        CoordinatePoint lrCorner;
        // assumption: first two - upper, second two - lower
        if (cornerCenters.get(0).getColumn() < cornerCenters.get(1).getColumn())
        {
            ulCorner = cornerCenters.get(0);
            urCorner = cornerCenters.get(1);
        }
        else
        {
            ulCorner = cornerCenters.get(1);
            urCorner = cornerCenters.get(0);
        }

        if (cornerCenters.get(2).getColumn() < cornerCenters.get(3).getColumn())
        {
            llCorner = cornerCenters.get(2);
            lrCorner = cornerCenters.get(3);
        }
        else
        {
            llCorner = cornerCenters.get(3);
            lrCorner = cornerCenters.get(2);
        }

        Stopwatch.report("Identified corners");

        MazeMap result = new MazeMap(targetWidth, targetHeight);

        // points on the original image to inspect to translate into maze features
        CoordinatePoint[][] grid = new CoordinatePoint[targetHeight][targetWidth];

        // need to account for perspective distortion
        Line topEdge = new Line(ulCorner, urCorner);
        Line bottomEdge = new Line(llCorner, lrCorner);

        Line leftEdge = new Line(ulCorner, llCorner);
        Line rightEdge = new Line(urCorner, lrCorner);

        boolean verticalAlmostParallel = this.almostTheSame(leftEdge.getM(), rightEdge.getM());
        boolean horizontalAlmostParallel = this.almostTheSame(topEdge.getM(), bottomEdge.getM());

        CoordinatePoint[] topPoints = new CoordinatePoint[targetWidth];
        CoordinatePoint[] bottomPoints = new CoordinatePoint[targetWidth];

        CoordinatePoint[] leftPoints = new CoordinatePoint[targetHeight];
        CoordinatePoint[] rightPoints = new CoordinatePoint[targetHeight];

        topPoints[0] = leftPoints[0] = new CoordinatePoint(ulCorner, 0.5);

        topPoints[targetWidth - 1] = rightPoints[0] = new CoordinatePoint(urCorner, 0.5);

        bottomPoints[0] = leftPoints[targetHeight - 1] = new CoordinatePoint(llCorner, 0.5);

        bottomPoints[targetWidth - 1] = rightPoints[targetHeight - 1] = new CoordinatePoint(lrCorner, 0.5);

        MutableList<String> stats = Lists.mutable.of();

        stats.add("=== ANALYSIS: MAZE ===");
        stats.add("LE " + leftEdge.toFormattedString());
        stats.add("RE " + rightEdge.toFormattedString());
        stats.add("TE " + topEdge.toFormattedString());
        stats.add("BE " + bottomEdge.toFormattedString());
        stats.add("UL " + ulCorner.toFormattedString());
        stats.add("UR " + urCorner.toFormattedString());
        stats.add("LL " + llCorner.toFormattedString());
        stats.add("LR " + lrCorner.toFormattedString());

        if (verticalAlmostParallel)
        {
            stats.add("VRT VP INF");

            // no perspective correction for row spacing
            this.computeGridPointsWithoutPerspectiveCorrection(leftPoints, rightPoints,
                    ulCorner, llCorner, urCorner, lrCorner,
                    targetHeight);
        }

        if (horizontalAlmostParallel)
        {
            stats.add("HRZ VP INF");

            // no perspective correction for column (width) spacing
            this.computeGridPointsWithoutPerspectiveCorrection(topPoints, bottomPoints,
                    ulCorner, urCorner, llCorner, lrCorner,
                    targetWidth);
        }

        if (horizontalAlmostParallel && !verticalAlmostParallel)
        {
            stats.add("VRT VP CALC");

            this.computeGridPointWithSingleVanishingPointCorrection(leftPoints, rightPoints,
                    topPoints, bottomPoints,
                    leftEdge, rightEdge, topEdge, bottomEdge,
                    ulCorner, lrCorner,
                    targetWidth);
        }
        else if (verticalAlmostParallel && !horizontalAlmostParallel)
        {
            stats.add("HRZ VP CALC");

            this.computeGridPointWithSingleVanishingPointCorrection(topPoints, bottomPoints,
                    leftPoints, rightPoints,
                    topEdge, bottomEdge, leftEdge, rightEdge,
                    ulCorner, lrCorner,
                    targetHeight);
        }
        else // 2-point perspective
        {
            CoordinatePoint verticalVp = CoordinatePoint.segmentIntersection(ulCorner, llCorner, urCorner, lrCorner);
            CoordinatePoint horizontalVp = CoordinatePoint.segmentIntersection(ulCorner, urCorner, llCorner, lrCorner);

            stats.add("VRT VP " + verticalVp.toFormattedString());
            stats.add("HRZ VP " + horizontalVp.toFormattedString());

            Line horizonLine = new Line(horizontalVp, verticalVp);

            Line parHorizonLine = horizonLine.parallelThroughPoint(lrCorner);

            this.computeGridPointsWithVanishingPointCorrection(topPoints, bottomPoints,
                    verticalVp, parHorizonLine,
                    ulCorner, urCorner, llCorner, lrCorner,
                    targetWidth);

            this.computeGridPointsWithVanishingPointCorrection(leftPoints, rightPoints,
                    horizontalVp, parHorizonLine,
                    ulCorner, llCorner, urCorner, lrCorner,
                    targetHeight);
        }

        for (int curRow = 0; curRow < targetHeight; curRow++)
        {
            for (int curCol = 0; curCol < targetWidth; curCol++)
            {
                // find segmentIntersection of curColTop--curColBottom and curRowLeft--curRowRight
                CoordinatePoint intersection =
                        CoordinatePoint.segmentIntersection(bottomPoints[curCol], topPoints[curCol],
                                rightPoints[curRow], leftPoints[curRow]);
                grid[curRow][curCol] = intersection;
            }
        }

        Stopwatch.report("Grid computed");

        stats.add("DIM " + targetHeight + ":" + targetWidth);
        MazeImageCreator.overlayGridOnImage(bi, grid);
        MazeImageCreator.addTextToImage(bi, stats);

        Stopwatch.report("grid drawn on image");

//        FileUtils.saveImageToFile(bi, "parsed_lined_maze.PNG");
//        Stopwatch.report("grid written to file");

        boolean foundStart = false;
        boolean foundFinish = false;

        // converting to double for stepping and round on each step to avoid accumulation of rounding errors
        // running on the left-right and top-bottom grid endpoints and checking intersections
        for (int curRow = 0; curRow < targetHeight; curRow++)
        {
            for (int curCol = 0; curCol < targetWidth; curCol++)
            {
                // determine the center of the area we want to check
                // and the range of the area to check to determine the dominant point type

                IntIntPair rowRange = this.findNeighborhoodBoundary(grid, curRow, curCol, curRow-1, curCol, imageHeight, CoordinatePoint::getRow);
                IntIntPair colRange = this.findNeighborhoodBoundary(grid, curRow, curCol, curRow, curCol-1, imageWidth, CoordinatePoint::getColumn);

                switch (parsedMaze.getPredominantPointTypeInTheArea(rowRange.getOne(), rowRange.getTwo(), colRange.getOne(), colRange.getTwo()))
                {
                    case EMPTY:
                        result.setEmpty(curRow, curCol);
                        break;
                    case CORNER:
                        result.setWall(curRow, curCol);
                        break;
                    case WALL:
                        result.setWall(curRow, curCol);
                        break;
                    case START:
                        foundStart = true;
                        result.setStart(curRow, curCol);
                        break;
                    case FINISH:
                        foundFinish = true;
                        result.setStop(curRow, curCol);
                        break;
                }
            }
        }

        result.setMazeImage(bi);
        result.setOriginalImageCoordinates(grid);

        if (!(foundStart && foundFinish))
        {
            throw new AmazeProcessingException(
                    (foundStart ? "" : "The start marker is missing. ") +
                            (foundFinish ? "" : "The finish marker is missing."));
        }

        return result;
    }

    private IntIntPair findNeighborhoodBoundary(
            CoordinatePoint[][] grid, int curRow, int curCol, int neighborRow, int neighborCol, int maxValue,
            DoubleFunction<CoordinatePoint> getCoordinate)
    {
        int centerCoord = (int) getCoordinate.doubleValueOf(grid[curRow][curCol]);
        if (neighborRow < 0) neighborRow = 1;
        if (neighborCol < 0) neighborCol = 1;

        int coordDelta = Math.max((int) Math.abs(getCoordinate.doubleValueOf(grid[neighborRow][neighborCol]) - centerCoord) / 8, 1);

        return PrimitiveTuples.pair(Math.max(centerCoord - coordDelta, 0), Math.min(centerCoord + coordDelta, maxValue));
    }

    private void computeGridPointsWithVanishingPointCorrection(
            CoordinatePoint[] side1Points, CoordinatePoint[] side2Points,
            CoordinatePoint vanishingPoint, Line parHorizonLine,
            CoordinatePoint side1Start, CoordinatePoint side1End, CoordinatePoint side2Start, CoordinatePoint side2End,
            int numberOfPoints)
    {
        Line vertVpLeft = new Line(vanishingPoint, side1Start);
        Line vertVpRight = new Line(vanishingPoint, side1End);

        CoordinatePoint start = parHorizonLine.intersectWith(vertVpLeft);
        CoordinatePoint end = parHorizonLine.intersectWith(vertVpRight);

        CoordinatePoint delta = this.computeIncrement(start, end, numberOfPoints - 1);

        CoordinatePoint currentPoint = start;
        for (int curCol = 1; curCol < numberOfPoints - 1; curCol++)
        {
            currentPoint = currentPoint.incrementBy(delta);

            side1Points[curCol] = CoordinatePoint.segmentIntersection(vanishingPoint, currentPoint, side1Start, side1End);
            side2Points[curCol] = CoordinatePoint.segmentIntersection(vanishingPoint, currentPoint, side2Start, side2End);
        }
    }

    private void computeGridPointWithSingleVanishingPointCorrection(
            CoordinatePoint[] side1Points, CoordinatePoint[] side2Points,
            CoordinatePoint[] parallelPoints1, CoordinatePoint[] parallelPoints2,
            Line side1Edge, Line side2Edge, Line parallelEdge1, Line parallelEdge2,
            CoordinatePoint diagonalStart, CoordinatePoint diagonalEnd,
            int numberOfPoints)
    {
        for (int curCol = 1; curCol < numberOfPoints - 1; curCol++)
        {
            CoordinatePoint curColTop = parallelPoints1[curCol];
            CoordinatePoint curColBottom = parallelPoints2[curCol];

            CoordinatePoint ulLrDiagonalIntersection = CoordinatePoint.segmentIntersection(diagonalStart, diagonalEnd, curColTop, curColBottom);

            Line topPar = parallelEdge1.parallelThroughPoint(ulLrDiagonalIntersection);
            Line bottomPar = parallelEdge2.parallelThroughPoint(ulLrDiagonalIntersection);

            Line avgPar = new Line(ulLrDiagonalIntersection, (topPar.getM() + bottomPar.getM()) * 0.5);

            // todo: this assumes the same number of rows and columns, may not be true
            side1Points[curCol] = avgPar.intersectWith(side1Edge);
            side2Points[curCol] = avgPar.intersectWith(side2Edge);
        }
    }

    private void computeGridPointsWithoutPerspectiveCorrection(
            CoordinatePoint[] side1Points, CoordinatePoint[] side2Points,
            CoordinatePoint side1StartPoint, CoordinatePoint side1EndPoint,
            CoordinatePoint side2StartPoint, CoordinatePoint side2EndPoint,
            int numberOfPoints)
    {
        CoordinatePoint side1Delta = this.computeIncrement(side1StartPoint, side1EndPoint, numberOfPoints - 1);
        CoordinatePoint side2Delta = this.computeIncrement(side2StartPoint, side2EndPoint, numberOfPoints - 1);

        CoordinatePoint curRowLeft = new CoordinatePoint(side1StartPoint, 0.5);
        CoordinatePoint curRowRight = new CoordinatePoint(side2StartPoint, 0.5);

        for (int curRow = 1; curRow < numberOfPoints - 1; curRow++)
        {
            curRowLeft = curRowLeft.incrementBy(side1Delta);
            curRowRight = curRowRight.incrementBy(side2Delta);

            side1Points[curRow] = curRowLeft;
            side2Points[curRow] = curRowRight;
        }
    }

    private boolean almostTheSame(double a, double b)
    {
        double smallNumber = 0.000001;

        double r;

        if (Math.abs(a) > smallNumber)
        {
            r = Math.abs(b / a);
        }
        else
        {
            r = (b > smallNumber) ? 0.0 : 1.0;
        }

        return r > 0.995 && r < 1.005;
    }

    private ListIterable<MazeFeature> floodFill(ParsedMazeImage parsedMaze, int row, int column, int tag)
    {
        MutableList<MazeFeature> boundary = Lists.mutable.of();
        Queue<MazeFeature> queue = new LinkedList<>();

        MazeFeature feature = parsedMaze.getFeatureAt(row, column);
        if (feature.isTagged())
        {
            return Lists.immutable.empty();
        }

        queue.add(feature);

        while (queue.size() > 0)
        {
            feature = queue.poll();

            if (feature.getTag() == tag)
            {
                continue;
            }

            MazeFeature west = feature;
            MazeFeature east = feature;

            int currentRow = feature.getRow();

            for (MazeFeature next = west;
                 next != null && next.getType() == PointType.CORNER && next.isNotTagged();
                 next = parsedMaze.getFeatureAt(currentRow, west.getColumn() - 1))
            {
                west = next;
            }

            for (MazeFeature next = east;
                 next != null && next.getType() == PointType.CORNER && next.isNotTagged();
                 next = parsedMaze.getFeatureAt(currentRow, east.getColumn() + 1))
            {
                east = next;
            }

            for (int col = west.getColumn(); col <= east.getColumn(); col++)
            {
                MazeFeature featureToTag = parsedMaze.getFeatureAt(currentRow, col);

                featureToTag.setTag(tag);

                if (parsedMaze.findAnyNeighborNot(col, currentRow, PointType.CORNER) != null)
                {
                    boundary.add(featureToTag);
                }

                MazeFeature featureNorth = parsedMaze.getFeatureAt(currentRow - 1, col);
                if (featureNorth != null && featureNorth.getType() == PointType.CORNER && featureNorth.isNotTagged())
                {
                    queue.add(featureNorth);
                }

                MazeFeature featureSouth = parsedMaze.getFeatureAt(currentRow + 1, col);
                if (featureSouth != null && featureSouth.getType() == PointType.CORNER && featureSouth.isNotTagged())
                {
                    queue.add(featureSouth);
                }
            }
        }

        return boundary;
    }

    private CoordinatePoint computeIncrement(CoordinatePoint start, CoordinatePoint end, int steps)
    {
        double deltaRows = (end.getRow() - start.getRow()) / steps;
        double deltaColumns = (end.getColumn() - start.getColumn()) / steps;
        return new CoordinatePoint(deltaRows, deltaColumns);
    }

    private ObjectLongPair<CoordinatePoint> findCenterAndDiameter(ListIterable<MazeFeature> cornerBoundary)
    {
        MazeFeature f1Max = null, f2Max = null;

        long maxDistance = -1;

        for (int i = 0; i < cornerBoundary.size() - 1; i++)
        {
            MazeFeature f1 = cornerBoundary.get(i);

            for (int j = i + 1; j < cornerBoundary.size(); j++)
            {
                MazeFeature f2 = cornerBoundary.get(j);

                long dX = f1.getColumn() - f2.getColumn();
                long dY = f1.getRow() - f2.getRow();
                long distance = dX * dX + dY * dY;

                if (distance > maxDistance)
                {
                    maxDistance = distance;
                    f1Max = f1;
                    f2Max = f2;
                }
            }
        }

        // degenerate boundary - one point
        if (cornerBoundary.size() == 1)
        {
            f1Max = f2Max = cornerBoundary.get(0);
        }

        CoordinatePoint almostCenter = new CoordinatePoint(
                Math.max(f1Max.getRow(), f2Max.getRow()) - Math.abs((f1Max.getRow() - f2Max.getRow()) / 2),
                Math.max(f1Max.getColumn(), f2Max.getColumn()) - Math.abs((f1Max.getColumn() - f2Max.getColumn()) / 2)
        );

        return PrimitiveTuples.pair(almostCenter, maxDistance);
    }
}
