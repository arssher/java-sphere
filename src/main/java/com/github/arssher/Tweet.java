package com.github.arssher;


import twitter4j.Status;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class Tweet implements Serializable {
    public Tweet(Status status) {
        id = status.getId();
        userID = status.getUser().getId();
        content = status.getText();
        timestamp = status.getCreatedAt();
        favoriteCount = status.getFavoriteCount();
        retweetCount = status.getRetweetCount();
        lang = status.getLang();
    }

    public Tweet(long id, long userID, String content, Date timestamp, int favoriteCount, int retweetCount, String lang) {
        this.id = id;
        this.userID = id;
        this.content = content;
        this.timestamp = timestamp;
        this.favoriteCount = favoriteCount;
        this.retweetCount = retweetCount;
        this.lang = lang;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tweet))
            return false;
        if (o == this)
            return true;
        Tweet t2 = (Tweet) o;
        return id == t2.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "ID " + id + ", created at " + timestamp + ", fav count: " + favoriteCount +
                ", ret count: " + retweetCount + ", content: " + content;
    }

    private final long id;
    private final long userID;
    private final String content;
    private final Date timestamp;
    private final int favoriteCount;
    private final int retweetCount;
    private final String lang;

    public long getID() { return id; }

    public String getStringID() { return String.valueOf(id); }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public String getLang() {
        return lang;
    }

    static class TimedStampComparator implements Comparator<Tweet> {
        public int compare(Tweet t1, Tweet t2) {
            return t1.getTimestamp().compareTo(t2.getTimestamp());
        }
    }

    static class FavCountComparator implements Comparator<Tweet> {
        public int compare(Tweet t1, Tweet t2) {
            return t1.getFavoriteCount() - t2.getFavoriteCount();
        }
    }

    static class RetweetCountComparator implements Comparator<Tweet> {
        public int compare(Tweet t1, Tweet t2) {
            return t1.getRetweetCount() - t2.getRetweetCount();
        }
    }
}
