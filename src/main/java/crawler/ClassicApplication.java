package crawler;

import static crawler.util.FileHelper.saveResult;

import crawler.util.HtmlParser;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class ClassicApplication {

    public static void main(String[] args) throws IOException {
        final String url = "https://en.wikipedia.org/wiki/Europe";
        final HtmlParser htmlParser = new HtmlParser();
        final Map<String, Integer> aggregator = new ConcurrentHashMap<>();

        final ForkJoinPool executorService = new ForkJoinPool();

        final List<ForkJoinTask> futures = new LinkedList<>();

        htmlParser.getUrls(url)
                .parallelStream()
                .peek(href -> aggregator.merge(href, 1, (v1, v2) -> v1 + v2))
                .distinct()
                .forEach(href -> futures
                        .add(executorService.submit(createUrlCounterRunnable(htmlParser, aggregator, href))));

        futures.stream()
                .filter(Objects::nonNull)
                .forEach(ForkJoinTask::join);

        executorService.shutdown();

        saveResult("output1.csv", aggregator);
    }

    private static Runnable createUrlCounterRunnable(HtmlParser htmlParser, Map<String, Integer> aggregator,
            String url) {
        return () -> htmlParser.getUrls(url)
                .parallelStream()
                .forEach(href -> aggregator.merge(href, 1, (v1, v2) -> v1 + v2));
    }
}
