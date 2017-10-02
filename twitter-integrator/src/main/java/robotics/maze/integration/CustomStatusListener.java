package robotics.maze.integration;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.eclipse.collections.impl.utility.ArrayIterate;
import robotics.maze.DifferentialMotor;
import robotics.maze.Ev3Traverser;
import robotics.maze.dijkstra.DijkstraAlgorithm;
import robotics.maze.dijkstra.MazeMapToVertexListAdapter;
import robotics.maze.dijkstra.Vertex;
import robotics.maze.exceptions.AmazeProcessingException;
import robotics.maze.image.JpegImageWrapper;
import robotics.maze.image.MazeImageCreator;
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
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

import java.io.File;
import java.util.List;

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
                        this.isProcessing = true;
                        File file = FileUtils.downloadAndSaveMedia(mediaEntity.getMediaURL(), mediaEntity.getId());
                        JpegImageWrapper imageWrapper = JpegImageWrapper.loadFile(file);
                        try
                        {
                            MazeParser mazeParser = new MazeParser();
                            MazeMap mazeMap = mazeParser.buildFromImage(imageWrapper, 19, 19);

                            MazeParserRunner.printMazeMap(mazeMap);

                            if (mazeMap.isSuccess())
                            {
                                MutableList<Vertex> vertices = MazeMapToVertexListAdapter.adapt(mazeMap);
                                MutableStack<Vertex> path = DijkstraAlgorithm.findPath(vertices);

                                MazeImageCreator.drawPathOnImage(
                                        mazeMap.getMazeImage(),
                                        mazeMap.getOriginalImageCoordinates(),
                                        path.collect(vertex -> PrimitiveTuples.pair(vertex.getX(), vertex.getY()), Lists.mutable.of()));
                                MazeImageCreator.addRightTextToImage(mazeMap.getMazeImage(),
                                        path.collect(vertex -> String.format("%02d-%02d", vertex.getX(), vertex.getY()), Lists.mutable.of()));

                                File solvedMaze = FileUtils.saveImageToFile(mazeMap.getMazeImage(), "parsed_maze_path");
                                String midString = path.isEmpty() ? " Could find path!" : " Solved your maze:";

                                StatusUpdate statusUpdate = new StatusUpdate("@" + status.getUser().getScreenName()
                                        + midString + mediaEntity.getId());
                                statusUpdate.setMedia(solvedMaze);
                                statusUpdate.inReplyToStatusId(status.getId());
                                twitter.updateStatus(statusUpdate);

                                if (path.notEmpty())
                                {
                                    List<Vertex> flattenedPath = Ev3Traverser.getFlattenedPath(path);
                                    Ev3Traverser.moveAlongPath(this.pilot, flattenedPath);
                                }
                            }
                            else
                            {
                                File unparsedMaze = FileUtils.saveImageToFile(mazeMap.getMazeImage(), "unparsed_maze_path");
                                StatusUpdate statusUpdate = new StatusUpdate("@" + status.getUser().getScreenName()
                                        + " " + mazeMap.getGrievances().makeString(", "));
                                statusUpdate.setMedia(unparsedMaze);
                                statusUpdate.inReplyToStatusId(status.getId());
                                twitter.updateStatus(statusUpdate);
                            }
                        }
                        catch (AmazeProcessingException e)
                        {
                            StatusUpdate statusUpdate = new StatusUpdate("@" + status.getUser().getScreenName()
                                    + " " + e.getMessage());
                            statusUpdate.inReplyToStatusId(status.getId());
                            twitter.updateStatus(statusUpdate);
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
