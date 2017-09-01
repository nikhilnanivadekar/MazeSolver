package nvv.robotics;

import nvv.robotics.integration.CustomStatusListener;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Scanner;

public class AppMain
{
    private static final String HASH_TO_POLL = "nikhilev3";

    public static void main(String[] args) throws Exception
    {
        Configuration build = AppMain.getConfiguration();
        TwitterStream twitterStream = new TwitterStreamFactory(build).getInstance();
        StatusListener listener = new CustomStatusListener();

        twitterStream.addListener(listener);
        twitterStream.filter(HASH_TO_POLL);

        boolean run = true;

        Scanner scanner = new Scanner(System.in);
        while (run)
        {
            run = !scanner.hasNext("stop");
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
