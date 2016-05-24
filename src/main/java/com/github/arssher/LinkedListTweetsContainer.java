package com.github.arssher;


import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class LinkedListTweetsContainer<T extends Tweet> implements TweetsContainer<T> {
    public LinkedListTweetsContainer() {
        tweets = new LinkedList<>();
    }

    @Override
    public int size() {
        return tweets.size();
    }

    @Override
    public boolean add(T tweet) {
        return tweets.add(tweet);
    }

    @Override
    public boolean contains(T tweet) {
        return tweets.contains(tweet);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return tweets.addAll(collection);
    }

    @Override
    public boolean remove(T tweet) {
        return tweets.remove(tweet);
    }

    @Override
    public void clear() {
        tweets.clear();
    }

    @Override
    public int countUnique() {
        Set<Long> ids = new HashSet<>();
        tweets.forEach(t -> ids.add(t.getID()));
        return ids.size();
    }

    @Override
    public Tweet getOldest() {
        return Collections.max(tweets, new Tweet.TimedStampComparator());
    }

    @Override
    public Tweet getTopRated() {
        return Collections.max(tweets, new Tweet.FavCountComparator());
    }

    @Override
    public void sort(Comparator<T> comparator) {
        Collections.sort(tweets, comparator);
    }

    @Override
    public Map<String, List<T>> groupByLang() {
        return tweets.stream().collect(Collectors.groupingBy(T::getLang));
    }

    @Override
    public Map<String, Integer> groupByLangCount() {
        return groupByLang().entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().size()));
    }

    @Override
    public Iterator<T> iterator() {
        return tweets.iterator();
    }

    @Override
    public T getFirst() {
        return tweets.get(0);
    }

    private final List<T> tweets;
}
