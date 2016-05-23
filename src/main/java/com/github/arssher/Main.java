package com.github.arssher;

import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2016, Calendar.MAY, 1, 0, 0);
        Date date = calendar.getTime();

        try {
//            System.out.println(new Accessor("madrid").searchAndCache("Madrid", date, 5));
            System.out.println(Accessor.search("Madrid", date, 5));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
