package crawler.akka;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static crawler.util.FileHelper.saveResult;

public class UrlAggregator extends AbstractActor {

    private final Map<String, Integer> urls = new HashMap<>(5000, 0.99F);
    private long totalUrlCount = 0;

    public static class AddUrl {

        private final String url;

        public AddUrl(String url) {
            this.url = url;
        }
    }

    public static class WriteOutput {

    }

    public static Props props() {
        return Props.create(UrlAggregator.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AddUrl.class, this::updateMap)
                .match(WriteOutput.class, this::writeOutput)
                .build();
    }

    private void updateMap(AddUrl message) {
        urls.computeIfPresent(message.url, (k, v) -> ++v);
        urls.putIfAbsent(message.url, 1);
        ++totalUrlCount;
    }

    private void writeOutput(WriteOutput message) throws IOException {
        System.out.println(totalUrlCount);
        saveResult("output.csv", urls);
        getContext().getSystem().terminate();
    }


}
