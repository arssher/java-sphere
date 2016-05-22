package com.github.arssher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2016, Calendar.MAY, 1, 0, 0);
        Date date = calendar.getTime();

        try {
            System.out.println(new Accessor().search("Madrid", date, 5));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Hello World!"); // Display the string.
    }
}
