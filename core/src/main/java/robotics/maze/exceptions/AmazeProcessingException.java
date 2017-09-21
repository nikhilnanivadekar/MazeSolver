package robotics.maze.exceptions;

public class AmazeProcessingException
extends RuntimeException
{
    public AmazeProcessingException(String message)
    {
        super(message);
    }

    public AmazeProcessingException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
