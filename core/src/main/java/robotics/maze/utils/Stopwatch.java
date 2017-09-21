package robotics.maze.utils;

public class Stopwatch
{
    static private long start;
    static private long last;

    static private boolean outputEnabled = false;

    static public void enableOutput()
    {
        outputEnabled = true;
    }

    private static boolean isOutputEnabled()
    {
        return outputEnabled;
    }

    static public void start(String text)
    {
        print(text);
        start = System.currentTimeMillis();
        last = start;
    }

    private static void print(String text)
    {
        if (isOutputEnabled())
        {
            System.out.println(text);
        }
    }

    static public void report(String text)
    {
        long now = System.currentTimeMillis();
        print(text + ": " + (now - last));
        last = now;
    }

    static public void stop(String text)
    {
        long now = System.currentTimeMillis();
        print(text + ": " + (now - start));
        last = now;
    }
}
