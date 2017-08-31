package nvv.robotics.dijkstra;

import org.eclipse.collections.impl.factory.Maps;

import java.awt.*;
import java.util.Map;

public final class Constants
{
    //Array size
    public static final int X_AXIS_SIZE = 2;
    public static final int Y_AXIS_SIZE = 2;

    //Array values
    public static final int SPACE = 0;
    public static final int OBSTACLE = 1;
    public static final int START = 2;
    public static final int END = 3;
    public static final int PATH = 4;

    //Movement Cost
    public static final int X_AXIS_COST = 1;
    public static final int Y_AXIS_COST = 1;
    public static final int DIAGONAL_COST = 1;

    public static final boolean IS_DIAGONAL_MOVEMENT_ALLOWED = false;

    //ImageProcessing
    public static final int IMAGE_SCALE = 50;
    public static final Map<Integer, Color> COLOR_MAP = Maps.mutable.empty();

    static
    {
        COLOR_MAP.put(SPACE, Color.WHITE);
        COLOR_MAP.put(OBSTACLE, Color.BLACK);
        COLOR_MAP.put(START, Color.GREEN);
        COLOR_MAP.put(END, Color.BLUE);
        COLOR_MAP.put(PATH, new Color(165, 42, 42));
    }
}
