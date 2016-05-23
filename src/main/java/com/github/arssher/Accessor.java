package com.github.arssher;

import twitter4j.*;

import java.io.*;
import java.nio.file.Files;
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
    public Accessor(String queryDir) throws IOException {
        if (dataPath == null) {
            throw new IOException("An attempt to use Accessor with caching using misconfigured dataPath");
        }
        this.queryDir = queryDir;
        loadState();
    }

    /**
     * Caches a collection of tweets searched by {@code query}, created later than {@code since}.
     * Returns {@code querySize} tweets
     *
     * @param query     - search string
     * @param since     - search tweets since specified day, month and year
     * @param querySize - number of tweets to retrieve
     */
    public boolean searchAndCache(String query, Date since, int querySize) throws
            IOException, InterruptedException, TwitterException {

        Query twitter4jQuery = buildSearchQuery(query, since, querySize);

        // tweets left to retrieve
        int tweetsToRetrieve = querySize - totalTweets;
        // counter of batches
        int batchCounter = 0;

        while (totalTweets < querySize) {
            logger.log(Level.INFO, "Starting batch {0}, {1} tweets left to retrieve",
                    new Object[]{batchCounter, tweetsToRetrieve});

            // check limits
            checkTwitterLimits();

            // download tweets and process them
            QueryResult qResult = twitter.search(twitter4jQuery);
            saveBatch(qResult.getTweets());

            // update counters
            tweetsToRetrieve = querySize - totalTweets;
            twitter4jQuery.setCount(numberOfTweetsPerQuery(tweetsToRetrieve));
            twitter4jQuery.setMaxId(maxID);
            batchCounter++;
        }

        return true;
    }

    /**
     * Returns a collection of tweets searched by {@code query}, created later than {@code since}.
     * Returns {@code querySize} tweets
     *
     * @param query     - search string
     * @param since     - search tweets since specified day, month and year
     * @param querySize - number of tweets to retrieve
     */
    public static boolean search(String query, Date since, int querySize) throws
            IOException, InterruptedException, TwitterException {
        Query twitter4jQuery = buildSearchQuery(query, since, querySize);

        // tweets left to retrieve
        int tweetsToRetrieve = querySize;
        // counter of batches
        int batchCounter = 0;
        QueryResult qResult;
        do {
            logger.log(Level.INFO, "Starting batch {0}, {1} tweets left to retrieve",
                    new Object[]{batchCounter, tweetsToRetrieve});

            // check limits
            checkTwitterLimits();

            // download tweets
            qResult = twitter.search(twitter4jQuery);
            List<Status> tweets = qResult.getTweets();
            for (Status s: tweets) {
                logger.log(Level.INFO, "{0}", s.getText());
            }

            tweetsToRetrieve -= tweets.size();
            twitter4jQuery = qResult.nextQuery();
            batchCounter++;
        } while (qResult.hasNext() && tweetsToRetrieve > 0);

        return true;
    }

    private static Query buildSearchQuery(String query, Date since, int querySize) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String sinceString = dateFormatter.format(since);
        Query twitter4jQuery = new Query(query);
        twitter4jQuery.setSince(sinceString);
        twitter4jQuery.setCount(numberOfTweetsPerQuery(querySize));
        return twitter4jQuery;
    }

    private static int numberOfTweetsPerQuery(int wanted) { return Math.min(wanted, batchSize); }

    /**
     * Sets maxID, totalTweets and their paths
     *
     */
    private void loadState() throws IOException {
        String queryPath = Paths.get(dataPath, queryDir).normalize().toString();
        // create directory for query cache, if doesn't exist
        new File(queryPath).mkdirs();

        maxIDPath = Paths.get(queryPath, maxIDFilename).normalize().toString();
        tweetsPath = Paths.get(queryPath, tweetsFilename).normalize().toString();

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

        // set totalTweets
        totalTweets = countLines(tweetsPath);
        logger.log(Level.INFO, "totalTweets is set to {0}", totalTweets);
    }

    /**
     * Saves a batch of tweets and updates maxID
     *
     * @param tweets - tweets to save
     */
    private void saveBatch(List<Status> tweets) throws IOException, TwitterException {
        logger.log(Level.INFO, "Saving batch...");

        // save tweets
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(tweetsPath, true));
            for (Status s: tweets) {
                String statusJson = TwitterObjectFactory.getRawJSON(s);
                pw.println(statusJson);

                logger.log(Level.INFO, "{0}", s.getText());
                if (s.getId() < maxID) {
                    maxID = s.getId() - 1;
                }
            }
        } finally {
            if (pw != null)
                pw.close();
        }

        // save maxID and update totalTweets
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new File(maxIDPath));
            writer.println(maxID);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        totalTweets += tweets.size();
        logger.log(Level.INFO, "totalTweets now is {0}", totalTweets);
    }

    private static void checkTwitterLimits() throws TwitterException, InterruptedException {
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
    }

    /**
     * Returns number of lines in file, 0 if file doesn't exists
     *
     * @param filename - name of file
     */
    private static int countLines(String filename) throws IOException {
        if (!(new File(filename).isFile()))
            return 0;

        LineNumberReader reader = null;
        int cnt;
        try {
            reader = new LineNumberReader(new FileReader(filename));
            while ((reader.readLine()) != null);

            cnt = reader.getLineNumber();
        } finally {
            if (reader != null)
                reader.close();
        }
        return cnt;
    }


    private static void configureLogging() {
        logger = Logger.getLogger(Accessor.class.getName());
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LogFormatter());
        logger.addHandler(handler);
    }

    private static Logger logger;
    // we will download tweets with id less than maxID
    private long maxID;
    // total tweets retrieved
    private int totalTweets;
    // directory inside dataPath where tweets are cached
    private String queryDir;
    // path to file with maxID
    private String maxIDPath;
    // path to stored tweets
    private String tweetsPath;

    private static final Twitter twitter = TwitterFactory.getSingleton();
    private static final int batchSize = 100;
    private static final String propsFilename = "javaTwitterTask.properties";
    private static final String maxIDFilename = "maxID.txt";
    private static final String tweetsFilename = "tweets.json";

    // path to directory with cached data
    private static String dataPath = null;

    // configure logging and load dataPath
    static {
        configureLogging();

        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = Accessor.class.getClassLoader().getResourceAsStream(propsFilename);
            if (input == null)
                logger.log(Level.WARNING, "Applications properties {0} not found, caching is not allowed", propsFilename);
            else {
                prop.load(input);
                dataPath = prop.getProperty("dataPath");
                if (dataPath == null)
                    logger.log(Level.WARNING, "dataPath member is not found in application properties, caching is not allowed");
                else {
                    new File(dataPath).mkdirs();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if (input != null)
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
