package com.github.arssher;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public interface TweetsContainer<T extends Tweet> extends Iterable<T>, Serializable {
    /**
     * Returns the number of elements in this container.  If this container contains
     * more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this list
     */
    int size();

    /**
     * Get first element of the container
     *
     * @return the first element
     */
     T getFirst();

    /**
     * Adds an element to this container
     *
     * @param tweet element to add
     * @return true if this container changed as a result of the call
     */
    boolean add(T tweet);

    /**
     * Checks whether tweet is in container
     *
     * @param tweet element to check
     * @return true if contains
     */
    boolean contains(T tweet);

    /**
     * Adds all of the elements in the specified collection to this container
     *
     * @param tweets collection containing elements to be added to this container
     * @return true if this container changed as a result of the call
     */
    boolean addAll(Collection<? extends T> tweets);

    /**
     * Removes a single instance of the specified element from this
     * container, if it is present
     *
     * @param tweet element to be removed from this container, if present
     * @return true if an element was removed as a result of this call
     */
    boolean remove(T tweet);

    /**
     * Removes all of the elements from this container.
     * The container will be empty after this method returns.
     */
    void clear();

    /**
     * Counts number of unique tweets
     */
    int countUnique();

    /**
     * @return oldest tweet
     */
    Tweet getOldest();

    /**
     * @return tweet most favorited
     */
    Tweet getTopRated();

    /**
     * Inplace sort using
     * @param comparator
     */
    void sort(Comparator<T> comparator);

    /**
     * Group elements in container by theirs langs
     * @return grouped
     */
    Map<String, List<T>> groupByLang();

    /**
     * Group elements in container by theirs langs and count them
     * @return grouped
     */
    Map<String, Integer> groupByLangCount();

    /**
     * 1. Select tweets where field lang equals
     * @param lang language of analysing tweets from TweetsContainer
     * 2. Get tags from selected tweets
     * `Tag` is a word(meaning) from tweet
     * To simplify the analysis we assume word to be `meaning` if it consists of more then 3 characters
     * @see https://dev.twitter.com/rest/reference/get/help/languages
     * 3. Count occurences of each unique tag
     * 4. Normalize occurences of each tag:
     *      | (sum of normalizedOccurences for teg in uniqueTegs) - 1| <= 10^(-6)
     *
     * @return map of Key: uniqueTag, Value: normalizedOcurencesNumber
     */
//    Map<String, Double> getTagCloud(String lang);
}
