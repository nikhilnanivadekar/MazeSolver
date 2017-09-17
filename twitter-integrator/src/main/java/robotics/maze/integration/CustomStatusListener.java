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
import robotics.maze.projection.projection.MazeMap;
import robotics.maze.utils.FileUtils;
import twitter4j.Logger;
import twitter4j.MediaEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

import java.io.File;
import java.util.List;
import java.util.Set;

public class CustomStatusListener implements StatusListener
{
    private static final Logger LOGGER = Logger.getLogger(CustomStatusListener.class);

    private final DifferentialMotor pilot;
    private final Twitter twitter;
    private boolean isProcessing;

    public CustomStatusListener(DifferentialMotor pilot, Twitter twitter)
    {
        this.pilot = pilot;
        this.twitter = twitter;
    }

    @Override
    public void onStatus(Status status)
    {
        try
        {
            if (!isProcessing)
            {
                if (!status.isRetweet())
                {
                    LOGGER.info("@" + status.getUser().getScreenName() + " - " + status.getText());

                    MediaEntity mediaEntity = ArrayIterate.getFirst(status.getMediaEntities());

                    if (mediaEntity != null)
                    {
                        LOGGER.info("Media Entity" + mediaEntity.getMediaURL());
                        long startTime = System.currentTimeMillis();
                        this.isProcessing = true;
                        File file = FileUtils.downloadAndSaveMedia(mediaEntity.getMediaURL(), mediaEntity.getId());
                        JpegImageWrapper imageWrapper = JpegImageWrapper.loadFile(file);

                        MazeParser mazeParser = new MazeParser();
                        MazeMap mazeMap = null;
                        try
                        {
                            mazeMap = mazeParser.buildFromImage(imageWrapper, 19, 19);
                        }
                        catch (Exception e)
                        {
                            StatusUpdate statusUpdate = new StatusUpdate("@" + status.getUser().getScreenName()
                                    + " Unable to parse the maze!");
                            statusUpdate.inReplyToStatusId(status.getId());
                            twitter.updateStatus(statusUpdate);
                        }

//                    MazeParserRunner.printMazeMap(mazeMap);
                        long mazeParsedTime = System.currentTimeMillis();
                        if (mazeMap != null)
                        {

                            MutableList<Vertex> vertices = MazeMapToVertexListAdapter.adapt(mazeMap);
                            Pair<MutableStack<Vertex>, Set<Vertex>> pathVisitedVerticesPair = null;
                            try
                            {
                                pathVisitedVerticesPair = DijkstraAlgorithm.findPath(vertices);
                            }
                            catch (Exception e)
                            {
                                StatusUpdate statusUpdate = new StatusUpdate("@" + status.getUser().getScreenName()
                                        + " Unable to find feasible path!");
                                statusUpdate.inReplyToStatusId(status.getId());
                                twitter.updateStatus(statusUpdate);
                            }
                            if (pathVisitedVerticesPair != null)
                            {

                                File solvedMaze = FileUtils.writeSolvedMaze(pathVisitedVerticesPair.getOne(), pathVisitedVerticesPair.getTwo(), mazeMap);
                                long mazeSolvedTime = System.currentTimeMillis();
                                StatusUpdate statusUpdate = new StatusUpdate("@" + status.getUser().getScreenName()
                                        + " Solved your maze:" + mediaEntity.getId());
                                statusUpdate.setMedia(solvedMaze);
                                statusUpdate.inReplyToStatusId(status.getId());
                                twitter.updateStatus(statusUpdate);

                                List<Vertex> flattenedPath = Ev3Traverser.getFlattenedPath(pathVisitedVerticesPair.getOne());
                                Ev3Traverser.moveAlongPath(this.pilot, flattenedPath);
                            }
                        }
                        this.isProcessing = false;
                    }
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
