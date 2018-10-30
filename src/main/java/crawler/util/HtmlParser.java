package crawler.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlParser {

  public List<String> getUrls(String url) {
    Document document;
    try {
      document = Jsoup.connect(url)
          .followRedirects(false)
          .timeout(2500)
          .get();
    } catch (Exception e) {
      System.err.println(e.getMessage() + " -- " + url);
      return Collections.emptyList();
    }
    return document.select("a[href^=http]")
        .stream()
        .map(h -> h.attr("href"))
        .collect(Collectors.toList());
  }
}
