package robotics.maze.projection;

import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.ObjectLongPair;
import org.eclipse.collections.impl.block.factory.Comparators;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import robotics.maze.image.*;
import robotics.maze.projection.projection.CoordinatePoint;
import robotics.maze.utils.FileUtils;
import robotics.maze.enums.PointType;
import robotics.maze.projection.projection.MazeMap;
import robotics.maze.utils.Stopwatch;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;

import static robotics.maze.enums.PointType.EMPTY;

public class MazeParser
{
    public MazeMap buildFromImage(ImageWrapper imageWrapper, int targetWidth, int targetHeight)
    {
        Stopwatch.report("Starting parsing");

        int imageWidth = imageWrapper.getWidth();
        int imageHeight = imageWrapper.getHeight();

        ParsedMazeImage parsedMaze = new ParsedMazeImage(imageWidth, imageHeight);

        MutableList<ListIterable<MazeFeature>> cornerBoundaries = Lists.mutable.of();

        int[] rgb = new int[3];

        MutableList<MazeFeature> cornerPoints = Lists.mutable.of();

        for (int column = 0; column < imageWidth; column++)
        {
            for (int row = 0; row < imageHeight; row++)
            {
                imageWrapper.retrieveRgbAt(column, row, rgb);

                if (MarkerColorRange.CORNER_MARKER.checkRGB(rgb))
                {
                    cornerPoints.add(parsedMaze.createAndSetFeature(column, row, PointType.CORNER));
                }
                else if (MarkerColorRange.START_MARKER.checkRGB(rgb))
                {
                    parsedMaze.createAndSetFeature(column, row, PointType.START);
                }
                else if (MarkerColorRange.STOP_MARKER.checkRGB(rgb))
                {
                    parsedMaze.createAndSetFeature(column, row, PointType.FINISH);
                }
                else if (MarkerColorRange.WALL_MARKER.checkRGB(rgb))
                {
                    parsedMaze.createAndSetFeature(column, row, PointType.WALL);
                }
                else
                {
                    parsedMaze.createAndSetFeature(column, row, EMPTY).setShading(rgb);
                }
            }
        }

        cornerPoints.forEachWithIndex(
                (feature, index) -> {
                    if (feature.isNotTagged())
                    {
                        // fill the corner area with tags
                        cornerBoundaries.add(this.floodFill(parsedMaze, feature.getRow(), feature.getColumn(), index));
                    }
                }
        );

        Stopwatch.report("done initial parsing");

//        this.printParsedMaze(parsedMaze);
        BufferedImage bi = MazeImageCreator.createImageFromParsedMaze(parsedMaze);
//        FileUtils.saveImageToFile(bi, "parsed_maze.PNG");
//        Stopwatch.report("Parsed and written to file");

        Stopwatch.report("created baseline image");

        if (cornerBoundaries.size() < 4)
        {
            throw new RuntimeException("Detected corner markers for " + cornerBoundaries.size() + " corners, 4 expected");
        }

        /*cornerBoundaries.collect(this::findCenterAndDiameter)
                .sortThis(Comparators.byFunction(Pair::getTwo, Comparators.reverseNaturalOrder()))
                .forEach(each -> System.out.println(each.getOne() + " -- " + each.getTwo()));*/

        MutableList<CoordinatePoint> cornerCenters = cornerBoundaries
                .collect(this::findCenterAndDiameter)
                .sortThisByLong(pair -> -pair.getTwo())
                .take(4)
                .collect(ObjectLongPair::getOne);

        Stopwatch.report("Selected 4 candidate corners");

        cornerCenters.sortThisByDouble(CoordinatePoint::getRow);

        CoordinatePoint ulCenter;
        CoordinatePoint urCenter;
        CoordinatePoint llCenter;
        CoordinatePoint lrCenter;
        // first two - upper, second two - lower
        if (cornerCenters.get(0).getColumn() < cornerCenters.get(1).getColumn())
        {
            ulCenter = cornerCenters.get(0);
            urCenter = cornerCenters.get(1);
        }
        else
        {
            ulCenter = cornerCenters.get(1);
            urCenter = cornerCenters.get(0);
        }

        if (cornerCenters.get(2).getColumn() < cornerCenters.get(3).getColumn())
        {
            llCenter = cornerCenters.get(2);
            lrCenter = cornerCenters.get(3);
        }
        else
        {
            llCenter = cornerCenters.get(3);
            lrCenter = cornerCenters.get(2);
        }

//        System.out.println("UL: " + ulCenter);
//        System.out.println("UR: " + urCenter);
//        System.out.println("LL: " + llCenter);
//        System.out.println("LR: " + lrCenter);

        Stopwatch.report("identified corners");

        MazeMap result = new MazeMap(targetWidth, targetHeight);

        // points on the original image to inspect to translate into maze features
        CoordinatePoint[][] grid = new CoordinatePoint[targetHeight][targetWidth];

        // need to account for perspective distortion
        Line topEdge = new Line(ulCenter, urCenter);
        Line bottomEdge = new Line(llCenter, lrCenter);

        Line leftEdge = new Line(ulCenter, llCenter);
        Line rightEdge = new Line(urCenter, lrCenter);

        double verticalSlopeRatio = this.computeRatio(leftEdge.getM(), rightEdge.getM());
        double horizontalSlopeRatio = this.computeRatio(topEdge.getM(), bottomEdge.getM());

        boolean verticalAlmostParallel = verticalSlopeRatio > 0.99 && verticalSlopeRatio < 1.01;
        boolean horizontalAlmostParallel = horizontalSlopeRatio > 0.99 && horizontalSlopeRatio < 1.01;

//        System.out.println("   Top: " + topLength + ", " + topEdge + " Bottom: " + bottomLength + ", " + bottomEdge + ", t/b: " + horizontalSlopeRatio);
//        System.out.println("  Left: " + leftLength + ", " + leftEdge + " Right: " + rightLength + ", " + rightEdge + ", l/r: " + verticalSlopeRatio);

        CoordinatePoint[] topPoints = new CoordinatePoint[targetWidth];
        CoordinatePoint[] bottomPoints = new CoordinatePoint[targetWidth];

        CoordinatePoint[] leftPoints = new CoordinatePoint[targetHeight];
        CoordinatePoint[] rightPoints = new CoordinatePoint[targetHeight];

        topPoints[0] = leftPoints[0] = new CoordinatePoint(ulCenter, 0.5);

        topPoints[targetWidth - 1] = rightPoints[0] = new CoordinatePoint(urCenter, 0.5);

        bottomPoints[0] = leftPoints[targetHeight - 1] = new CoordinatePoint(llCenter, 0.5);

        bottomPoints[targetWidth - 1] = rightPoints[targetHeight - 1] = new CoordinatePoint(lrCenter, 0.5);

        MutableList<String> stats = Lists.mutable.of();

        stats.add("=== ANALYSIS: MAZE ===");
        stats.add(String.format("VSR %.4f" , verticalSlopeRatio));
        stats.add(String.format("HSR %.4f" , horizontalSlopeRatio));
        stats.add("UL " + ulCenter.toFormattedString());
        stats.add("UR " + urCenter.toFormattedString());
        stats.add("LL " + llCenter.toFormattedString());
        stats.add("LR " + lrCenter.toFormattedString());

        if (verticalAlmostParallel && horizontalAlmostParallel)
        {
            stats.add("VERTVP INF");
            stats.add("HORZVP INF");
            // don't bother with perspective correction at all
            CoordinatePoint leftDeltaHeight = this.computeIncrement(ulCenter, llCenter, targetHeight - 1);
            CoordinatePoint rightDeltaHeight = this.computeIncrement(urCenter, lrCenter, targetHeight - 1);

            CoordinatePoint topDeltaWidth = this.computeIncrement(ulCenter, urCenter, targetWidth - 1);
            CoordinatePoint bottomDeltaWidth = this.computeIncrement(llCenter, lrCenter, targetWidth - 1);

            CoordinatePoint curRowLeft = new CoordinatePoint(ulCenter, 0.5);
            CoordinatePoint curRowRight = new CoordinatePoint(urCenter, 0.5);

            for (int curRow = 1; curRow < targetHeight - 1; curRow++)
            {
                curRowLeft = curRowLeft.incrementBy(leftDeltaHeight);
                curRowRight = curRowRight.incrementBy(rightDeltaHeight);

                leftPoints[curRow] = curRowLeft;
                rightPoints[curRow] = curRowRight;
            }

            CoordinatePoint curColTop = new CoordinatePoint(ulCenter, 0.5);
            CoordinatePoint curColBottom = new CoordinatePoint(llCenter, 0.5);

            for (int curCol = 1; curCol < targetWidth - 1; curCol++)
            {
                curColTop = curColTop.incrementBy(topDeltaWidth);
                curColBottom = curColBottom.incrementBy(bottomDeltaWidth);

                topPoints[curCol] = curColTop;
                bottomPoints[curCol] = curColBottom;
            }
        }
        else if (horizontalAlmostParallel)
        {
            stats.add("VERTVP CALC");
            stats.add("HORZVP INF");

            // no perspective correction for column (width) segments
            CoordinatePoint topDeltaWidth = this.computeIncrement(ulCenter, urCenter, targetWidth - 1);
            CoordinatePoint bottomDeltaWidth = this.computeIncrement(llCenter, lrCenter, targetWidth - 1);

            CoordinatePoint curColTop = new CoordinatePoint(ulCenter, 0.5);
            CoordinatePoint curColBottom = new CoordinatePoint(llCenter, 0.5);

            for (int curCol = 1; curCol < targetWidth - 1; curCol++)
            {
                curColTop = curColTop.incrementBy(topDeltaWidth);
                curColBottom = curColBottom.incrementBy(bottomDeltaWidth);

                topPoints[curCol] = curColTop;
                bottomPoints[curCol] = curColBottom;

                CoordinatePoint ulLrDiagonalIntersection = CoordinatePoint.intersection(ulCenter, lrCenter, curColTop, curColBottom);

                Line topPar = topEdge.parallelThroughPoint(ulLrDiagonalIntersection);
                Line bottomPar = bottomEdge.parallelThroughPoint(ulLrDiagonalIntersection);

                Line avgPar = new Line(ulLrDiagonalIntersection, (topPar.getM() + bottomPar.getM()) * 0.5);

                // todo: this assumes the same number of rows and columns, may not be true
                leftPoints[curCol] = avgPar.intersectWith(leftEdge);
                rightPoints[curCol] = avgPar.intersectWith(rightEdge);
            }
        }
        else if (verticalAlmostParallel)
        {
            stats.add("VERTVP INF");
            stats.add("HORZVP CALS");

            // no perspective correction for rows
            CoordinatePoint leftDeltaHeight = this.computeIncrement(ulCenter, llCenter, targetHeight - 1);
            CoordinatePoint rightDeltaHeight = this.computeIncrement(urCenter, lrCenter, targetHeight - 1);

            CoordinatePoint curRowLeft = new CoordinatePoint(ulCenter, 0.5);
            CoordinatePoint curRowRight = new CoordinatePoint(llCenter, 0.5);

            for (int curRow = 1; curRow < targetHeight - 1; curRow++)
            {
                curRowLeft = curRowLeft.incrementBy(leftDeltaHeight);
                curRowRight = curRowRight.incrementBy(rightDeltaHeight);

                leftPoints[curRow] = curRowLeft;
                rightPoints[curRow] = curRowRight;

                CoordinatePoint ulLrDiagonalIntersection = CoordinatePoint.intersection(ulCenter, lrCenter, curRowLeft, curRowRight);

                Line leftPar = leftEdge.parallelThroughPoint(ulLrDiagonalIntersection);
                Line rightPar = rightEdge.parallelThroughPoint(ulLrDiagonalIntersection);

                Line avgPar = new Line(ulLrDiagonalIntersection, (leftPar.getM() + rightPar.getM()) * 0.5);

                // todo: this assumes the same number of rows and columns, may not be true
                topPoints[curRow] = avgPar.intersectWith(topEdge);
                bottomPoints[curRow] = avgPar.intersectWith(bottomEdge);
            }
        }
        else // 2-point perspective
        {
            CoordinatePoint verticalVp = CoordinatePoint.intersection(ulCenter, llCenter, urCenter, lrCenter);
            CoordinatePoint horizontalVp = CoordinatePoint.intersection(ulCenter, urCenter, llCenter, lrCenter);

            stats.add("VERTVP " + verticalVp.toFormattedString());
            stats.add("HORZVP " + horizontalVp.toFormattedString());

            Line horizonLine = new Line(horizontalVp, verticalVp);

            Line parHorizonLine = horizonLine.parallelThroughPoint(lrCenter);

            Line vertVpLeft = new Line(verticalVp, ulCenter);
            Line vertVpRight = new Line(verticalVp, urCenter);

            CoordinatePoint widthStart = parHorizonLine.intersectWith(vertVpLeft);
            CoordinatePoint widthEnd = parHorizonLine.intersectWith(vertVpRight);

            CoordinatePoint deltaWidth = this.computeIncrement(widthStart, widthEnd, targetWidth - 1);

            CoordinatePoint curWidthPoint = widthStart;
            for (int curCol = 1; curCol < targetWidth - 1; curCol++)
            {
                curWidthPoint = curWidthPoint.incrementBy(deltaWidth);

                topPoints[curCol] = CoordinatePoint.intersection(verticalVp, curWidthPoint, ulCenter, urCenter);
                bottomPoints[curCol] = CoordinatePoint.intersection(verticalVp, curWidthPoint, llCenter, lrCenter);
            }

            Line horVpTop = new Line(horizontalVp, ulCenter);
            Line horVpBottom = new Line(horizontalVp, llCenter);

            CoordinatePoint heightStart = parHorizonLine.intersectWith(horVpTop);
            CoordinatePoint heightEnd = parHorizonLine.intersectWith(horVpBottom);

            CoordinatePoint deltaHeight = this.computeIncrement(heightStart, heightEnd, targetHeight - 1);

            CoordinatePoint curHeightPoint = heightStart;
            for (int curRow = 1; curRow < targetHeight - 1; curRow++)
            {
                curHeightPoint = curHeightPoint.incrementBy(deltaHeight);

                leftPoints[curRow] = CoordinatePoint.intersection(horizontalVp, curHeightPoint, ulCenter, llCenter);
                rightPoints[curRow] = CoordinatePoint.intersection(horizontalVp, curHeightPoint, urCenter, lrCenter);
            }
        }

        for (int curRow = 0; curRow < targetHeight; curRow++)
        {
            for (int curCol = 0; curCol < targetWidth; curCol++)
            {
                // find intersection of curColTop--curColBottom and curRowLeft--curRowRight
                CoordinatePoint intersection =
                        CoordinatePoint.intersection(bottomPoints[curCol], topPoints[curCol],
                                rightPoints[curRow], leftPoints[curRow]);
                grid[curRow][curCol] = intersection;
            }
        }

        Stopwatch.report("grid computed");

        stats.add("GRD " + targetHeight + ":" + targetWidth);
        MazeImageCreator.overlayGridOnImage(bi, grid);
        MazeImageCreator.addTextToImage(bi, stats);

        Stopwatch.report("grid drawn on image");

//        FileUtils.saveImageToFile(bi, "parsed_lined_maze.PNG");
//        Stopwatch.report("grid written to file");

        // converting to double for stepping and round on each step
        // to avoid accumulation of rounding errors
        // running on the left-right and top-bottom grid endpoints and checking intersections

        for (int curRow = 0; curRow < targetHeight; curRow++)
        {
            for (int curCol = 0; curCol < targetWidth; curCol++)
            {
                // determine the center of the area we want to check
                // and the range of the area to check to determine the dominant point type
                int rowToCheck = (int) grid[curRow][curCol].getRow();
                int adjacentRow = (curRow == 0) ? 1 : curRow - 1;
                int rowDelta = Math.max((int) Math.abs(grid[adjacentRow][curCol].getRow() - rowToCheck)/8, 1);
                int rowFrom = Math.max(rowToCheck - rowDelta, 0);
                int rowTo = Math.min(rowToCheck + rowDelta, imageHeight);

                int colToCheck = (int) grid[curRow][curCol].getColumn();
                int adjacentCol = (curCol == 0) ? 1 : curCol - 1;
                int colDelta = Math.max((int) Math.abs(grid[curRow][adjacentCol].getColumn() - colToCheck)/8, 1);
                int colFrom = Math.max(colToCheck - colDelta, 0);
                int colTo = Math.min(colToCheck + colDelta, imageWidth);

                switch (parsedMaze.getPredominantPointTypeInTheArea(rowFrom, rowTo, colFrom, colTo))
//                switch (parsedMaze.getFeatureAt(rowToCheck, colToCheck).getType())
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
                        result.setStart(curRow, curCol);
                        break;
                    case FINISH:
                        result.setStop(curRow, curCol);
                        break;
                }
            }
        }

        result.setMazeImage(bi);
        result.setOriginalImageCoordinates(grid);

        return result;
    }

    private double computeRatio(double a, double b)
    {
        double smallNumber = 0.000001;
        if (Math.abs(a) > smallNumber)
        {
            return Math.abs(b / a);
        }
        else
        {
            return (b <= smallNumber) ? 1.0 : 0.0;
        }
    }

    private ListIterable<MazeFeature> floodFill(ParsedMazeImage parsedMaze, int row, int column, int tag)
    {
        MutableSet<MazeFeature> boundary = Sets.mutable.of();
        Queue<MazeFeature> queue = new LinkedList<>();

        MazeFeature feature = parsedMaze.getFeatureAt(row, column);
        if (feature.isTagged())
        {
            return boundary.toList();
        }
        queue.add(feature);

        while (queue.size() > 0)
        {
            feature = queue.poll();
            MazeFeature west = feature;
            MazeFeature east = feature;

            for (MazeFeature next = west;
                 next != null && next.getType() == PointType.CORNER && next.isNotTagged();
                 next = parsedMaze.getFeatureAt(west.getRow(), west.getColumn() - 1)) // AND CORNER
            {
                west = next;
            }

            if (parsedMaze.findAnyNeighborNot(west.getColumn(), west.getRow(), PointType.CORNER) != null)
            {
                boundary.add(west);
            }

            for (MazeFeature next = east;
                 next != null && next.getType() == PointType.CORNER && next.isNotTagged();
                 next = parsedMaze.getFeatureAt(east.getRow(), east.getColumn() + 1)) // AND CORNER
            {
                east = next;
            }

            if (parsedMaze.findAnyNeighborNot(east.getColumn(), east.getRow(), PointType.CORNER) != null)
            {
                boundary.add(east);
            }

            int currentRow = feature.getRow();
            for (int col = west.getColumn(); col <= east.getColumn(); col++)
            {
                parsedMaze.getFeatureAt(currentRow, col).setTag(tag);

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

        return boundary.toList();
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
