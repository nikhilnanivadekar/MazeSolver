package robotics.maze;

import robotics.maze.integration.CustomStatusListener;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Scanner;

public class AppMain
{
    private static final String HASH_TO_POLL = "nikhilev3";
    private static final String EV3_IP_ADDRESS = "10.0.1.1";
    private static final double WHEEL_DIAMETER = 33.0;
    private static final double TRACK_WIDTH = 150.0;

    public static void main(String[] args) throws Exception
    {
        DifferentialMotor pilot = new DifferentialMotor(
                WHEEL_DIAMETER,
                TRACK_WIDTH,
                "A",
                "B",
                100.0,
                100.0,
                EV3_IP_ADDRESS);

        Configuration build = AppMain.getConfiguration();
        TwitterStream twitterStream = new TwitterStreamFactory(build).getInstance();
        StatusListener listener = new CustomStatusListener(pilot);

        twitterStream.addListener(listener);
        twitterStream.filter(HASH_TO_POLL);

        boolean run = true;

        Scanner scanner = new Scanner(System.in);
        while (run)
        {
            run = !scanner.hasNext("stop");
        }

        if (pilot != null)
        {
            pilot.getPilot().stop();
            pilot.getPilot().close();

            pilot.getEv3().disConnect();
        }

        twitterStream.clearListeners();
        twitterStream.cleanUp();
        twitterStream.shutdown();

        System.exit(0);
    }

    private static Configuration getConfiguration()
    {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("Enter OAuth Consumer Key")
                .setOAuthConsumerSecret("Enter OAuth Consumer Secret")
                .setOAuthAccessToken("Enter OAuth Access Token")
                .setOAuthAccessTokenSecret("Enter OAuth Access Token Secret");

        return cb.build();
    }
}
