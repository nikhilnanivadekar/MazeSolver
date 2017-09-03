package robotics.maze;

import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.factory.Lists;
import robotics.maze.dijkstra.Vertex;
import robotics.maze.enums.HeadingType;

import java.util.List;

public class Ev3Traverser
{
    private static final Vertex HOME = new Vertex(0, 0, null);

    /**
     * The co-ordinate axis are:
     * <pre>
     *          -X
     *     -Y __|__ +Y
     *          |
     *          +X
     * </pre>
     * <p>
     * Assume that the robot always faces -X direction and is located at (0,0).
     * Wait for 15 secs and then move back to (0,0) and face the -X direction.
     */
    public static void moveAlongPath(DifferentialMotor pilot, List<Vertex> path) throws InterruptedException
    {
        HeadingType currentHeading = Ev3Traverser.goToStart(pilot, path.get(0));
        Thread.sleep(5 * 1000);

        for (int i = 0; i < path.size() - 1; i++)
        {
            Vertex currentPosition = path.get(i);
            Vertex nextPosition = path.get(i + 1);

            HeadingType nextHeading = Ev3Traverser.rotateAndMove(pilot, currentHeading, currentPosition, nextPosition);

            currentHeading = nextHeading;
        }
        Vertex currentPosition = path.get(path.size() - 2);
        Vertex pathEnd = path.get(path.size() - 1);
        HeadingType nextHeading = Ev3Traverser.rotateAndMove(pilot, currentHeading, currentPosition, pathEnd);
        Thread.sleep(10 * 1000);
        // Go back home
        Ev3Traverser.homeRobot(pilot, pathEnd, nextHeading);
    }

    private static HeadingType rotateAndMove(DifferentialMotor pilot, HeadingType currentHeading, Vertex currentPosition, Vertex nextPosition)
    {
        int xDist = currentPosition.getX() - nextPosition.getX();
        int yDist = currentPosition.getY() - nextPosition.getY();
        HeadingType nextHeading = Ev3Traverser.getNextHeading(xDist, yDist, currentHeading);
        pilot.rotate(currentHeading.getDegrees(nextHeading));

        if (xDist != 0)
        {
            pilot.move(Math.abs(xDist));
        }
        if (yDist != 0)
        {
            pilot.move(Math.abs(yDist));
        }
        return nextHeading;
    }

    private static HeadingType getNextHeading(int xDist, int yDist, HeadingType currentHeading)
    {
        if (xDist < 0)
        {
            return HeadingType.NEGATIVE_X;
        }
        if (xDist > 0)
        {
            return HeadingType.POSITIVE_X;
        }
        if (yDist < 0)
        {
            return HeadingType.NEGATIVE_Y;
        }
        if (yDist > 0)
        {
            return HeadingType.POSITIVE_Y;
        }
        return currentHeading;
    }

    public static void homeRobot(DifferentialMotor pilot, Vertex currentLocation, HeadingType currentHeading)
    {
        // Need to break in 2 parts
        if (currentLocation.getX() != 0 && currentLocation.getY() != 0)
        {
            Vertex stop1 = new Vertex(0, currentLocation.getY(), null);
            currentHeading = Ev3Traverser.rotateAndMove(pilot, currentHeading, currentLocation, stop1);
            currentHeading = Ev3Traverser.rotateAndMove(pilot, currentHeading, stop1, HOME);
        }
        else
        {
            currentHeading = Ev3Traverser.rotateAndMove(pilot, currentHeading, currentLocation, HOME);
        }
        if (currentHeading != HeadingType.NEGATIVE_X)
        {
            pilot.rotate(currentHeading.getDegrees(HeadingType.NEGATIVE_X));
        }
    }

    public static HeadingType goToStart(DifferentialMotor pilot, Vertex startLocation)
    {
        // Need to break in 2 parts
        if (startLocation.getX() != 0 && startLocation.getY() != 0)
        {
            Vertex stop1 = new Vertex(startLocation.getX(), 0, null);
            HeadingType currentHeading = Ev3Traverser.rotateAndMove(pilot, HeadingType.NEGATIVE_X, HOME, stop1);
            return Ev3Traverser.rotateAndMove(pilot, currentHeading, stop1, startLocation);
        }
        return Ev3Traverser.rotateAndMove(pilot, HeadingType.NEGATIVE_X, HOME, startLocation);
    }

    public static List<Vertex> getFlattenedPath(MutableStack<Vertex> path)
    {
        if (path.isEmpty())
        {
            throw new IllegalStateException("Path is empty, nothing to flatten");
        }
        if (path.size() == 1)
        {
            throw new IllegalStateException("Need at least 2 points on the path!");
        }

        if (path.size() == 2)
        {
            return Lists.mutable.with(path.pop(), path.pop());
        }
        List<Vertex> flattenedPath = Lists.mutable.empty();
        flattenedPath.add(path.pop());
        while (path.size() > 2)
        {
            Vertex currentVertex = flattenedPath.get(flattenedPath.size() - 1);
            Vertex nextVertex = path.peek();
            Vertex nextNextVertex = path.peekAt(1);

            if ((currentVertex.getX() != nextVertex.getX()
                    && nextVertex.getX() != nextNextVertex.getX()
                    && currentVertex.getY() == nextVertex.getY()
                    && nextVertex.getY() == nextNextVertex.getY())
                    || (currentVertex.getX() == nextVertex.getX()
                    && nextVertex.getX() == nextNextVertex.getX()
                    && currentVertex.getY() != nextVertex.getY()
                    && nextVertex.getY() != nextNextVertex.getY()))
            {
                // Don't care about this point since it is on the same axis
                path.pop();
            }
            else
            {
                flattenedPath.add(path.pop());
            }
        }
        flattenedPath.add(path.pop());
        return flattenedPath;
    }
}
