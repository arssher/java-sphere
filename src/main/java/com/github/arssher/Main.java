package com.github.arssher;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2016, Calendar.MAY, 1, 0, 0);
        Date date = calendar.getTime();

        try {
//            System.out.println(new Accessor("madrid").searchAndCache("Madrid", date, 5));
            TweetsContainer<Tweet> tweets = Accessor.search("Madrid", date, 5);
            for (Tweet tweet: tweets) {
                System.out.println(tweet);
            }
            System.out.println("______________________________");
            tweets.sort(new Tweet.RetweetCountComparator());
            for (Tweet tweet: tweets) {
                System.out.println(tweet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
