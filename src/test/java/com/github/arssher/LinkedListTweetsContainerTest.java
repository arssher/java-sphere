package com.github.arssher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LinkedListTweetsContainerTest  {
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        tweetsContainer.clear();
    }

    @Test
    public void testAdd() {
        Tweet t1 = new Tweet(1, 2, "param pam", new Date(), 1, 1, "en");
        Tweet t2 = new Tweet(2, 2, "param pam", new Date(), 1, 1, "en");
        tweetsContainer.add(t1);
        tweetsContainer.add(t2);
        assertTrue(tweetsContainer.contains(t1));
        assertTrue(tweetsContainer.contains(t2));
    }

    @Test
    public void testAddAll() {
        Tweet t1 = new Tweet(1, 2, "param pam", new Date(), 1, 1, "en");
        Tweet t2 = new Tweet(2, 2, "param pam", new Date(), 1, 1, "en");
        tweetsContainer.add(t1);
        tweetsContainer.add(t2);
        Tweet t3 = new Tweet(3, 2, "param pam", new Date(), 1, 1, "en");
        Tweet t4 = new Tweet(4, 2, "param pam", new Date(), 1, 1, "en");
        List<Tweet> tweets = new ArrayList<>();
        tweets.add(t3);
        tweets.add(t4);
        tweetsContainer.addAll(tweets);
        assertTrue(tweetsContainer.contains(t1));
        assertTrue(tweetsContainer.contains(t2));
        assertTrue(tweetsContainer.contains(t3));
        assertTrue(tweetsContainer.contains(t4));
    }

    @Test
    public void testSize() {
        Tweet t1 = new Tweet(1, 2, "param pam", new Date(), 1, 1, "en");
        Tweet t2 = new Tweet(2, 2, "param pam", new Date(), 1, 1, "en");
        Tweet t3 = new Tweet(3, 2, "param pam", new Date(), 1, 1, "en");
        Tweet t4 = new Tweet(4, 2, "param pam", new Date(), 1, 1, "en");
        tweetsContainer.add(t1);
        tweetsContainer.add(t2);
        tweetsContainer.add(t3);
        tweetsContainer.add(t4);
        assertEquals(tweetsContainer.size(), 4);
    }

    @Test
    public void testRemove() {
        Tweet t1 = new Tweet(1, 2, "param pam", new Date(), 1, 1, "en");
        Tweet t2 = new Tweet(2, 2, "param pam", new Date(), 1, 1, "en");
        tweetsContainer.add(t1);
        tweetsContainer.add(t2);
        tweetsContainer.remove(t1);
        assertFalse(tweetsContainer.contains(t1));
    }

    @Test
    public void testClear() {
        Tweet t1 = new Tweet(1, 2, "param pam", new Date(), 1, 1, "en");
        Tweet t2 = new Tweet(2, 2, "param pam", new Date(), 1, 1, "en");
        tweetsContainer.add(t1);
        tweetsContainer.add(t2);
        tweetsContainer.clear();
        assertEquals(tweetsContainer.size(), 0);
    }

    @Test
    public void testCountUnique() {
        Tweet t1 = new Tweet(1, 2, "param pam", new Date(), 1, 1, "en");
        Tweet t2 = new Tweet(2, 2, "param pam", new Date(), 1, 1, "en");
        Tweet t3 = new Tweet(2, 2, "param pam", new Date(), 1, 1, "en");
        Tweet t4 = new Tweet(4, 2, "param pam", new Date(), 1, 1, "en");
        tweetsContainer.add(t1);
        tweetsContainer.add(t2);
        tweetsContainer.add(t3);
        tweetsContainer.add(t4);
        assertEquals(tweetsContainer.countUnique(), 3);
    }

    @Test
    public void testGetOldest() throws ParseException {
        Tweet t1 = new Tweet(1, 2, "param pam", new SimpleDateFormat("dd/MM/yyyy").parse("21/12/2015"), 1, 1, "en");
        Tweet t2 = new Tweet(2, 2, "param pam", new SimpleDateFormat("dd/MM/yyyy").parse("21/12/2012"), 1, 1, "en");
        Tweet t3 = new Tweet(3, 2, "param pam", new SimpleDateFormat("dd/MM/yyyy").parse("21/05/2012"), 1, 1, "en");
        tweetsContainer.add(t1);
        tweetsContainer.add(t2);
        tweetsContainer.add(t3);
        assertEquals(tweetsContainer.getOldest().getID(), 1);
    }

    @Test
    public void testTopRated() {
        Tweet t1 = new Tweet(1, 2, "param pam", new Date(), 1, 1, "en");
        Tweet t2 = new Tweet(2, 2, "param pam", new Date(), 10, 1, "en");
        Tweet t3 = new Tweet(3, 2, "param pam", new Date(), 100, 1, "en");
        tweetsContainer.add(t1);
        tweetsContainer.add(t2);
        tweetsContainer.add(t3);
        assertEquals(tweetsContainer.getTopRated().getID(), 3);
    }

    @Test
    public void testSort() {
        Tweet t1 = new Tweet(1, 2, "param pam", new Date(), 10, 1, "en");
        Tweet t2 = new Tweet(2, 2, "param pam", new Date(), 1, 1, "en");
        Tweet t3 = new Tweet(3, 2, "param pam", new Date(), 100, 1, "en");
        tweetsContainer.add(t1);
        tweetsContainer.add(t2);
        tweetsContainer.add(t3);
        Comparator<Tweet> comparator = new Tweet.FavCountComparator();
        tweetsContainer.sort(comparator);
        Tweet previousTweet = tweetsContainer.getFirst();
        for (Tweet tweet: tweetsContainer) {
            assertTrue(comparator.compare(previousTweet, tweet) <= 0);
        }
    }

    @Test
    public void testGroupByLang() {
        Tweet t1 = new Tweet(1, 2, "param pam", new Date(), 1, 1, "fr");
        Tweet t2 = new Tweet(2, 2, "param pam", new Date(), 1, 1, "fr");
        Tweet t3 = new Tweet(3, 2, "param pam", new Date(), 1, 1, "en");
        tweetsContainer.add(t1);
        tweetsContainer.add(t2);
        tweetsContainer.add(t3);
        Map<String, List<Tweet>> grouped = tweetsContainer.groupByLang();
        assertEquals(grouped.get("fr").size(), 2);
        assertEquals(grouped.get("en").size(), 1);
    }

    @Test
    public void testGetFirst() {
        Tweet t1 = new Tweet(1, 2, "param pam", new Date(), 1, 1, "fr");
        Tweet t2 = new Tweet(2, 2, "param pam", new Date(), 1, 1, "fr");
        Tweet t3 = new Tweet(3, 2, "param pam", new Date(), 1, 1, "en");
        tweetsContainer.add(t1);
        tweetsContainer.add(t2);
        tweetsContainer.add(t3);
        assertEquals(tweetsContainer.getFirst(), t1);
    }

    private TweetsContainer<Tweet> tweetsContainer = new LinkedListTweetsContainer<>();
}
