package robotics.maze.integration;

import robotics.maze.dijkstra.MazeMapToVertexListAdapter;
import robotics.maze.dijkstra.Vertex;
import robotics.maze.robotics.image.JpegImageWrapper;
import robotics.maze.robotics.projection.MazeMap;
import robotics.maze.robotics.projection.MazeParser;
import robotics.maze.robotics.projection.MazeParserRunner;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.utility.ArrayIterate;
import twitter4j.Logger;
import twitter4j.MediaEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.io.File;

public class CustomStatusListener implements StatusListener
{
    private static final Logger LOGGER = Logger.getLogger(CustomStatusListener.class);

    public CustomStatusListener()
    {

    }

    @Override
    public void onStatus(Status status)
    {
        try
        {
            LOGGER.info("@" + status.getUser().getScreenName() + " - " + status.getText());

            MediaEntity mediaEntity = ArrayIterate.getFirst(status.getMediaEntities());

            if (mediaEntity != null)
            {
                LOGGER.info("Media Entity" + mediaEntity.getMediaURL());
                File file = FileUtils.downloadAndSaveMedia(mediaEntity);
                JpegImageWrapper imageWrapper = JpegImageWrapper.loadFile(file);

                MazeParser mazeParser = new MazeParser();

                MazeMap mazeMap = mazeParser.buildFromImage(imageWrapper, 19, 19);

                MazeParserRunner.printMazeMap(mazeMap);

                MutableList<Vertex> vertices = MazeMapToVertexListAdapter.adapt(mazeMap);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception while working with Twitter:", e);
        }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice)
    {
        // Do nothing
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses)
    {
        LOGGER.info("Got track limitation notice:" + numberOfLimitedStatuses);
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId)
    {
        // Do nothing
    }

    @Override
    public void onStallWarning(StallWarning warning)
    {
        LOGGER.info("Got stall warning:" + warning);
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onException(Exception ex)
    {
        LOGGER.error("Received exception", ex);
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
