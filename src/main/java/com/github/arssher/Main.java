package com.github.arssher;

import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2016, Calendar.MAY, 1, 0, 0);
        Date date = calendar.getTime();

        System.out.println(new Accessor().search("Madrid", date, 5));
        System.out.println("Hello World!"); // Display the string.
    }
}
