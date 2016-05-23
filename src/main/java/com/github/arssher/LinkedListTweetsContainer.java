package com.github.arssher;


import java.util.*;
import java.util.function.Consumer;

public class LinkedListTweetsContainer<T extends Tweet> implements TweetsContainer {
    public LinkedListTweetsContainer() {
        tweets = new LinkedList<>();
    }

    @Override
    public boolean add(Tweet tweet) {
        return tweets.add(tweet);
    }

    @Override
    public boolean addAll(Collection collection) {
        return tweets.addAll(collection);
    }

    @Override
    public boolean remove(Tweet tweet) {
        return tweets.remove(tweet);
    }

    @Override
    public void clear() {
        tweets.clear();
    }

    @Override
    public Tweet getTopRated() {
        return Collections.max(tweets, new Tweet.FavCountComparator());
    }

    @Override
    public void sort(Comparator comparator) {
        Collections.sort(tweets, comparator);
    }


    @Override
    public Iterator iterator() {
        return tweets.iterator();
    }

    @Override
    public void forEach(Consumer action) {
        tweets.forEach(tweet -> action.accept(tweet));
    }

    private final List<Tweet> tweets;
}
