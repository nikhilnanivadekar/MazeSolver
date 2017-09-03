package robotics.maze.integration;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.utility.ArrayIterate;
import robotics.maze.DifferentialMotor;
import robotics.maze.Ev3Traverser;
import robotics.maze.dijkstra.DijkstraAlgorithm;
import robotics.maze.dijkstra.MazeMapToVertexListAdapter;
import robotics.maze.dijkstra.Vertex;
import robotics.maze.image.JpegImageWrapper;
import robotics.maze.projection.MazeParser;
import robotics.maze.projection.MazeParserRunner;
import robotics.maze.projection.projection.MazeMap;
import robotics.maze.utils.FileUtils;
import twitter4j.Logger;
import twitter4j.MediaEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.io.File;
import java.util.List;
import java.util.Set;

public class CustomStatusListener implements StatusListener
{
    private static final Logger LOGGER = Logger.getLogger(CustomStatusListener.class);

    private final DifferentialMotor pilot;

    public CustomStatusListener(DifferentialMotor pilot)
    {
        this.pilot = pilot;
    }

    @Override
    public void onStatus(Status status)
    {
        try
        {
            if (!status.isRetweet())
            {
                LOGGER.info("@" + status.getUser().getScreenName() + " - " + status.getText());

                MediaEntity mediaEntity = ArrayIterate.getFirst(status.getMediaEntities());

                if (mediaEntity != null)
                {
                    LOGGER.info("Media Entity" + mediaEntity.getMediaURL());
                    File file = FileUtils.downloadAndSaveMedia(mediaEntity.getMediaURL(), mediaEntity.getId());
                    JpegImageWrapper imageWrapper = JpegImageWrapper.loadFile(file);

                    MazeParser mazeParser = new MazeParser();

                    MazeMap mazeMap = mazeParser.buildFromImage(imageWrapper, 19, 19);

//                    MazeParserRunner.printMazeMap(mazeMap);

                    MutableList<Vertex> vertices = MazeMapToVertexListAdapter.adapt(mazeMap);
                    Pair<MutableStack<Vertex>, Set<Vertex>> pathVisitedVerticesPair = DijkstraAlgorithm.findPath(vertices);
                    FileUtils.writeSolvedMaze(pathVisitedVerticesPair.getOne(), pathVisitedVerticesPair.getTwo(), mazeMap);
                    List<Vertex> flattenedPath = Ev3Traverser.getFlattenedPath(pathVisitedVerticesPair.getOne());
                    Ev3Traverser.moveAlongPath(this.pilot, flattenedPath);
                }
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
