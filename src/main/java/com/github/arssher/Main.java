package com.github.arssher;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.InsetsChooserPanel;
import org.jfree.ui.RefineryUtilities;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class Main {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2016, Calendar.MAY, 1, 0, 0);
        Date date = calendar.getTime();

        try {
//            TweetsContainer<Tweet> realMadridTweets = Accessor.search("Real Madrid", date, 600);
//            TweetsContainer<Tweet> atleticoMadridTweets = Accessor.search("Atletico Madrid", date, 1500);
//            Map<String, String> languages = Accessor.getLanguages();
//            drawBarChart(realMadridTweets, atleticoMadridTweets, languages);

            new Accessor("madrid").searchAndCache("Atletico Madrid", date, 1500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  public static void drawBarChart(TweetsContainer<Tweet> realMadridTweets, TweetsContainer<Tweet> athleticoMadridTweets,
                                  Map<String, String> langs) {
      System.out.println(athleticoMadridTweets.size());
      System.out.println(realMadridTweets.size());
      Map<String, Integer> realMadridTweetsGroupedCounted = realMadridTweets.groupByLangCount();
      Map<String, Integer> athleticoMadridGroupedCounted = athleticoMadridTweets.groupByLangCount();

      Set<String> allLangCodes = new HashSet<>();
      allLangCodes.addAll(realMadridTweetsGroupedCounted.keySet());
      allLangCodes.addAll(athleticoMadridGroupedCounted.keySet());

      DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      for (String langCode : allLangCodes) {
          String langName = langs.getOrDefault(langCode, "Unknown");
          dataset.addValue(realMadridTweetsGroupedCounted.getOrDefault(langCode, 0), "RealMadrid tweets", langName);
          dataset.addValue(athleticoMadridGroupedCounted.getOrDefault(langCode, 0), "AthleticoMadrid tweets", langName);
      }

      BarChartAWT chart = new BarChartAWT("RealMadrid and AthleticoMadrid tweets by language",
              "RealMadrid and AthleticoMadrid tweets by language", dataset);
      chart.pack();
      RefineryUtilities.centerFrameOnScreen(chart);
      chart.setVisible( true );
  }
}
