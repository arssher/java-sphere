package com.github.arssher;

import twitter4j.*;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Accessor {
    public Accessor() throws IOException {
        logger = Logger.getLogger(Accessor.class.getName());
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());
        logger.addHandler(handler);

        loadData();
    }

    /**
     * Returns a collection of tweets searched by {@code query}, created later than {@code since}.
     * Returns {@code querySize} tweets
     *
     * @param query     - search string
     * @param since     - search tweets since specified day, month and year
     * @param querySize - number of tweets to retrieve
     */
    public boolean search(String query, Date since, int querySize) throws
            IOException, InterruptedException, TwitterException {
        Twitter twitter = TwitterFactory.getSingleton();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String sinceString = dateFormatter.format(since);
        Query twitter4jQuery = new Query(query);
        twitter4jQuery.setSince(sinceString);
        twitter4jQuery.setCount(numberOfTweetsPerQuery(querySize));

        try {
            // total tweets retrieved
            int totalTweets = 0;
            // tweets left to retrieve
            int tweetsToRetrieve = querySize;
            // counter of batches
            int batchCounter = 0;
            // we will download tweets with id less that maxID
            maxID = Long.MAX_VALUE;
            while (totalTweets < querySize) {
                logger.log(Level.INFO, "Starting batch {0}, {1} tweets left to retrieve",
                        new Object[]{batchCounter, tweetsToRetrieve});

                // check limits
                RateLimitStatus searchTweetsRateLimit = twitter.getRateLimitStatus("search").get("/search/tweets");
                int callsLeft = searchTweetsRateLimit.getRemaining();
                int secondsToSleep = searchTweetsRateLimit.getSecondsUntilReset();
                logger.log(Level.INFO, "You have {0} calls remaining out of {1}, Limit resets in {2} seconds",
                        new Object[]{
                                callsLeft,
                                searchTweetsRateLimit.getLimit(),
                                secondsToSleep});
                if (callsLeft == 0) {
                    logger.log(Level.INFO, "Sleeping {0} seconds due to rate limis", secondsToSleep);
                    Thread.sleep((secondsToSleep + 5) * 1000);
                }

                // download tweets
                QueryResult qResult = twitter.search(twitter4jQuery);
                List<Status> tweets = qResult.getTweets();

                // process tweets and update maxID
                for (Status s: tweets) {
                    logger.log(Level.INFO, "{0}", s.getText());
                    if (s.getId() < maxID) {
                        maxID = s.getId() - 1;
                    }
                }

                // update counters
                totalTweets += tweets.size();
                tweetsToRetrieve = querySize - totalTweets;
                twitter4jQuery.setCount(numberOfTweetsPerQuery(tweetsToRetrieve));
                twitter4jQuery.setMaxId(maxID);
                batchCounter++;
            }

        } finally {
            saveData();
        }

        logger.log(Level.INFO, "hie!");


//        QueryResult result = twitter.search(twitter4jQuery);

//        List<Status> statuses = null;
//        try {
//            statuses = twitter.getHomeTimeline();
//        } catch (TwitterException e) {
//            e.printStackTrace();
//        }
////        List<Status> statuses = Collections.<Status>emptyList();
//        System.out.println("Showing home timeline.");
//        for (Status status : statuses) {
//            System.out.println(status.getUser().getName() + ":" +
//                    status.getText());
//            System.out.println("Successfully updated the status to [" + status.getText() + "].");
//        }
        return true;
    }

    private int numberOfTweetsPerQuery(int wanted) { return Math.min(wanted, 2); }

    private void loadData() throws IOException {
        prop = new Properties();
        InputStream input = null;
        try {
            input = getClass().getClassLoader().getResourceAsStream(propsFilename);
            prop.load(input);
            dataPath = prop.getProperty("dataPath");
            if (dataPath == null)
                throw new IOException("dataPath member is not found in application properties");
        }
        finally {
            if (input != null)
                 input.close();
        }
        maxIDPath = Paths.get(dataPath, maxIDFilename).normalize().toString();

        // now read maxID
        if (new File(maxIDPath).isFile()) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(new FileReader(maxIDPath));
                maxID = scanner.nextLong();
            } finally {
                if (scanner != null)
                    scanner.close();
            }
        }
        else {
            maxID = Long.MAX_VALUE;
        }
        logger.log(Level.INFO, "maxID is set to {0}", maxID);
    }

    private void saveData() throws IOException {
        logger.log(Level.INFO, "Saving data...");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new File(maxIDPath));
            writer.println(maxID);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }


    private final Logger logger;
    private String dataPath;
    private long maxID;
    private Properties prop;
    private static final String propsFilename = "javaTwitterTask.properties";
    private static final String maxIDFilename = "maxID.txt";
    private static String maxIDPath;
}
